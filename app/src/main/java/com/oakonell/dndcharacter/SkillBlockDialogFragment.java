package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterClass;
import com.oakonell.dndcharacter.model.Proficient;
import com.oakonell.dndcharacter.model.SkillBlock;
import com.oakonell.dndcharacter.views.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.classes.EditClassLevelDialogFragment;
import com.oakonell.dndcharacter.views.race.ApplyRaceDialogFragment;

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


        SkillReasonAdapter adapter = new SkillReasonAdapter(this, skillBlock.getProficiencies());
        listView.setAdapter(adapter);

        return view;
    }

    private static class ViewHolder {
        TextView value;
        TextView source;
    }

    public static class SkillReasonAdapter extends BaseAdapter {
        private List<Character.ProficientAndReason> list;
        SkillBlockDialogFragment fragment;

        SkillReasonAdapter(SkillBlockDialogFragment fragment, List<Character.ProficientAndReason> list) {
            this.list = list;
            this.fragment = fragment;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Character.ProficientAndReason getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;
            if (view != null) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.skill_prof_row, parent, false);
                holder = new ViewHolder();
                holder.value = (TextView) view.findViewById(R.id.value);
                holder.source = (TextView) view.findViewById(R.id.source);
                view.setTag(holder);
            }

            Character.ProficientAndReason item = getItem(position);
            Proficient value = item.getProficient();
            holder.value.setText(value.toString());
            final BaseCharacterComponent source = item.getSource();
            if (source == null) {
                // a base stat
                holder.source.setText("Base Stat");
            } else {
                holder.source.setText(source.getSourceString());
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Character character = fragment.skillBlock.getCharacter();
                    if (source == null) {
                        // ?? probably not possible
                    } else {
                        switch (source.getType()) {
                            case BACKGROUND:
                                ApplyBackgroundDialogFragment dialog = ApplyBackgroundDialogFragment.createDialog(character);
                                dialog.show(fragment.getFragmentManager(), "background");
                                break;
                            case CLASS:
                                CharacterClass charClass = (CharacterClass) source;
                                int position = character.getClasses().indexOf(charClass);
                                EditClassLevelDialogFragment classDialog = EditClassLevelDialogFragment.createDialog(character, charClass, position, null);
                                classDialog.show(fragment.getFragmentManager(), "class");
                                break;
                            case RACE:
                                ApplyRaceDialogFragment raceDialog = ApplyRaceDialogFragment.createDialog(character);
                                raceDialog.show(fragment.getFragmentManager(), "race");
                                break;
                            case ITEM:
                                break;
                        }
                    }
                }
            });

            return view;
        }
    }


}
