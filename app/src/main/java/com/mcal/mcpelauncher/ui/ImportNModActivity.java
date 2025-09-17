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
package com.mcal.mcpelauncher.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.ModdedPEApplication;
import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.activities.BaseActivity;
import com.mcal.mcpelauncher.activities.NModDescriptionActivity;
import com.mcal.pesdk.nmod.ExtractFailedException;
import com.mcal.pesdk.nmod.NMod;
import com.mcal.pesdk.nmod.ZippedNMod;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class ImportNModActivity extends BaseActivity {
    private static final int MSG_SUCCEED = 1;
    private static final int MSG_FAILED = 2;
    private final UIHandler mUIHandler = new UIHandler();
    private NMod mTargetNMod;
    private ExtractFailedException mFailedInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nmod_importer_loading);
        setTitle(R.string.import_nmod_title);

        File targetFile = getTargetNModFile();
        new ImportThread(targetFile).start();
    }

    @Nullable
    private File getTargetNModFile() {
        try {
            Intent intent = getIntent();
            Uri uri = intent.getData();
            return new File(new URI(uri.toString()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void onViewMoreClicked() {
        NModDescriptionActivity.startThisActivity(this, mTargetNMod);
    }

    private void onFailedViewMoreClicked() {
        setContentView(R.layout.nmod_importer_failed);
        AppCompatTextView errorText = findViewById(R.id.nmod_importer_failed_text_view);
        errorText.setText(getString(R.string.nmod_import_failed_full_info_message, new Object[]{mFailedInfo.toTypeString(), mFailedInfo.getCause().toString()}));
    }

    private class ImportThread extends Thread {
        private final File mTargetFile;

        ImportThread(File file) {
            mTargetFile = file;
        }

        @Override
        public void run() {
            super.run();
            try {
                if (mTargetFile != null && mTargetFile.getName().toLowerCase().endsWith(".so")) {
                    com.mcal.pesdk.somod.SoModManager mgr = new com.mcal.pesdk.somod.SoModManager(ImportNModActivity.this);
                    mgr.importSoFile(mTargetFile);
                    Message msg = new Message();
                    msg.what = MSG_SUCCEED;
                    msg.obj = null;
                    mUIHandler.sendMessage(msg);
                    return;
                }
                ZippedNMod zippedNMod = ModdedPEApplication.getMPESdk().getNModAPI().archiveZippedNMod(mTargetFile.getAbsolutePath());
                ModdedPEApplication.getMPESdk().getNModAPI().importNMod(zippedNMod);
                Message msg = new Message();
                msg.what = MSG_SUCCEED;
                msg.obj = zippedNMod;
                mUIHandler.sendMessage(msg);
            } catch (ExtractFailedException archiveFailedException) {
                Message msg = new Message();
                msg.what = MSG_FAILED;
                msg.obj = archiveFailedException;
                mUIHandler.sendMessage(msg);
            } catch (Throwable t) {
                Message msg = new Message();
                msg.what = MSG_FAILED;
                msg.obj = new com.mcal.pesdk.nmod.ExtractFailedException(com.mcal.pesdk.nmod.ExtractFailedException.TYPE_IO_EXCEPTION, t);
                mUIHandler.sendMessage(msg);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_SUCCEED) {
                setContentView(R.layout.nmod_importer_succeed);
                findViewById(R.id.import_succeed_view_more_button).setOnClickListener(p1 -> onViewMoreClicked());
                mTargetNMod = (NMod) msg.obj;
            } else if (msg.what == MSG_FAILED) {
                setContentView(R.layout.nmod_importer_failed_msg);
                findViewById(R.id.import_failed_view_more_button).setOnClickListener(p1 -> onFailedViewMoreClicked());
                mFailedInfo = (ExtractFailedException) msg.obj;
                setTitle(R.string.nmod_import_failed);
                AppCompatTextView errorText = findViewById(R.id.nmod_import_failed_title_text_view);
                switch (mFailedInfo.getType()) {
                    case ExtractFailedException.TYPE_DECODE_FAILED:
                        errorText.setText(R.string.nmod_import_failed_message_decode);
                        break;
                    case ExtractFailedException.TYPE_INEQUAL_PACKAGE_NAME:
                        errorText.setText(R.string.nmod_import_failed_message_inequal_package_name);
                        break;
                    case ExtractFailedException.TYPE_INVAILD_PACKAGE_NAME:
                        errorText.setText(R.string.nmod_import_failed_message_invalid_package_name);
                        break;
                    case ExtractFailedException.TYPE_IO_EXCEPTION:
                        errorText.setText(R.string.nmod_import_failed_message_io_exception);
                        break;
                    case ExtractFailedException.TYPE_JSON_SYNTAX_EXCEPTION:
                        errorText.setText(R.string.nmod_import_failed_message_manifest_json_syntax_error);
                        break;
                    case ExtractFailedException.TYPE_NO_MANIFEST:
                        errorText.setText(R.string.nmod_import_failed_message_no_manifest);
                        break;
                    case ExtractFailedException.TYPE_UNDEFINED_PACKAGE_NAME:
                    case ExtractFailedException.TYPE_REDUNDANT_MANIFEST:
                        errorText.setText(R.string.nmod_import_failed_message_no_package_name);
                        break;
                    default:
                        errorText.setText(R.string.nmod_import_failed_message_unexpected);
                        break;
                }
            }
        }
    }
}