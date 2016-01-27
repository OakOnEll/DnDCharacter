package com.oakonell.dndcharacter.views.effect;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.effect.Effect;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;

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
