package com.oakonell.dndcharacter.views.character;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.feat.InitiativeDialogFragment;
import com.oakonell.dndcharacter.views.character.feat.PassivePerceptionDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.dndcharacter.views.character.item.ArmorClassDialogFragment;
import com.oakonell.dndcharacter.views.character.race.SpeedDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.SaveThrowBlockDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.SavingThrowBlockView;
import com.oakonell.dndcharacter.views.character.stats.SkillBlockDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.SkillBlockView;
import com.oakonell.dndcharacter.views.character.stats.StatBlockDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.StatBlockView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 12/3/2016.
 */

public class AbstractCharacterViewHelper {

    final Map<StatType, StatBlockView> statViewsByType = new HashMap<>();
    final Map<StatType, SavingThrowBlockView> saveThrowViewsByType = new HashMap<>();
    final Map<SkillType, SkillBlockView> skillViewsByType = new HashMap<>();

    TextView speed;
    TextView ac;
    TextView hp;
    View temp_hp_layout;
    View death_saves_layout;
    View stable;
    ViewGroup fail_layout;
    ViewGroup success_layout;
    Button stabilize;

    View hp_layout;

    TextView tempHp;
    TextView proficiency;
    private TextView initiative;
    private TextView passivePerception;
    private TextView speed_lbl;

    private AbstractSheetFragment mainFragment;
    private boolean isForCompanion;

    public AbstractCharacterViewHelper(AbstractSheetFragment mainFragment, boolean isForCompanion) {
        this.mainFragment = mainFragment;
        this.isForCompanion = isForCompanion;
    }


