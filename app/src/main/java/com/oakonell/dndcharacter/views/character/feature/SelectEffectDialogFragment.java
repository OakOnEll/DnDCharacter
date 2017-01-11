package com.oakonell.dndcharacter.views.character.feature;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.effect.AddEffectToCharacterVisitor;
import com.oakonell.dndcharacter.model.effect.Effect;
import com.oakonell.dndcharacter.views.character.AbstractSelectComponentDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

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

    protected int getDialogResource() {
        return R.layout.effect_search_dialog;
    }

    @Nullable
    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateTheView(inflater, container, savedInstanceState);
        View custom_effect = view.findViewById(R.id.custom_effect);
        custom_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomEffectDialogFragment dialog = CustomEffectDialogFragment.createDialog();
                dialog.show(getFragmentManager(), "custom_effect");
                dismiss();
            }
        });
        return view;
    }

    @NonNull
    @Override
    public Class<? extends Effect> getComponentClass() {
        return Effect.class;
    }

    @Override
    protected boolean applyAction(long id) {
        if (listener != null) {
            return listener.effectSelected(id);
        }
        throw new RuntimeException("No Listener!");
    }


    public static class AddEffectToCharacterListener implements EffectSelectedListener {

        private final CharacterActivity activity;

        public AddEffectToCharacterListener(CharacterActivity activity) {
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

            Runnable continuation = new Runnable() {
                @Override
                public void run() {
                    activity.updateViews();
                    activity.saveCharacter();
                }
            };
            AddEffectToCharacterVisitor.applyToCharacter(activity, effect, activity.getCharacter(), continuation);

            return true;
        }

    }

}
