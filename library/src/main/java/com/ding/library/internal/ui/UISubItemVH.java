package com.ding.library.internal.ui;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.ding.library.R;
import com.ding.library.internal.utils.GetCaptureDataUtils;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractAdapterItem;

import java.util.List;

/**
 * author:DingDeGao
 * time:2019-10-31-17:21
 * function: default function
 */
public class UISubItemVH extends AbstractAdapterItem {

    private TextView mName;
    private CaptureContentAdapter rvContentAdapter;
    private SubEntity mEntity;

    public UISubItemVH(CaptureContentAdapter rvContent) {
        this.rvContentAdapter = rvContent;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_capture_tv;
    }

    @Override
    public void onBindViews(final View root) {
        mName = root.findViewById(R.id.tv_name);
        root.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                GetCaptureDataUtils.getData(root.getContext(), mEntity.parentFileName, mEntity.name,
                        new GetCaptureDataUtils.CallBack() {
                    @Override
                    public void success(List<CaptureContentAdapter.Entity> list) {
                        rvContentAdapter.setData(list);
                    }
                });
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        if (model instanceof SubEntity) {
            mEntity = (SubEntity) model;
            mName.setText(mEntity.name);
        }
    }

    public static class SubEntity{
        public String name;
        public String parentFileName;

        public SubEntity(String name, String parentFileName) {
            this.name = name;
            this.parentFileName = parentFileName;
        }
    }
}
