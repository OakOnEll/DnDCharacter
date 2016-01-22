package com.oakonell.dndcharacter.views.character.spell;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;

import java.util.List;

/**
 * Created by Rob on 1/19/2016.
 */
public class SpellsFragment extends AbstractSheetFragment {
    private View rootView;
    private RecyclerView spell_level_list;
    private SpellLevelsAdapter spellsAdapter;

    @Override
    protected View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.spell_sheet, container, false);

        spell_level_list = (RecyclerView) rootView.findViewById(R.id.spell_level_list);

        superCreateViews(rootView);
        return rootView;
    }


    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);


        spellsAdapter = new SpellLevelsAdapter(this, character);
        spell_level_list.setAdapter(spellsAdapter);
        spell_level_list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        spell_level_list.setHasFixedSize(false);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        spell_level_list.addItemDecoration(itemDecoration);

    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        spellsAdapter.reloadList(getCharacter());
    }

    public abstract static class AbstractSpellLevelViewHolder extends BindableComponentViewHolder<Character.SpellLevelInfo, SpellsFragment, SpellLevelsAdapter> {
        private final RecyclerView list;
        private ImageButton add_spell;
        private AbstractSpellAdapter spellAdapter;

        public AbstractSpellLevelViewHolder(View itemView) {
            super(itemView);
            list = (RecyclerView) itemView.findViewById(R.id.spells);
            add_spell = (ImageButton) itemView.findViewById(R.id.add_spell);
        }

        @Override
        public void bind(final SpellsFragment context, final SpellLevelsAdapter adapter, final Character.SpellLevelInfo info) {
            add_spell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context.getActivity(), "Adding a spell", Toast.LENGTH_SHORT).show();
                }
            });
            if (spellAdapter == null) {
                spellAdapter = newAdapter(context, info);
                list.setAdapter(spellAdapter);
                list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(context.getActivity(), LinearLayoutManager.VERTICAL, false));
                list.setHasFixedSize(false);

                DividerItemDecoration itemDecoration = new DividerItemDecoration(context.getActivity(), DividerItemDecoration.VERTICAL_LIST);
                list.addItemDecoration(itemDecoration);
            } else {
                spellAdapter.reloadList(info);
            }
        }

        protected abstract AbstractSpellAdapter newAdapter(SpellsFragment context, Character.SpellLevelInfo info);
    }

    public static class CantripsViewHolder extends AbstractSpellLevelViewHolder {
        public CantripsViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected AbstractSpellAdapter newAdapter(SpellsFragment context, Character.SpellLevelInfo info) {
            return new CantripAdapter(context, info);
        }

//        @Override
//        public void bind(SpellsFragment context, SpellLevelsAdapter adapter, Character.SpellLevelInfo info) {
//
//        }


    }

    public static class SpellsViewHolder extends AbstractSpellLevelViewHolder {
        private final TextView available_slots;
        private final TextView total_slots;
        private final TextView level;

        public SpellsViewHolder(View itemView) {
            super(itemView);
            level = (TextView) itemView.findViewById(R.id.level);
            available_slots = (TextView) itemView.findViewById(R.id.available_slots);
            total_slots = (TextView) itemView.findViewById(R.id.total_slots);
        }

        @Override
        public void bind(SpellsFragment context, SpellLevelsAdapter adapter, Character.SpellLevelInfo info) {
            super.bind(context, adapter, info);
            available_slots.setText(info.getSlotsAvailable() + "");
            total_slots.setText(info.getMaxSlots() + "");
            level.setText(info.getLevel() + "");
        }

        @Override
        protected AbstractSpellAdapter newAdapter(SpellsFragment context, Character.SpellLevelInfo info) {
            return new SpellAdapter(context, info);
        }


    }


    public static class SpellLevelsAdapter extends RecyclerView.Adapter<AbstractSpellLevelViewHolder> {
        private final SpellsFragment context;
        private List<Character.SpellLevelInfo> spellLevels;

        public SpellLevelsAdapter(SpellsFragment spellsFragment, Character character) {
            this.context = spellsFragment;
            this.spellLevels = character.getSpellInfos();
        }

        public void reloadList(Character character) {
            this.spellLevels = character.getSpellInfos();
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            return 1;
        }

        @Override
        public AbstractSpellLevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_char_cantrips, parent, false);
                return new CantripsViewHolder(newView);
            }
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_char_spells, parent, false);
            return new SpellsViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(AbstractSpellLevelViewHolder holder, int position) {
            final Character.SpellLevelInfo levelInfo = spellLevels.get(position);
            holder.bind(context, this, levelInfo);
        }

        @Override
        public int getItemCount() {
            return spellLevels.size();
        }
    }


    public static class AbstractSpellViewHolder extends BindableComponentViewHolder<Character.SpellInfo, SpellsFragment, AbstractSpellAdapter<?>> {
        private final TextView name;
        private final TextView school;
        private final TextView source;

        public AbstractSpellViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            source = (TextView) itemView.findViewById(R.id.source);
            school = (TextView) itemView.findViewById(R.id.school);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // launch spell cast dialog
                }
            });

        }

        @Override
        public void bind(SpellsFragment context, AbstractSpellAdapter adapter, Character.SpellInfo info) {
            name.setText(info.getName());
            school.setText(info.getSchool());
            source.setText("");
        }
    }

    public static class SpellViewHolder extends AbstractSpellViewHolder {
        CheckBox prepared;

        public SpellViewHolder(View itemView) {
            super(itemView);
            prepared = (CheckBox) itemView.findViewById(R.id.prepared);
        }

        @Override
        public void bind(SpellsFragment context, AbstractSpellAdapter adapter, Character.SpellInfo info) {
            super.bind(context, adapter, info);
            if (!info.isPreparable()) {
                prepared.setVisibility(View.INVISIBLE);
            } else {
                prepared.setVisibility(View.VISIBLE);
            }
            prepared.setChecked(info.isPrepared());
        }
    }

    public static class CantripViewHolder extends AbstractSpellViewHolder {
        public CantripViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class CantripAdapter extends AbstractSpellAdapter<CantripViewHolder> {
        CantripAdapter(SpellsFragment context, Character.SpellLevelInfo spellLevelInfo) {
            super(context, spellLevelInfo);
        }

        @Override
        public AbstractSpellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_char_cantrip_row, parent, false);
            return new CantripViewHolder(newView);
        }
    }

    public static class SpellAdapter extends AbstractSpellAdapter<SpellViewHolder> {
        SpellAdapter(SpellsFragment context, Character.SpellLevelInfo spellLevelInfo) {
            super(context, spellLevelInfo);
        }

        @Override
        public SpellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_char_row, parent, false);
            return new SpellViewHolder(newView);
        }
    }

    public abstract static class AbstractSpellAdapter<V extends AbstractSpellViewHolder> extends RecyclerView.Adapter<AbstractSpellViewHolder> {
        private final SpellsFragment context;
        List<Character.SpellInfo> spellInfos;

        AbstractSpellAdapter(SpellsFragment context, Character.SpellLevelInfo spellLevelInfo) {
            this.context = context;
            this.spellInfos = spellLevelInfo.getSpellInfos();
        }

        public void reloadList(Character.SpellLevelInfo spellLevelInfo) {
            this.spellInfos = spellLevelInfo.getSpellInfos();
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(AbstractSpellViewHolder holder, int position) {
            Character.SpellInfo spellInfo = spellInfos.get(position);
            holder.bind(context, this, spellInfo);
        }

        @Override
        public int getItemCount() {
            return spellInfos.size();
        }
    }


}
