package com.oakonell.dndcharacter.views.character.spell;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.model.spell.ApplySpellToCharacterVisitor;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;

import java.util.List;

/**
 * Created by Rob on 1/19/2016.
 */
public class SpellsFragment extends AbstractSheetFragment implements SelectSpellDialogFragment.SpellSelectedListener {
    public static final String ADD_CANTRIP_DIALOG = "add_cantrip_frag";
    public static final String ADD_SPELL_DIALOG = "add_spell_frag";
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

        if (savedInstanceState != null) {
            SelectSpellDialogFragment dialogFrag = (SelectSpellDialogFragment) getMainActivity().getSupportFragmentManager()
                    .findFragmentByTag(ADD_CANTRIP_DIALOG);
            if (dialogFrag != null) {
                dialogFrag.setListener(this);
            }
            dialogFrag = (SelectSpellDialogFragment) getMainActivity().getSupportFragmentManager()
                    .findFragmentByTag(ADD_SPELL_DIALOG);
            if (dialogFrag != null) {
                dialogFrag.setListener(this);
            }
        }

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
                SelectSpellDialogFragment frag = SelectSpellDialogFragment.createDialog(getCharacter().getCasterClassInfo().keySet(), true, SpellsFragment.this);
                frag.show(getFragmentManager(), ADD_CANTRIP_DIALOG);
            }
        });
        add_spell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectSpellDialogFragment frag = SelectSpellDialogFragment.createDialog(getCharacter().getCasterClassInfo().keySet(), false, SpellsFragment.this);
                frag.show(getFragmentManager(), ADD_SPELL_DIALOG);
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

    @Override
    public boolean spellSelected(long id, String className) {
        Spell spell = Spell.load(Spell.class, id);

        final List<CharacterSpell> existingSpells = getCharacter().getSpells(spell.getLevel());
        for (CharacterSpell each : existingSpells) {
            if (each.getName().equals(spell.getName())) {
                int titleRes = R.string.select_spell;
                if (spell.getLevel() == 0) {
                    titleRes = R.string.select_cantrip;
                }
                new AlertDialog.Builder(getContext()).setTitle(titleRes).setMessage(getString(R.string.spell_already_known, each.getName(), each.getOriginString())).setNeutralButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return false;
            }
        }

        final CharacterSpell characterSpell = ApplySpellToCharacterVisitor.createCharacterSpell(spell, getCharacter());
        characterSpell.setSource(ComponentType.CLASS);
        characterSpell.setCasterClass(className);

        if (spell.getLevel() > 0) {
            final Character.CastingClassInfo info = getCharacter().getCasterClassInfo().get(className);
            if (info.usesPreparedSpells()) {
                characterSpell.setPreparable(true);
            }
        }

        getMainActivity().updateViews();
        getMainActivity().saveCharacter();
        return true;
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
