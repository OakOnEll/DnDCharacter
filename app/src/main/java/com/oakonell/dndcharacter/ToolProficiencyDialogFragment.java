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
import com.oakonell.dndcharacter.model.SkillType;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.views.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.RowWithSourceAdapter;

import java.util.List;

/**
 * Created by Rob on 11/30/2015.
 */
public class ToolProficiencyDialogFragment extends AbstractCharacterDialogFragment {

    private TextView proficiency_label;
    private ListView listView;

    public static ToolProficiencyDialogFragment create( ProficiencyType type) {
        ToolProficiencyDialogFragment frag = new ToolProficiencyDialogFragment();
        int typeIndex = type.ordinal();
        Bundle args = new Bundle();
        args.putInt("type", typeIndex);
        frag.setArguments(args);

        return frag;
    }


    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tool_proficiency_dialog, container);

        proficiency_label = (TextView) view.findViewById(R.id.proficiency_label);

        listView = (ListView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        int typeIndex = getArguments().getInt("type");
        final ProficiencyType proficiencyType = ProficiencyType.values()[typeIndex];

        proficiency_label.setText(proficiencyType.toString());
        RowWithSourceAdapter.ListRetriever<Character.ToolProficiencyWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ToolProficiencyWithSource>() {
            @Override
            public List<Character.ToolProficiencyWithSource> getList(Character character) {
                return character.deriveToolProficiencies(proficiencyType);
            }
        };

        ToolProficiencySourceAdapter adapter = new ToolProficiencySourceAdapter(this, listRetriever);
        listView.setAdapter(adapter);

    }

    public static class ToolProficiencySourceAdapter extends RowWithSourceAdapter<Character.ToolProficiencyWithSource> {
        ToolProficiencySourceAdapter(ToolProficiencyDialogFragment fragment, ListRetriever<Character.ToolProficiencyWithSource> listRetriever) {
            super(fragment.getMainActivity(), listRetriever);
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
