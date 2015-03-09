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
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.namelessrom.ota.R;
import org.namelessrom.ota.listeners.ChangeListener;
import org.namelessrom.ota.utils.Logger;

import java.util.HashMap;

public class ChangelogFragment extends Fragment implements ChangeListener {
    private static final String KEY_STATE = "key_state";

    private final HashMap<String, TreeNode> mNodeMap = new HashMap<>();

    private AndroidTreeView mTreeView;
    private TextView mStatusChanges;
    private ChangeFetcher mChangeFetcher;

    public ChangelogFragment() { }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_recent_changes, container, false);

        mStatusChanges = (TextView) v.findViewById(R.id.status_fetched_changes);

        final FrameLayout treeContainer = (FrameLayout) v.findViewById(R.id.container);

        mTreeView = new AndroidTreeView(getActivity(), TreeNode.root());
        mTreeView.setDefaultContainerStyle(R.style.TreeNodeStyleDivided, true);
        treeContainer.addView(mTreeView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString(KEY_STATE);
            if (!TextUtils.isEmpty(state)) {
                mTreeView.restoreState(state);
            }
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mChangeFetcher = new ChangeFetcher(getActivity(), this);
        fetchChanges();
    }

    private void fetchChanges() {
        mStatusChanges.setText(R.string.searching_changes);
        mChangeFetcher.fetchNext();
    }

    private void addChanges(final Change change) {
        final TreeNode parent;
        if (mNodeMap.containsKey(change.project)) {
            parent = mNodeMap.get(change.project);
        } else {
            parent = new TreeNode(change.project,
                    new ProjectHolder.ProjectItem(mapToProject(change.project), change.project));
            parent.setViewHolder(new ProjectHolder(getActivity()));
            mTreeView.getRoot().addChild(parent);
            mNodeMap.put(change.project, parent);
        }
        final TreeNode child = new TreeNode(change.subject, change);
        child.setViewHolder(new ChangeHolder(getActivity()));
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

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh: {
                if (getActivity() instanceof ChangelogActivity) {
                    ((ChangelogActivity) getActivity()).closeDrawer();
                }
                fetchChanges();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChangesFetched(final boolean success, @Nullable final Change[] changes) {
        if (changes == null) {
            Logger.w(this, "Fetched changes are null!");
            return;
        }

        Logger.d(this, "Fetched changes -> %s", changes.length);

        // clear current changes to not add duplicates
        mNodeMap.clear();
        mTreeView.getRoot().clearChildren();

        // add changes
        for (final Change change : changes) {
            addChanges(change);
        }

        // sort and refresh
        mTreeView.getRoot().sort();
        mTreeView.expandNode(mTreeView.getRoot());

        mStatusChanges.setText(getString(R.string.found_changes, changes.length));
    }

}
