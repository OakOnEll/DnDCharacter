package com.oakonell.dndcharacter.views.character.companion;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.companion.Companion;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.DeathSaveDialogFragment;
import com.oakonell.dndcharacter.views.character.HitPointDiaogFragment;
import com.oakonell.dndcharacter.views.character.MainFragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 10/26/2015.
 */
public class CompanionsFragment extends AbstractSheetFragment {
    //    Button toXml;
    private View addCompanion;

    private CompanionsAdapter adapter;
    private RecyclerView companions_list;
    private View companion_layout;

    private TextView nameView;
    private TextView acView;
    private TextView speedView;
    private TextView initiativeView;

    TextView hp;
    View temp_hp_layout;
    View death_saves_layout;
    View stable;
    ViewGroup fail_layout;
    ViewGroup success_layout;
    Button stabilize;

    View hp_layout;

    TextView tempHp;
//    TextView hitDice;
    //TextView proficiency;
    private TextView initiative;
    private TextView passivePerception;
    private TextView speed_lbl;

    final Map<StatType, StatBlockView> statViewsByType = new HashMap<>();
    final Map<StatType, SavingThrowBlockView> saveThrowViewsByType = new HashMap<>();
    final Map<SkillType, SkillBlockView> skillViewsByType = new HashMap<>();

