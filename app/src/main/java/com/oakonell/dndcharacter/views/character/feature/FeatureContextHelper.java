package com.oakonell.dndcharacter.views.character.feature;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ContextNote;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.character.ContextualComponentAdapter;

import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 1/11/2017.
 */

public class FeatureContextHelper {
    private RecyclerView context_list;
    private ContextualComponentAdapter contextualComponentAdapter;

    private ViewGroup context_group;

    private ImageView add_note;

    private ContextFilterable filterable;

    public interface ContextFilterable {
        FeatureContextArgument getNoteContext();
        Set<FeatureContextArgument> getContextFilter();

        AbstractCharacter getContextCharacter();
//  TODO this one seems wrong- it is made to open a dialog to use the "displayed" companion
        //   this is a code smell
        boolean isForCompanion();

        String getNoteTitle();
    }

    public FeatureContextHelper(ContextFilterable filterable) {
        this.filterable =filterable;
    }

    public void onCreateView(View view) {
        add_note = (ImageView) view.findViewById(R.id.add_note);

        context_list = (RecyclerView) view.findViewById(R.id.context_list);
        context_group = (ViewGroup) view.findViewById(R.id.context_group);

    }

    public void extraDoneActions(AbstractCharacter character) {
        if (contextualComponentAdapter != null) {
            contextualComponentAdapter.deletePendingEffects(character);
        }
    }

    public void onCharacterLoaded(final Context context, final AbstractCharacter character) {
        if (context_list == null) return;

        final FeatureContextArgument noteContext = filterable.getNoteContext();
        if (noteContext != null) {
            add_note.setVisibility(View.VISIBLE);
            add_note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FeatureContextArgument noteContext = filterable.getNoteContext();
                    final List<ContextNote> notes = character.getContextNotes(noteContext.getContext());
                    addNote(context, notes, noteContext);
                    contextualComponentAdapter.reloadList(character);
                }
            });
        } else {
            add_note.setVisibility(View.GONE);
        }

        contextualComponentAdapter = new ContextualComponentAdapter(context, filterable);
        context_list.setAdapter(contextualComponentAdapter);
        // decide on 1 or 2 columns based on screen size
        //int numColumns = getResources().getInteger(R.integer.feature_columns);
        context_list.setLayoutManager(UIUtils.createLinearLayoutManager(context, android.support.v7.widget.LinearLayoutManager.VERTICAL, false));

        if (contextualComponentAdapter.getItemCount() == 0 && noteContext == null) {
            context_group.setVisibility(View.GONE);
        } else {
            context_group.setVisibility(View.VISIBLE);
        }
    }

    private void addNote(Context context, final List<ContextNote> notes, final FeatureContextArgument noteContext) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle(context.getString(R.string.enter_a_note, filterable.getNoteTitle()));

        b.setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final EditText input = new EditText(context);
        b.setView(input);
        b.setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
                notes.add(new ContextNote(noteContext, input.getText().toString()));
                contextualComponentAdapter.reloadList(filterable.getContextCharacter());
            }
        });
        b.show();
    }

    public void onCharacterChanged(AbstractCharacter character) {
        if (contextualComponentAdapter != null) {
            contextualComponentAdapter.reloadList(character);
            if (contextualComponentAdapter.getItemCount() == 0 && filterable.getNoteContext() == null) {
                context_group.setVisibility(View.GONE);
            } else {
                context_group.setVisibility(View.VISIBLE);
            }
        }
    }
}
