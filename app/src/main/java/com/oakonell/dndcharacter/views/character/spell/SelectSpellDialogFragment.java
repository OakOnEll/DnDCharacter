package com.oakonell.dndcharacter.views.character.spell;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.DnDContentProvider;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.CursorIndexesByName;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.character.AbstractSelectComponentDialogFragment;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Rob on 1/3/2016.
 */
public class SelectSpellDialogFragment extends AbstractSelectComponentDialogFragment<AbstractSelectComponentDialogFragment.RowViewHolder> {

    public static final String CASTER_CLASSES = "casterClasses";
    public static final String CANTRIPS = "cantrips";
    private static final String SCHOOLS = "schools";
    private static final String RITUAL_ONLY = "ritualOnly";
    private static final String IGNORE_LIST = "ignoreList";

    private NoDefaultSpinner classNameSpinner;
    private TextView max_spell_level;
    private boolean cantrips;
    private SpellSelectedListener listener;

    @Nullable
    private List<SpellSchool> schoolsFilter = null;
    private boolean ritualOnly;
    @Nullable
    private ArrayList<String> ignoreSpellNamesList;


    private NoDefaultSpinner schoolFilterSpinner;
    private NoDefaultSpinner levelFilterSpinner;
    private ArrayAdapter<String> schoolFilterAdapter;
    private ArrayAdapter<String> levelFilterAdapter;

    public void setListener(SpellSelectedListener listener) {
        this.listener = listener;
    }

    public interface SpellSelectedListener {
        boolean spellSelected(long spellId, String className);
    }

    // Will use the current character's classes/caster classes to filter
    @NonNull
    public static SelectSpellDialogFragment createDialog(@NonNull Character character, boolean cantrips, SpellSelectedListener listener, @Nullable List<String> ignoreSpellNames) {
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

        if (ignoreSpellNames != null && !ignoreSpellNames.isEmpty()) {
            ArrayList<String> ignoreList = new ArrayList<>(ignoreSpellNames);
            args.putStringArrayList(IGNORE_LIST, ignoreList);
        }

        dialog.setArguments(args);
        dialog.setListener(listener);
        return dialog;
    }


    @NonNull
    public static SelectSpellDialogFragment createDialog(@NonNull String casterClass, int maxLevel, @Nullable List<SpellSchool> schools, boolean limitToRitual, SpellSelectedListener listener, @Nullable List<String> ignoreSpellNames) {
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

        if (ignoreSpellNames != null && !ignoreSpellNames.isEmpty()) {
            ArrayList<String> ignoreList = new ArrayList<>(ignoreSpellNames);
            args.putStringArrayList(IGNORE_LIST, ignoreList);
        }

        if (schools != null && !schools.isEmpty()) {
            ArrayList<String> schoolNames = new ArrayList<>();
            for (SpellSchool each : schools) {
                schoolNames.add(each.name());
            }
            args.putStringArrayList(SCHOOLS, schoolNames);
        }
        args.putBoolean(CANTRIPS, maxLevel == 0);
        args.putBoolean(RITUAL_ONLY, limitToRitual);

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
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateTheView(inflater, container, savedInstanceState);

        max_spell_level = (TextView) view.findViewById(R.id.max_spell_level);

        classNameSpinner = (NoDefaultSpinner) view.findViewById(R.id.class_name);
        final String prompt = getString(R.string.caster_class_prompt);
        classNameSpinner.setPrompt(prompt);
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (prompt.length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, classNameSpinner.getResources().getDisplayMetrics());
        classNameSpinner.setMinimumWidth((int) minWidth);

        schoolFilterSpinner = (NoDefaultSpinner) view.findViewById(R.id.school_filter);
        schoolFilterAdapter = new ArrayAdapter<>(getContext(), R.layout.large_spinner_text, new ArrayList<String>());
        schoolFilterAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        schoolFilterSpinner.setAdapter(schoolFilterAdapter);

        levelFilterSpinner = (NoDefaultSpinner) view.findViewById(R.id.level_filter);
        levelFilterAdapter = new ArrayAdapter<>(getContext(), R.layout.large_spinner_text, new ArrayList<String>());
        levelFilterAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        levelFilterSpinner.setAdapter(levelFilterAdapter);


        List<ClassChoice> classChoices = getArguments().getParcelableArrayList(CASTER_CLASSES);
        cantrips = getArguments().getBoolean(CANTRIPS);
        ritualOnly = getArguments().getBoolean(RITUAL_ONLY);


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
            updateSchoolFilterChoices(schoolsFilter);
        } else {
            view.findViewById(R.id.schools_group).setVisibility(View.GONE);
            updateSchoolFilterChoices(Arrays.asList(SpellSchool.values()));
        }


