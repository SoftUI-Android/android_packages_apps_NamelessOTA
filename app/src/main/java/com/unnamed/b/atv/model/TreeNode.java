package com.unnamed.b.atv.model;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.unnamed.b.atv.view.AndroidTreeView;
import com.unnamed.b.atv.view.TreeNodeWrapperView;

import org.namelessrom.ota.R;
import org.namelessrom.ota.changelog.ProjectHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Bogdan Melnychuk on 2/10/15.
 */
public class TreeNode {
    public static final String NODES_ID_SEPARATOR = ":";

    private int mId;
    private TreeNode mParent;
    private boolean mSelected;
    private boolean mSelectable = true;
    private final List<TreeNode> children;
    private BaseNodeViewHolder mViewHolder;
    private TreeNodeClickListener mListener;
    private String mTitle;
    private Object mValue;
    private boolean mExpanded;

    public static TreeNode root() {
        TreeNode root = new TreeNode("", null);
        root.setSelectable(false);
        return root;
    }

    public TreeNode(final String title, final Object value) {
        children = new ArrayList<>();
        mTitle = title;
        mValue = value;
    }

    public TreeNode addChild(TreeNode childNode) {
        childNode.mParent = this;
        //TODO think about id generation
        childNode.mId = size();
        children.add(childNode);
        return this;
    }

    public TreeNode addChildren(TreeNode... nodes) {
        for (TreeNode n : nodes) {
            addChild(n);
        }
        return this;
    }

    public TreeNode addChildren(Collection<TreeNode> nodes) {
        for (TreeNode n : nodes) {
            addChild(n);
        }
        return this;
    }

    public int deleteChild(TreeNode child) {
        for (int i = 0; i < children.size(); i++) {
            if (child.mId == children.get(i).mId) {
                children.remove(i);
                return i;
            }
        }
        return -1;
    }

    public List<TreeNode> sort() {
        Collections.sort(children, sTreeComparator);
        for (final TreeNode n : children) {
            n.sort();
        }
        return children;
    }

    private static final Comparator<TreeNode> sTreeComparator = new Comparator<TreeNode>() {
        @Override
        public int compare(TreeNode left, TreeNode right) {
            if (left == right) {
                return 0;
            }
            if (TextUtils.isEmpty(left.mTitle)) {
                return -1;
            }
            if (TextUtils.isEmpty(right.mTitle)) {
                return 1;
            }

            return left.mTitle.compareToIgnoreCase(right.mTitle);
        }
    };

    public List<TreeNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public int size() {
        return children.size();
    }

    public TreeNode getParent() {
        return mParent;
    }

    public int getId() {
        return mId;
    }

    public boolean isLeaf() {
        return size() == 0;
    }

    public Object getValue() {
        return mValue;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public TreeNode setExpanded(boolean expanded) {
        mExpanded = expanded;
        return this;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public boolean isSelected() {
        if (mSelectable) {
            return mSelected;
        } else {
            return false;
        }
    }

    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    public boolean isSelectable() {
        return mSelectable;
    }

    public String getPath() {
        final StringBuilder path = new StringBuilder();
        TreeNode node = this;
        while (node.mParent != null) {
            path.append(mId);
            node = node.mParent;
            if (node.mParent != null) {
                path.append(NODES_ID_SEPARATOR);
            }
        }
        return path.toString();
    }


    public int getLevel() {
        int level = 0;
        TreeNode root = this;
        while (root.mParent != null) {
            root = root.mParent;
            level++;
        }
        return level;
    }

    public boolean isLastChild() {
        if (!isRoot()) {
            int parentSize = mParent.children.size();
            if (parentSize > 0) {
                final List<TreeNode> parentChildren = mParent.children;
                return parentChildren.get(parentSize - 1).mId == mId;
            }
        }
        return false;
    }

    public TreeNode setViewHolder(BaseNodeViewHolder viewHolder) {
        mViewHolder = viewHolder;
        if (viewHolder != null) {
            viewHolder.mNode = this;
        }
        return this;
    }

    public TreeNode setClickListener(TreeNodeClickListener listener) {
        mListener = listener;
        return this;
    }


    public TreeNodeClickListener getClickListener() {
        return this.mListener;
    }

    public BaseNodeViewHolder getViewHolder() {
        return mViewHolder;
    }

    public boolean isFirstChild() {
        if (!isRoot()) {
            List<TreeNode> parentChildren = mParent.children;
            return parentChildren.get(0).mId == mId;
        }
        return false;
    }

    public boolean isRoot() {
        return mParent == null;
    }

    public TreeNode getRoot() {
        TreeNode root = this;
        while (root.mParent != null) {
            root = root.mParent;
        }
        return root;
    }

    public interface TreeNodeClickListener {
        void onClick(TreeNode node, Object value);
    }

    public static abstract class BaseNodeViewHolder<E> {
        protected AndroidTreeView tView;
        protected TreeNode mNode;
        private View mView;
        protected int containerStyle;
        protected Context context;

        public BaseNodeViewHolder(Context context) {
            this.context = context;
        }

        public View getView() {
            if (mView != null) {
                return mView;
            }
            final View nodeView = getNodeView();
            final TreeNodeWrapperView nodeWrapperView = new TreeNodeWrapperView(nodeView.getContext(), getContainerStyle());
            nodeWrapperView.insertNodeView(nodeView);
            mView = nodeWrapperView;

            return mView;
        }

        public void setTreeViev(AndroidTreeView treeViev) {
            this.tView = treeViev;
        }

        public AndroidTreeView getTreeView() {
            return tView;
        }

        public void setContainerStyle(int style) {
            containerStyle = style;
        }

        public View getNodeView() {
            return createNodeView(mNode, (E) mNode.getValue());
        }

        public ViewGroup getNodeItemsView() {
            return (ViewGroup) getView().findViewById(R.id.node_items);
        }

        public boolean isInitialized() {
            return mView != null;
        }

        public int getContainerStyle() {
            return containerStyle;
        }


        public abstract View createNodeView(TreeNode node, E value);

        public void toggle(boolean active) {
            // empty
        }

        public void toggleSelectionMode(boolean editModeEnabled) {
            // empty
        }
    }
}