package com.oakonell.dndcharacter.views.character.companion;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.companion.AbstractCompanionType;
import com.oakonell.dndcharacter.model.character.companion.AbstractHardCompanionType;
import com.oakonell.dndcharacter.model.character.companion.CharacterCompanion;
import com.oakonell.dndcharacter.model.character.companion.CompanionRace;
import com.oakonell.dndcharacter.model.character.stats.BaseStatsType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.companion.Companion;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.CursorIndexesByName;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.character.AbstractSelectComponentDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.OnCharacterLoaded;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 12/3/2016.
 */

public class SelectCompanionDialogFragment extends AbstractSelectComponentDialogFragment<AbstractSelectComponentDialogFragment.RowViewHolder> implements OnCharacterLoaded {
    private SelectCompanionDialogFragment.CompanionSelectedListener listener;
    private NoDefaultSpinner typeSpinner;
    private List<AbstractCompanionType> companionTypes;
    private TextView typeInvisibleErrorTextView;
    private TextView descrTextView;
    private NoDefaultSpinner crSpinner;
    private TextView crInvisibleErrorTextView;
    private CheckBox limit_by_cr;
    private ArrayAdapter<String> crAdapter;
    private boolean delaySearch;


    private static class CompanionRowViewHolder extends RowViewHolder {

        @NonNull
        private final TextView crTextView;
        @NonNull
        private final TextView sizeTextView;

        public CompanionRowViewHolder(@NonNull View itemView) {
            super(itemView);
            crTextView = (TextView) itemView.findViewById(R.id.cr);
            sizeTextView = (TextView) itemView.findViewById(R.id.size);
        }


        @Override
        public void bindTo(final Cursor cursor, AbstractSelectComponentDialogFragment context, final CursorComponentListAdapter adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);

            final String cr = cursor.getString(cursorIndexesByName.getIndex(cursor, "cr"));
            crTextView.setText(cr);

