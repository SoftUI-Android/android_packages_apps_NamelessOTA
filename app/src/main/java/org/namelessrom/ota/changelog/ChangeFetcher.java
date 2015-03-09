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

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.namelessrom.ota.listeners.ChangeListener;
import org.namelessrom.ota.requests.ChangesJsonArrayRequest;
import org.namelessrom.ota.updater.UpdateApplication;
import org.namelessrom.ota.utils.Logger;

public class ChangeFetcher implements Response.Listener<Change[]>, Response.ErrorListener {
    private static final String URL =
            "https://gerrit.nameless-rom.org/changes/?q=status:merged&n=%s";
    private static final int MAX_CHANGES = 2000;

    private final Context mContext;
    private final ChangeListener mListener;

    private int mOffset;

    public ChangeFetcher(final Context context, final ChangeListener listener) {
        this(context, listener, ChangeConfig.get(context).changesInitial);
    }

    public ChangeFetcher(final Context context, final ChangeListener listener, final int offset) {
        mContext = context;
        mListener = listener;
        mOffset = offset;
    }

    public ChangeFetcher fetchNext() {
        return fetchNext(ChangeConfig.get(mContext).changesToFetch);
    }

    public ChangeFetcher fetchNext(final int count) {
        // build the URL
        final String url = String.format(URL, mOffset);

        // increase the offset for the next fetch if we did not hit the cap
        if (mOffset > MAX_CHANGES) {
            mOffset = MAX_CHANGES;
        } else {
            mOffset += count;
        }

        Logger.d(this, "Url -> %s\nOffset -> %s", url, mOffset);

        final ChangesJsonArrayRequest jsArrReq = new ChangesJsonArrayRequest(url, this, this);
        ((UpdateApplication) mContext.getApplicationContext()).getQueue().add(jsArrReq);

        return this;
    }

    @Override
    public void onResponse(final Change[] changes) {
        Logger.v(this, "onResponse -> number of changes -> %s", changes.length);

        // notify the change listener
        if (mListener != null) mListener.onChangesFetched(true, changes);
    }


    @Override
    public void onErrorResponse(final VolleyError volleyError) {
        Logger.v(this, "onErrorResponse: %s", volleyError.toString());
        if (Logger.getEnabled()) volleyError.fillInStackTrace();

        // notify the change listener
        if (mListener != null) mListener.onChangesFetched(false, null);
    }
}
