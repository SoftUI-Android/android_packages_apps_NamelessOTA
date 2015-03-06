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
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.namelessrom.ota.Device;
import org.namelessrom.ota.changelog.Change;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ChangesJsonArrayRequest extends Request<Change[]> {
    private static final String MAGIC = ")]}'";

    private final Gson mGson = new Gson();
    private final HashMap<String, String> mHeaders = new HashMap<>();
    private final Response.Listener<Change[]> mListener;

    public ChangesJsonArrayRequest(final String url, final Response.Listener<Change[]> listener,
            final Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        mHeaders.put("User-Agent", Device.get().userAgent);
        mHeaders.put("Cache-Control", "no-cache");
        mHeaders.put("Accept", "application/json");
        return mHeaders;
    }

    @Override
    protected void deliverResponse(final Change[] changes) {
        mListener.onResponse(changes);
    }

    @Override
    protected Response<Change[]> parseNetworkResponse(final NetworkResponse networkResponse) {
        try {
            String json = new String(networkResponse.data,
                    HttpHeaderParser.parseCharset(networkResponse.headers));

            // remove gerrit magic if it exists
            if (json.startsWith(MAGIC)) {
                json = json.replaceFirst(String.format("\\%s", MAGIC), "");
            }

            return Response.success(mGson.fromJson(json, Change[].class),
                    HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

}
