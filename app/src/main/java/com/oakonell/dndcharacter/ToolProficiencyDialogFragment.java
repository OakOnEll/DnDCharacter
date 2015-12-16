package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.Proficient;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.views.RowWithSourceAdapter;

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

        final List<Character.ToolProficiencyWithSource> proficiencies = mainActivity.character.deriveToolProficiencies(proficiencyType);


        ToolProficiencySourceAdapter adapter = new ToolProficiencySourceAdapter(this, proficiencies);
        listView.setAdapter(adapter);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    public static class ToolProficiencySourceAdapter extends RowWithSourceAdapter<Character.ToolProficiencyWithSource> {
        ToolProficiencySourceAdapter(ToolProficiencyDialogFragment fragment, List<Character.ToolProficiencyWithSource> list) {
            super(fragment.mainActivity, list);
        }

        @Override
        protected void bindView(View view, WithSourceViewHolder<Character.ToolProficiencyWithSource> holder, Character.ToolProficiencyWithSource item) {
            String category = item.getProficiency().getCategory();
            String text;
            if (category != null) {
                text = category;
            } else {
                text = item.getProficiency().getName();
            }
            Proficient proficient = item.getProficiency().getProficient();
            if (proficient.getMultiplier() != 1) {
                text += "[" + proficient + "]";
            }
            holder.value.setText(text);
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
