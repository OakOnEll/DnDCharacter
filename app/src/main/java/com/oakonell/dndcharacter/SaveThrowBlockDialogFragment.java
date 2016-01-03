package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.Proficient;
import com.oakonell.dndcharacter.model.StatBlock;
import com.oakonell.dndcharacter.views.FeatureContext;
import com.oakonell.dndcharacter.views.RowWithSourceAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/7/2015.
 */
public class SaveThrowBlockDialogFragment extends AbstractStatBlockBasedDialog {
    private ListView listView;

    private TextView statModLabel;
    private TextView statMod;
    private TextView proficiency;
    private View proficiencyLayout;

    private TextView total;

    private SaveThrowSourcesAdapter adapter;

    public static SaveThrowBlockDialogFragment create(StatBlock block) {
        SaveThrowBlockDialogFragment frag = new SaveThrowBlockDialogFragment();
        frag.setStatTypeArg(block);
        return frag;
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.save_throw_dialog, container);
        superCreateView(view, savedInstanceState);

        statModLabel = (TextView) view.findViewById(R.id.stat_mod_lbl);
        statMod = (TextView) view.findViewById(R.id.stat_mod);
        proficiency = (TextView) view.findViewById(R.id.proficiency);
        proficiencyLayout = view.findViewById(R.id.proficiency_layout);

        total = (TextView) view.findViewById(R.id.modifier);
        listView = (ListView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    protected String getTitle() {
        return "Saving Throw";
    }


    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        StatBlock statBlock = setStatBlock(character);


        updateView(statBlock);

        RowWithSourceAdapter.ListRetriever<Character.ProficientWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ProficientWithSource>() {
            @Override
            public List<Character.ProficientWithSource> getList(Character character) {
                return getStatBlock().getSaveProficiencies();
            }
        };

        adapter = new SaveThrowSourcesAdapter(this, listRetriever);
        listView.setAdapter(adapter);

    }

    @Override
    protected Set<FeatureContext> getContextFilter() {
        Set<FeatureContext> filter = new HashSet<>();
        filter.add(FeatureContext.DICE_ROLL);
        filter.add(FeatureContext.SAVING_THROW);
        return filter;
    }

    private void updateView(StatBlock statBlock) {
        setModifier(statBlock.getSaveModifier());

        List<Character.ProficientWithSource> proficiencies = statBlock.getSaveProficiencies();

        if (proficiencies.isEmpty()) {
            proficiencyLayout.setVisibility(View.GONE);
        } else {
            proficiencyLayout.setVisibility(View.VISIBLE);
            proficiency.setText(statBlock.getCharacter().getProficiency() + "");
        }

        statModLabel.setText(statBlock.getType().toString() + " modifier");
        statMod.setText(statBlock.getModifier() + "");
        getDialog().setTitle(statBlock.getType().toString() + " Saving Throw");
        total.setText(statBlock.getSaveModifier() + "");
    }

    @Override
    public void onCharacterChanged(Character character) {
        super.onCharacterChanged(character);

        StatBlock statBlock = setStatBlock(character);

        updateView(statBlock);
        adapter.reloadList(character);
    }

    public static class SaveThrowSourcesAdapter extends RowWithSourceAdapter<Character.ProficientWithSource> {
        SaveThrowSourcesAdapter(SaveThrowBlockDialogFragment fragment, ListRetriever<Character.ProficientWithSource> listRetriever) {
            super(fragment.getMainActivity(), listRetriever);
        }

        @Override
        protected void bindView(View view, WithSourceViewHolder<Character.ProficientWithSource> holder, Character.ProficientWithSource item) {
            Proficient value = item.getProficient();
            holder.value.setText(value.toString());
            final BaseCharacterComponent source = item.getSource();
            if (source == null) {
                // a base stat
                holder.source.setText("Base Stat");
            } else {
                holder.source.setText(source.getSourceString());
            }
        }

    }


}
