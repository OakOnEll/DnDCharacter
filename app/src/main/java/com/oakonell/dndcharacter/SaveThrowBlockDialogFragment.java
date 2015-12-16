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
import com.oakonell.dndcharacter.views.RowWithSourceAdapter;

import java.util.List;

/**
 * Created by Rob on 11/7/2015.
 */
public class SaveThrowBlockDialogFragment extends RollableDialogFragment {
    private StatBlock statBlock;

    public static SaveThrowBlockDialogFragment create(MainActivity activity, StatBlock block) {
        SaveThrowBlockDialogFragment frag = new SaveThrowBlockDialogFragment();
        frag.setMainActivity(activity);
        frag.setStatBlock(block);
        return frag;
    }

    private void setStatBlock(StatBlock statBlock) {
        this.statBlock = statBlock;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.save_throw_dialog, container);
        superCreateView(view);
        setModifier(statBlock.getSaveModifier());

        TextView statLabel = (TextView) view.findViewById(R.id.stat_label);
        TextView statModLabel = (TextView) view.findViewById(R.id.stat_mod_lbl);
        TextView statMod = (TextView) view.findViewById(R.id.stat_mod);
        TextView proficiency = (TextView) view.findViewById(R.id.proficiency);
        View proficiencyLayout = view.findViewById(R.id.proficiency_layout);


        TextView total = (TextView) view.findViewById(R.id.modifier);
        ListView listView = (ListView) view.findViewById(R.id.list);

        List<Character.ProficientWithSource> proficiencies = statBlock.getSaveProficiencies();

        if (proficiencies.isEmpty()) {
            proficiencyLayout.setVisibility(View.GONE);
        } else {
            proficiencyLayout.setVisibility(View.VISIBLE);
            proficiency.setText(statBlock.getCharacter().getProficiency() + "");
        }

        statModLabel.setText(statBlock.getType().toString() + " modifier");
        statMod.setText(statBlock.getModifier() + "");
        statLabel.setText(statBlock.getType().toString());
        total.setText(statBlock.getSaveModifier() + "");

        RowWithSourceAdapter.ListRetriever<Character.ProficientWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ProficientWithSource>() {
            @Override
            public List<Character.ProficientWithSource> getList(Character character) {
                return statBlock.getSaveProficiencies();
            }
        };

        SaveThrowSourcesAdapter adapter = new SaveThrowSourcesAdapter(this, listRetriever);
        listView.setAdapter(adapter);

        return view;
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
