package com.oakonell.dndcharacter.views.item;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.CursorIndexesByName;

/**
 * Created by Rob on 11/2/2015.
 */
public class ItemsListActivity extends AbstractComponentListActivity<ItemRow> {
    @NonNull
    @Override
    protected Class<? extends ItemRow> getComponentClass() {
        return ItemRow.class;
    }

    @NonNull
    @Override
    protected ItemRow createNewRecord() {
        return new ItemRow();
    }

    @Override
    protected void openRecord(long id) {
        ItemRow background = ItemRow.load(ItemRow.class, id);

        EditItemDialogFragment dialog = EditItemDialogFragment.create(background);
        dialog.show(getSupportFragmentManager(), "item_edit");
    }

    @NonNull
    @Override
    protected String getSubtitle() {
        return getString(R.string.items);
    }

    @NonNull
    @Override
    protected ItemRowViewHolderCursor newRowViewHolder(@NonNull View newView) {
        return new ItemRowViewHolderCursor(newView);
    }

    @Override
    protected int getListItemResource() {
        return R.layout.item_list_item;
    }

    @Override
    protected void deleteRow(long id) {
        ItemRow.delete(ItemRow.class, id);
    }

    protected static class ItemRowViewHolderCursor extends RowViewHolderCursor<AbstractComponentModel> {
        @NonNull
        public final TextView category;

        public ItemRowViewHolderCursor(@NonNull View itemView) {
            super(itemView);
            category = (TextView) itemView.findViewById(R.id.category);
        }

        @Override
        public void bindTo(Cursor cursor, AbstractComponentListActivity context, ComponentListAdapter<AbstractComponentListActivity<AbstractComponentModel>, AbstractComponentModel> adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);
            final String categoryString = cursor.getString(cursorIndexesByName.getIndex(cursor, "category"));
            category.setText(categoryString);
        }
    }
}
