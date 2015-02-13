/*
 * <!--
 *    Copyright (C) 2013 - 2015 The NamelessRom Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * -->
 */

package org.namelessrom.ota.utils;

import android.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Who can live nowadays without Utils class?
 */
public class Utils {
    private static final String TAG = "Utils";

    public static final String CHECK_DOWNLOADS_FINISHED =
            "org.namelessrom.ota.CHECK_DOWNLOADS_FINISHED";
    public static final String CHECK_DOWNLOADS_ID = "org.namelessrom.ota.CHECK_DOWNLOADS_ID";

    public static int tryParseInt(final String intToParse, final int def) {
        try {
            return Integer.parseInt(intToParse);
        } catch (NumberFormatException nfe) {
            Logger.e(TAG, String.format("Could not parse: %s", intToParse), nfe);
            return def;
        }
    }

    public static long tryParseLong(final String longToParse) {
        return tryParseLong(longToParse, -1);
    }

    public static long tryParseLong(final String longToParse, final long def) {
        try {
            return Long.parseLong(longToParse);
        } catch (NumberFormatException nfe) {
            Logger.e(TAG, String.format("Could not parse: %s", longToParse), nfe);
            return def;
        }
    }

    public static String readBuildProp(final String prop) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            fileReader = new FileReader("/system/build.prop");
            bufferedReader = new BufferedReader(fileReader, 512);
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                if (tmp.contains(prop)) return tmp.replace(prop + "=", "");
            }
        } catch (final Exception e) {
            Logger.e(TAG, "an error occurred", e);
        } finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
            } catch (Exception ignored) { }
            try {
                if (fileReader != null) fileReader.close();
            } catch (Exception ignored) { }
        }

        return "NULL";
    }

    public static int getBuildDate() {
        return tryParseInt(readBuildProp("ro.nameless.date"), 20140101);
    }

    public static String getCommandResult(String command) {
        Process p = null;
        DataOutputStream os = null;
        try {
            p = Runtime.getRuntime().exec(command);
            os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("sync\n");
            os.writeBytes("exit\n");
            os.flush();
            p.waitFor();
            return getStreamLines(p.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (os != null) os.close();
            } catch (IOException ignored) { }
            if (p != null) p.destroy();
        }
    }

    private static String getStreamLines(final InputStream is) {
        String out = null;
        StringBuffer buffer = null;
        DataInputStream dis = null;

        try {
            dis = new DataInputStream(is);
            if (dis.available() > 0) {
                buffer = new StringBuffer(dis.readLine());
                while (dis.available() > 0) {
                    buffer.append("\n").append(dis.readLine());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (dis != null) { dis.close(); }
            } catch (IOException ignored) { }
        }
        if (buffer != null) {
            out = buffer.toString();
        }
        return out;
    }

    public static String getRomPrefix() {
        return "NamelessRom-";
    }

    public static String getDateAndTime() {
        return new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss")
                .format(new Date(System.currentTimeMillis()));
    }

    public static void writeToFile(final File file, final String content) {
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos);
            bw = new BufferedWriter(osw);
            bw.write(content);
            bw.flush();
        } catch (IOException ignored) {
        } finally {
            closeQuietly(bw);
            closeQuietly(osw);
            closeQuietly(fos);
        }
    }

    @Nullable public static String readFromFile(final File file) {
        if (!file.exists()) {
            return null;
        }

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException ignored) {
        } finally {
            closeQuietly(br);
            closeQuietly(isr);
            closeQuietly(fis);
        }

        return null;
    }

    private static void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) { }
        }
    }
}
