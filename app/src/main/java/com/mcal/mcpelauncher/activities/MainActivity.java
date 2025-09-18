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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.mcpelauncher.fragments.MainManageNModFragment;
import com.mcal.mcpelauncher.fragments.MainSettingsFragment;
import com.mcal.mcpelauncher.fragments.MainStartFragment;
import com.mcal.mcpelauncher.ui.view.Dialogs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MainActivity extends BaseActivity {
    private ViewPager mMainViewPager;
    private MainManageNModFragment mManageNModFragment;
    private MainSettingsFragment mMainSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moddedpe_main_pager);
        ArrayList<Fragment> fragment_list = new ArrayList<>();
        ArrayList<CharSequence> titles_list = new ArrayList<>();

        MainStartFragment startFragment = new MainStartFragment();
        fragment_list.add(startFragment);
        titles_list.add(getString(R.string.main_title));

        mManageNModFragment = new MainManageNModFragment();
        fragment_list.add(mManageNModFragment);
        titles_list.add(getString(R.string.manage_nmod_title));

        mMainSettingsFragment = new MainSettingsFragment();
        fragment_list.add(mMainSettingsFragment);
        titles_list.add(getString(R.string.settings_title));

        MainFragmentPagerAdapter pagerAdapter = new MainFragmentPagerAdapter(fragment_list, titles_list);

        mMainViewPager = findViewById(R.id.moddedpe_main_view_pager);
        mMainViewPager.setAdapter(pagerAdapter);
        mMainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setTitle(mMainViewPager.getAdapter().getPageTitle(position));
                //conterchange.setText(""+(1+position));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Settings.ACTION_MANAGE_OVERLAY_PERMISSION}, 1);
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !Environment.isExternalStorageManager()) {
            Dialogs.showScopedStorageDialog(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mManageNModFragment.onActivityResult(requestCode, resultCode, data);
        mMainSettingsFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String errorString = Preferences.getOpenGameFailed();
        if (errorString != null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            dialog.setTitle(R.string.launch_failed_title);
            dialog.setMessage(getString(R.string.launch_failed_message, errorString));
            dialog.setPositiveButton(android.R.string.ok, (dialog1, which) -> dialog1.dismiss());
            dialog.show();
            Preferences.setOpenGameFailed(null);
        }
    }

    private class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments;
        private final List<CharSequence> mTitles;

        MainFragmentPagerAdapter(List<Fragment> fragments, List<CharSequence> titles) {
            super(getSupportFragmentManager());
            mFragments = fragments;
            mTitles = titles;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public @NotNull Fragment getItem(int p1) {
            return mFragments.get(p1);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }
}
