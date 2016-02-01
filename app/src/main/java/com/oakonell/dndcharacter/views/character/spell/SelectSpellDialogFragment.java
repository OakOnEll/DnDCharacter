package com.oakonell.dndcharacter.views.character.spell;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.character.AbstractSelectComponentDialogFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Rob on 1/3/2016.
 */
public class SelectSpellDialogFragment extends AbstractSelectComponentDialogFragment<AbstractSelectComponentDialogFragment.RowViewHolder> {

    public static final String CASTER_CLASSES = "casterClasses";
    public static final String CANTRIPS = "cantrips";
    private NoDefaultSpinner classNameSpinner;
    private TextView max_spell_level;
    private boolean cantrips;
    private SpellSelectedListener listener;

    public void setListener(SpellSelectedListener listener) {
        this.listener = listener;
    }

    public interface SpellSelectedListener {
        boolean spellSelected(long spellId, String className);
    }

    @NonNull
    public static SelectSpellDialogFragment createDialog(@NonNull Collection<String> casterClasses, boolean cantrips, SpellSelectedListener listener) {
        SelectSpellDialogFragment dialog = new SelectSpellDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(CASTER_CLASSES, new ArrayList<>(casterClasses));
        args.putBoolean(CANTRIPS, cantrips);
        dialog.setArguments(args);
        dialog.setListener(listener);
        return dialog;
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateTheView(inflater, container, savedInstanceState);

        max_spell_level = (TextView) view.findViewById(R.id.max_spell_level);

        classNameSpinner = (NoDefaultSpinner) view.findViewById(R.id.class_name);
        final String prompt = getString(R.string.caster_class_prompt);
        classNameSpinner.setPrompt(prompt);
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (prompt.length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, classNameSpinner.getResources().getDisplayMetrics());
        classNameSpinner.setMinimumWidth((int) minWidth);


        final List<String> casterClasses = getArguments().getStringArrayList(CASTER_CLASSES);
        cantrips = getArguments().getBoolean(CANTRIPS);

        if (cantrips) {
            view.findViewById(R.id.max_spell_level_group).setVisibility(View.GONE);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, casterClasses);
        dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        classNameSpinner.setAdapter(dataAdapter);

        if (casterClasses.size() == 1) {
            classNameSpinner.setSelection(0);
            classNameSpinner.setEnabled(false);
        }

        classNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String className = (String) classNameSpinner.getSelectedItem();
                final Character.CastingClassInfo castingClassInfo = getCharacter().getCasterClassInfo().get(className);
                if (castingClassInfo != null) {
                    max_spell_level.setText(NumberUtils.formatNumber(castingClassInfo.getMaxSpellLevel()));
                } else {
                    max_spell_level.setText("?");
                }

                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        final List<SpellClass> list = new Select()
//                .from(SpellClass.class).orderBy("spell, aClass").execute();
//        for (SpellClass each : list) {
//            Spell spell = each.getSpell();
//            String name = spell == null ? "null" : spell.getName();
//            Log.i("SpellDialog", "'" + name + "': '" + each.getAClass() + "'");
//        }
//
//        final List<Spell> list1 = new Select().from(Spell.class).where("exists ( select 'X' from spell_class sc where sc.spell = spell._id  and upper(aClass) = ?)", "WARLOCK").execute();
//        for (Spell each : list1) {
//            Log.i("SpellDialog", each.getName());
//        }

        //Toast.makeText(getContext(), "Found spell " + spell.getLevel(), Toast.LENGTH_SHORT).show();

        return view;
    }

    @Override
    protected boolean canSearch() {
        return classNameSpinner != null && classNameSpinner.getSelectedItemPosition() >= 0;
    }

    @Nullable
    @Override
    protected String getSelection() {
        if (classNameSpinner == null) return null;
        if (classNameSpinner.getSelectedItem() == null) return null;
        if (cantrips) {
            return " exists ( select 'X' from spell_class sc where sc.spell = spell._id  and upper(aClass) = ?) and level = 0";
        } else {
            return " exists ( select 'X' from spell_class sc where sc.spell = spell._id  and upper(aClass) = ?) and level > 0 and level <= ? ";
        }
    }

    @Nullable
    @Override
    protected String[] getSelectionArgs() {
        if (classNameSpinner == null) return null;
        final String className = (String) classNameSpinner.getSelectedItem();
        if (cantrips) {
            return new String[]{className.toUpperCase()};
        }
        int maxLevel = 9;
        if (className == null) return null;
        final Character.CastingClassInfo castingClassInfo = getCharacter().getCasterClassInfo().get(className);
        if (castingClassInfo != null) {
            maxLevel = castingClassInfo.getMaxSpellLevel();
        }
        return new String[]{className.toUpperCase(), Integer.toString(maxLevel)};
    }

    protected int getDialogResource() {
        return R.layout.spell_search_dialog;
    }

    @Override
    protected String getTitle() {
        if (cantrips) {
            return getString(R.string.select_cantrip);
        }
        return getString(R.string.select_spell);
    }

    @NonNull
    @Override
    public Class<? extends Spell> getComponentClass() {
        return Spell.class;
    }

    @Override
    protected boolean applyAction(long id) {
        if (listener != null) {
            final String className = (String) classNameSpinner.getSelectedItem();
            return listener.spellSelected(id, className);
        }
        throw new RuntimeException("No Listener!");
    }
}
