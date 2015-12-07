package com.oakonell.dndcharacter.views;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Rob on 12/4/2015.
 */
public class BindableRecyclerViewHolder extends RecyclerView.ViewHolder {

    public BindableRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public void bindTo(Cursor cursor, AbstractComponentListActivity context, RecyclerView.Adapter adapter, CursorIndexesByName cursorIndexesByName) {

    }
}
