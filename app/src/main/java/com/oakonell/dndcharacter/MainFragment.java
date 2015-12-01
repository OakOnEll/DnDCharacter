package com.oakonell.dndcharacter;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.views.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.HitPointDiaogFragment;
import com.oakonell.dndcharacter.views.SavingThrowBlockView;
import com.oakonell.dndcharacter.views.SkillBlockView;
import com.oakonell.dndcharacter.views.StatBlockView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 10/22/2015.
 */
public class MainFragment extends AbstractSheetFragment {

    Map<StatType, StatBlockView> statViewsByType = new HashMap<StatType, StatBlockView>();
    Map<StatType, SavingThrowBlockView> saveThrowViewsByType = new HashMap<StatType, SavingThrowBlockView>();
    Map<SkillType, SkillBlockView> skillViewsByType = new HashMap<SkillType, SkillBlockView>();

    TextView speed;
    TextView ac;
    TextView hp;
    View temp_hp_layout;
    TextView tempHp;
    TextView hitDice;
    TextView proficiency;
    private TextView initiative;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_sheet, container, false);

        superCreateViews(rootView);
        hp = (TextView) rootView.findViewById(R.id.hp);
        tempHp = (TextView) rootView.findViewById(R.id.temp_hp);
        temp_hp_layout = rootView.findViewById(R.id.temp_hp_layout);

        hitDice = (TextView) rootView.findViewById(R.id.hit_dice);
        proficiency = (TextView) rootView.findViewById(R.id.proficiency);

        speed = (TextView) rootView.findViewById(R.id.speed);
        ac = (TextView) rootView.findViewById(R.id.ac);
        initiative = (TextView) rootView.findViewById(R.id.initiative);
        final View.OnClickListener onClickHp = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = MainFragment.this.getChildFragmentManager();
                HitPointDiaogFragment hpFragment = new HitPointDiaogFragment();
                hpFragment.setCharacter(character);
                hpFragment.setFragment(MainFragment.this);
                hpFragment.show(fm, "fragment_hp");
            }
        };
        rootView.findViewById(R.id.hp_layout).setOnClickListener(onClickHp);
        rootView.findViewById(R.id.temp_hp_layout).setOnClickListener(onClickHp);

        statViewsByType.put(StatType.STRENGTH, (StatBlockView) rootView.findViewById(R.id.strength_stat));
        statViewsByType.put(StatType.DEXTERITY, (StatBlockView) rootView.findViewById(R.id.dexterity_stat));
        statViewsByType.put(StatType.CONSTITUTION, (StatBlockView) rootView.findViewById(R.id.constitution_stat));
        statViewsByType.put(StatType.INTELLIGENCE, (StatBlockView) rootView.findViewById(R.id.intelligence_stat));
        statViewsByType.put(StatType.WISDOM, (StatBlockView) rootView.findViewById(R.id.wisdom_stat));
        statViewsByType.put(StatType.CHARISMA, (StatBlockView) rootView.findViewById(R.id.charisma_stat));

        saveThrowViewsByType.put(StatType.STRENGTH, (SavingThrowBlockView) rootView.findViewById(R.id.strength_save));
        saveThrowViewsByType.put(StatType.DEXTERITY, (SavingThrowBlockView) rootView.findViewById(R.id.dexterity_save));
        saveThrowViewsByType.put(StatType.CONSTITUTION, (SavingThrowBlockView) rootView.findViewById(R.id.constitution_save));
        saveThrowViewsByType.put(StatType.INTELLIGENCE, (SavingThrowBlockView) rootView.findViewById(R.id.intelligence_save));
        saveThrowViewsByType.put(StatType.WISDOM, (SavingThrowBlockView) rootView.findViewById(R.id.wisdom_save));
        saveThrowViewsByType.put(StatType.CHARISMA, (SavingThrowBlockView) rootView.findViewById(R.id.charisma_save));


        skillViewsByType.put(SkillType.ACROBATICS, (SkillBlockView) rootView.findViewById(R.id.acrobatics_skill));
        skillViewsByType.put(SkillType.ANIMAL_HANDLING, (SkillBlockView) rootView.findViewById(R.id.animal_handling_skill));
        skillViewsByType.put(SkillType.ARCANA, (SkillBlockView) rootView.findViewById(R.id.arcana_skill));
        skillViewsByType.put(SkillType.ATHLETICS, (SkillBlockView) rootView.findViewById(R.id.athletics_skill));
        skillViewsByType.put(SkillType.DECEPTION, (SkillBlockView) rootView.findViewById(R.id.deception_skill));
        skillViewsByType.put(SkillType.HISTORY, (SkillBlockView) rootView.findViewById(R.id.history_skill));
        skillViewsByType.put(SkillType.INSIGHT, (SkillBlockView) rootView.findViewById(R.id.insight_skill));
        skillViewsByType.put(SkillType.INTIMIDATION, (SkillBlockView) rootView.findViewById(R.id.intimidation_skill));
        skillViewsByType.put(SkillType.INVESTIGATION, (SkillBlockView) rootView.findViewById(R.id.investigation_skill));
        skillViewsByType.put(SkillType.MEDICINE, (SkillBlockView) rootView.findViewById(R.id.medicine_skill));
        skillViewsByType.put(SkillType.NATURE, (SkillBlockView) rootView.findViewById(R.id.nature_skill));
        skillViewsByType.put(SkillType.PERCEPTION, (SkillBlockView) rootView.findViewById(R.id.perception_skill));
        skillViewsByType.put(SkillType.PERFORMANCE, (SkillBlockView) rootView.findViewById(R.id.performance_skill));
        skillViewsByType.put(SkillType.PERSUASION, (SkillBlockView) rootView.findViewById(R.id.persuasion_skill));
        skillViewsByType.put(SkillType.RELIGION, (SkillBlockView) rootView.findViewById(R.id.religion_skill));
        skillViewsByType.put(SkillType.SLEIGHT_OF_HAND, (SkillBlockView) rootView.findViewById(R.id.sleight_of_hand_skill));
        skillViewsByType.put(SkillType.STEALTH, (SkillBlockView) rootView.findViewById(R.id.stealth_skill));
        skillViewsByType.put(SkillType.SURVIVAL, (SkillBlockView) rootView.findViewById(R.id.survival_skill));

        updateViews(rootView);

        return rootView;
    }


    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        if (character == null) {
            ac.setText("");
            initiative.setText("");
            hp.setText("0 / 0");
            temp_hp_layout.setVisibility(View.GONE);
            for (final Map.Entry<StatType, StatBlockView> entry : statViewsByType.entrySet()) {
                entry.getValue().setCharacter(null);
                entry.getValue().setType(entry.getKey());
            }

            for (final Map.Entry<StatType, SavingThrowBlockView> entry : saveThrowViewsByType.entrySet()) {
                entry.getValue().setCharacter(null);
                entry.getValue().setType(entry.getKey());
            }

            for (final Map.Entry<SkillType, SkillBlockView> entry : skillViewsByType.entrySet()) {
                entry.getValue().setCharacter(null);
                entry.getValue().setType(entry.getKey());
            }

            return;
        }
        ac.setText(character.getArmorClass() + "");
        initiative.setText(character.getStatBlock(StatType.DEXTERITY).getModifier() + "");

        hp.setText(character.getHP() + " / " + character.getMaxHP());
        int tempHpVal = character.getTempHp();
        if (tempHpVal > 0) {
            temp_hp_layout.setVisibility(View.VISIBLE);
            tempHp.setText(tempHpVal + "");
        } else {
            temp_hp_layout.setVisibility(View.GONE);
        }

        hitDice.setText(character.getHitDiceString());
        proficiency.setText(character.getProficiency() + "");

        for (final Map.Entry<StatType, StatBlockView> entry : statViewsByType.entrySet()) {
            entry.getValue().setCharacter(character);
            entry.getValue().setType(entry.getKey());
            entry.getValue().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StatBlockDialogFragment dialog = StatBlockDialogFragment.create((MainActivity) getActivity(), character.getStatBlock(entry.getKey()));
                    dialog.show(getFragmentManager(), "stat_frag");
                }
            });
        }

        for (final Map.Entry<StatType, SavingThrowBlockView> entry : saveThrowViewsByType.entrySet()) {
            entry.getValue().setCharacter(character);
            entry.getValue().setType(entry.getKey());
            entry.getValue().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveThrowBlockDialogFragment dialog = SaveThrowBlockDialogFragment.create((MainActivity) getActivity(), character.getStatBlock(entry.getKey()));
                    dialog.show(getFragmentManager(), "save_throw_frag");
                }
            });
        }

        for (final Map.Entry<SkillType, SkillBlockView> entry : skillViewsByType.entrySet()) {
            entry.getValue().setCharacter(character);
            entry.getValue().setType(entry.getKey());
            entry.getValue().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SkillBlockDialogFragment dialog = SkillBlockDialogFragment.create((MainActivity) getActivity(), character.getSkillBlock(entry.getKey()));
                    dialog.show(getFragmentManager(), "skill_frag");
                }
            });
        }


    }

}
