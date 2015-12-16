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
import com.oakonell.dndcharacter.model.Proficient;
import com.oakonell.dndcharacter.model.StatBlock;
import com.oakonell.dndcharacter.views.ComponentLaunchHelper;

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

        List<Character.ProficientAndReason> proficiencies = statBlock.getSaveProficiencies();

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


        SaveThrowReasonAdapter adapter = new SaveThrowReasonAdapter(this, proficiencies);
        listView.setAdapter(adapter);

        return view;
    }

    private static class ViewHolder {
        TextView value;
        TextView source;
    }

    public static class SaveThrowReasonAdapter extends BaseAdapter {
        private List<Character.ProficientAndReason> list;
        SaveThrowBlockDialogFragment fragment;

        SaveThrowReasonAdapter(SaveThrowBlockDialogFragment fragment, List<Character.ProficientAndReason> list) {
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
                    final Character character = fragment.statBlock.getCharacter();
                    if (source == null) {
                        // ?? probably not possible
                    } else {
                        ComponentLaunchHelper.editComponent((MainActivity) fragment.getActivity(), character, source);
                    }
                }
            });

            return view;
        }
    }


}
