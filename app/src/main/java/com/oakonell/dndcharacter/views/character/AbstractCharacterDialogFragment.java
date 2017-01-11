package com.oakonell.dndcharacter.views.character;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.oakonell.dndcharacter.DndCharacterApp;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ContextNote;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.character.feature.FeatureContextHelper;
import com.squareup.leakcanary.RefWatcher;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import hugo.weaving.DebugLog;

/**
 * Created by Rob on 12/24/2015.
 */
public abstract class AbstractCharacterDialogFragment extends AbstractDialogFragment implements OnCharacterLoaded, CharacterChangedListener, FeatureContextHelper.ContextFilterable {
    protected static final String COMPANION_ARG = "isForCompanion";

    private FeatureContextHelper contextHelper = new FeatureContextHelper(this);

    public String getNoteTitle() {
        return getTitle();
    }

    public AbstractCharacter getContextCharacter() {
        return getDisplayedCharacter();
    }

    public boolean isForCompanion() {
        Bundle args = getArguments();
        if (args == null) return false;
        return args.getBoolean(COMPANION_ARG, false);
    }


    public AbstractCharacter getDisplayedCharacter() {
        AbstractCharacter character = getCharacter();
        if (isForCompanion()) {
            character = getCharacter().getDisplayedCompanion();
        }
        return character;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        contextHelper.onCreateView(view);


        getMainActivity().addCharacterLoadLister(this);

        return view;
    }

    protected void extraDoneActions() {
        contextHelper.extraDoneActions(getDisplayedCharacter());

        getMainActivity().updateViews();
        getMainActivity().saveCharacter();
    }


    @NonNull
    public final CharacterActivity getMainActivity() {
        return (CharacterActivity) getActivity();
    }

    @Nullable
    public final com.oakonell.dndcharacter.model.character.Character getCharacter() {
        return getMainActivity().getCharacter();
    }

    @DebugLog
    public void onCharacterLoaded(final Character character) {
        contextHelper.onCharacterLoaded(getMainActivity(), getDisplayedCharacter());
    }


    @DebugLog
    public void onCharacterChanged(@NonNull Character character) {
        contextHelper.onCharacterChanged(getDisplayedCharacter());
    }

    @NonNull
    public Set<FeatureContextArgument> getContextFilter() {
        return Collections.emptySet();
    }

    public FeatureContextArgument getNoteContext() {
        return null;
    }

    protected boolean onDone() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = DndCharacterApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