            final String size = cursor.getString(cursorIndexesByName.getIndex(cursor, "creature_size"));
            sizeTextView.setText(size);
        }

    }

    public void setListener(SelectCompanionDialogFragment.CompanionSelectedListener listener) {
        this.listener = listener;
    }

    public interface CompanionSelectedListener {
        boolean companionSelected(long id, AbstractCompanionType type);
    }

    @NonNull
    public static SelectCompanionDialogFragment createDialog(SelectCompanionDialogFragment.CompanionSelectedListener listener) {
        final SelectCompanionDialogFragment dialogFragment = new SelectCompanionDialogFragment();
        dialogFragment.setListener(listener);
        return dialogFragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.add_companion);
    }

    protected boolean limitRacesToType() {
        AbstractCompanionType type = companionTypes.get(typeSpinner.getSelectedItemPosition());
        return type.usesLimitedRaces();
    }

    @Override
    protected boolean canSearch() {
        if (typeSpinner == null || limit_by_cr == null) return false;
        int typeIndex = typeSpinner.getSelectedItemPosition();
        if (typeIndex < 0) {
            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            typeSpinner.startAnimation(shake);
            typeInvisibleErrorTextView.setError(getString(R.string.choose_companion_type_error));
            return false;
        }

        if (limit_by_cr.isChecked()) {
            if (crSpinner.getSelectedItemPosition() < 0) {
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                crSpinner.startAnimation(shake);
                crInvisibleErrorTextView.setError(getString(R.string.cr_required_error));
                return false;
            }
        }
        return true;
    }

    @Nullable
    @Override
    protected String getSelection() {
        if (typeSpinner == null) return null;
        if (typeSpinner.getSelectedItem() == null) return null;

        String filter = "";

        AbstractCompanionType type = companionTypes.get(typeSpinner.getSelectedItemPosition());
        if (type.usesLimitedRaces()) {
            StringBuilder builder = new StringBuilder();
            builder.append(" upper(name) in (");
            // TODO add limited races
            boolean appendComma = false;
            for (String each : type.getLimitedRaces()) {
                if (appendComma) {
                    builder.append(",");
                }
                appendComma = true;
                builder.append("upper('");
                builder.append(each);
                builder.append("')");
            }

            builder.append(")");
            filter += builder.toString();
        }

        if (limit_by_cr.isChecked()) {
            // TODO
            String cr = (String) crSpinner.getSelectedItem();
            double value = Companion.getCRRealValue(cr);
            if (filter.length() > 0) {
                filter += " AND ";
            }
            filter += " cr_value <= ? ";
        }

        return filter;
    }

    @Nullable
    @Override
    protected String[] getSelectionArgs() {
        if (typeSpinner == null) return null;
        if (typeSpinner.getSelectedItem() == null) return null;

        AbstractCompanionType type = companionTypes.get(typeSpinner.getSelectedItemPosition());

        List<String> args = new ArrayList<>();


        if (limit_by_cr.isChecked()) {
            // TODO
            String cr = (String) crSpinner.getSelectedItem();
            double value = Companion.getCRRealValue(cr);
            args.add(Double.toString(value));
        }

        return args.toArray(new String[args.size()]);
    }


    protected int getDialogResource() {
        return R.layout.companion_search_dialog;
    }


    protected int getListItemResource() {
        return R.layout.companion_search_item;
    }

    @NonNull
    @Override
    public RowViewHolder newRowViewHolder(@NonNull View newView) {
        return new CompanionRowViewHolder(newView);
    }

    @Nullable
    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateTheView(inflater, container, savedInstanceState);


        limit_by_cr = (CheckBox) view.findViewById(R.id.limit_by_cr);
        crSpinner = (NoDefaultSpinner) view.findViewById(R.id.cr);
        crInvisibleErrorTextView = (TextView) view.findViewById(R.id.crInvisibleError);
        String crPrompt = getString(R.string.challenge_rating);
        crSpinner.setPrompt("[" + crPrompt + "]");
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (crPrompt.length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, crSpinner.getResources().getDisplayMetrics());
        crSpinner.setMinimumWidth((int) minWidth);

        List<String> crValues = new ArrayList<>();
        crValues.add("0");
        crValues.add("1/8");
        crValues.add("1/4");
        crValues.add("1/2");
        for (int i = 1; i < 20; i++) {
            crValues.add(i + "");
        }

        crAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, crValues);
        crAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        crSpinner.setAdapter(crAdapter);

        limit_by_cr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crInvisibleErrorTextView.setError(null);
                if (!delaySearch) {
                    search();
                }
            }
        });

        crSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                crInvisibleErrorTextView.setError(null);
                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                descrTextView.setText("");
            }
        });


        // TODO handle filter by companion type
        typeSpinner = (NoDefaultSpinner) view.findViewById(R.id.type);
        typeInvisibleErrorTextView = (TextView) view.findViewById(R.id.typeInvisibleError);
        descrTextView = (TextView) view.findViewById(R.id.type_descr);

        String prompt = getString(R.string.companion_type_prompt);

        typeSpinner.setPrompt("[" + prompt + "]");
        minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (prompt.length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, typeSpinner.getResources().getDisplayMetrics());
        typeSpinner.setMinimumWidth((int) minWidth);


        getMainActivity().addCharacterLoadLister(this);


        return view;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        companionTypes = new ArrayList<>(AbstractHardCompanionType.values());

        companionTypes.addAll(character.getCompanionTypes());

        Collections.sort(companionTypes, new Comparator<AbstractCompanionType>() {
            @Override
            public int compare(AbstractCompanionType o1, AbstractCompanionType o2) {
                return o1.getName(getResources()).compareTo(o2.getName(getResources()));
            }
        });

        List<String> typeLabels = new ArrayList<>();
        for (AbstractCompanionType each : companionTypes) {
            typeLabels.add(each.getName(getResources()));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, typeLabels);
        dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        typeSpinner.setAdapter(dataAdapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeInvisibleErrorTextView.setError(null);
                delaySearch = true;
                int index = typeSpinner.getSelectedItemPosition();
                String descr = "";
                if (index >= 0) {
                    AbstractCompanionType type = companionTypes.get(index);
                    descr = type.getShortDescription(getResources());

                    final String crLimit = type.getCrLimit(getCharacter());
                    if (crLimit != null && crLimit.trim().length() > 0) {
                        limit_by_cr.setChecked(true);
                        int crIndex = -1;
                        for (int i = 0; i < crAdapter.getCount(); i++) {
                            String val = crAdapter.getItem(i);
                            if (val.equals(crLimit)) {
                                crIndex = i;
                                break;
                            }
                        }
                        crSpinner.setSelection(crIndex);
                    } else {
                        limit_by_cr.setChecked(false);
                        crSpinner.setNoSelection();
                    }
                }
                // TODO on click, show the full description
                descrTextView.setText(descr);
                delaySearch = false;

                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                descrTextView.setText("");
            }
        });

    }


    @NonNull
    @Override
    public Class<? extends Companion> getComponentClass() {
        return Companion.class;
    }

    @Override
    protected boolean applyAction(long id) {
        if (listener != null) {
            int typeIndex = typeSpinner.getSelectedItemPosition();
            if (typeIndex < 0) {
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                typeSpinner.startAnimation(shake);
                typeInvisibleErrorTextView.setError(getString(R.string.choose_companion_type_error));
                return false;
            }
            AbstractCompanionType type = companionTypes.get(typeIndex);
            return listener.companionSelected(id, type);
        }
        throw new RuntimeException("No Listener!");
    }


    public static class AddCompanionToCharacterListener implements SelectCompanionDialogFragment.CompanionSelectedListener {

        private final CharacterActivity activity;

        public AddCompanionToCharacterListener(CharacterActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean companionSelected(long id, final AbstractCompanionType type) {
            final Companion companion = Companion.load(Companion.class, id);

            // prompt for name TODO make a more standard dialog

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Name of new " + type.getName(activity.getResources()) + " " + companion.getName());

// Set up the input
            final EditText input = new EditText(activity);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();

                    addCompanion(companion, name, type);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();


            return true;
        }

        protected void addCompanion(Companion companion, String name, AbstractCompanionType type) {
            final Element root = XmlUtils.getDocument(companion.getXml()).getDocumentElement();
            Map<StatType, Integer> baseStats = new HashMap<>();
            Element stats = XmlUtils.getElement(root, "stats");


            final List<CharacterCompanion> companions = activity.getCharacter().getCompanions();

            // TODO copy data from model Companion to the character companion being added
            // TODO set the type from the search filter
            CharacterCompanion charCompanion = new CharacterCompanion();
            charCompanion.setName(name);

            charCompanion.setType(type);

            charCompanion.setStatsType(BaseStatsType.CUSTOM);

            baseStats.put(StatType.STRENGTH, Integer.parseInt(XmlUtils.getElementText(stats, "strength")));
            baseStats.put(StatType.DEXTERITY, Integer.parseInt(XmlUtils.getElementText(stats, "dexterity")));
            baseStats.put(StatType.CONSTITUTION, Integer.parseInt(XmlUtils.getElementText(stats, "constitution")));
            baseStats.put(StatType.INTELLIGENCE, Integer.parseInt(XmlUtils.getElementText(stats, "intelligence")));
            baseStats.put(StatType.WISDOM, Integer.parseInt(XmlUtils.getElementText(stats, "wisdom")));
            baseStats.put(StatType.CHARISMA, Integer.parseInt(XmlUtils.getElementText(stats, "charisma")));
            charCompanion.setBaseStats(baseStats);

            charCompanion.setBaseAC(Integer.parseInt(XmlUtils.getElementText(root, "ac")));
            charCompanion.setHP(charCompanion.getMaxHP());


            CompanionRace race = new CompanionRace();

            // TODO
            ApplyChangesToGenericComponent.applyToCharacter(activity, root, null, race, charCompanion, false);

            final List<Element> speeds = XmlUtils.getChildElements(root, "speed");
            SpeedType defaultType = SpeedType.WALK;
            for (Element each : speeds) {
                String speedFormula = each.getTextContent();
                String typeStr = each.getAttribute("type");
                SpeedType speedType = SpeedType.WALK;
                if (typeStr != null && typeStr.length() > 0) {
                    speedType = EnumHelper.stringToEnum(typeStr, SpeedType.class);
                }
                race.setSpeedFormula(speedType, speedFormula);
                String defaultStr = each.getAttribute("default");
                if ("true".equals(defaultStr)) {
                    defaultType = speedType;
                }
            }
            race.setName(XmlUtils.getElementText(root, "name"));

            charCompanion.setRace(race);
            companions.add(charCompanion);
            charCompanion.setSpeedType(defaultType);

            activity.updateViews();
            Runnable continuation = new Runnable() {
                @Override
                public void run() {
                    activity.updateViews();
                    activity.saveCharacter();
                }
            };
            continuation.run();
        }

    }

    @Nullable
    public final com.oakonell.dndcharacter.model.character.Character getCharacter() {
        if (getActivity() instanceof CharacterActivity) {
            return getMainActivity().getCharacter();
        }
        return null;
    }

    protected CharacterActivity getMainActivity() {
        return (CharacterActivity) getActivity();
    }


}
