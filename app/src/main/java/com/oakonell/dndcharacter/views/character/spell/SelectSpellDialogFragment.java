package com.oakonell.dndcharacter.views.character.spell;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.DnDContentProvider;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.CursorIndexesByName;
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
    private static final String SCHOOLS = "schools";

    private NoDefaultSpinner classNameSpinner;
    private TextView max_spell_level;
    private boolean cantrips;
    private SpellSelectedListener listener;

    private List<SpellSchool> schoolsFilter = null;

    public void setListener(SpellSelectedListener listener) {
        this.listener = listener;
    }

    public interface SpellSelectedListener {
        boolean spellSelected(long spellId, String className);
    }

    // Will use the current character's classes/caster classes to filter
    @NonNull
    public static SelectSpellDialogFragment createDialog(Character character, boolean cantrips, SpellSelectedListener listener) {
        SelectSpellDialogFragment dialog = new SelectSpellDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(CANTRIPS, cantrips);

        ArrayList<ClassChoice> classChoices = new ArrayList<>();
        Collection<Character.CastingClassInfo> casterClassInfos = character.getCasterClassInfo();
        for (Character.CastingClassInfo each : casterClassInfos) {
            String owningClassName = each.getOwningClassName();
            String casterClassName = each.getCasterClassName();
            classChoices.add(new ClassChoice(owningClassName, casterClassName, each.getMaxSpellLevel()));
        }
        args.putParcelableArrayList(CASTER_CLASSES, classChoices);

        dialog.setArguments(args);
        dialog.setListener(listener);
        return dialog;
    }


    @NonNull
    public static SelectSpellDialogFragment createDialog(@NonNull String casterClass, int maxLevel, List<SpellSchool> schools, SpellSelectedListener listener) {
        SelectSpellDialogFragment dialog = new SelectSpellDialogFragment();
        Bundle args = new Bundle();
        ArrayList<ClassChoice> classChoices = new ArrayList<>();
        if (casterClass.equals("*")) {
            // TODO better way to find these? SQL to find all distinct spell_class entries
            classChoices.add(new ClassChoice("Bard", "Bard", maxLevel));
            classChoices.add(new ClassChoice("Cleric", "Cleric", maxLevel));
            classChoices.add(new ClassChoice("Druid", "Druid", maxLevel));
            classChoices.add(new ClassChoice("Paladin", "Paladin", maxLevel));
            classChoices.add(new ClassChoice("Ranger", "Ranger", maxLevel));
            classChoices.add(new ClassChoice("Sorcerer", "Sorcerer", maxLevel));
            classChoices.add(new ClassChoice("Warlock", "Warlock", maxLevel));
            classChoices.add(new ClassChoice("Wizard", "Wizard", maxLevel));
        } else {
            classChoices.add(new ClassChoice(casterClass, casterClass, maxLevel));
        }
        args.putParcelableArrayList(CASTER_CLASSES, classChoices);
        if (schools != null && !schools.isEmpty()) {
            ArrayList<String> schoolNames = new ArrayList<>();
            for (SpellSchool each : schools) {
                schoolNames.add(each.name());
            }
            args.putStringArrayList(SCHOOLS, schoolNames);
        }
        args.putBoolean(CANTRIPS, maxLevel == 0);

        dialog.setArguments(args);
        dialog.setListener(listener);
        return dialog;
    }

