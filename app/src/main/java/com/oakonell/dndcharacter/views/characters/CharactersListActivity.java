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

    @NonNull
    @Override
    protected CharacterRow createNewRecord() {
        return new CharacterRow();
    }

    @Override
    protected void openRecord(long id) {
        openCharacter(id);
    }

    @NonNull
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
    protected CharacterRowViewHolderCursor newRowViewHolder(@NonNull View newView) {
        return new CharacterRowViewHolderCursor(newView);
    }

    @Override
    protected int getListItemResource() {
        return R.layout.character_list_item;
    }

    protected static class CharacterRowViewHolderCursor extends CursorBindableRecyclerViewHolder<AbstractComponentListActivity> {
        @NonNull
        public final TextView name;
        @NonNull
        public final TextView classes;
        @NonNull
        private final TextView race;
        @NonNull
        private final TextView hp;

        public CharacterRowViewHolderCursor(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            classes = (TextView) itemView.findViewById(R.id.classes);
            race = (TextView) itemView.findViewById(R.id.race);
            hp = (TextView) itemView.findViewById(R.id.hp);
        }

        @Override
        public void bindTo(@NonNull Cursor cursor, @NonNull final AbstractComponentListActivity context, RecyclerView.Adapter adapter, @NonNull CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);

            final long id = cursor.getInt(cursorIndexesByName.getIndex(cursor, BaseColumns._ID));
            String nameString = cursor.getString(cursorIndexesByName.getIndex(cursor, "name"));
            String classesString = cursor.getString(cursorIndexesByName.getIndex(cursor, "classesString"));
            String raceString = cursor.getString(cursorIndexesByName.getIndex(cursor, "race_display_name"));
            String hpString = cursor.getString(cursorIndexesByName.getIndex(cursor, "hp"));
            if (nameString == null || nameString.trim().length() == 0) {
                nameString = context.getString(R.string.unnamed_character);
            }
            if (classesString == null || classesString.trim().length() == 0) {
                classesString = context.getString(R.string.no_class);
            }
            if (raceString == null || raceString.trim().length() == 0) {
                raceString = context.getString(R.string.no_race);
            }
            if (hpString != null) {
                hp.setText(hpString);
            } else {
                hp.setText("");
            }

            name.setText(nameString);
            classes.setText(classesString);
            race.setText(raceString);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.openCharacter(id);
                }
            });
        }
    }

}
