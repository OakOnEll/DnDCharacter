package com.oakonell.dndcharacter.views.character.feat;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.views.CursorIndexesByName;
import com.oakonell.dndcharacter.views.character.AbstractSelectComponentDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

/**
 * Created by Rob on 2/1/2016.
 */
public class SelectFeatDialogFragment extends AbstractSelectComponentDialogFragment<AbstractSelectComponentDialogFragment.RowViewHolder> {

    private FeatSelectedListener listener;

    public void setListener(FeatSelectedListener listener) {
        this.listener = listener;
    }

    public interface FeatSelectedListener {
        boolean featSelected(long id);
    }

    @NonNull
    public static SelectFeatDialogFragment createDialog(@NonNull FeatSelectedListener listener) {
        SelectFeatDialogFragment dialog = new SelectFeatDialogFragment();
        dialog.setListener(listener);
        return dialog;
    }


    @Override
    protected String getTitle() {
        return getString(R.string.select_feat);
    }


    @NonNull
    @Override
    public Class<? extends Feat> getComponentClass() {
        return Feat.class;
    }

    @Override
    protected boolean applyAction(long id) {
        if (listener != null) {
            return listener.featSelected(id);
        }
        throw new RuntimeException("No Listener!");
    }


}
