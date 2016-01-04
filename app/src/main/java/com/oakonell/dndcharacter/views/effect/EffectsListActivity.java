package com.oakonell.dndcharacter.views.effect;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.effect.Effect;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.CursorIndexesByName;
import com.oakonell.dndcharacter.views.background.EditBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.race.EditRaceDialogFragment;

/**
 * Created by Rob on 11/2/2015.
 */
public class EffectsListActivity extends AbstractComponentListActivity<Effect> {
    @NonNull
    @Override
    protected Class<? extends Effect> getComponentClass() {
        return Effect.class;
    }

    @Override
    protected Effect createNewRecord() {
        return new Effect();
    }

    @Override
    protected void openRecord(long id) {
        Effect effect = Effect.load(Effect.class, id);

        EditEffectDialogFragment dialog = EditEffectDialogFragment.create(effect);
        dialog.show(getSupportFragmentManager(), "background_edit");
    }

    @Override
    protected String getSubtitle() {
        return "Effects";
    }

    @Override
    protected void deleteRow(long id) {
        Effect.delete(Effect.class, id);
    }

}
