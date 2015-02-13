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

package org.namelessrom.ota;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import org.namelessrom.ota.utils.Logger;
import org.namelessrom.ota.utils.Utils;

/**
 * Represents an UpdateEntry
 * <p/>
 * Example json:
 * [{
 * "channel":"NIGHTLY",
 * "filename":"nameless-4.4.4-20140731-find7u-NIGHTLY.zip",
 * "md5sum":"1a3019cb8a488454cfe3ae06bdb992a9",
 * "downloadurl":"http://sourceforge.net/projects/namelessrom/files/find7u/nameless-4.4.4-20140731-find7u-NIGHTLY.zip/download",
 * "timestamp":"20140731",
 * "codename":"find7u",
 * "md5sumdelta":"93ca0c5a1a5b75ceb4ac3382295c2464"
 * }]
 */
public class UpdateEntry {

    public String json;
    @SerializedName("channel") public String channel;
    @SerializedName("filename") public String filename;
    @SerializedName("md5sum") public String md5sum;
    @SerializedName("downloadurl") public String downloadurl;
    @SerializedName("timestamp") public long timestamp;
    @SerializedName("codename") public String codename;
    @SerializedName("md5sumdelta") public String md5sumdelta;

    public UpdateEntry() {
        // NOOP
    }

    public UpdateEntry(final JSONObject jsonObject) {
        json = jsonObject.toString();
        channel = getJsonString(jsonObject, "channel");
        filename = getJsonString(jsonObject, "filename");
        md5sum = getJsonString(jsonObject, "md5sum");
        downloadurl = getJsonString(jsonObject, "downloadurl");
        timestamp = Utils.tryParseLong(getJsonString(jsonObject, "timestamp"));
        codename = getJsonString(jsonObject, "codename");
        md5sumdelta = getJsonString(jsonObject, "md5sumdelta");
    }

    private String getJsonString(final JSONObject jsonObject, final String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException jse) {
            Logger.e(this, "getJsonString", jse);
            return "unknown";
        }
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

}
