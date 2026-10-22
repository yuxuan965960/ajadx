package com.ppma.filemanager.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppma.filemanager.R;
import com.ppma.filemanager.adapter.base.RecyclerViewAdapter;
import com.ppma.filemanager.adapter.base.RecyclerViewHolder;
import jadx.gui.treemodel.JClass;
import jadx.gui.treemodel.JNode;
import jadx.gui.treemodel.JPackage;

public class ClassHolder extends RecyclerViewHolder<ClassHolder> {

    ImageView fileIcon;
    TextView fileName;
    TextView fileChildCount;
    TextView fileSize;
    ImageView dir_enter_image;

    public ClassHolder(View view) {
        super(view);
        fileIcon = view.findViewById(R.id.fileIcon);
        fileName = view.findViewById(R.id.fileName);
        fileChildCount = view.findViewById(R.id.fileChildCount);
        fileSize = view.findViewById(R.id.fileSize);
        dir_enter_image = view.findViewById(R.id.dir_enter_image);
    }

@Override
    public void onBindViewHolder(final ClassHolder classHolder, RecyclerViewAdapter adapter, int position) {
        JNode jNode = (JNode) adapter.getItem(position);
        String name = jNode.getName();
        if (R.mipmap.package_obj == jNode.getIcon()) {
            // 包：第一行只显示包的最后一层，第二行显示项数
            if (name != null) {
                int idx = name.lastIndexOf('.');
                if (idx >= 0 && idx < name.length() - 1) {
                    name = name.substring(idx + 1);
                }
            }
            classHolder.fileName.setText(name);
            JPackage jPackage = (JPackage) jNode;
            classHolder.fileChildCount.setVisibility(View.VISIBLE);
            classHolder.fileChildCount.setText(jPackage.getClasses().size() + jPackage.getInnerPackages().size() + "项");
            classHolder.fileSize.setVisibility(View.GONE);
            classHolder.dir_enter_image.setVisibility(View.VISIBLE);
        } else {
            // 类：第一行类名（短名一定能看全），第二行用灰字显示所在包
            classHolder.fileName.setText(name);
            String pkg = "";
            if (jNode instanceof JClass) {
                pkg = ((JClass) jNode).getPackageName();
            }
            if (pkg != null && pkg.length() > 0) {
                classHolder.fileChildCount.setVisibility(View.VISIBLE);
                classHolder.fileChildCount.setText(pkg);
            } else {
                classHolder.fileChildCount.setVisibility(View.GONE);
            }
            classHolder.fileSize.setVisibility(View.GONE);
            classHolder.dir_enter_image.setVisibility(View.GONE);
        }

//设置图标
        classHolder.fileIcon.setImageResource(jNode.getIcon());
    }
}