//    @NonNull
//    public static SelectSpellDialogFragment createDialog(@NonNull Character character, @NonNull Collection<String> casterClasses, boolean cantrips, SpellSelectedListener listener) {
//        SelectSpellDialogFragment dialog = new SelectSpellDialogFragment();
//        Bundle args = new Bundle();
//        ArrayList<ClassChoice> classChoices = new ArrayList<>();
//        Collection<Character.CastingClassInfo> casterClassInfos = character.getCasterClassInfo();
//        // will this get the correct up-to-date info?
//        for (Character.CastingClassInfo each : casterClassInfos) {
//            if (casterClasses.contains(each.getCasterClassName())) {
//                classChoices.add(new ClassChoice(each.getOwningClassName(), each., maxLevel));
//            }
//        }
//        args.putParcelableArrayList(CASTER_CLASSES, classChoices);
//
//
//        args.putBoolean(CANTRIPS, cantrips);
//        dialog.setArguments(args);
//        dialog.setListener(listener);
//        return dialog;
//    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateTheView(inflater, container, savedInstanceState);

        max_spell_level = (TextView) view.findViewById(R.id.max_spell_level);

        classNameSpinner = (NoDefaultSpinner) view.findViewById(R.id.class_name);
        final String prompt = getString(R.string.caster_class_prompt);
        classNameSpinner.setPrompt(prompt);
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (prompt.length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, classNameSpinner.getResources().getDisplayMetrics());
        classNameSpinner.setMinimumWidth((int) minWidth);


        List<ClassChoice> classChoices = getArguments().getParcelableArrayList(CASTER_CLASSES);
        cantrips = getArguments().getBoolean(CANTRIPS);


        final List<String> schoolNames = getArguments().getStringArrayList(SCHOOLS);
        if (schoolNames != null) {
            schoolsFilter = new ArrayList<>();
            StringBuilder namesList = new StringBuilder();
            boolean isFirst = true;
            for (String each : schoolNames) {
                SpellSchool spellSchool = EnumHelper.stringToEnum(each, SpellSchool.class);
                schoolsFilter.add(spellSchool);
                if (!isFirst) {
                    namesList.append(", ");
                }
                isFirst = false;
                namesList.append(getString(spellSchool.getStringResId()));
            }

            TextView schoolsTextView = (TextView) view.findViewById(R.id.schools);
            schoolsTextView.setText(namesList);

        } else {
            view.findViewById(R.id.schools_group).setVisibility(View.GONE);
        }

        if (cantrips) {
            view.findViewById(R.id.max_spell_level_group).setVisibility(View.GONE);
        }

        ArrayAdapter<ClassChoice> dataAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, classChoices);
        dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        classNameSpinner.setAdapter(dataAdapter);

        if (classChoices.size() == 1) {
            classNameSpinner.setSelection(0);
            classNameSpinner.setEnabled(false);
            ClassChoice selectedItem = (ClassChoice) classNameSpinner.getSelectedItem();
            max_spell_level.setText(NumberUtils.formatNumber(selectedItem.maxLevel));
        } else {
            classNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    final ClassChoice selectedItem = (ClassChoice) classNameSpinner.getSelectedItem();
                    max_spell_level.setText(NumberUtils.formatNumber(selectedItem.maxLevel));
                    search();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        search();
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

        String schoolsFilterString = "";
        if (schoolsFilter != null) {
            // TODO cache this filter string
            StringBuilder builder = new StringBuilder(" and school in (");
            boolean isFirst = true;
            for (SpellSchool each : schoolsFilter) {
                if (!isFirst) {
                    builder.append(", ");
                }
                builder.append("'");
                builder.append(each.name());
                builder.append("'");
                isFirst = false;
            }
            builder.append(")");
            schoolsFilterString = builder.toString();
        }

        // TODO suspect this exists clause slows down the query
        // perhaps do a join...?
        if (cantrips) {
            return " upper(aClass) = ? and level = 0 " + schoolsFilterString;
        } else {
            return " upper(aClass) = ? and level > 0 and level <= ? " + schoolsFilterString;
        }
    }


    protected Uri getContentProviderUri() {
        final StringBuilder uri = new StringBuilder();
        uri.append("content://");
        uri.append(DnDContentProvider.sAuthority);
        uri.append("/");
        uri.append(DnDContentProvider.SPELL_AND_CLASSES);

        return Uri.parse(uri.toString());
    }

    @Nullable
    @Override
    protected String[] getSelectionArgs() {
        if (classNameSpinner == null) return null;
        final String className = ((ClassChoice) classNameSpinner.getSelectedItem()).casterClass;
        if (cantrips) {
            return new String[]{className.toUpperCase()};
        }
        String maxLevelString = max_spell_level.getText().toString();
//        int maxLevel = 9;
//        if (className == null) return null;
//        final Character.CastingClassInfo castingClassInfo = getCharacter().getCasterClassInfo().get(className);
//        if (castingClassInfo != null) {
//            maxLevel = castingClassInfo.getMaxSpellLevel();
//        }
        return new String[]{className.toUpperCase(), maxLevelString};
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
            final String className = ((ClassChoice) classNameSpinner.getSelectedItem()).ownerClass;
            return listener.spellSelected(id, className);
        }
        throw new RuntimeException("No Listener!");
    }

    static class ClassChoice implements Parcelable {
        final String ownerClass;
        final String casterClass;
        final int maxLevel;


        public static final Creator<ClassChoice> CREATOR = new Creator<ClassChoice>() {
            @NonNull
            @Override
            public ClassChoice createFromParcel(@NonNull Parcel in) {
                return new ClassChoice(in);
            }

            @NonNull
            @Override
            public ClassChoice[] newArray(int size) {
                return new ClassChoice[size];
            }
        };

        public ClassChoice(String owningClassName, String casterClassName, int maxLevel) {
            this.ownerClass = owningClassName;
            this.casterClass = casterClassName;
            this.maxLevel = maxLevel;
        }

        public ClassChoice(Parcel in) {
            ownerClass = in.readString();
            casterClass = in.readString();
            maxLevel = in.readInt();
        }

        @Override
        public String toString() {
            if (casterClass == null || casterClass.equals(ownerClass)) return ownerClass;
            return ownerClass + "(" + casterClass + ")";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ownerClass);
            dest.writeString(casterClass);
            dest.writeInt(maxLevel);
        }
    }


    protected int getListItemResource() {
        return R.layout.spell_list_item;
    }

    @NonNull
    @Override
    public RowViewHolder newRowViewHolder(@NonNull View newView) {
        return new SpellRowViewHolder(newView);
    }

    public static class SpellRowViewHolder extends RowViewHolder {

        private final TextView schoolTextView;
        private final TextView levelTextView;

        public SpellRowViewHolder(@NonNull View itemView) {
            super(itemView);
            schoolTextView = (TextView) itemView.findViewById(R.id.school);
            levelTextView = (TextView) itemView.findViewById(R.id.level);
        }


        @NonNull
        protected String getIdColumnName() {
            return "spell";
        }

        @Override
        public void bindTo(@NonNull Cursor cursor, @NonNull AbstractSelectComponentDialogFragment context, RecyclerView.Adapter adapter, @NonNull CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);

//            StringBuilder builder = new StringBuilder();
//            for(String eachColumn : cursor.getColumnNames()) {
//                int index = cursor.getColumnIndex(eachColumn);
//                builder.append(eachColumn);
//                builder.append(" = ");
//                builder.append(cursor.getString(index));
//                builder.append(", ");
//            }
//            Log.i("Spell", builder.toString());

            final int level = cursor.getInt(cursorIndexesByName.getIndex(cursor, "level"));
            String levelString;
            if (level == 0) {
                levelString = context.getString(R.string.cantrip_label);
            } else {
                levelString = level + "";
            }
            levelTextView.setText(levelString);

            final String schoolString = cursor.getString(cursorIndexesByName.getIndex(cursor, "school"));
            SpellSchool school = EnumHelper.stringToEnum(schoolString, SpellSchool.class);
            schoolTextView.setText(school.getStringResId());
        }
    }
}
