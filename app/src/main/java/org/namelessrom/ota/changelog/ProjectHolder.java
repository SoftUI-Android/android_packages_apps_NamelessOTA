package org.namelessrom.ota.changelog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

import org.namelessrom.ota.R;

public class ProjectHolder extends TreeNode.BaseNodeViewHolder<ProjectHolder.ProjectItem> {
    private PrintView arrowView;

    public ProjectHolder(final Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, final ProjectItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.node_project, null, false);

        final TextView title = (TextView) view.findViewById(R.id.node_project_title);
        title.setText(value.text);

        final PrintView icon = (PrintView) view.findViewById(R.id.node_project_icon);
        icon.setIconText(context.getResources().getString(value.icon));

        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);
        if (node.isLeaf()) {
            arrowView.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void toggle(final boolean active) {
        arrowView.setIconText(context.getResources().getString(active
                ? R.string.ic_keyboard_arrow_down
                : R.string.ic_keyboard_arrow_right));
    }

    public static class ProjectItem {
        public int icon;
        public String text;

        public ProjectItem(final int icon, final String text) {
            this.icon = icon;
            this.text = text;
        }
    }
}
