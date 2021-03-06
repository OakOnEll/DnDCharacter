package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.Proficient;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;

import java.util.List;

/**
 * Created by Rob on 11/30/2015.
 */
public class ToolProficiencyDialogFragment extends AbstractCharacterDialogFragment {
    public static final String TYPE = "type";
    private RecyclerView listView;

    private ToolProficiencySourceAdapter adapter;

    @NonNull
    public static ToolProficiencyDialogFragment create(@NonNull ProficiencyType type) {
        ToolProficiencyDialogFragment frag = new ToolProficiencyDialogFragment();
        int typeIndex = type.ordinal();
        Bundle args = new Bundle();
        args.putInt(TYPE, typeIndex);
        frag.setArguments(args);

        return frag;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.tool_proficiencies);
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tool_proficiency_dialog, container);

        listView = (RecyclerView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        final ProficiencyType proficiencyType = updateView();

        RowWithSourceAdapter.ListRetriever<Character.ToolProficiencyWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ToolProficiencyWithSource>() {
            @NonNull
            @Override
            public List<Character.ToolProficiencyWithSource> getList(@NonNull AbstractCharacter character) {
                return ((Character)character).deriveToolProficiencies(proficiencyType);
            }
        };

        adapter = new ToolProficiencySourceAdapter(this, listRetriever, false);
        listView.setAdapter(adapter);

        listView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);
    }

    @NonNull
    private ProficiencyType updateView() {
        final ProficiencyType proficiencyType = getProficiencyType();
        getDialog().setTitle(getString(R.string.proficiency_type_proficiencies, getString(proficiencyType.getStringResId())));

        return proficiencyType;
    }

    private ProficiencyType getProficiencyType() {
        int typeIndex = getArguments().getInt(TYPE);
        return ProficiencyType.values()[typeIndex];
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        updateView();
        adapter.reloadList(character);
    }

    public static class ToolProfViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.ToolProficiencyWithSource> {
        public ToolProfViewHolder(@NonNull View view) {
            super(view);
        }

        @Override
        public void bind(@NonNull CharacterActivity activity, @NonNull RowWithSourceAdapter<Character.ToolProficiencyWithSource, RowWithSourceAdapter.WithSourceViewHolder<Character.ToolProficiencyWithSource>> adapter, @NonNull Character.ToolProficiencyWithSource item) {
            super.bind(activity, adapter, item);
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
            this.value.setText(text);
            final ComponentSource source = item.getSource();
            if (source == null) {
                // a base stat
                this.source.setText(R.string.base_stat);
            } else {
                this.source.setText(source.getSourceString(activity.getResources()));
            }
        }
    }

    public static class ToolProficiencySourceAdapter extends RowWithSourceAdapter<Character.ToolProficiencyWithSource, ToolProfViewHolder> {
        ToolProficiencySourceAdapter(@NonNull ToolProficiencyDialogFragment fragment, @NonNull ListRetriever<Character.ToolProficiencyWithSource> listRetriever, boolean isForCompanion) {
            super(fragment.getMainActivity(), listRetriever, isForCompanion);
        }


        @NonNull
        @Override
        protected ToolProfViewHolder newViewHolder(@NonNull View view) {
            return new ToolProfViewHolder(view);
        }
    }


}
