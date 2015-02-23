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

package org.namelessrom.ota.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.namelessrom.ota.Device;

import java.util.HashMap;
import java.util.Map;

public class UpdatesJsonArrayRequest extends JsonArrayRequest {
    private final HashMap<String, String> mHeaders = new HashMap<>();

    public UpdatesJsonArrayRequest(final String url, final Response.Listener<JSONArray> listener,
            final Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        mHeaders.put("User-Agent", Device.get().userAgent);
        mHeaders.put("Cache-Control", "no-cache");
        return mHeaders;
    }

}
