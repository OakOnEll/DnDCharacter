package com.oakonell.dndcharacter.views;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Rob on 12/4/2015.
 */
public class CursorBindableRecyclerViewHolder<C> extends RecyclerView.ViewHolder {

    public CursorBindableRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public void bindTo(Cursor cursor, C context, RecyclerView.Adapter adapter, CursorIndexesByName cursorIndexesByName) {

    }
}