    private static final int UNDO_DELAY = 5000;

    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.companions_sheet, container, false);

        addCompanion = rootView.findViewById(R.id.add_companion);
        companions_list = (RecyclerView) rootView.findViewById(R.id.companions_list);
        companion_layout = rootView.findViewById(R.id.companion_layout);
        nameView = (TextView) rootView.findViewById(R.id.name);
        acView = (TextView) rootView.findViewById(R.id.ac);
        speedView = (TextView) rootView.findViewById(R.id.speed);
        speed_lbl = (TextView) rootView.findViewById(R.id.speed_lbl);
        rootView.findViewById(R.id.speed_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO open a speed dialog, to select speed type to display, and to show sources of speed
                SpeedDialogFragment dialog = SpeedDialogFragment.create();
                dialog.show(getFragmentManager(), "speed");
            }
        });

        initiativeView = (TextView) rootView.findViewById(R.id.initiative);

        hp = (TextView) rootView.findViewById(R.id.hp);
        tempHp = (TextView) rootView.findViewById(R.id.temp_hp);
        temp_hp_layout = rootView.findViewById(R.id.temp_hp_layout);
        death_saves_layout = rootView.findViewById(R.id.death_save_layout);
        fail_layout = (ViewGroup) rootView.findViewById(R.id.fail_layout);
        success_layout = (ViewGroup) rootView.findViewById(R.id.success_layout);
        stabilize = (Button) rootView.findViewById(R.id.stabilize);
        stable = rootView.findViewById(R.id.stable);

        hp_layout = rootView.findViewById(R.id.hp_layout);


        //hitDice = (TextView) rootView.findViewById(R.id.hit_dice);
        //proficiency = (TextView) rootView.findViewById(R.id.proficiency);

        rootView.findViewById(R.id.ac_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArmorClassDialogFragment dialog = ArmorClassDialogFragment.createDialog();
                dialog.show(getFragmentManager(), "ac");
            }
        });

        initiative = (TextView) rootView.findViewById(R.id.initiative);
        rootView.findViewById(R.id.initiative_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitiativeDialogFragment dialog = InitiativeDialogFragment.create();
                dialog.show(getFragmentManager(), "initiative");
            }
        });

        passivePerception = (TextView) rootView.findViewById(R.id.passive_perception);
        rootView.findViewById(R.id.passive_perception_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassivePerceptionDialogFragment dialog = PassivePerceptionDialogFragment.create();
                dialog.show(getFragmentManager(), "passive_perception");
            }
        });


        final View.OnClickListener onClickHp = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = CompanionsFragment.this.getFragmentManager();
                HitPointDiaogFragment hpFragment = HitPointDiaogFragment.createDialog();
                hpFragment.show(fm, "fragment_hp");
            }
        };
        rootView.findViewById(R.id.hp_layout).setOnClickListener(onClickHp);
        rootView.findViewById(R.id.temp_hp_layout).setOnClickListener(onClickHp);


        addCompanion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Companion> companions = getCharacter().getCompanions();
                Companion companion = new Companion();
                companion.setName("Companion " + companions.size());
                companions.add(companion);

                updateViews(rootView);
            }
        });

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


        superCreateViews(rootView);

        updateViews(rootView);

        return rootView;
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        if (getCharacter() == null) {

        } else {
            final Companion companion = getCharacter().getDisplayedCompanion();
            if (companion == null) {
                companion_layout.setVisibility(View.GONE);
            } else {
                companion_layout.setVisibility(View.VISIBLE);
                if (updateCompanionViews(rootView, companion)) return;
            }


            if (adapter != null) {
                adapter.reloadList(getCharacter());
            }
        }

    }

    private boolean updateCompanionViews(View rootView, final Companion companion) {
        nameView.setText(companion.getName());

        String acText = NumberUtils.formatNumber(companion.getArmorClass());
        // if any TO_HIT context features, they kind of affect armor class, and will show up here
        // so that clicking will bring up the AC dialog for details
        if (getCharacter().anyContextFeats(FeatureContext.TO_HIT)) {
            acText += "!";
        }
        if (acView == null) {
            Log.d("Main", "ac = null");
            //odd state
            return true;
        }
        acView.setText(acText);
        SpeedType speedType = companion.getSpeedType();
        if (speedType != SpeedType.WALK) {
            speed_lbl.setText(speedType.getStringResId());
        } else {
            speed_lbl.setText(R.string.speed_label);
        }
        speedView.setText(NumberUtils.formatNumber(companion.getSpeed(speedType)));

        initiative.setText(NumberUtils.formatNumber(companion.getInitiative()));

        passivePerception.setText(NumberUtils.formatNumber(companion.getPassivePerception()));

        hp.setText(getString(R.string.fraction_d_slash_d, companion.getHP(), companion.getMaxHP()));
        int tempHpVal = companion.getTempHp();
        if (tempHpVal > 0) {
            temp_hp_layout.setVisibility(View.VISIBLE);
            tempHp.setText(NumberUtils.formatNumber(tempHpVal));
        } else {
            temp_hp_layout.setVisibility(View.GONE);
        }
        if (companion.getHP() <= 0 && !companion.isStable() && companion.getMaxHP() > 0) {
//            death_saves_layout.setVisibility(View.VISIBLE);
//            hp_layout.setVisibility(View.GONE);
//            rootView.findViewById(R.id.fail1).setVisibility(companion.getDeathSaveFails() >= 1 ? View.VISIBLE : View.INVISIBLE);
//            rootView.findViewById(R.id.fail2).setVisibility(companion.getDeathSaveFails() >= 2 ? View.VISIBLE : View.INVISIBLE);
//            rootView.findViewById(R.id.fail3).setVisibility(companion.getDeathSaveFails() >= 3 ? View.VISIBLE : View.INVISIBLE);
//
//
//            rootView.findViewById(R.id.success1).setVisibility(companion.getDeathSaveSuccesses() >= 1 ? View.VISIBLE : View.INVISIBLE);
//            rootView.findViewById(R.id.success2).setVisibility(companion.getDeathSaveSuccesses() >= 2 ? View.VISIBLE : View.INVISIBLE);
//            rootView.findViewById(R.id.success3).setVisibility(companion.getDeathSaveSuccesses() >= 3 ? View.VISIBLE : View.INVISIBLE);
//
//            stabilize.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    companion.stabilize();
//                    getMainActivity().updateViews();
//                    getMainActivity().saveCharacter();
//                }
//            });
//
//            death_saves_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    DeathSaveDialogFragment dialog = DeathSaveDialogFragment.create();
//                    dialog.show(getFragmentManager(), "death_save");
//                }
//            });

        } else {
            death_saves_layout.setVisibility(View.GONE);
            hp_layout.setVisibility(View.VISIBLE);
            if (companion.getHP() == 0 && companion.isStable()) {
                stable.setVisibility(View.VISIBLE);
            } else {
                stable.setVisibility(View.GONE);
            }
        }

        //hitDice.setText(companion.getHitDiceString());
        //proficiency.setText(NumberUtils.formatNumber(companion.getProficiency()));

//        for (final Map.Entry<StatType, StatBlockView> entry : statViewsByType.entrySet()) {
//            entry.getValue().setCharacter(character);
//            entry.getValue().setType(entry.getKey());
//            entry.getValue().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    StatBlockDialogFragment dialog = StatBlockDialogFragment.create(getCharacter().getStatBlock(entry.getKey()));
//                    dialog.show(getFragmentManager(), "stat_frag");
//                }
//            });
//        }
//
//        for (final Map.Entry<StatType, SavingThrowBlockView> entry : saveThrowViewsByType.entrySet()) {
//            entry.getValue().setCharacter(character);
//            entry.getValue().setType(entry.getKey());
//            entry.getValue().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SaveThrowBlockDialogFragment dialog = SaveThrowBlockDialogFragment.create(getCharacter().getStatBlock(entry.getKey()));
//                    dialog.show(getFragmentManager(), "save_throw_frag");
//                }
//            });
//        }
//
//        for (final Map.Entry<SkillType, SkillBlockView> entry : skillViewsByType.entrySet()) {
//            entry.getValue().setCharacter(character);
//            entry.getValue().setType(entry.getKey());
//            entry.getValue().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SkillBlockDialogFragment dialog = SkillBlockDialogFragment.create(getCharacter().getSkillBlock(entry.getKey()));
//                    dialog.show(getFragmentManager(), "skill_frag");
//                }
//            });
//        }
        return false;
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
        if (getActivity() == null) return;


        adapter = new CompanionsAdapter((CharacterActivity) this.getActivity());
        companions_list.setAdapter(adapter);
        companions_list.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        companions_list.addItemDecoration(itemDecoration);

        companions_list.setHasFixedSize(false);

        updateViews();
    }


    public static class DeletingCompanionViewHolder extends BindableComponentViewHolder<Companion, CharacterActivity, CompanionsFragment.CompanionsAdapter> {

        private final TextView name;
        @NonNull
        private final View undo;

        public DeletingCompanionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);

            undo = itemView.findViewById(R.id.undo);
        }

        @Override
        public void bind(final CharacterActivity context, final CompanionsAdapter adapter, final Companion info) {
            name.setText(info.getName());

            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.setDeleting(false);
                    adapter.notifyDataSetChanged();
                    undo.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.saveCharacter();
                            context.updateViews();
                        }
                    }, 100);
                }
            });

        }
    }


    public static class CompanionViewHolder extends BindableComponentViewHolder<Companion, CharacterActivity, CompanionsFragment.CompanionsAdapter> {

        private final TextView name;
        private final TextView race;
        private final TextView type;
        private final TextView description;
        @NonNull
        private final View delete;

        public CompanionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            type = (TextView) itemView.findViewById(R.id.type);
            race = (TextView) itemView.findViewById(R.id.race);
            description = (TextView) itemView.findViewById(R.id.description);

            delete = itemView.findViewById(R.id.delete);
        }

        @Override
        public void bind(final CharacterActivity context, final CompanionsAdapter adapter, final Companion info) {
            name.setText(info.getName());
            race.setText(info.getRace());
            //type.setText(info.getType());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int index = getAdapterPosition();
                    if (index == context.getCharacter().getDisplayedCompanionIndex() || info.isDeleting()) {
                        context.getCharacter().setDisplayedCompanion(-1);
                    } else {
                        context.getCharacter().setDisplayedCompanion(index);
                    }
                    context.updateViews();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.setDeleting(true);
                    final List<Companion> companions = context.getCharacter().getCompanions();
                    int position = getAdapterPosition();
                    if (position == context.getCharacter().getDisplayedCompanionIndex()) {
                        context.getCharacter().setDisplayedCompanion(-1);
                    }

                    adapter.notifyDataSetChanged();
                    delete.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.saveCharacter();
                            context.updateViews();
                        }
                    }, 100);
                }
            });


            delete.postDelayed(new Runnable() {
                public void run() {
                    if (!info.isDeleting()) return;

                    // actually delete the record, now
                    // taking care to adjust the displayed companion index
                    final List<Companion> companions = context.getCharacter().getCompanions();
                    int index = companions.indexOf(info);
                    if (context.getCharacter().getDisplayedCompanionIndex() > index) {
                        context.getCharacter().setDisplayedCompanion(context.getCharacter().getDisplayedCompanionIndex() - 1);
                    }
                    companions.remove(info);
                    //int position = getAdapterPosition();
                    //adapter.notifyItemRemoved(position);
                    adapter.notifyDataSetChanged();
                    delete.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.saveCharacter();
                            context.updateViews();
                        }
                    }, 100);
                }
            }, UNDO_DELAY);
        }
    }

    public static class CompanionsAdapter extends RecyclerView.Adapter<BindableComponentViewHolder<Companion, CharacterActivity, CompanionsFragment.CompanionsAdapter>> {
        private static final int DELETING_VIEW_TYPE = -1;
        @NonNull
        private final CharacterActivity context;
        private List<Companion> list;

        public CompanionsAdapter(@NonNull CharacterActivity context) {
            this.context = context;
            list = new ArrayList<>(context.getCharacter().getCompanions());
        }


        public void reloadList(@NonNull Character character) {
            list = new ArrayList<>(character.getCompanions());
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        @Nullable
        public Companion getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            final Companion companion = getItem(position);
            if (companion.isDeleting()) {
                return DELETING_VIEW_TYPE;
            }
            return 0;
        }

        @NonNull
        @Override
        public BindableComponentViewHolder<Companion, CharacterActivity, CompanionsFragment.CompanionsAdapter> onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == DELETING_VIEW_TYPE) {
                View view = LayoutInflater.from(context).inflate(R.layout.deleted_companion_item_layout, parent, false);
                CompanionsFragment.DeletingCompanionViewHolder holder = new CompanionsFragment.DeletingCompanionViewHolder(view);
                return holder;
            }
            View view = LayoutInflater.from(context).inflate(R.layout.companion_item_layout, parent, false);
            CompanionsFragment.CompanionViewHolder holder = new CompanionsFragment.CompanionViewHolder(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(@NonNull final BindableComponentViewHolder<Companion, CharacterActivity, CompanionsFragment.CompanionsAdapter> viewHolder, final int position) {
            final Companion companion = getItem(position);
            viewHolder.bind(context, this, companion);
        }


    }


}
