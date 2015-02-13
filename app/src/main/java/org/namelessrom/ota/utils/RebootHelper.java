/*
 * Copyright 2014 ParanoidAndroid Project
 * Modifications Copyright (C) 2014 Alexander "Evisceration" Martinz
 *
 * This file is part of Paranoid OTA.
 *
 * Paranoid OTA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Paranoid OTA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Paranoid OTA.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.namelessrom.ota.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.FileUtils;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import org.namelessrom.ota.R;
import org.namelessrom.ota.utils.recovery.RecoveryInfo;

import java.io.File;
import java.io.FileOutputStream;

public class RebootHelper {

    private RecoveryHelper mRecoveryHelper;

    public RebootHelper(RecoveryHelper recoveryHelper) {
        mRecoveryHelper = recoveryHelper;
    }

    private void showBackupDialog(final Context context) {
        double spaceLeft = IOUtils.get().getSpaceLeft();
        if (spaceLeft < 1.0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(R.string.alert_backup_space_title);
            alert.setMessage(context.getResources().getString(
                    R.string.alert_backup_space_message, 1.0));

            alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();

                    reallyShowBackupDialog(context);
                }
            });

            alert.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alert.show();
        } else {
            reallyShowBackupDialog(context);
        }
    }

    private void reallyShowBackupDialog(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.alert_backup_title);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_backup,
                (ViewGroup) ((Activity) context).findViewById(R.id.backup_dialog_layout));
        alert.setView(view);

        final CheckBox cbSystem = (CheckBox) view.findViewById(R.id.system);
        final CheckBox cbData = (CheckBox) view.findViewById(R.id.data);
        final CheckBox cbCache = (CheckBox) view.findViewById(R.id.cache);
        final CheckBox cbRecovery = (CheckBox) view.findViewById(R.id.recovery);
        final CheckBox cbBoot = (CheckBox) view.findViewById(R.id.boot);
        final CheckBox cbSecure = (CheckBox) view.findViewById(R.id.androidsecure);
        final CheckBox cbSdext = (CheckBox) view.findViewById(R.id.sdext);
        final EditText input = (EditText) view.findViewById(R.id.backupname);

        input.setText(Utils.getRomPrefix() + Utils.getDateAndTime());
        input.selectAll();

        if (!IOUtils.get().hasAndroidSecure()) {
            cbSecure.setVisibility(View.GONE);
        }
        if (!IOUtils.get().hasSdExt()) {
            cbSdext.setVisibility(View.GONE);
        }

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

                String text = input.getText().toString();
                text = text.replaceAll("[^a-zA-Z0-9.-]", "");

                String backupOptions = "";
                if (cbSystem.isChecked()) {
                    backupOptions += "S";
                }
                if (cbData.isChecked()) {
                    backupOptions += "D";
                }
                if (cbCache.isChecked()) {
                    backupOptions += "C";
                }
                if (cbRecovery.isChecked()) {
                    backupOptions += "R";
                }
                if (cbBoot.isChecked()) {
                    backupOptions += "B";
                }
                if (cbSecure.isChecked()) {
                    backupOptions += "A";
                }
                if (cbSdext.isChecked()) {
                    backupOptions += "E";
                }

                reboot(context, text, backupOptions);
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void showRebootDialog(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.alert_reboot_install_title);
        alert.setMessage(context.getResources().getString(R.string.alert_reboot_one_message));
        alert.setPositiveButton(R.string.alert_reboot_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                reboot(context, null, null);
            }
        });

        alert.setNegativeButton(R.string.backup, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showBackupDialog(context);
            }
        });

        alert.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void reboot(final Context context, final String backupFolder,
            final String backupOptions) {
        try {
            File f = new File("/cache/recovery/command");
            f.delete();

            final String updateZipPath =
                    new File(IOUtils.DOWNLOAD_PATH, "update.zip").getAbsolutePath();
            final String[] fakeArray = { updateZipPath };
            int[] recoveries = new int[]{
                    RecoveryInfo.TWRP_BASED, RecoveryInfo.CWM_BASED
            };

            for (int i = 0; i < recoveries.length; i++) {
                String file = mRecoveryHelper.getCommandsFile(recoveries[i]);

                FileOutputStream os = null;
                try {
                    os = new FileOutputStream("/cache/recovery/" + file, false);

                    String[] commands = mRecoveryHelper.getCommands(recoveries[i], fakeArray,
                            fakeArray, false, true, backupFolder, backupOptions);
                    if (commands != null) {
                        int size = commands.length, j = 0;
                        for (; j < size; j++) {
                            os.write((commands[j] + "\n").getBytes("UTF-8"));
                        }
                    }
                } finally {
                    if (os != null) {
                        os.close();
                        FileUtils.setPermissions("/cache/recovery/" + file, 0644,
                                android.os.Process.myUid(), 2001);
                    }
                }
            }

            ((PowerManager) context.getSystemService(Activity.POWER_SERVICE)).reboot("recovery");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
