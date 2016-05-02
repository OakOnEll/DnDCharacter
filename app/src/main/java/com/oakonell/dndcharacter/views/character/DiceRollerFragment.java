package com.oakonell.dndcharacter.views.character;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
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
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.SoundFXUtils;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.expression.Expression;
import com.oakonell.expression.ExpressionContext;
import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.context.SimpleFunctionContext;
import com.oakonell.expression.context.SimpleVariableContext;
import com.oakonell.expression.grammar.ExpressionParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 5/2/2016.
 */
public class DiceRollerFragment extends AbstractDialogFragment {

    private NoDefaultSpinner dice_formulas;
    private EditText dice_formula;
    private ImageView save_dice_formula;
    private Button roll_button;
    private TextView roll_total;

    private Map<String, String> formulas;
    private List<String> savedFormulaNames;
    private ArrayAdapter<String> formulaNamesAdapter;

    public static DiceRollerFragment createDialog() {
        DiceRollerFragment newMe = new DiceRollerFragment();
        return newMe;
    }

    @Nullable
    @Override
    protected String getTitle() {
        return getString(R.string.dice_roller);
    }

    @Nullable
    @Override
    protected View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dice_roller_dialog, container);

        dice_formulas = (NoDefaultSpinner) view.findViewById(R.id.dice_formulas);
        dice_formula = (EditText) view.findViewById(R.id.dice_formula);
        save_dice_formula = (ImageView) view.findViewById(R.id.save_dice_formula);
        roll_button = (Button) view.findViewById(R.id.roll_button);
        roll_total = (TextView) view.findViewById(R.id.roll_total);


        dice_formula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                roll_button.setEnabled(s.length() > 0);
            }
        });

        save_dice_formula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on save
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                b.setTitle(R.string.enter_formula_name);

                final EditText input = new EditText(getActivity());
                b.setView(input);
                b.setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final @NonNull DialogInterface dialog, int which) {
                        // validate input name is provided
                        final String name = input.getText().toString();
                        if (name.trim().length() == 0) {
                            input.setError(getString(R.string.enter_a_name));
                            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                            input.startAnimation(shake);
                            return;
                        }
                        // deal with duplicate formula name- prompt to overwrite, else leave dialog open
                        if (savedFormulaNames.contains(name)) {
                            promptOverwriteExistingFormula(new Runnable() {
                                @Override
                                public void run() {
                                    saveFormula(dialog, name);
                                }
                            });
                            return;
                        }
                        saveFormula(dialog, name);
                    }
                });
                b.setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                b.show();
            }
        });

        roll_button.setEnabled(false);
        roll_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roll();
            }
        });


        String data = getActivity().getSharedPreferences(CharacterActivity.MyPREFERENCES, Context.MODE_PRIVATE).getString("saved_formulas", null);
        formulas = new HashMap<>();
        if (data != null) {
            try {
                JSONArray arr = new JSONArray(data);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject json = arr.getJSONObject(i);
                    formulas.put(json.getString("name"), json.getString("formula"));
                }
            } catch (Exception e) {
                // skip any
            }
        }


        final String prompt = getString(R.string.saved_dice_formula_prompt);
        dice_formulas.setPrompt(prompt);
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (prompt.length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, dice_formulas.getResources().getDisplayMetrics());
        dice_formulas.setMinimumWidth((int) minWidth);

        // populate formulas
        savedFormulaNames = new ArrayList<>();
        for (String each : formulas.keySet()) {
            savedFormulaNames.add(each);
        }
        formulaNamesAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, savedFormulaNames);
        formulaNamesAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        dice_formulas.setAdapter(formulaNamesAdapter);

        dice_formulas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) dice_formulas.getItemAtPosition(position);
                String formula = formulas.get(name);
                dice_formula.setText(formula);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    protected void saveFormula(@NonNull DialogInterface dialog, String name) {
        dialog.dismiss();

        formulas.put(name, dice_formula.getText().toString());
        savedFormulaNames.remove(name);
        savedFormulaNames.add(name);
        formulaNamesAdapter.notifyDataSetChanged();
        //  store formula
        try {
            JSONArray arr = new JSONArray();
            for (Map.Entry<String, String> entry : formulas.entrySet()) {
                JSONObject json = new JSONObject();
                json.put("name", entry.getKey());
                json.put("formula", entry.getValue());
                arr.put(json);
            }
            getActivity().getSharedPreferences(CharacterActivity.MyPREFERENCES, Context.MODE_PRIVATE).edit().putString("saved_formulas", arr.toString()).apply();
        } catch (JSONException exception) {
            // Do something with exception
        }
        // when done, set the spinner to the newly entered formula name
        dice_formulas.setSelection(formulaNamesAdapter.getCount() - 1);
    }

    private void promptOverwriteExistingFormula(final Runnable runnable) {
        new AlertDialog.Builder(this.getActivity())
                .setMessage(R.string.overwrite_existing_formula)
                .setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        runnable.run();
                    }
                })
                .setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
    }

    private void roll() {
        String formula = dice_formula.getText().toString();
        Integer result = 0;
        try {
            final Expression<Integer> expression = Expression.parse(formula, ExpressionType.NUMBER_TYPE, new ExpressionContext(new SimpleFunctionContext(), new SimpleVariableContext()));
            result = expression.evaluate();
        } catch (Exception e) {
            dice_formula.setError(getString(R.string.invalid_dice_formula));
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            dice_formula.startAnimation(shake);
            return;
        }

        SoundFXUtils.playDiceRoll(getActivity());
        roll_total.setText(NumberUtils.formatNumber(result));
    }

}
