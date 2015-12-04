package com.oakonell.dndcharacter.views.race;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;

/**
 * Created by Rob on 11/2/2015.
 */
public class RacesListActivity extends AbstractComponentListActivity<Race> {

    protected static class RaceRowViewHolder extends RowViewHolder {
        public TextView parentRace;

        public RaceRowViewHolder(View itemView) {
            super(itemView);
            parentRace = (TextView) itemView.findViewById(R.id.parent_race);
        }

        @Override
        public void bindTo(Cursor cursor, AbstractComponentListActivity context, RecyclerView.Adapter adapter, IndexesByName indexesByName) {
            super.bindTo(cursor, context, adapter, indexesByName);
            final String parentName = cursor.getString(indexesByName.getIndex(cursor, "parentRace"));
            parentRace.setText(parentName);
        }
    }

    @NonNull
    protected RaceRowViewHolder newRowViewHolder(View newView) {
        return new RaceRowViewHolder(newView);
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

    @Override
    protected String getRecordTypeName() {
        return "Race";
    }

    @Override
    protected String getSubtitle() {
        return "Races";
    }

    @Override
    protected void deleteRow(long id) {
        Race.delete(Race.class, id);
    }
}
