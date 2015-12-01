package com.oakonell.dndcharacter.views.race;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;

/**
 * Created by Rob on 11/2/2015.
 */
public class RacesListActivity extends AbstractComponentListActivity<Race> {
    private int parentIndex = -1;

    protected static class RaceRowViewHolder extends RowViewHolder {
        public TextView parentRace;
    }

    @NonNull
    protected RaceRowViewHolder newRowViewHolder(View newView) {
        RaceRowViewHolder result = new RaceRowViewHolder();
        result.parentRace = (TextView) newView.findViewById(R.id.parent_race);
        return result;
    }

    @Override
    protected int getListItemResource() {
        return R.layout.race_list_item;
    }

    @Override
    protected void updateRowView(View view, Cursor cursor, RowViewHolder holder) {
        super.updateRowView(view, cursor, holder);

        if (parentIndex < 0) {
            parentIndex = cursor.getColumnIndex("parentRace");
        }
        final String parentName = cursor.getString(parentIndex);
        ((RaceRowViewHolder) holder).parentRace.setText(parentName);
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
}
