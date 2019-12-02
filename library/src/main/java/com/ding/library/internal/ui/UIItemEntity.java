package com.ding.library.internal.ui;

import com.zaihuishou.expandablerecycleradapter.model.ExpandableListItem;

import java.util.List;

/**
 * author:DingDeGao
 * time:2019-10-31-16:57
 * function: default function
 */
public class UIItemEntity implements ExpandableListItem {

    public boolean mExpanded = false;
    public String name;
    public List<UISubItemVH.SubEntity> subFileList;

    @Override
    public List<?> getChildItemList() {
        return subFileList;
    }

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        mExpanded = isExpanded;
    }
}
