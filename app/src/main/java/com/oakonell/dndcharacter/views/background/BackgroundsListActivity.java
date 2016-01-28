package com.oakonell.dndcharacter.views.background;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;

/**
 * Created by Rob on 11/2/2015.
 */
public class BackgroundsListActivity extends AbstractComponentListActivity<Background> {
    @NonNull
    @Override
    protected Class<? extends Background> getComponentClass() {
        return Background.class;
    }

    @NonNull
    @Override
    protected Background createNewRecord() {
        return new Background();
    }

    @Override
    protected void openRecord(long id) {
        Background background = Background.load(Background.class, id);

        EditBackgroundDialogFragment dialog = EditBackgroundDialogFragment.create(background);
        dialog.show(getSupportFragmentManager(), "background_edit");
    }

    @NonNull
    @Override
    protected String getSubtitle() {
        return getString(R.string.backgrounds);
    }

    @Override
    protected void deleteRow(long id) {
        Background.delete(Background.class, id);
    }
}
