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
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.Proficient;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/7/2015.
 */
public class SaveThrowBlockDialogFragment extends AbstractStatBlockBasedDialog {
    private RecyclerView listView;

    private TextView statModLabel;
    private TextView statMod;
    private TextView proficiency;
    private View proficiencyLayout;

    private TextView total;

    private SaveThrowSourcesAdapter adapter;

    @NonNull
    public static SaveThrowBlockDialogFragment create(@NonNull StatBlock block) {
        SaveThrowBlockDialogFragment frag = new SaveThrowBlockDialogFragment();
        frag.setStatTypeArg(block);
        return frag;
    }

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.save_throw_dialog, container);
        superCreateView(view, savedInstanceState);

        statModLabel = (TextView) view.findViewById(R.id.stat_mod_lbl);
        statMod = (TextView) view.findViewById(R.id.stat_mod);
        proficiency = (TextView) view.findViewById(R.id.proficiency);
        proficiencyLayout = view.findViewById(R.id.proficiency_layout);

        total = (TextView) view.findViewById(R.id.modifier);
        listView = (RecyclerView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.saving_throw);
    }


    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
        StatBlock statBlock = setStatBlock(character);


        updateView(statBlock);

        RowWithSourceAdapter.ListRetriever<Character.ProficientWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ProficientWithSource>() {
            @NonNull
            @Override
            public List<Character.ProficientWithSource> getList(Character character) {
                return getStatBlock().getSaveProficiencies();
            }
        };

        adapter = new SaveThrowSourcesAdapter(this, listRetriever);
        listView.setAdapter(adapter);

        listView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.DICE_ROLL));
        filter.add(new FeatureContextArgument(FeatureContext.SAVING_THROW, getType().name()));
        return filter;
    }

    private void updateView(@NonNull StatBlock statBlock) {
        setModifier(statBlock.getSaveModifier());

        List<Character.ProficientWithSource> proficiencies = statBlock.getSaveProficiencies();

        if (proficiencies.isEmpty()) {
            proficiencyLayout.setVisibility(View.GONE);
        } else {
            proficiencyLayout.setVisibility(View.VISIBLE);
            proficiency.setText(NumberUtils.formatNumber(statBlock.getCharacter().getProficiency()));
        }

        statModLabel.setText(getString(R.string.statname_modifier_label, getString(statBlock.getType().getStringResId())));
        statMod.setText(NumberUtils.formatNumber(statBlock.getModifier()));
        getDialog().setTitle(getString(R.string.statname_saving_throw_title, getString(statBlock.getType().getStringResId())));
        total.setText(NumberUtils.formatNumber(statBlock.getSaveModifier()));
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);

        StatBlock statBlock = setStatBlock(character);

        updateView(statBlock);
        adapter.reloadList(character);
    }

    public static class SaveThrowSourceViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.ProficientWithSource> {
        public SaveThrowSourceViewHolder(@NonNull View view) {
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

    public static class SaveThrowSourcesAdapter extends RowWithSourceAdapter<Character.ProficientWithSource, SaveThrowSourceViewHolder> {
        SaveThrowSourcesAdapter(@NonNull SaveThrowBlockDialogFragment fragment, @NonNull ListRetriever<Character.ProficientWithSource> listRetriever) {
            super(fragment.getMainActivity(), listRetriever);
        }


        @NonNull
        @Override
        protected SaveThrowSourceViewHolder newViewHolder(@NonNull View view) {
            return new SaveThrowSourceViewHolder(view);
        }
    }


}
