package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.SkillBlock;

import java.util.List;

/**
 * Created by Rob on 11/7/2015.
 */
public class SkillBlockDialogFragment extends RollableDialogFragment {
    private SkillBlock skillBlock;

    public static SkillBlockDialogFragment create(MainActivity activity, SkillBlock block) {
        SkillBlockDialogFragment frag = new SkillBlockDialogFragment();
        frag.setMainActivity(activity);
        frag.setSkillBlock(block);
        return frag;
    }

    private void setSkillBlock(SkillBlock skillBlock) {
        this.skillBlock = skillBlock;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.skill_dialog, container);
        superCreateView(view);
        setModifier(skillBlock.getBonus());

        TextView statLabel = (TextView) view.findViewById(R.id.stat_label);
        TextView skillLabel = (TextView) view.findViewById(R.id.skill_label);
        TextView statModLabel = (TextView) view.findViewById(R.id.stat_mod_lbl);
        TextView statMod = (TextView) view.findViewById(R.id.stat_mod);
        TextView proficiency = (TextView) view.findViewById(R.id.proficiency);
        View proficiencyLayout = view.findViewById(R.id.proficiency_layout);


        TextView total = (TextView) view.findViewById(R.id.total);
        ListView listView = (ListView) view.findViewById(R.id.list);

        List<Character.ProficientAndReason> proficiencies = skillBlock.getProficiencies();

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


        ListAdapter adapter = new ArrayAdapter<Character.ProficientAndReason>(getActivity(), android.R.layout.simple_list_item_1, proficiencies);
        listView.setAdapter(adapter);

        return view;
    }

}
