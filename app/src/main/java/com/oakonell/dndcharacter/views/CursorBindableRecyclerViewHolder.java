package com.oakonell.dndcharacter.views;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Rob on 12/4/2015.
 */
public class CursorBindableRecyclerViewHolder<C, A extends RecyclerView.Adapter> extends BindableViewHolder<C, A> {

    public CursorBindableRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bindTo(Cursor cursor, C context, A adapter, CursorIndexesByName cursorIndexesByName) {

    }
}