        ignoreSpellNamesList = getArguments().getStringArrayList(IGNORE_LIST);

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
            updateLevelFilterChoices(selectedItem.maxLevel);
        } else {
            classNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    final ClassChoice selectedItem = (ClassChoice) classNameSpinner.getSelectedItem();
                    max_spell_level.setText(NumberUtils.formatNumber(selectedItem.maxLevel));
                    updateLevelFilterChoices(selectedItem.maxLevel);
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

    private void updateLevelFilterChoices(int maxLevel) {
        levelFilterAdapter.clear();
        if (!cantrips && maxLevel > 1) {
            levelFilterSpinner.setVisibility(View.VISIBLE);
            // TODO
            levelFilterAdapter.add("Any Level");
            for (int level =1; level <= maxLevel; level++) {
                levelFilterAdapter.add(Integer.toString(level));
            }
        } else {
            levelFilterSpinner.setVisibility(View.GONE);
        }
        levelFilterAdapter.notifyDataSetChanged();

        levelFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateSchoolFilterChoices(List<SpellSchool> schoolsFilter) {
        schoolFilterAdapter.clear();
        if (schoolsFilter.size() > 1) {
            schoolFilterSpinner.setVisibility(View.VISIBLE);
            // TODO
            schoolFilterAdapter.add("Any School");
            for (SpellSchool each : schoolsFilter) {
                schoolFilterAdapter.add(getContext().getString(each.getStringResId()));
            }
        } else {
            schoolFilterSpinner.setVisibility(View.GONE);
        }
        schoolFilterAdapter.notifyDataSetChanged();

        schoolFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        if (schoolFilterSpinner.getSelectedItemPosition() > 0) {
            StringBuilder builder = new StringBuilder(" and school =");
            String school = (String) schoolFilterSpinner.getSelectedItem();
            builder.append("'");
            builder.append(EnumHelper.stringToEnum(school, SpellSchool.class).name());
            builder.append("' ");
            schoolsFilterString = builder.toString();
        } else if (schoolsFilter != null) {
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
            builder.append(") ");
            schoolsFilterString = builder.toString();
        }

        String notInString = "";
        if (ignoreSpellNamesList != null && ignoreSpellNamesList.size() > 0) {
            StringBuilder notInStringBuilder = new StringBuilder(" AND upper(name) not in (");
            boolean first = true;
            for (String each : ignoreSpellNamesList) {
                if (!first) notInStringBuilder.append(',');
                notInStringBuilder.append("'");
                notInStringBuilder.append(each.replaceAll("'", "''").toUpperCase());
                notInStringBuilder.append("'");
                first = false;
            }
            notInStringBuilder.append(") ");
            notInString = notInStringBuilder.toString();
        }

        String ritualFilter = "";
        if (ritualOnly) {
            ritualFilter = " AND ritual = 1 ";
        }

        String levelFilterString = "";
        if (levelFilterSpinner.getSelectedItemPosition() > 0) {
            StringBuilder builder = new StringBuilder(" and level = ");
            int level =  levelFilterSpinner.getSelectedItemPosition();
            builder.append(level);
            builder.append(" ");
            levelFilterString = builder.toString();
        }

        // doing a join for performance
        if (cantrips) {
            return " upper(aClass) = ? and level = 0 " + schoolsFilterString + ritualFilter + notInString + levelFilterString;
        } else {
            return " upper(aClass) = ? and level > 0 and level <= ? " + schoolsFilterString + ritualFilter + notInString + levelFilterString;
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

        public ClassChoice(@NonNull Parcel in) {
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
        public void writeToParcel(@NonNull Parcel dest, int flags) {
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

        @NonNull
        private final TextView schoolTextView;
        @NonNull
        private final TextView levelTextView;
        @NonNull
        private final TextView ritualTextView;


        public SpellRowViewHolder(@NonNull View itemView) {
            super(itemView);
            schoolTextView = (TextView) itemView.findViewById(R.id.school);
            levelTextView = (TextView) itemView.findViewById(R.id.level);
            ritualTextView = (TextView) itemView.findViewById(R.id.ritual);
        }


        @NonNull
        protected String getIdColumnName() {
            return "spell";
        }

        @Override
        public void bindTo(final Cursor cursor, AbstractSelectComponentDialogFragment context, final CursorComponentListAdapter adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);

            final int ritualInt = cursor.getInt(cursorIndexesByName.getIndex(cursor, "ritual"));
            if (ritualInt != 0) {
                ritualTextView.setVisibility(View.VISIBLE);
            } else {
                ritualTextView.setVisibility(View.GONE);
            }

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
