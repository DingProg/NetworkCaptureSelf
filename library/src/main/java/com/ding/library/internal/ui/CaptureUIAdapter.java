package com.ding.library.internal.ui;

import com.zaihuishou.expandablerecycleradapter.adapter.BaseExpandableAdapter;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractAdapterItem;

import java.util.List;

/**
 * author:DingDeGao
 * time:2019-10-31-16:26
 * function: default function
 */
public class CaptureUIAdapter extends BaseExpandableAdapter {

    private static final int ITEM_PARENT = 1;
    private static final int ITEM_SUB = 2;

    private CaptureContentAdapter rvContentAdapter;

    CaptureUIAdapter(List data, CaptureContentAdapter rvContent) {
        super(data);
        this.rvContentAdapter = rvContent;
    }

    @Override
    public AbstractAdapterItem<Object> getItemView(Object type) {
        int type1 = (int) type;
        if(type1 == ITEM_PARENT){
            return new UIItemVH();
        }else if(type1 == ITEM_SUB){
            return new UISubItemVH(rvContentAdapter);
        }
        return null;
    }

    @Override
    public Object getItemViewType(Object type) {
        if(type instanceof UIItemEntity){
            return ITEM_PARENT;
        }else if(type instanceof UISubItemVH.SubEntity){
            return ITEM_SUB;
        }
        return -1;
    }
}
