package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.Proficient;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;

import java.util.List;

/**
 * Created by Rob on 11/30/2015.
 */
public class ToolProficiencyDialogFragment extends AbstractCharacterDialogFragment {
    private ListView listView;

    private ToolProficiencySourceAdapter adapter;

    public static ToolProficiencyDialogFragment create(ProficiencyType type) {
        ToolProficiencyDialogFragment frag = new ToolProficiencyDialogFragment();
        int typeIndex = type.ordinal();
        Bundle args = new Bundle();
        args.putInt("type", typeIndex);
        frag.setArguments(args);

        return frag;
    }

    @Override
    protected String getTitle() {
        return "Tool Proficiencies";
    }


    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tool_proficiency_dialog, container);

        listView = (ListView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        final ProficiencyType proficiencyType = updateView();

        RowWithSourceAdapter.ListRetriever<Character.ToolProficiencyWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ToolProficiencyWithSource>() {
            @Override
            public List<Character.ToolProficiencyWithSource> getList(Character character) {
                return character.deriveToolProficiencies(proficiencyType);
            }
        };

        adapter = new ToolProficiencySourceAdapter(this, listRetriever);
        listView.setAdapter(adapter);
    }

    @NonNull
    private ProficiencyType updateView() {
        final ProficiencyType proficiencyType = getProficiencyType();
        getDialog().setTitle(proficiencyType.toString() + " proficiencies");

        return proficiencyType;
    }

    private ProficiencyType getProficiencyType() {
        int typeIndex = getArguments().getInt("type");
        return ProficiencyType.values()[typeIndex];
    }

    @Override
    public void onCharacterChanged(Character character) {
        updateView();
        adapter.reloadList(character);
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
                holder.source.setText(R.string.base_stat);
            } else {
                holder.source.setText(source.getSourceString());
            }
        }
    }


}
