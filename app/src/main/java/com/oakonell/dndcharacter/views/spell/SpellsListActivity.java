package com.oakonell.dndcharacter.views.spell;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.CursorIndexesByName;

/**
 * Created by Rob on 11/2/2015.
 */
public class SpellsListActivity extends AbstractComponentListActivity<Spell> {

    @NonNull
    protected SpellRowViewHolderCursor newRowViewHolder(View newView) {
        return new SpellRowViewHolderCursor(newView);
    }

    @Override
    protected int getListItemResource() {
        return R.layout.spell_list_item;
    }

    @NonNull
    @Override
    protected Class<? extends Spell> getComponentClass() {
        return Spell.class;
    }

    @Override
    protected Spell createNewRecord() {
        return new Spell();
    }

    @Override
    protected void openRecord(long id) {
        Spell spell = Spell.load(Spell.class, id);

        EditSpellDialogFragment dialog = EditSpellDialogFragment.create(spell);
        dialog.show(getSupportFragmentManager(), "spell_edit");
    }

    @Override
    protected String getSubtitle() {
        return "Spells";
    }

    @Override
    protected void deleteRow(long id) {
        Spell.delete(Spell.class, id);
    }

    protected static class SpellRowViewHolderCursor extends RowViewHolderCursor {
        private final TextView levelTextView;

        public SpellRowViewHolderCursor(View itemView) {
            super(itemView);
            levelTextView = (TextView) itemView.findViewById(R.id.level);
        }

        @Override
        public void bindTo(Cursor cursor, AbstractComponentListActivity context, RecyclerView.Adapter adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);
            final int level = cursor.getInt(cursorIndexesByName.getIndex(cursor, "level"));
            String levelString;
            if (level == 0) {
                levelString = "Cantrip";
            } else {
                levelString = level + "";
            }
            levelTextView.setText(levelString);

        }
    }
}