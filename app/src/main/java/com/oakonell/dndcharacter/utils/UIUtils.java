package com.oakonell.dndcharacter.utils;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


/**
 * Created by Rob on 4/4/2016.
 */
public class UIUtils {

    public static RecyclerView.LayoutManager createLinearLayoutManager(FragmentActivity mainActivity, int orientation, boolean b) {
        return new LinearLayoutManager(mainActivity, orientation, b);
    }

    public static RecyclerView.LayoutManager createLinearLayoutManager(FragmentActivity activity) {
        return new LinearLayoutManager(activity);
    }
}
