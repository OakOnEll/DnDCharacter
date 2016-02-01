package com.oakonell.dndcharacter.views.character.feature;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.effect.AddEffectToCharacterVisitor;
import com.oakonell.dndcharacter.model.effect.Effect;
import com.oakonell.dndcharacter.views.character.AbstractSelectComponentDialogFragment;
import com.oakonell.dndcharacter.views.character.MainActivity;

/**
 * Created by Rob on 1/3/2016.
 */
public class SelectEffectDialogFragment extends AbstractSelectComponentDialogFragment<AbstractSelectComponentDialogFragment.RowViewHolder> {
    private EffectSelectedListener listener;

    public void setListener(EffectSelectedListener listener) {
        this.listener = listener;
    }

    public interface EffectSelectedListener {
        boolean effectSelected(long id);
    }

    @NonNull
    public static SelectEffectDialogFragment createDialog(EffectSelectedListener listener) {
        final SelectEffectDialogFragment dialogFragment = new SelectEffectDialogFragment();
        dialogFragment.setListener(listener);
        return dialogFragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.add_effect);
    }

    @NonNull
    @Override
    public Class<? extends Model> getComponentClass() {
        return Effect.class;
    }

    @Override
    protected boolean applyAction(long id) {
        if (listener != null) {
            return listener.effectSelected(id);
        }
        throw new RuntimeException("No Listener!");
    }


    @NonNull
    @Override
    public RowViewHolder newRowViewHolder(View newView) {
        return new RowViewHolder(newView);
    }


    public static class AddEffectToCharacterListener implements EffectSelectedListener {

        private final MainActivity activity;

        public AddEffectToCharacterListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean effectSelected(long id) {
            Effect effect = Effect.load(Effect.class, id);

            final String name = effect.getName();
            final CharacterEffect existingEffect = activity.getCharacter().getEffectNamed(name);
            if (existingEffect != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.already_has_effect, name));
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return false;
            }

            CharacterEffect charEffect = AddEffectToCharacterVisitor.applyToCharacter(effect, activity.getCharacter());
            activity.updateViews();
            activity.saveCharacter();

            return true;
        }
    }

}
