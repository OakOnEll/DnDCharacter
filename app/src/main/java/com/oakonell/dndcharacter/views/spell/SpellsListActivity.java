package com.oakonell.dndcharacter.views.spell;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.model.spell.SpellClass;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.CursorIndexesByName;

/**
 * Created by Rob on 11/2/2015.
 */
public class SpellsListActivity extends AbstractComponentListActivity<Spell> {

    @NonNull
    protected SpellRowViewHolderCursor newRowViewHolder(@NonNull View newView) {
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

    @NonNull
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

    @NonNull
    @Override
    protected String getSubtitle() {
        return getString(R.string.spells_title);
    }

    @Override
    protected void deleteRow(long id) {
        // delete spell class rows first
        final From delete = new Delete().from(SpellClass.class).where("spell = ?", id);
        delete.execute();
        Spell.delete(Spell.class, id);
    }

    protected static class SpellRowViewHolderCursor extends RowViewHolderCursor {
        @NonNull
        private final TextView levelTextView;
        @NonNull
        private final TextView schoolTextView;
        @NonNull
        private final TextView ritualTextView;

        public SpellRowViewHolderCursor(@NonNull View itemView) {
            super(itemView);
            levelTextView = (TextView) itemView.findViewById(R.id.level);
            schoolTextView = (TextView) itemView.findViewById(R.id.school);
            ritualTextView = (TextView) itemView.findViewById(R.id.ritual);
        }

        @Override
        public void bindTo(@NonNull Cursor cursor, @NonNull AbstractComponentListActivity context, RecyclerView.Adapter adapter, @NonNull CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);
            final int level = cursor.getInt(cursorIndexesByName.getIndex(cursor, "level"));
            String levelString;
            if (level == 0) {
                levelString = context.getString(R.string.cantrip_label);
            } else {
                levelString = level + "";
            }
            levelTextView.setText(levelString);

            final int ritualInt = cursor.getInt(cursorIndexesByName.getIndex(cursor, "ritual"));
            if (ritualInt != 0) {
                ritualTextView.setVisibility(View.VISIBLE);
            } else {
                ritualTextView.setVisibility(View.GONE);
            }

            final String schoolString = cursor.getString(cursorIndexesByName.getIndex(cursor, "school"));
            SpellSchool school = EnumHelper.stringToEnum(schoolString, SpellSchool.class);
            schoolTextView.setText(school.getStringResId());

        }
    }
}
