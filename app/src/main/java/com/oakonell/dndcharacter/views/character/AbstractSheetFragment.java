package com.oakonell.dndcharacter.views.character;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.oakonell.dndcharacter.DndCharacterApp;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.views.AbstractBaseActivity;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.character.classes.CharacterLevelsDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.AddEffectDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.ViewEffectDialogFragment;
import com.oakonell.dndcharacter.views.character.race.ApplyRaceDialogFragment;
import com.squareup.leakcanary.RefWatcher;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.List;

import hugo.weaving.DebugLog;

/**
 * Created by Rob on 10/27/2015.
 */
public abstract class AbstractSheetFragment extends Fragment implements OnCharacterLoaded, CharacterChangedListener {
    private EditText character_name;
    private TextView classes;
    private TextView race;
    private TextView background;
    private TextView character_name_read_only;
    private Button changeName;
    private Button cancelName;
    private RecyclerView effectList;

    private EffectsBarAdapter effectListAdapter;
    private ViewGroup effect_group;
    private ImageButton add_effect;

    @DebugLog
    @Override
    public final void onCharacterChanged(Character character) {
        updateViews();
    }

    public void updateViews() {
        updateViews(getView());
    }

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected Character getCharacter() {
        return getMainActivity().getCharacter();
    }

    @DebugLog
    protected void updateViews(View rootView) {
        Character character = getCharacter();
        if (character == null) {
            Toast.makeText(getActivity(), "Update views with a null character!?", Toast.LENGTH_SHORT).show();

            character_name.setText("");
            character_name_read_only.setText("");

            race.setText("");
            background.setText("");
            classes.setText("");
            return;
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(character.getName());
        character_name_read_only.setText(character.getName());
        character_name.setText(character.getName());

        race.setText(character.getDisplayRaceName());
        background.setText(character.getBackgroundName());
        classes.setText(character.getClassesString());

        if (effectListAdapter != null) {
            effectListAdapter.reloadList(character);
            effect_group.setVisibility(effectListAdapter.effects.size() > 0 ? View.VISIBLE : View.GONE);
        } else {
            effect_group.setVisibility(View.GONE);
        }

    }

    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        View view = onCreateTheView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).addCharacterLoadLister(this);
        return view;
    }

    protected abstract View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected void superCreateViews(View rootView) {
        character_name_read_only = (TextView) rootView.findViewById(R.id.character_name_read);
        character_name = (EditText) rootView.findViewById(R.id.character_name);
        classes = (TextView) rootView.findViewById(R.id.classes);
        race = (TextView) rootView.findViewById(R.id.race);
        background = (TextView) rootView.findViewById(R.id.background);
        changeName = (Button) rootView.findViewById(R.id.change_name);
        cancelName = (Button) rootView.findViewById(R.id.cancel);

        character_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                changeName.callOnClick();
                return false;
            }
        });

        effectList = (RecyclerView) rootView.findViewById(R.id.effect_list);
        effect_group = (ViewGroup) rootView.findViewById(R.id.effect_group);
        add_effect = (ImageButton) rootView.findViewById(R.id.add_effect);

    }

    private void allowNameEdit(boolean b) {
        int editVis = b ? View.VISIBLE : View.GONE;
        int readOnlyVis = b ? View.GONE : View.VISIBLE;


        character_name.selectAll();

        if (b) {
            character_name.requestFocus();
//            // show keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        } else {
            character_name.clearFocus();
//            // close keyboard
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(character_name.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        character_name.setVisibility(editVis);
        changeName.setVisibility(editVis);
        cancelName.setVisibility(editVis);

        character_name_read_only.setVisibility(readOnlyVis);
    }

    @DebugLog
    @Override
    public void onCharacterLoaded(Character character) {
        character_name_read_only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowNameEdit(true);
                character_name.requestFocus();
            }
        });

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCharacter().setName(character_name.getText().toString());
                allowNameEdit(false);
                getMainActivity().saveCharacter();
                updateViews();
                // a name change should update recent characters
                ((AbstractBaseActivity) getActivity()).populateRecentCharacters();
            }
        });
        cancelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                character_name.setText(getCharacter().getName());
                allowNameEdit(false);
                updateViews();
            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ApplyBackgroundDialogFragment dialog = ApplyBackgroundDialogFragment.createDialog();
                    dialog.show(getFragmentManager(), "background");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to build ui: \n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException("Unable to build ui", e);
                }
            }
        });
        race.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ApplyRaceDialogFragment dialog = ApplyRaceDialogFragment.createDialog();
                    dialog.show(getFragmentManager(), "race");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to build ui: \n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException("Unable to build ui", e);
                }

            }
        });
        classes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CharacterLevelsDialogFragment dialog = CharacterLevelsDialogFragment.createDialog();
                    dialog.show(getFragmentManager(), "classes");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to build ui: \n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException("Unable to build ui", e);
                }
            }
        });

        effectListAdapter = new EffectsBarAdapter(this, character);
        effectList.setAdapter(effectListAdapter);


        effectList.setHasFixedSize(false);
        effectList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST);
        effectList.addItemDecoration(itemDecoration);

        effect_group.setVisibility(effectListAdapter.effects.size() > 0 ? View.VISIBLE : View.GONE);

        add_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEffectDialogFragment dialog = AddEffectDialogFragment.createDialog();
                dialog.show(getFragmentManager(), "add_effect_dialog");
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getCharacter() != null) {
            updateViews();
        }
        // when the activity resumes, the characterLoad stuff will run, causing updates
    }

    static class EffectBarRowViewHolder extends BindableComponentViewHolder<CharacterEffect, AbstractSheetFragment, EffectsBarAdapter> {
        private final TextView name;

        public EffectBarRowViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bind(final AbstractSheetFragment context, EffectsBarAdapter adapter, final CharacterEffect effect) {
            name.setText(effect.getName());
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewEffectDialogFragment dialog = ViewEffectDialogFragment.createDialog(effect);
                    dialog.show(context.getFragmentManager(), "effect_dialog");
                }
            });
        }
    }

    static class EffectsBarAdapter extends RecyclerView.Adapter<EffectBarRowViewHolder> {
        private final AbstractSheetFragment context;
        private List<CharacterEffect> effects;

        EffectsBarAdapter(AbstractSheetFragment context, Character character) {
            effects = character.getEffects();
            this.context = context;
        }

        public void reloadList(Character character) {
            effects = character.getEffects();
            notifyDataSetChanged();
        }

        @Override
        public EffectBarRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.effect_bar_item, parent, false);
            return new EffectBarRowViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(EffectBarRowViewHolder holder, int position) {
            final CharacterEffect effect = effects.get(position);
            holder.bind(context, this, effect);
        }

        @Override
        public int getItemCount() {
            return effects.size();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = DndCharacterApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
