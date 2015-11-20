package com.oakonell.dndcharacter.views.race;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;

/**
 * Created by Rob on 11/2/2015.
 */
public class RacesListActivity extends AbstractComponentListActivity {

    @NonNull
    @Override
    protected Class<? extends Model> getComponentClass() {
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
}
