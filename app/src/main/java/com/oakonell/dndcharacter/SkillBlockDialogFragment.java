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
import com.oakonell.dndcharacter.model.SkillBlock;
import com.oakonell.dndcharacter.model.SkillType;
import com.oakonell.dndcharacter.views.RowWithSourceAdapter;

import java.util.List;

/**
 * Created by Rob on 11/7/2015.
 */
public class SkillBlockDialogFragment extends RollableDialogFragment {
    private SkillBlock skillBlock;
    private TextView statLabel;
    private TextView skillLabel;
    private TextView statModLabel;
    private TextView statMod;
    private TextView proficiency;
    private View proficiencyLayout;
    private TextView total;
    private ListView listView;


    private SkillSourceAdapter adapter;
    private SkillType type;

    public static SkillBlockDialogFragment create(SkillBlock block) {
        SkillBlockDialogFragment frag = new SkillBlockDialogFragment();
        int typeIndex = block.getType().ordinal();
        Bundle args = new Bundle();
        args.putInt("type", typeIndex);
        frag.setArguments(args);

        return frag;
    }


    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.skill_dialog, container);
        superCreateView(view);

        statLabel = (TextView) view.findViewById(R.id.stat_label);
        skillLabel = (TextView) view.findViewById(R.id.skill_label);
        statModLabel = (TextView) view.findViewById(R.id.stat_mod_lbl);
        statMod = (TextView) view.findViewById(R.id.stat_mod);
        proficiency = (TextView) view.findViewById(R.id.proficiency);
        proficiencyLayout = view.findViewById(R.id.proficiency_layout);

        total = (TextView) view.findViewById(R.id.total);
        listView = (ListView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        int typeIndex = getArguments().getInt("type");
        type = SkillType.values()[typeIndex];
        skillBlock = character.getSkillBlock(type);

        updateView(character);

        RowWithSourceAdapter.ListRetriever<Character.ProficientWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ProficientWithSource>() {
            @Override
            public List<Character.ProficientWithSource> getList(Character character) {
                return character.getSkillBlock(type).getProficiencies();
            }
        };

        adapter = new SkillSourceAdapter(this, listRetriever);
        listView.setAdapter(adapter);

    }


    private void updateView(Character character) {
        setModifier(skillBlock.getBonus());

        List<Character.ProficientWithSource> proficiencies = skillBlock.getProficiencies();

        if (proficiencies.isEmpty()) {
            proficiencyLayout.setVisibility(View.GONE);
        } else {
            proficiencyLayout.setVisibility(View.VISIBLE);
            proficiency.setText(skillBlock.getCharacter().getProficiency() + "");
        }

        statModLabel.setText(skillBlock.getType().getStatType().toString() + " modifier");
        statMod.setText(skillBlock.getStatModifier() + "");
        statLabel.setText(skillBlock.getType().getStatType().toString());
        skillLabel.setText(skillBlock.getType().toString());
        total.setText(skillBlock.getBonus() + "");
    }

    @Override
    public void onCharacterChanged(Character character) {
        int typeIndex = getArguments().getInt("type");
        SkillType type = SkillType.values()[typeIndex];
        skillBlock = character.getSkillBlock(type);

        updateView(character);

        adapter.reloadList(character);
    }


    public static class SkillSourceAdapter extends RowWithSourceAdapter<Character.ProficientWithSource> {
        SkillSourceAdapter(SkillBlockDialogFragment fragment, ListRetriever<Character.ProficientWithSource> listRetriever) {
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
