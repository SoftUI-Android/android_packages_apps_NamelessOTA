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

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.namelessrom.ota.R;
import org.namelessrom.ota.listeners.ChangeListener;
import org.namelessrom.ota.utils.Logger;

import java.util.HashMap;

public class ChangelogActivity extends Activity implements ChangeListener {
    private static final String KEY_STATE = "key_state";

    private final HashMap<String, TreeNode> mNodeMap = new HashMap<>();

    private AndroidTreeView mTreeView;
    private TextView mStatusChanges;
    private ChangeFetcher mChangeFetcher;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_recent_changes);
        mStatusChanges = (TextView) findViewById(R.id.status_fetched_changes);
        mStatusChanges.setText(getString(R.string.fetched_changes, 0));

        final FrameLayout container = (FrameLayout) findViewById(R.id.container);

        mTreeView = new AndroidTreeView(this, TreeNode.root());
        mTreeView.setDefaultContainerStyle(R.style.TreeNodeStyleDivided, true);
        container.addView(mTreeView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString(KEY_STATE);
            if (!TextUtils.isEmpty(state)) {
                mTreeView.restoreState(state);
            }
        }

        mChangeFetcher = new ChangeFetcher(this, this);
        mChangeFetcher.fetchNext();
    }

    private void addChanges(final Change change) {
        final TreeNode parent;
        if (mNodeMap.containsKey(change.project)) {
            parent = mNodeMap.get(change.project);
        } else {
            parent = new TreeNode(change.project,
                    new ProjectHolder.ProjectItem(mapToProject(change.project), change.project));
            parent.setViewHolder(new ProjectHolder(this));
            mTreeView.getRoot().addChild(parent);
            mNodeMap.put(change.project, parent);
        }
        final TreeNode child = new TreeNode(change.subject, change);
        child.setViewHolder(new ChangeHolder(this));
        parent.addChildren(child);
    }

    private int mapToProject(final String project) {
        if (TextUtils.isEmpty(project)) {
            return R.string.ic_android;
        }

        if (project.contains("device")) {
            return R.string.ic_phone2;
        }

        return R.string.ic_android;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_STATE, mTreeView.getSaveState());
    }

    @Override
    public void onChangesFetched(final boolean success, @Nullable final Change[] changes) {
        if (changes == null) {
            Logger.w(this, "Fetched changes are null!");
            return;
        }

        Logger.d(this, "Fetched changes -> %s", changes.length);
        for (final Change change : changes) {
            addChanges(change);
        }
        mTreeView.getRoot().sort();
        mTreeView.expandNode(mTreeView.getRoot());

        mStatusChanges.setText(getString(R.string.fetched_changes, mChangeFetcher.countFetched()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
