package com.oakonell.dndcharacter.views.characters;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.CharacterRow;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.CursorBindableRecyclerViewHolder;
import com.oakonell.dndcharacter.views.CursorIndexesByName;

/**
 * Created by Rob on 11/2/2015.
 */
public class CharactersListActivity extends AbstractComponentListActivity<CharacterRow> {
    @NonNull
    @Override
    protected Class<? extends CharacterRow> getComponentClass() {
        return CharacterRow.class;
    }

    @Override
    protected CharacterRow createNewRecord() {
        return new CharacterRow();
    }

    @Override
    protected void openRecord(long id) {
        openCharacter(id);
    }

    @Override
    protected String getSubtitle() {
        return getString(R.string.characters_title);
    }

    @Override
    protected void deleteRow(long id) {
        CharacterRow.delete(CharacterRow.class, id);
    }

    @NonNull
    protected String getCursorSortBy() {
        return "last_updated desc";
    }

    @NonNull
    @Override
    protected CharacterRowViewHolderCursor newRowViewHolder(View newView) {
        return new CharacterRowViewHolderCursor(newView);
    }

    @Override
    protected int getListItemResource() {
        return R.layout.character_list_item;
    }

    protected static class CharacterRowViewHolderCursor extends CursorBindableRecyclerViewHolder<AbstractComponentListActivity> {
        public final TextView name;
        public final TextView classes;

        public CharacterRowViewHolderCursor(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            classes = (TextView) itemView.findViewById(R.id.classes);
        }

        @Override
        public void bindTo(Cursor cursor, final AbstractComponentListActivity context, RecyclerView.Adapter adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);

            final long id = cursor.getInt(cursorIndexesByName.getIndex(cursor, BaseColumns._ID));
            final String nameString = cursor.getString(cursorIndexesByName.getIndex(cursor, "name"));
            final String classesString = cursor.getString(cursorIndexesByName.getIndex(cursor, "classesString"));
            name.setText(nameString);
            classes.setText(classesString);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.openCharacter(id);
                }
            });
        }
    }

}
