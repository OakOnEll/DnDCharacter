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
import com.squareup.leakcanary.RefWatcher;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import hugo.weaving.DebugLog;

/**
 * Created by Rob on 12/24/2015.
 */
public abstract class AbstractCharacterDialogFragment extends AbstractDialogFragment implements OnCharacterLoaded, CharacterChangedListener {
    protected static final String COMPANION_ARG = "isForCompanion";
    private RecyclerView context_list;

    private ContextualComponentAdapter contextualComponentAdapter;
    private ViewGroup context_group;
    private ImageView add_note;


    protected boolean isForCompanion() {
        Bundle args = getArguments();
        if (args == null) return false;
        return args.getBoolean(COMPANION_ARG, false);
    }


    protected AbstractCharacter getDisplayedCharacter() {
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
        add_note = (ImageView) view.findViewById(R.id.add_note);

        context_list = (RecyclerView) view.findViewById(R.id.context_list);
        context_group = (ViewGroup) view.findViewById(R.id.context_group);


        getMainActivity().addCharacterLoadLister(this);

        return view;
    }

    protected void extraDoneActions() {
        if (contextualComponentAdapter != null) {
            contextualComponentAdapter.deletePendingEffects(getDisplayedCharacter());
        }

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
        if (context_list != null) {
            if (getNoteContext() != null) {
                add_note.setVisibility(View.VISIBLE);
                add_note.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final List<ContextNote> notes = getDisplayedCharacter().getContextNotes(getNoteContext().getContext());
                        addNote(notes, getNoteContext());
                        contextualComponentAdapter.reloadList(getDisplayedCharacter());
                    }
                });
            } else {
                add_note.setVisibility(View.GONE);
            }

            contextualComponentAdapter = new ContextualComponentAdapter(this, getContextFilter());
            context_list.setAdapter(contextualComponentAdapter);
            // decide on 1 or 2 columns based on screen size
            //int numColumns = getResources().getInteger(R.integer.feature_columns);
            context_list.setLayoutManager(UIUtils.createLinearLayoutManager(getMainActivity(), android.support.v7.widget.LinearLayoutManager.VERTICAL, false));

            if (contextualComponentAdapter.getItemCount() == 0 && getNoteContext() == null) {
                context_group.setVisibility(View.GONE);
            } else {
                context_group.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addNote(final List<ContextNote> notes, final FeatureContextArgument noteContext) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(getString(R.string.enter_a_note, getTitle()));

        b.setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final EditText input = new EditText(getActivity());
        b.setView(input);
        b.setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
                notes.add(new ContextNote(noteContext, input.getText().toString()));
                contextualComponentAdapter.reloadList(getDisplayedCharacter());
            }
        });
        b.show();
    }

    @DebugLog
    public void onCharacterChanged(@NonNull Character character) {
        if (contextualComponentAdapter != null) {
            contextualComponentAdapter.reloadList(getDisplayedCharacter());
            if (contextualComponentAdapter.getItemCount() == 0 && getNoteContext() == null) {
                context_group.setVisibility(View.GONE);
            } else {
                context_group.setVisibility(View.VISIBLE);
            }
        }
    }

    @NonNull
    protected Set<FeatureContextArgument> getContextFilter() {
        return Collections.emptySet();
    }

    protected FeatureContextArgument getNoteContext() {
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
