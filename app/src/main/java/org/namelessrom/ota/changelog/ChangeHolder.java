package org.namelessrom.ota.changelog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import org.namelessrom.ota.R;

public class ChangeHolder extends TreeNode.BaseNodeViewHolder<Change> {

    public ChangeHolder(final Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, final Change change) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.node_change, null, false);

        final TextView subject = (TextView) view.findViewById(R.id.node_change_subject);
        subject.setText(change.subject);

        final TextView updated = (TextView) view.findViewById(R.id.node_change_updated);
        if (!TextUtils.isEmpty(change.updated)) {
            change.updated = change.updated.split("\\.")[0];
        }
        updated.setText(change.updated);

        return view;
    }

}
