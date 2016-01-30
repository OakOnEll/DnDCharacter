package com.oakonell.dndcharacter.views.feat;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.effect.Effect;
import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.effect.EditEffectDialogFragment;

/**
 * Created by Rob on 11/2/2015.
 */
public class FeatsListActivity extends AbstractComponentListActivity<Feat> {
    @NonNull
    @Override
    protected Class<? extends Feat> getComponentClass() {
        return Feat.class;
    }

    @NonNull
    @Override
    protected Feat createNewRecord() {
        return new Feat();
    }

    @Override
    protected void openRecord(long id) {
        Feat feat = Feat.load(Feat.class, id);

        EditFeatDialogFragment dialog = EditFeatDialogFragment.create(feat);
        dialog.show(getSupportFragmentManager(), "feat_edit");
    }

    @NonNull
    @Override
    protected String getSubtitle() {
        return getString(R.string.feats);
    }

    @Override
    protected void deleteRow(long id) {
        Feat.delete(Feat.class, id);
    }

}
