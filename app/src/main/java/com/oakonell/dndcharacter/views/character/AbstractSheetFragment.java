package com.oakonell.dndcharacter.views.character;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.oakonell.dndcharacter.DndCharacterApp;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.character.classes.CharacterLevelsDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.SelectEffectDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.ViewEffectDialogFragment;
import com.oakonell.dndcharacter.views.character.race.ApplyRaceDialogFragment;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import hugo.weaving.DebugLog;

/**
 * Created by Rob on 10/27/2015.
 */
public abstract class AbstractSheetFragment extends Fragment implements OnCharacterLoaded, CharacterChangedListener {
    public static final String ADD_EFFECT_DIALOG = "add_effect_dialog";
    private static final String NAME_FRAG = "name_frag";
    private TextView classes;
    private TextView race;
    private TextView background;
    private TextView character_name_read_only;
    private RecyclerView effectList;

    private EffectsBarAdapter effectListAdapter;
    private ViewGroup effect_group;
    private ImageButton add_effect;

    @DebugLog
    @Override
    public final void onCharacterChanged(@NonNull Character character) {
        updateViews();
    }

    public void updateViews() {
        updateViews(getView());
    }

    @NonNull
    public CharacterActivity getMainActivity() {
        return (CharacterActivity) getActivity();
    }

    @Nullable
    public Character getCharacter() {
        return getMainActivity().getCharacter();
    }

    @DebugLog
    protected void updateViews(View rootView) {
        Log.d("SheetFragment", "updateViews");
        Character character = getCharacter();
        if (character == null) {
            character_name_read_only.setText("");

            race.setText("");
            background.setText("");
            classes.setText("");
            return;
        }
        String name = character.getName();
        if (name == null || name.trim().length() == 0) {
            name = getString(R.string.unnamed_character);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(name);
        if (character_name_read_only == null) {
            // odd case of the activity in a bad state..?
            return;
        }
        character_name_read_only.setText(name);

        final String displayRaceName = character.getDisplayRaceName();
        if (displayRaceName == null) {
            race.setText(R.string.no_race);
        } else {
            race.setText(displayRaceName);
        }
        final String backgroundName = character.getBackgroundName();
        if (backgroundName == null) {
            background.setText(R.string.no_background);
        } else {
            background.setText(backgroundName);
        }
        final String classesString = character.getClassesString();
        if (classesString == null) {
            classes.setText(R.string.no_class);
        } else {
            classes.setText(classesString);
        }

        if (effectListAdapter != null) {
            effectListAdapter.reloadList(character);
            effect_group.setVisibility(effectListAdapter.effects.size() > 0 ? View.VISIBLE : View.GONE);
        } else {
            effect_group.setVisibility(View.GONE);
        }

    }

    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        Log.d("SheetFragment", "onCreateView");
        View view = onCreateTheView(inflater, container, savedInstanceState);
        getMainActivity().addCharacterLoadLister(this);
        if (savedInstanceState != null) {
            SelectEffectDialogFragment dpf = (SelectEffectDialogFragment) getActivity().getSupportFragmentManager()
                    .findFragmentByTag(ADD_EFFECT_DIALOG);
            if (dpf != null) {
                dpf.setListener(new SelectEffectDialogFragment.AddEffectToCharacterListener(getMainActivity()));
            }
        }
        return view;
    }

    protected abstract View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected void superCreateViews(@NonNull View rootView) {
        character_name_read_only = (TextView) rootView.findViewById(R.id.character_name_read);
        classes = (TextView) rootView.findViewById(R.id.classes);
        race = (TextView) rootView.findViewById(R.id.race);
        background = (TextView) rootView.findViewById(R.id.background);

        effectList = (RecyclerView) rootView.findViewById(R.id.effect_list);
        effect_group = (ViewGroup) rootView.findViewById(R.id.effect_group);
        add_effect = (ImageButton) rootView.findViewById(R.id.add_effect);

    }


    @DebugLog
    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        Log.d("SheetFragment", "onCharacterLoaded");
        if (getActivity() == null) return;
        character_name_read_only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameDialog dialog = NameDialog.create();
                dialog.show(getFragmentManager(), NAME_FRAG);
            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ApplyBackgroundDialogFragment dialog = ApplyBackgroundDialogFragment.createDialog();
                    dialog.show(getFragmentManager(), CharacterActivity.BACKGROUND_FRAG);
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
                    dialog.show(getFragmentManager(), CharacterActivity.RACE_FRAG);
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
        effectList.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST);
        effectList.addItemDecoration(itemDecoration);

        effect_group.setVisibility(effectListAdapter.effects.size() > 0 ? View.VISIBLE : View.GONE);

        add_effect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectEffectDialogFragment dialog = SelectEffectDialogFragment.createDialog(new SelectEffectDialogFragment.AddEffectToCharacterListener(getMainActivity()));
                dialog.show(getFragmentManager(), ADD_EFFECT_DIALOG);
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
        @NonNull
        private final TextView name;

        public EffectBarRowViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bind(@NonNull final AbstractSheetFragment context, EffectsBarAdapter adapter, @NonNull final CharacterEffect effect) {
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

        EffectsBarAdapter(AbstractSheetFragment context, @NonNull Character character) {
            effects = character.getEffects();
            this.context = context;
        }

        public void reloadList(@NonNull Character character) {
            effects = character.getEffects();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public EffectBarRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.effect_bar_item, parent, false);
            return new EffectBarRowViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull EffectBarRowViewHolder holder, int position) {
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
