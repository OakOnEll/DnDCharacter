package com.oakonell.dndcharacter.views.item;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;

/**
 * Created by Rob on 11/2/2015.
 */
public class ItemsListActivity extends AbstractComponentListActivity<ItemRow> {
    @NonNull
    @Override
    protected Class<? extends ItemRow> getComponentClass() {
        return ItemRow.class;
    }

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

    @Override
    protected String getRecordTypeName() {
        return "Item";
    }

    @Override
    protected String getSubtitle() {
        return "Items";
    }


    private int categoryIndex = -1;

    protected static class ItemRowViewHolder extends RowViewHolder {
        public TextView category;
    }

    @NonNull
    protected ItemRowViewHolder newRowViewHolder(View newView) {
        ItemRowViewHolder result = new ItemRowViewHolder();
        result.category = (TextView) newView.findViewById(R.id.category);
        return result;
    }

    @Override
    protected int getListItemResource() {
        return R.layout.item_list_item;
    }

    @Override
    protected void updateRowView(View view, Cursor cursor, RowViewHolder holder) {
        super.updateRowView(view, cursor, holder);

        if (categoryIndex < 0) {
            categoryIndex = cursor.getColumnIndex("category");
        }
        final String category = cursor.getString(categoryIndex);
        ((ItemRowViewHolder) holder).category.setText(category);
    }
}
