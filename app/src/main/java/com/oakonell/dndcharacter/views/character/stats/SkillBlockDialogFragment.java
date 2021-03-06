package com.oakonell.dndcharacter.views.character.stats;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.Proficient;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.stats.SkillBlock;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.RollableDialogFragment;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/7/2015.
 */
public class SkillBlockDialogFragment extends RollableDialogFragment {
    public static final String TYPE = "type";
    private TextView statLabel;
    private TextView statModLabel;
    private TextView statMod;
    private TextView proficiency;
    private View proficiencyLayout;
    private TextView total;
    private RecyclerView listView;

    private SkillBlock skillBlock;
    private SkillType type;

    private SkillSourceAdapter adapter;

    @NonNull
    public static SkillBlockDialogFragment create(@NonNull SkillBlock block, boolean isForCompanion) {
        SkillBlockDialogFragment frag = new SkillBlockDialogFragment();
        int typeIndex = block.getType().ordinal();
        Bundle args = new Bundle();
        args.putBoolean(COMPANION_ARG, isForCompanion);
        args.putInt(TYPE, typeIndex);
        frag.setArguments(args);

        return frag;
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.skill_dialog, container);
        superCreateView(view, savedInstanceState);

        statLabel = (TextView) view.findViewById(R.id.stat_label);
        statModLabel = (TextView) view.findViewById(R.id.stat_mod_lbl);
        statMod = (TextView) view.findViewById(R.id.stat_mod);
        proficiency = (TextView) view.findViewById(R.id.proficiency);
        proficiencyLayout = view.findViewById(R.id.proficiency_layout);

        total = (TextView) view.findViewById(R.id.total);
        listView = (RecyclerView) view.findViewById(R.id.list);

        int typeIndex = getArguments().getInt(TYPE);
        type = SkillType.values()[typeIndex];

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(type.getStringResId());
        //return getString(R.string.skill_proficiency);
    }


    @Override
    public void onCharacterLoaded(@NonNull Character topCharacter) {
        super.onCharacterLoaded(topCharacter);
        AbstractCharacter character = getDisplayedCharacter();

        skillBlock = character.getSkillBlock(type);

        updateView(character);

        RowWithSourceAdapter.ListRetriever<Character.ProficientWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ProficientWithSource>() {
            @NonNull
            @Override
            public List<Character.ProficientWithSource> getList(@NonNull AbstractCharacter character) {
                return character.getSkillBlock(type).getProficiencies();
            }
        };

        adapter = new SkillSourceAdapter(this, listRetriever, isForCompanion());
        listView.setAdapter(adapter);

        listView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);
    }

    @NonNull
    @Override
    public Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.DICE_ROLL));
        filter.add(new FeatureContextArgument(FeatureContext.SKILL_ROLL, type.name()));
        filter.add(new FeatureContextArgument(FeatureContext.SKILL_ROLL, type.getStatType().name()));
        return filter;
    }

    @Override
    public FeatureContextArgument getNoteContext() {
        return new FeatureContextArgument(FeatureContext.SKILL_ROLL, type.getStatType().name());
    }

    private void updateView(AbstractCharacter character) {
        setModifier(skillBlock.getBonus());

        List<Character.ProficientWithSource> proficiencies = skillBlock.getProficiencies();

        if (proficiencies.isEmpty()) {
            proficiencyLayout.setVisibility(View.GONE);
        } else {
            proficiencyLayout.setVisibility(View.VISIBLE);
            proficiency.setText(NumberUtils.formatNumber(skillBlock.getCharacter().getProficiency()));
        }

        statModLabel.setText(getString(R.string.statname_modifier_label, getString(skillBlock.getType().getStatType().getStringResId())));
        statMod.setText(NumberUtils.formatNumber(skillBlock.getStatModifier()));
        statLabel.setText(skillBlock.getType().getStatType().getStringResId());
        //getDialog().setTitle(skillBlock.getType().getStringResId());
        total.setText(NumberUtils.formatNumber(skillBlock.getBonus()));
    }

    @Override
    public void onCharacterChanged(@NonNull Character topCharacter) {
        super.onCharacterChanged(topCharacter);
        AbstractCharacter character = getDisplayedCharacter();

        int typeIndex = getArguments().getInt(TYPE);
        SkillType type = SkillType.values()[typeIndex];
        skillBlock = character.getSkillBlock(type);

        updateView(character);

        adapter.reloadList(character);
    }

    public static class SkillProfWithSourceViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.ProficientWithSource> {

        public SkillProfWithSourceViewHolder(@NonNull View view) {
            super(view);
        }

        @Override
        public void bind(@NonNull CharacterActivity activity, @NonNull RowWithSourceAdapter<Character.ProficientWithSource, RowWithSourceAdapter.WithSourceViewHolder<Character.ProficientWithSource>> adapter, @NonNull Character.ProficientWithSource item) {
            super.bind(activity, adapter, item);
            Proficient value = item.getProficient();
            this.value.setText(activity.getString(value.getStringResId()));
            final ComponentSource source = item.getSource();
            if (source == null) {
                // a base stat
                this.source.setText(R.string.base_stat);
            } else {
                this.source.setText(source.getSourceString(activity.getResources()));
            }
        }
    }

    public static class SkillSourceAdapter extends RowWithSourceAdapter<Character.ProficientWithSource, SkillProfWithSourceViewHolder> {
        SkillSourceAdapter(@NonNull SkillBlockDialogFragment fragment, @NonNull ListRetriever<Character.ProficientWithSource> listRetriever, boolean isForCompanion) {
            super(fragment.getMainActivity(), listRetriever, isForCompanion);
        }


        @NonNull
        @Override
        protected SkillProfWithSourceViewHolder newViewHolder(@NonNull View view) {
            return new SkillProfWithSourceViewHolder(view);
        }
    }


}
