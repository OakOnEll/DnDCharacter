package com.oakonell.dndcharacter.views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Rob on 1/4/2016.
 */
public abstract class BindableComponentViewHolder<T, C, A extends RecyclerView.Adapter<?>> extends BindableViewHolder<C, A> {
    public BindableComponentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(final C context, final A adapter, final T info);
}
