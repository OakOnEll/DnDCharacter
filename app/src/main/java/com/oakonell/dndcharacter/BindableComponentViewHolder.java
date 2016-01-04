package com.oakonell.dndcharacter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Rob on 1/4/2016.
 */
public abstract class BindableComponentViewHolder<T, C> extends RecyclerView.ViewHolder {
    public BindableComponentViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(final C context, final RecyclerView.Adapter<?> adapter, final T info) ;
}
