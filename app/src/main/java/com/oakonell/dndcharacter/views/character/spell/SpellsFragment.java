package com.oakonell.dndcharacter.views.character.spell;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;

/**
 * Created by Rob on 1/19/2016.
 */
public class SpellsFragment extends AbstractSheetFragment {
    private RecyclerView spell_level_list;
    private SpellLevelsAdapter spellsAdapter;
    private RecyclerView caster_list;
    private CasterInfoAdapter casterInfoAdapter;
    private RecyclerView cantrips;
    private CantripAdapter cantripsAdapter;
    private ImageButton add_spell;
    private ImageButton add_cantrip;

    @Override
    protected View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spell_sheet, container, false);

        spell_level_list = (RecyclerView) rootView.findViewById(R.id.spell_level_list);
        caster_list = (RecyclerView) rootView.findViewById(R.id.caster_list);
        cantrips = (RecyclerView) rootView.findViewById(R.id.cantrips);

        add_spell = (ImageButton) rootView.findViewById(R.id.add_spell);
        add_cantrip = (ImageButton) rootView.findViewById(R.id.add_cantrip);

        superCreateViews(rootView);
        return rootView;
    }


    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);

        casterInfoAdapter = new CasterInfoAdapter(this, character);
        caster_list.setAdapter(casterInfoAdapter);
        caster_list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        caster_list.setHasFixedSize(false);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        caster_list.addItemDecoration(itemDecoration);


        spellsAdapter = new SpellLevelsAdapter(this, character);
        spell_level_list.setAdapter(spellsAdapter);
        spell_level_list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        spell_level_list.setHasFixedSize(false);
//
//        spell_level_list.addItemDecoration(itemDecoration);


        cantripsAdapter = new CantripAdapter(this, character.getSpellInfos().get(0));
        cantrips.setAdapter(cantripsAdapter);
        cantrips.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        cantrips.setHasFixedSize(false);

        cantrips.addItemDecoration(itemDecoration);

        add_cantrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSpellDialogFragment frag = AddSpellDialogFragment.createDialog(getCharacter().getCasterClassInfo().keySet(), true);
                frag.show(getFragmentManager(), "add_cantrip_frag");
            }
        });
        add_spell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSpellDialogFragment frag = AddSpellDialogFragment.createDialog(getCharacter().getCasterClassInfo().keySet(), false);
                frag.show(getFragmentManager(), "add_spell_frag");
            }
        });
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        spellsAdapter.reloadList(getCharacter());
        cantripsAdapter.reloadList(getCharacter().getSpellInfos().get(0));
        casterInfoAdapter.reloadList(getCharacter());
    }


    public static class CantripAdapter extends AbstractSpellAdapter<CantripViewHolder> {
        CantripAdapter(SpellsFragment context, com.oakonell.dndcharacter.model.character.Character.SpellLevelInfo spellLevelInfo) {
            super(context, spellLevelInfo);
        }

        @NonNull
        @Override
        public AbstractSpellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_char_cantrip_row, parent, false);
            return new CantripViewHolder(newView);
        }
    }

    public static class CantripViewHolder extends AbstractSpellAdapter.AbstractSpellViewHolder {
        public CantripViewHolder(View itemView) {
            super(itemView);
        }
    }


}
