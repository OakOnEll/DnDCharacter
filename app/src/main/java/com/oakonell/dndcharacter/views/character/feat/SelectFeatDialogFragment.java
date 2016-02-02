package com.oakonell.dndcharacter.views.character.feat;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.views.character.AbstractSelectComponentDialogFragment;
import com.oakonell.dndcharacter.views.character.MainActivity;

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
    public Class<? extends Model> getComponentClass() {
        return Feat.class;
    }

    @Override
    protected boolean applyAction(long id) {
        if (listener != null) {
            return listener.featSelected(id);
        }
        throw new RuntimeException("No Listener!");
    }

//
//    @NonNull
//    @Override
//    public RowViewHolder newRowViewHolder(View newView) {
//        return new RowViewHolder(newView);
//    }


    public static class AddFeatToCharacterListener implements FeatSelectedListener {

        private final MainActivity activity;

        public AddFeatToCharacterListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean featSelected(long id) {
//            Effect effect = Effect.load(Effect.class, id);
//
//            final String name = effect.getName();
//            final CharacterEffect existingEffect = activity.getCharacter().getEffectNamed(name);
//            if (existingEffect != null) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                builder.setTitle(activity.getString(R.string.already_has_effect, name));
//                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(@NonNull DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.show();
//                return false;
//            }
//
//            CharacterEffect charEffect = AddEffectToCharacterVisitor.applyToCharacter(effect, activity.getCharacter());
//            activity.updateViews();
//            activity.saveCharacter();

            return true;
        }
    }
}
