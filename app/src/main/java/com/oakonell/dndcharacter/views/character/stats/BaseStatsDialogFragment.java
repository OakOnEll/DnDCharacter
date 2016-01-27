package com.oakonell.dndcharacter.views.character.stats;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.stats.BaseStatsType;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.RandomUtils;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 12/15/2015.
 */
public class BaseStatsDialogFragment extends AbstractCharacterDialogFragment {
    private Spinner stat_type;
    private TextView race_bonuses;
    private ViewGroup customGroup;
    private ViewGroup simpleGroup;
    private ViewGroup point_buyGroup;
    private ViewGroup roll_layout;


    private BaseStatsType statsType;
    private final Map<StatType, EditText> customInputs = new HashMap<>();
    private final List<BaseStatRow> baseStatRows = new ArrayList<>();

    // point buy
    private final List<BaseStatPointBuy> pointBuyRows = new ArrayList<>();
    private TextView remaining_points;

    // rolled stats
    private final Integer[] statRolls = new Integer[6];
    private Spinner roll_type;
    private List<BaseStatsType> list;

    public static BaseStatsDialogFragment createDialog() {
        return new BaseStatsDialogFragment();
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_stats, container);

        stat_type = (Spinner) view.findViewById(R.id.stat_type);
        race_bonuses = (TextView) view.findViewById(R.id.race_bonuses);

        customGroup = (ViewGroup) view.findViewById(R.id.custom);
        simpleGroup = (ViewGroup) view.findViewById(R.id.simple);
        point_buyGroup = (ViewGroup) view.findViewById(R.id.point_buy);
        roll_layout = (ViewGroup) view.findViewById(R.id.roll_layout);


        list = new ArrayList<>(Arrays.asList(BaseStatsType.values()));
        ArrayAdapter<BaseStatsType> dataAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        stat_type.setAdapter(dataAdapter);

        stat_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statsType = list.get(position);

                switch (statsType) {
                    case CUSTOM:
                        break;
                    case SIMPLE:
                        baseStatRows.get(0).setValue(15);
                        baseStatRows.get(1).setValue(14);
                        baseStatRows.get(2).setValue(13);
                        baseStatRows.get(3).setValue(12);
                        baseStatRows.get(4).setValue(10);
                        baseStatRows.get(5).setValue(8);
                        break;
                    case POINT_BUY:
                        break;
                    case ROLL:
                        int i = 0;
                        for (Integer val : statRolls) {
                            baseStatRows.get(i).setValue(val);
                            i++;
                        }
                        break;
                }


                updateView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // custom inputs
        customInputs.put(StatType.STRENGTH, (EditText) view.findViewById(R.id.strength));
        customInputs.put(StatType.DEXTERITY, (EditText) view.findViewById(R.id.dexterity));
        customInputs.put(StatType.CONSTITUTION, (EditText) view.findViewById(R.id.constitution));
        customInputs.put(StatType.INTELLIGENCE, (EditText) view.findViewById(R.id.intelligence));
        customInputs.put(StatType.WISDOM, (EditText) view.findViewById(R.id.wisdom));
        customInputs.put(StatType.CHARISMA, (EditText) view.findViewById(R.id.charisma));

