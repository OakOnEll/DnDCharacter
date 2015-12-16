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
import com.oakonell.dndcharacter.model.StatBlock;
import com.oakonell.dndcharacter.views.BaseStatsDialogFragment;
import com.oakonell.dndcharacter.views.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.classes.EditClassLevelDialogFragment;
import com.oakonell.dndcharacter.views.race.ApplyRaceDialogFragment;

import java.util.List;

/**
 * Created by Rob on 11/7/2015.
 */
public class StatBlockDialogFragment extends RollableDialogFragment {
    private StatBlock statBlock;

    public static StatBlockDialogFragment create(MainActivity activity, StatBlock block) {
        StatBlockDialogFragment frag = new StatBlockDialogFragment();
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
        View view = inflater.inflate(R.layout.stat_dialog, container);
        superCreateView(view);
        setModifier(statBlock.getModifier());

        TextView statLabel = (TextView) view.findViewById(R.id.stat_label);
        statLabel.setText(statBlock.getType().toString());

        TextView total = (TextView) view.findViewById(R.id.total);
        TextView modifier = (TextView) view.findViewById(R.id.modifier);
        ListView listView = (ListView) view.findViewById(R.id.list);

        total.setText(statBlock.getValue() + "");
        modifier.setText(statBlock.getModifier() + "");

        StatReasonAdapter adapter = new StatReasonAdapter(this, statBlock.getModifiers());
        listView.setAdapter(adapter);

        return view;
    }


    private static class ViewHolder {
        TextView value;
        TextView source;
    }

    public static class StatReasonAdapter extends BaseAdapter {
        private List<Character.ModifierAndReason> list;
        StatBlockDialogFragment fragment;

        StatReasonAdapter(StatBlockDialogFragment fragment, List<Character.ModifierAndReason> list) {
            this.list = list;
            this.fragment = fragment;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Character.ModifierAndReason getItem(int position) {
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
                view = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.stat_mod_row, parent, false);
                holder = new ViewHolder();
                holder.value = (TextView) view.findViewById(R.id.value);
                holder.source = (TextView) view.findViewById(R.id.source);
                view.setTag(holder);
            }

            Character.ModifierAndReason item = getItem(position);
            int value = item.getModifier();
            final BaseCharacterComponent source = item.getSource();
            if (source == null) {
                // a base stat
                holder.source.setText("Base Stat");
                holder.value.setText(value + "");
            } else {
                holder.source.setText(source.getSourceString());
                holder.value.setText("+" + value);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Character character = fragment.statBlock.getCharacter();
                    if (source == null) {
                        // a base stat
                        BaseStatsDialogFragment dialog = BaseStatsDialogFragment.createDialog(character);
                        dialog.show(fragment.getFragmentManager(), "base_stats");
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
