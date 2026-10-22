package com.ppma.filemanager.adapter;

import android.view.View;
import android.widget.TextView;

import com.ppma.filemanager.R;
import com.ppma.filemanager.adapter.base.RecyclerViewAdapter;
import com.ppma.filemanager.adapter.base.RecyclerViewHolder;

import jadx.gui.treemodel.JNode;
import jadx.gui.treemodel.JPackage;

public class PackageHolder extends RecyclerViewHolder<PackageHolder> {

    TextView textView;

    public PackageHolder(View itemView) {
        super(itemView);

        textView = itemView.findViewById(R.id.title_Name);
    }

    @Override
    public void onBindViewHolder(PackageHolder lineHolder, RecyclerViewAdapter adapter, int position) {
        JNode jPackage = (JNode) adapter.getItem(position);
        // 面包屑只显示包名的最后一段（如 com.google.common -> common），
        // 完整路径太长会把标题栏撑乱、后面层级被挤出屏幕
        String name = jPackage.getName();
        if (name != null) {
            int idx = name.lastIndexOf('.');
            if (idx >= 0 && idx < name.length() - 1) {
                name = name.substring(idx + 1);
            }
        }
        lineHolder.textView.setText(name + ">");
    }
}
