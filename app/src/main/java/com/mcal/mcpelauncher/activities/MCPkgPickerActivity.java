/*
 * Copyright (C) 2018-2021 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.mcpelauncher.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MCPkgPickerActivity extends BaseActivity {
    public static final int REQUEST_PICK_PACKAGE = 5;
    private static final int MSG_SHOW_LIST_VIEW = 1;
    private static final int MSG_SHOW_UNFOUND_VIEW = 2;
    private final UIHandler mUIHandler = new UIHandler();
    private List<PackageInfo> mInstalledPackages = null;

    public static void startThisActivity(AppCompatActivity context) {
        Intent intent = new Intent(context, MCPkgPickerActivity.class);
        context.startActivityForResult(intent, REQUEST_PICK_PACKAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pkg_picker);
        setResult(RESULT_CANCELED);

        View loading_view = findViewById(R.id.pkg_picker_package_loading_view);
        loading_view.setVisibility(View.VISIBLE);

        findViewById(R.id.pkg_picker_reset_button).setOnClickListener(p1 -> onResetClicked());
        new LoadingThread().start();
    }

    private void showListView() {
        View loading_view = findViewById(R.id.pkg_picker_package_loading_view);
        loading_view.setVisibility(View.GONE);

        View list_view = findViewById(R.id.pkg_picker_package_list_view);
        list_view.setVisibility(View.VISIBLE);

        ListView list = (ListView) list_view;
        list.setAdapter(new PackageListAdapter());
    }

    private void showUnfoundView() {
        View loading_view = findViewById(R.id.pkg_picker_package_loading_view);
        loading_view.setVisibility(View.GONE);

        View view = findViewById(R.id.pkg_picker_package_unfound_view);
        view.setVisibility(View.VISIBLE);
    }

    private void onResetClicked() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.pick_tips_title);
        dialog.setMessage(R.string.pick_tips_reset_message);
        dialog.setPositiveButton(android.R.string.ok, (p1, p2) -> {
            p1.dismiss();
            Intent intent = new Intent();
            Bundle extras = new Bundle();
            extras.putString("package_name", "com.mojang.minecraftpe");
            intent.putExtras(extras);
            setResult(RESULT_OK, intent);
            finish();
        });
        dialog.setNegativeButton(android.R.string.cancel, (p1, p2) -> p1.dismiss());
        dialog.show();
    }

    @SuppressLint("HandlerLeak")
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_SHOW_LIST_VIEW) {
                showListView();
            } else if (msg.what == MSG_SHOW_UNFOUND_VIEW) {
                showUnfoundView();
            }
        }
    }

    private class LoadingThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mInstalledPackages = getPackageManager().getInstalledPackages(PackageManager.GET_CONFIGURATIONS);
            if (mInstalledPackages != null && mInstalledPackages.size() > 0) {
                mUIHandler.sendEmptyMessage(MSG_SHOW_LIST_VIEW);
            } else {
                mUIHandler.sendEmptyMessage(MSG_SHOW_UNFOUND_VIEW);
            }
        }
    }

    private class PackageListAdapter extends BaseAdapter {
        public PackageListAdapter() {
            Collections.sort(mInstalledPackages, new Comparator<PackageInfo>() {
                final PackageManager pm = getPackageManager();

                @Override
                public int compare(PackageInfo o1, PackageInfo o2) {
                    return pm.getApplicationLabel(o1.applicationInfo).toString().compareToIgnoreCase(pm.getApplicationLabel(o2.applicationInfo).toString());
                }
            });
        }

        @Override
        public int getCount() {
            return mInstalledPackages.size();
        }

        @Override
        public Object getItem(int p1) {
            return p1;
        }

        @Override
        public long getItemId(int p1) {
            return p1;
        }

        @Override
        public View getView(int p1, View p2, ViewGroup p3) {
            final PackageInfo pkg = mInstalledPackages.get(p1);
            @SuppressLint("ViewHolder") View baseCardView = getLayoutInflater().inflate(R.layout.pkg_picker_item, null);
            AppCompatImageView imageView = baseCardView.findViewById(R.id.pkg_picker_package_item_card_view_image_view);
            try {
                Bitmap appIcon = BitmapFactory.decodeResource(createPackageContext(pkg.packageName, 0).getResources(), pkg.applicationInfo.icon);
                if (appIcon == null)
                    appIcon = BitmapFactory.decodeResource(getResources(), R.drawable.mcd_null_pack);
                imageView.setImageBitmap(appIcon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            AppCompatTextView name = baseCardView.findViewById(R.id.pkg_picker_package_item_card_view_text_name);
            name.setText(pkg.applicationInfo.loadLabel(getPackageManager()));
            AppCompatTextView pkgname = baseCardView.findViewById(R.id.pkg_picker_package_item_card_view_text_package_name);
            pkgname.setText(pkg.packageName);
            baseCardView.setOnClickListener(p113 -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MCPkgPickerActivity.this, R.style.AlertDialogTheme);
                dialog.setTitle(R.string.pick_tips_title);
                dialog.setMessage(getString(R.string.pick_tips_message, pkg.packageName, pkg.applicationInfo.loadLabel(getPackageManager())));
                dialog.setPositiveButton(android.R.string.ok, (p11, p21) -> {
                    p11.dismiss();
                    Intent intent = new Intent();
                    Bundle extras = new Bundle();
                    extras.putString("package_name", pkg.packageName);
                    intent.putExtras(extras);
                    setResult(RESULT_OK, intent);
                    finish();
                });
                dialog.setNegativeButton(android.R.string.cancel, (p112, p212) -> p112.dismiss());
                dialog.show();
            });
            return baseCardView;
        }
    }
}