        //  add text watcher validation to custom edit texts (eg, only allow 3-18 inclusive)
        for (final EditText each : customInputs.values()) {
            each.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String string = s.toString();
                    if (string.trim().length() == 0) {
                        // allow empty text boxes, until done hit
                        return;
                    }
                    int value = Integer.parseInt(string);
                    // allow a '1', assuming it will be followed by a 0-8...
                    if (value != 1 && (value < 3 || value > 18)) {
                        each.setError(getString(R.string.enter_a_number_between_3_18));
                        return;
                    }
                    each.setError(null);
                }
            });
        }


        // simple inputs
        baseStatRows.add(new BaseStatRow(0, StatType.STRENGTH, (TextView) view.findViewById(R.id.stat_name1), (TextView) view.findViewById(R.id.value1), (ImageView) view.findViewById(R.id.up1), (ImageView) view.findViewById(R.id.down1)));
        baseStatRows.add(new BaseStatRow(1, StatType.DEXTERITY, (TextView) view.findViewById(R.id.stat_name2), (TextView) view.findViewById(R.id.value2), (ImageView) view.findViewById(R.id.up2), (ImageView) view.findViewById(R.id.down2)));
        baseStatRows.add(new BaseStatRow(2, StatType.CONSTITUTION, (TextView) view.findViewById(R.id.stat_name3), (TextView) view.findViewById(R.id.value3), (ImageView) view.findViewById(R.id.up3), (ImageView) view.findViewById(R.id.down3)));
        baseStatRows.add(new BaseStatRow(3, StatType.INTELLIGENCE, (TextView) view.findViewById(R.id.stat_name4), (TextView) view.findViewById(R.id.value4), (ImageView) view.findViewById(R.id.up4), (ImageView) view.findViewById(R.id.down4)));
        baseStatRows.add(new BaseStatRow(4, StatType.WISDOM, (TextView) view.findViewById(R.id.stat_name5), (TextView) view.findViewById(R.id.value5), (ImageView) view.findViewById(R.id.up5), (ImageView) view.findViewById(R.id.down5)));
        baseStatRows.add(new BaseStatRow(5, StatType.CHARISMA, (TextView) view.findViewById(R.id.stat_name6), (TextView) view.findViewById(R.id.value6), (ImageView) view.findViewById(R.id.up6), (ImageView) view.findViewById(R.id.down6)));

        baseStatRows.get(0).up.setVisibility(View.INVISIBLE);
        baseStatRows.get(5).down.setVisibility(View.INVISIBLE);

        for (final BaseStatRow each : baseStatRows) {
            each.up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final BaseStatRow other = baseStatRows.get(each.index - 1);
                    StatType tempType = other.type;
                    other.type = each.type;
                    each.type = tempType;
                    other.typeView.setText(other.type.toString());
                    each.typeView.setText(each.type.toString());
                }
            });
            each.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final BaseStatRow other = baseStatRows.get(each.index + 1);
                    StatType tempType = other.type;
                    other.type = each.type;
                    each.type = tempType;
                    other.typeView.setText(other.type.toString());
                    each.typeView.setText(each.type.toString());
                }
            });

        }

        // Point buy inputs
        pointBuyRows.add(0, new BaseStatPointBuy(StatType.STRENGTH, (Button) view.findViewById(R.id.strength_add), (TextView) view.findViewById(R.id.strength_buy), (Button) view.findViewById(R.id.strength_subtract)));
        pointBuyRows.add(1, new BaseStatPointBuy(StatType.DEXTERITY, (Button) view.findViewById(R.id.dexterity_add), (TextView) view.findViewById(R.id.dexterity_buy), (Button) view.findViewById(R.id.dexterity_subtract)));
        pointBuyRows.add(2, new BaseStatPointBuy(StatType.CONSTITUTION, (Button) view.findViewById(R.id.constitution_add), (TextView) view.findViewById(R.id.constitution_buy), (Button) view.findViewById(R.id.constitution_subtract)));
        pointBuyRows.add(3, new BaseStatPointBuy(StatType.INTELLIGENCE, (Button) view.findViewById(R.id.intelligence_add), (TextView) view.findViewById(R.id.intelligence_buy), (Button) view.findViewById(R.id.intelligence_subtract)));
        pointBuyRows.add(4, new BaseStatPointBuy(StatType.WISDOM, (Button) view.findViewById(R.id.wisdom_add), (TextView) view.findViewById(R.id.wisdom_buy), (Button) view.findViewById(R.id.wisdom_subtract)));
        pointBuyRows.add(5, new BaseStatPointBuy(StatType.CHARISMA, (Button) view.findViewById(R.id.charisma_add), (TextView) view.findViewById(R.id.charisma_buy), (Button) view.findViewById(R.id.charisma_subtract)));

        for (final BaseStatPointBuy each : pointBuyRows) {
            each.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pointBuy(each, 1);
                }
            });
            each.subtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pointBuy(each, -1);
                }
            });
        }

        remaining_points = (TextView) view.findViewById(R.id.remaining_points);

        // roll inputs
        roll_type = (Spinner) view.findViewById(R.id.roll_type);
        Button roll = (Button) view.findViewById(R.id.roll);

        final List<String> rollTypes = new ArrayList<>();
        rollTypes.add(getString(R.string.roll_3d6));
        rollTypes.add(getString(R.string.roll_4d6_best_3));
        ArrayAdapter<String> rollTypeAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, rollTypes);
        rollTypeAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        roll_type.setAdapter(rollTypeAdapter);
        roll_type.setSelection(0);

        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollStats();
            }
        });
        //                         baseStatRows.get(0).value.setText("15");


        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.base_stats_title);
    }


    @Override
    protected boolean onDone() {
        // update dialog that opened this one...
        Map<StatType, Integer> baseStats = new HashMap<>();
        // TODO validate overall...? any general?

        // push chosen values to character model
        switch (statsType) {
            case CUSTOM:
                // validate custom inputs
                boolean hadError = false;
                for (Map.Entry<StatType, EditText> entry : customInputs.entrySet()) {
                    EditText editText = entry.getValue();
                    String string = editText.getText().toString();
                    if (string.trim().length() == 0) {
                        editText.setError(getString(R.string.enter_a_number_between_3_18));
                    }
                    int num = Integer.parseInt(string);
                    if (num < 3 || num > 18) {
                        editText.setError(getString(R.string.enter_a_number_between_3_18));
                    }
                    if (editText.getError() != null) {
                        if (!hadError) {
                            Animation shake = AnimationUtils.loadAnimation(BaseStatsDialogFragment.this.getContext(), R.anim.shake);
                            editText.startAnimation(shake);
                            //editText.requestFocus();
                        }
                        hadError = true;
                    }

                }
                if (hadError) return false;

                for (Map.Entry<StatType, EditText> entry : customInputs.entrySet()) {
                    StatType type = entry.getKey();
                    EditText editText = entry.getValue();
                    baseStats.put(type, Integer.parseInt(editText.getText().toString()));
                }
                break;
            case ROLL:
                // fall through
            case SIMPLE:
                for (BaseStatRow each : baseStatRows) {
                    baseStats.put(each.type, each.getValue());
                }
                break;
            case POINT_BUY:
                // validate point buy
                if (Integer.parseInt(remaining_points.getText().toString()) != 0) {
                    Animation shake = AnimationUtils.loadAnimation(BaseStatsDialogFragment.this.getContext(), R.anim.shake);
                    remaining_points.startAnimation(shake);
                    remaining_points.setError(getString(R.string.there_are_still_remaining_points, remaining_points.getText()));
                    return false;
                }
                for (BaseStatPointBuy each : pointBuyRows) {
                    baseStats.put(each.type, each.getValue());
                }
                break;
        }
        Character character = getCharacter();
        character.setBaseStats(baseStats);
        character.setStatsType(statsType);

        return super.onDone();
    }

    @Override
    public void onCharacterChanged(Character character) {
        super.onCharacterChanged(character);
        // pull current values from the character, and display
        statsType = character.getStatsType();
        stat_type.setSelection(list.indexOf(statsType));

        Map<StatType, Integer> stats = character.getBaseStats();

        if (statsType == null) statsType = BaseStatsType.CUSTOM;
        switch (statsType) {
            case CUSTOM: {
                int i = 0;
                for (Map.Entry<StatType, EditText> entry : customInputs.entrySet()) {
                    StatType type = entry.getKey();
                    final Integer value = stats.get(type);
                    statRolls[i] = value;
                    EditText editText = entry.getValue();
                    editText.setText(NumberUtils.formatNumber(value));
                    i++;
                }
            }
            break;
            case POINT_BUY:
                pointBuyRows.get(0).setValue(stats.get(StatType.STRENGTH));
                pointBuyRows.get(1).setValue(stats.get(StatType.DEXTERITY));
                pointBuyRows.get(2).setValue(stats.get(StatType.CONSTITUTION));
                pointBuyRows.get(3).setValue(stats.get(StatType.INTELLIGENCE));
                pointBuyRows.get(4).setValue(stats.get(StatType.WISDOM));
                pointBuyRows.get(5).setValue(stats.get(StatType.CHARISMA));

                final int remainingPoints = calculateRemainingPoints();
                remaining_points.setText(NumberUtils.formatNumber(remainingPoints));

                updatePointBuyRows(remainingPoints);
                break;
            case ROLL:
                // fall through
            case SIMPLE: {
                List<Map.Entry<StatType, Integer>> types = new ArrayList<>(stats.entrySet());
                Collections.sort(types, new Comparator<Map.Entry<StatType, Integer>>() {
                    @Override
                    public int compare(Map.Entry<StatType, Integer> lhs, Map.Entry<StatType, Integer> rhs) {
                        return -lhs.getValue().compareTo(rhs.getValue());
                    }
                });
                int i = 0;
                for (Map.Entry<StatType, Integer> each : types) {
                    statRolls[i] = each.getValue();
                    final BaseStatRow baseStatRow = baseStatRows.get(i);
                    baseStatRow.type = each.getKey();
                    baseStatRow.setValue(each.getValue());
                    baseStatRow.typeView.setText(baseStatRow.type.toString());
                    i++;
                }
            }
            break;
        }
        updateView();
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);

        onCharacterChanged(character);

    }

    private void pointBuy(BaseStatPointBuy each, int increase) {
        remaining_points.setError(null);
        int current = each.getValue();
        int currentCost = pointCost(current);
        int newScore = each.getValue() + increase;
        int newCost = pointCost(newScore);

        int currentPoints = Integer.parseInt(remaining_points.getText().toString());
        int newPoints = currentPoints - newCost + currentCost;

        remaining_points.setText(NumberUtils.formatNumber(newPoints));
        each.setValue(newScore);

        updatePointBuyRows(newPoints);
    }

    private void updatePointBuyRows(int currentPoints) {
        for (BaseStatPointBuy each : pointBuyRows) {
            int current = each.getValue();
            int currentCost = pointCost(current);
            int newScore = each.getValue() + 1;
            int newCost = pointCost(newScore);
            int newPoints = currentPoints - newCost + currentCost;

            each.add.setEnabled(each.getValue() < 15 && newPoints >= 0);

            each.subtract.setEnabled(each.getValue() > 8);
        }
    }

    private int pointCost(int score) {
        if (score <= 8) return 0;
        if (score == 9) return 1;
        if (score == 10) return 2;
        if (score == 11) return 3;
        if (score == 12) return 4;
        if (score == 13) return 5;
        if (score == 14) return 7;
        if (score == 15) return 9;
        return 1000;
    }

    private int calculateRemainingPoints() {
        int points = 27;
        for (BaseStatPointBuy each : pointBuyRows) {
            int val = each.getValue();
            points -= pointCost(val);
        }
        return points;
    }

    private void rollStats() {
        boolean useBest3_4 = roll_type.getSelectedItemPosition() == 1;
        for (int i = 0; i < 6; i++) {
            statRolls[i] = rollStat(useBest3_4);
        }
        Arrays.sort(statRolls, Collections.reverseOrder());
        for (int i = 0; i < 6; i++) {
            BaseStatRow row = baseStatRows.get(i);
            Integer newValue = statRolls[i];
            row.setValue(newValue);
        }

    }

    private int rollStat(boolean useBest3_4) {
        int rolls = 3;
        if (useBest3_4) rolls++;

        Integer[] values = new Integer[4];
        for (int i = 0; i < rolls; i++) {
            values[i] = RandomUtils.random(1, 6);
        }
        int result = 0;
        if (useBest3_4) {
            Arrays.sort(values, Collections.reverseOrder());
        }
        for (int i = 0; i < 3; i++) {
            result += values[i];
        }

        return result;
    }

    private void updateView() {
        switch (statsType) {
            case CUSTOM:
                customGroup.setVisibility(View.VISIBLE);
                simpleGroup.setVisibility(View.GONE);
                point_buyGroup.setVisibility(View.GONE);
                roll_layout.setVisibility(View.GONE);
                break;
            case SIMPLE:
                customGroup.setVisibility(View.GONE);
                simpleGroup.setVisibility(View.VISIBLE);
                point_buyGroup.setVisibility(View.GONE);
                roll_layout.setVisibility(View.GONE);
                break;
            case POINT_BUY:
                final int remainingPoints = calculateRemainingPoints();
                remaining_points.setText(NumberUtils.formatNumber(remainingPoints));

                updatePointBuyRows(remainingPoints);

                customGroup.setVisibility(View.GONE);
                simpleGroup.setVisibility(View.GONE);
                point_buyGroup.setVisibility(View.VISIBLE);
                roll_layout.setVisibility(View.GONE);
                break;
            case ROLL:
                customGroup.setVisibility(View.GONE);
                simpleGroup.setVisibility(View.VISIBLE);
                point_buyGroup.setVisibility(View.GONE);
                roll_layout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private static class BaseStatPointBuy {
        final StatType type;
        final Button add;
        final TextView valueTextView;
        final Button subtract;

        public BaseStatPointBuy(StatType type, Button add, TextView value, Button subtract) {
            this.type = type;
            this.add = add;
            this.valueTextView = value;
            this.subtract = subtract;
        }

        public int getValue() {
            return Integer.parseInt(valueTextView.getText().toString());
        }

        public void setValue(int theValue) {
            valueTextView.setText(NumberUtils.formatNumber(theValue));
        }
    }

    private static class BaseStatRow {
        final int index;
        StatType type;
        final TextView typeView;
        final TextView valueTextView;
        final ImageView up;
        final ImageView down;

        public BaseStatRow(int index, StatType type, TextView typeView, TextView value, ImageView up, ImageView down) {
            this.index = index;
            this.type = type;
            this.typeView = typeView;
            this.valueTextView = value;
            this.up = up;
            this.down = down;
        }

        public int getValue() {
            return Integer.parseInt(valueTextView.getText().toString());
        }

        public void setValue(int theValue) {
            valueTextView.setText(NumberUtils.formatNumber(theValue));
        }
    }

}
