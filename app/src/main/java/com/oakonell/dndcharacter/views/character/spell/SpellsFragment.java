package com.oakonell.dndcharacter.views.character.spell;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
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

    @Override
    protected View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spell_sheet, container, false);

        spell_level_list = (RecyclerView) rootView.findViewById(R.id.spell_level_list);
        caster_list = (RecyclerView) rootView.findViewById(R.id.caster_list);

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

//

        spellsAdapter = new SpellLevelsAdapter(this, character);
        spell_level_list.setAdapter(spellsAdapter);
        spell_level_list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        spell_level_list.setHasFixedSize(false);

        //DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        spell_level_list.addItemDecoration(itemDecoration);

    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        spellsAdapter.reloadList(getCharacter());
        casterInfoAdapter.reloadList(getCharacter());
    }


}
