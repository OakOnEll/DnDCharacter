package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.Proficient;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.views.ComponentLaunchHelper;

import java.util.List;

/**
 * Created by Rob on 11/30/2015.
 */
public class ToolProficiencyDialogFragment extends DialogFragment {
    private MainActivity mainActivity;
    private ProficiencyType proficiencyType;


    public static ToolProficiencyDialogFragment create(MainActivity activity, ProficiencyType type) {
        ToolProficiencyDialogFragment frag = new ToolProficiencyDialogFragment();
        frag.setMainActivity(activity);
        frag.setProficiencyType(type);
        return frag;
    }

    private void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private void setProficiencyType(ProficiencyType proficiencyType) {
        this.proficiencyType = proficiencyType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tool_proficiency_dialog, container);

        TextView proficiency_label = (TextView) view.findViewById(R.id.proficiency_label);
        proficiency_label.setText(proficiencyType.toString());

        Button done = (Button) view.findViewById(R.id.done);
        ListView listView = (ListView) view.findViewById(R.id.list);

        final List<Character.ToolProficiencyAndReason> proficienciesAndReasons = mainActivity.character.deriveToolProficienciesReasons(proficiencyType);


        ToolProficiencyReasonAdapter adapter = new ToolProficiencyReasonAdapter(this, proficienciesAndReasons);
        listView.setAdapter(adapter);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private static class ViewHolder {
        TextView value;
        TextView source;
    }

    public static class ToolProficiencyReasonAdapter extends BaseAdapter {
        private List<Character.ToolProficiencyAndReason> list;
        ToolProficiencyDialogFragment fragment;

        ToolProficiencyReasonAdapter(ToolProficiencyDialogFragment fragment, List<Character.ToolProficiencyAndReason> list) {
            this.list = list;
            this.fragment = fragment;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Character.ToolProficiencyAndReason getItem(int position) {
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

            Character.ToolProficiencyAndReason item = getItem(position);
            String category = item.getProficiency().getCategory();
            String text;
            if (category != null) {
                text = category;
            } else {
                text = item.getProficiency().getName();
            }
            Proficient proficient = item.getProficiency().getProficient();

            holder.value.setText(text + "[" + proficient + "]");
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
                    final Character character = fragment.mainActivity.character;
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
