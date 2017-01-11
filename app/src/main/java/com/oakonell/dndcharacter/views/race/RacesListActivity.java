package com.oakonell.dndcharacter.views.race;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.CursorIndexesByName;

/**
 * Created by Rob on 11/2/2015.
 */
public class RacesListActivity extends AbstractComponentListActivity<Race> {

    @NonNull
    protected RaceRowViewHolderCursor newRowViewHolder(@NonNull View newView) {
        return new RaceRowViewHolderCursor(newView);
    }

    @Override
    protected int getListItemResource() {
        return R.layout.race_list_item;
    }

    @NonNull
    @Override
    protected Class<? extends Race> getComponentClass() {
        return Race.class;
    }

    @NonNull
    @Override
    protected Race createNewRecord() {
        return new Race();
    }

    @Override
    protected void openRecord(long id) {
        Race race = Race.load(Race.class, id);

        EditRaceDialogFragment dialog = EditRaceDialogFragment.create(race);
        dialog.show(getSupportFragmentManager(), "race_edit");
    }

    @NonNull
    @Override
    protected String getSubtitle() {
        return getString(R.string.races);
    }

    @Override
    protected void deleteRow(long id) {
        Race.delete(Race.class, id);
    }

    protected static class RaceRowViewHolderCursor extends RowViewHolderCursor<AbstractComponentModel> {
        @NonNull
        public final TextView parentRace;

        public RaceRowViewHolderCursor(@NonNull View itemView) {
            super(itemView);
            parentRace = (TextView) itemView.findViewById(R.id.parent_race);
        }

        @Override
        public void bindTo(Cursor cursor, AbstractComponentListActivity context, ComponentListAdapter<AbstractComponentListActivity<AbstractComponentModel>, AbstractComponentModel> adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);
            final String parentName = cursor.getString(cursorIndexesByName.getIndex(cursor, "parentRace"));
            parentRace.setText(parentName);
        }
    }
}
