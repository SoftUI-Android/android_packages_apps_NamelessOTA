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

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import org.namelessrom.ota.R;

import java.io.File;

public class DownloadHelper {
    private static final Handler sUpdateHandler = new Handler();

    private static Context sContext;
    private static PreferenceHelper sPreferenceHelper;

    private static DownloadManager sDownloadManager;
    private static DownloadCallback sCallback;

    private static boolean sDownloadingRom = false;

    public static boolean isDownloaded() {
        return !sDownloadingRom && new File(IOUtils.DOWNLOAD_PATH, "update.zip").exists();
    }

    public static interface DownloadCallback {

        public abstract void onDownloadStarted();

        public abstract void onDownloadProgress(int progress);

        public abstract void onDownloadFinished(Uri uri, String md5);

        public abstract void onDownloadError();
    }

    private static final Runnable sUpdateProgress = new Runnable() {

        public void run() {
            if (!sDownloadingRom) {
                return;
            }

            long idRom = sPreferenceHelper.getDownloadRomId();

            long[] statusRom = getDownloadProgress(idRom);

            int status = DownloadManager.STATUS_SUCCESSFUL;
            if (statusRom[0] == DownloadManager.STATUS_FAILED) {
                status = DownloadManager.STATUS_FAILED;
            } else if (statusRom[0] == DownloadManager.STATUS_PENDING) {
                status = DownloadManager.STATUS_PENDING;
            }

            switch (status) {
                case DownloadManager.STATUS_PENDING:
                    sCallback.onDownloadProgress(-1);
                    break;
                case DownloadManager.STATUS_FAILED:
                    sCallback.onDownloadError();
                    break;
                default:
                    long totalBytes = statusRom[1];
                    long downloadedBytes = statusRom[2];
                    long percent = totalBytes == -1 && downloadedBytes == -1
                            ? -1
                            : downloadedBytes * 100 / totalBytes;
                    if (totalBytes != -1 && downloadedBytes != -1 && percent != -1) {
                        Logger.v(this, "onDownloadProgress(%s)", percent);
                        sCallback.onDownloadProgress((int) percent);
                    }
                    break;
            }

            if (status != DownloadManager.STATUS_FAILED) {
                sUpdateHandler.postDelayed(this, 1000);
            }
        }
    };

    public static void init(Context context, DownloadCallback callback) {
        sContext = context;
        if (sDownloadManager == null) {
            sDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        sPreferenceHelper = PreferenceHelper.get(context);
        registerCallback(callback);
        checkIfDownloading();
    }

    public static void registerCallback(DownloadCallback callback) {
        sCallback = callback;
        sUpdateHandler.post(sUpdateProgress);
    }

    public static void unregisterCallback() {
        sUpdateHandler.removeCallbacks(sUpdateProgress);
    }

    public static void checkDownloadFinished(Context context, long downloadId) {
        sContext = context;
        if (sDownloadManager == null) {
            sDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        sPreferenceHelper = PreferenceHelper.get(context);
        checkDownloadFinished(downloadId);
    }

    private static void checkDownloadFinished(long downloadId) {
        long id = sPreferenceHelper.getDownloadRomId();
        if (id == -1L || (downloadId != 0 && downloadId != id)) {
            return;
        }
        String md5 = sPreferenceHelper.getDownloadRomMd5();
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = sDownloadManager.query(query);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            switch (status) {
                case DownloadManager.STATUS_FAILED:
                    removeDownload(id, true);
                    sCallback.onDownloadError();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    sDownloadingRom = false;
                    final String uriString = cursor.getString(cursor
                            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    sCallback.onDownloadFinished(Uri.parse(uriString), md5);
                    downloadSuccessful(/*id*/);
                    break;
                default:
                    cancelDownload(id);
                    break;
            }
        } else {
            removeDownload(id, true);
        }

        cursor.close();
    }

    public static boolean isDownloading() {
        return sDownloadingRom;
    }

    public static void downloadFile(String url, String fileName, String md5) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setTitle(fileName);
        IOUtils.get().setupDownloadPath();
        request.setDestinationUri(Uri.fromFile(new File(IOUtils.DOWNLOAD_PATH, fileName)));

        long id = sDownloadManager.enqueue(request);
        sDownloadingRom = true;
        sPreferenceHelper.setDownloadRomId(id, md5, fileName);

        sUpdateHandler.post(sUpdateProgress);
        sCallback.onDownloadStarted();
    }

    private static void removeDownload(long id, boolean removeDownload) {
        sDownloadingRom = false;
        sPreferenceHelper.setDownloadRomId(null, null, null);
        if (removeDownload) {
            sDownloadManager.remove(id);
        }
        sUpdateHandler.removeCallbacks(sUpdateProgress);
        sCallback.onDownloadFinished(null, null);
    }

    private static void downloadSuccessful() {
        sDownloadingRom = false;
        sPreferenceHelper.setDownloadRomId(null, null, null);
        sUpdateHandler.removeCallbacks(sUpdateProgress);
    }

    public static void cancelDownload() {
        cancelDownload(sPreferenceHelper.getDownloadRomId());
    }

    public static void cancelDownload(final long id) {
        new AlertDialog.Builder(sContext)
                .setTitle(R.string.cancel_download)
                .setMessage(R.string.cancel_download_summary)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                removeDownload(id, true);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    private static long[] getDownloadProgress(long id) {
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(id);

        Cursor cursor = sDownloadManager.query(q);
        int status;

        if (cursor == null || !cursor.moveToFirst()) {
            status = DownloadManager.STATUS_FAILED;
        } else {
            status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        }

        long totalBytes = -1;
        long downloadedBytes = -1;

        switch (status) {
            case DownloadManager.STATUS_PAUSED:
            case DownloadManager.STATUS_RUNNING:
                downloadedBytes = cursor.getLong(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                totalBytes = cursor.getLong(cursor
                        .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                sDownloadingRom = false;
                break;
            case DownloadManager.STATUS_FAILED:
                sDownloadingRom = false;
                break;
        }

        if (cursor != null) {
            cursor.close();
        }

        return new long[]{ status, totalBytes, downloadedBytes };
    }

    private static void checkIfDownloading() {
        long romId = sPreferenceHelper.getDownloadRomId();
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(romId);
        Cursor cursor = sDownloadManager.query(query);
        sDownloadingRom = cursor.moveToFirst();
        cursor.close();
        if (romId >= 0L && !sDownloadingRom) {
            removeDownload(romId, false);
        }
    }
}
