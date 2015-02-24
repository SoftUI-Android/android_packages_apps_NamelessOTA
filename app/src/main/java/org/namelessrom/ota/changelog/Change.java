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

package org.namelessrom.ota.changelog;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;
import org.namelessrom.ota.JsonAble;

/**
 * Represents a Change, querried from Gerrit Code Review
 * Example: <a href="https://gerrit.nameless-rom.org/changes/?n=25">https://gerrit.nameless-rom.org/changes/?n=25</a>
 * <p/>
 * Note: Gerrit inserts a magic into every query, so we need to strip the first line from the
 * response in order to get valid JSON.
 * <p/>
 * Example json:
 * <pre>
 * [
 *   {
 *     "id": "NamelessRom%2Fandroid_packages_apps_Screencast~n-2.0~I0f08378bd382f5ab55f4d37cfe0ee2169e9ead54",
 *     "project": "NamelessRom/android_packages_apps_Screencast",
 *     "branch": "n-2.0",
 *     "hashtags": [],
 *     "change_id": "I0f08378bd382f5ab55f4d37cfe0ee2169e9ead54",
 *     "subject": "bump i-frame-interval from 3 to 5",
 *     "status": "NEW",
 *     "created": "2015-02-23 21:55:05.204000000",
 *     "updated": "2015-02-23 21:58:42.422000000",
 *     "mergeable": true,
 *     "insertions": 1,
 *     "deletions": 1,
 *     "_number": 16117,
 *     "owner": {
 *     "_account_id": 1000000
 *     }
 *   },
 *   {
 *     "id": "Evisceration%2Fandroid_packages_apps_DeviceControl~n-2.0~I45b938bba83910daed7a6a2222c07edebafd0c12",
 *     "project": "Evisceration/android_packages_apps_DeviceControl",
 *     "branch": "n-2.0",
 *     "hashtags": [],
 *     "change_id": "I45b938bba83910daed7a6a2222c07edebafd0c12",
 *     "subject": "hotplugging: add PnpMgr support (Power and Performance Manager)",
 *     "status": "NEW",
 *     "created": "2015-02-18 07:56:15.906000000",
 *     "updated": "2015-02-23 19:20:16.634000000",
 *     "mergeable": true,
 *     "insertions": 91,
 *     "deletions": 5,
 *     "_number": 16017,
 *     "owner": {
 *     "_account_id": 1000000
 *     }
 *   }
 * ]
 * </pre>
 */
public class Change extends JsonAble {

    @SerializedName("project") public String project;
    @SerializedName("branch") public String branch;
    @SerializedName("subject") public String subject;
    @SerializedName("insertions") public String insertions;
    @SerializedName("deletions") public String deletions;
    @SerializedName("created") public String created;
    @SerializedName("updated") public String updated;

    public Change() {
        // NOOP
    }

    public Change(final JSONObject jsonObject) {
        project = getJsonString(jsonObject, "project");
        branch = getJsonString(jsonObject, "branch");
        subject = getJsonString(jsonObject, "subject");
        insertions = getJsonString(jsonObject, "insertions");
        deletions = getJsonString(jsonObject, "deletions");
        created = getJsonString(jsonObject, "created");
        updated = getJsonString(jsonObject, "updated");
    }

}
