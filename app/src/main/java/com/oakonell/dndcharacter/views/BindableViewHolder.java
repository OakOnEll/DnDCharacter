package com.oakonell.dndcharacter.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Rob on 6/6/2016.
 */
public abstract class BindableViewHolder<C, A extends RecyclerView.Adapter> extends RecyclerView.ViewHolder {
    public BindableViewHolder(View itemView) {
        super(itemView);
    }
}