    public void onCreateView(View rootView) {
        hp = (TextView) rootView.findViewById(R.id.hp);
        tempHp = (TextView) rootView.findViewById(R.id.temp_hp);
        temp_hp_layout = rootView.findViewById(R.id.temp_hp_layout);
        death_saves_layout = rootView.findViewById(R.id.death_save_layout);
        fail_layout = (ViewGroup) rootView.findViewById(R.id.fail_layout);
        success_layout = (ViewGroup) rootView.findViewById(R.id.success_layout);
        stabilize = (Button) rootView.findViewById(R.id.stabilize);
        stable = rootView.findViewById(R.id.stable);

        hp_layout = rootView.findViewById(R.id.hp_layout);


        proficiency = (TextView) rootView.findViewById(R.id.proficiency);

        // TODO connect the dialogs to the possible character companion!!!


        speed = (TextView) rootView.findViewById(R.id.speed);
        speed_lbl = (TextView) rootView.findViewById(R.id.speed_lbl);
        rootView.findViewById(R.id.speed_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO open a speed dialog, to select speed type to display, and to show sources of speed
                SpeedDialogFragment dialog = SpeedDialogFragment.create(isForCompanion);
                dialog.show(getFragmentManager(), "speed");
            }
        });

        ac = (TextView) rootView.findViewById(R.id.ac);

        rootView.findViewById(R.id.ac_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArmorClassDialogFragment dialog = ArmorClassDialogFragment.createDialog(isForCompanion);
                dialog.show(getFragmentManager(), "ac");
            }
        });

        initiative = (TextView) rootView.findViewById(R.id.initiative);
        rootView.findViewById(R.id.initiative_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitiativeDialogFragment dialog = InitiativeDialogFragment.create(isForCompanion);
                dialog.show(getFragmentManager(), "initiative");
            }
        });

        passivePerception = (TextView) rootView.findViewById(R.id.passive_perception);
        rootView.findViewById(R.id.passive_perception_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassivePerceptionDialogFragment dialog = PassivePerceptionDialogFragment.create(isForCompanion);
                dialog.show(getFragmentManager(), "passive_perception");
            }
        });


        final View.OnClickListener onClickHp = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                HitPointDiaogFragment hpFragment = HitPointDiaogFragment.createDialog(isForCompanion);
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

        updateViews(rootView, null);
    }

    public void updateViews(View rootView, final AbstractCharacter character) {
        if (rootView == null) return;
        if (character == null) {
// TODO shouldn't be possible now...
            Log.d("Main", "updateViews character = null");
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
        Log.d("Main", "updateViews character != null");

        String acText = NumberUtils.formatNumber(character.getArmorClass());
        // if any TO_HIT context features, they kind of affect armor class, and will show up here
        // so that clicking will bring up the AC dialog for details
        if (character.anyContextFeats(FeatureContext.TO_HIT)) {
            acText += "!";
        }
        if (ac == null) {
            Log.d("Main", "ac = null");
            //odd state
            return;
        }
        ac.setText(acText);
        SpeedType speedType = character.getSpeedType();
        if (speedType != SpeedType.WALK) {
            speed_lbl.setText(speedType.getStringResId());
        } else {
            speed_lbl.setText(R.string.speed_label);
        }
        speed.setText(NumberUtils.formatNumber(character.getSpeed(speedType)));

        initiative.setText(NumberUtils.formatNumber(character.getInitiative()));

        passivePerception.setText(NumberUtils.formatNumber(character.getPassivePerception()));

        hp.setText(getString(R.string.fraction_d_slash_d, character.getHP(), character.getMaxHP()));
        int tempHpVal = character.getTempHp();
        if (tempHpVal > 0) {
            temp_hp_layout.setVisibility(View.VISIBLE);
            tempHp.setText(NumberUtils.formatNumber(tempHpVal));
        } else {
            temp_hp_layout.setVisibility(View.GONE);
        }
        if (character.getHP() <= 0 && !character.isStable() && character.getMaxHP() > 0) {
            death_saves_layout.setVisibility(View.VISIBLE);
            hp_layout.setVisibility(View.GONE);
            rootView.findViewById(R.id.fail1).setVisibility(character.getDeathSaveFails() >= 1 ? View.VISIBLE : View.INVISIBLE);
            rootView.findViewById(R.id.fail2).setVisibility(character.getDeathSaveFails() >= 2 ? View.VISIBLE : View.INVISIBLE);
            rootView.findViewById(R.id.fail3).setVisibility(character.getDeathSaveFails() >= 3 ? View.VISIBLE : View.INVISIBLE);


            rootView.findViewById(R.id.success1).setVisibility(character.getDeathSaveSuccesses() >= 1 ? View.VISIBLE : View.INVISIBLE);
            rootView.findViewById(R.id.success2).setVisibility(character.getDeathSaveSuccesses() >= 2 ? View.VISIBLE : View.INVISIBLE);
            rootView.findViewById(R.id.success3).setVisibility(character.getDeathSaveSuccesses() >= 3 ? View.VISIBLE : View.INVISIBLE);

            stabilize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    character.stabilize();
                    getMainActivity().updateViews();
                    getMainActivity().saveCharacter();
                }
            });

            death_saves_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeathSaveDialogFragment dialog = DeathSaveDialogFragment.create(isForCompanion);
                    dialog.show(getFragmentManager(), "death_save");
                }
            });

        } else {
            death_saves_layout.setVisibility(View.GONE);
            hp_layout.setVisibility(View.VISIBLE);
            if (character.getHP() == 0 && character.isStable()) {
                stable.setVisibility(View.VISIBLE);
            } else {
                stable.setVisibility(View.GONE);
            }
        }

        proficiency.setText(NumberUtils.formatNumber(character.getProficiency()));

        for (final Map.Entry<StatType, StatBlockView> entry : statViewsByType.entrySet()) {
            entry.getValue().setCharacter(character);
            entry.getValue().setType(entry.getKey());
            entry.getValue().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StatBlockDialogFragment dialog = StatBlockDialogFragment.create(character.getStatBlock(entry.getKey()), isForCompanion);
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
                    SaveThrowBlockDialogFragment dialog = SaveThrowBlockDialogFragment.create(character.getStatBlock(entry.getKey()), isForCompanion);
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
                    SkillBlockDialogFragment dialog = SkillBlockDialogFragment.create(character.getSkillBlock(entry.getKey()));
                    dialog.show(getFragmentManager(), "skill_frag");
                }
            });
        }
    }

    private String getString(int resId, Object... args) {
        return mainFragment.getString(resId, args);
    }

    private CharacterActivity getMainActivity() {
        return mainFragment.getMainActivity();
    }


    private FragmentManager getFragmentManager() {
        return mainFragment.getFragmentManager();
    }
}
