package com.oakonell.dndcharacter.views.character.persona;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import org.solovyev.android.views.llm.LinearLayoutManager;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 1/7/2016.
 */
public class RaceLooksDialogFragment extends AbstractCharacterDialogFragment {
    public static final String RACE_NAME_ARG = "raceName";
    public static final String SUBRACE_NAME_ARG = "subraceName";
    public static final String DATA_ARG = "data";
    private EditText ageText;
    private EditText weightText;
    private EditText heightText;

    private EditText hairText;
    private EditText skinText;
    private EditText eyesText;

    private TextView age_description;

    private TextView averageHeight;
    private TextView averageWeight;

    private ViewGroup random_height_group;
    private TextView base_height;
    private TextView result_height;
    private EditText random_height_roll;
    private Button height_roll_button;
    private Button acceptHeight;

    private ViewGroup random_weight_group;
    private TextView base_weight;
    private EditText random_weight_roll;
    private Button weight_roll_button;
    private TextView result_weight;
    private TextView dup_height_roll;
    private Button acceptWeight;

    private Map<String, String> data = null;


    @NonNull
    public static RaceLooksDialogFragment create(String raceName, String subraceName, Map<String, String> data) {
        RaceLooksDialogFragment dialog = new RaceLooksDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RACE_NAME_ARG, raceName);
        bundle.putString(SUBRACE_NAME_ARG, subraceName);
        Bundle dataBundle = new Bundle();
        for (Map.Entry<String, String> each : data.entrySet()) {
            dataBundle.putString(each.getKey(), each.getValue());
        }
        bundle.putBundle(DATA_ARG, dataBundle);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    public static RaceLooksDialogFragment create() {
        return new RaceLooksDialogFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.looks);
    }

    @Override
    protected View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (getArguments() != null) {
            view = inflater.inflate(R.layout.race_looks_layout, container, false);
        } else {
            view = inflater.inflate(R.layout.race_looks_dialog, container, false);
        }

        ageText = (EditText) view.findViewById(R.id.age);
        weightText = (EditText) view.findViewById(R.id.weight);
        heightText = (EditText) view.findViewById(R.id.height);

        hairText = (EditText) view.findViewById(R.id.hair);
        skinText = (EditText) view.findViewById(R.id.skin);
        eyesText = (EditText) view.findViewById(R.id.eyes);

        age_description = (TextView) view.findViewById(R.id.age_description);

        averageHeight = (TextView) view.findViewById(R.id.average_height);
        averageWeight = (TextView) view.findViewById(R.id.average_weight);

        random_height_group = (ViewGroup) view.findViewById(R.id.random_height_group);
        random_height_group.setVisibility(View.GONE);

        base_height = (TextView) view.findViewById(R.id.base_height);
        random_height_roll = (EditText) view.findViewById(R.id.random_height_roll);
        height_roll_button = (Button) view.findViewById(R.id.height_roll_button);
        result_height = (TextView) view.findViewById(R.id.result_height);
        acceptHeight = (Button) view.findViewById(R.id.accept_height);


        random_weight_group = (ViewGroup) view.findViewById(R.id.random_weight_group);
        random_weight_group.setVisibility(View.GONE);

        base_weight = (TextView) view.findViewById(R.id.base_weight);
        random_weight_roll = (EditText) view.findViewById(R.id.random_weight_roll);
        weight_roll_button = (Button) view.findViewById(R.id.weight_roll_button);
        dup_height_roll = (TextView) view.findViewById(R.id.dup_height_roll);
        result_weight = (TextView) view.findViewById(R.id.result_weight);
        acceptWeight = (Button) view.findViewById(R.id.accept_weight);

        if (getArguments() != null) {
            Bundle dataBundle = getArguments().getBundle(DATA_ARG);
            if (dataBundle != null) {
                data = new HashMap<>();
                for (String key : dataBundle.keySet()) {
                    data.put(key, dataBundle.getString(key));
                }
            }
        }

        return view;
    }


    private String getRaceProperty(Element raceRoot, Element subraceRoot, String propertyName) {
        if (subraceRoot != null) {
            String text = XmlUtils.getElementText(subraceRoot, propertyName);
            if (text != null && text.trim().length() > 0) return text;
        }
        String text = XmlUtils.getElementText(raceRoot, propertyName);
        if (text == null || text.trim().length() == 0) return null;
        return text;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        // TODO load the race, and get the height/weight/age descriptions
        String raceName = getArguments() != null ? getArguments().getString(RACE_NAME_ARG) : null;
        String subraceName;
        if (raceName != null) {
            subraceName = getArguments().getString(SUBRACE_NAME_ARG);
        } else {
            raceName = character.getRaceName();
            subraceName = character.getSubRaceName();
        }

        final Race race = new Select().from(Race.class).where("upper(name) = upper(?)", raceName).executeSingle();
        final Element raceRoot = XmlUtils.getDocument(race.getXml()).getDocumentElement();

        Race subrace = null;
        Element subraceRoot = null;
        if (subraceName != null) {
            subrace = new Select().from(Race.class).where("upper(name) = upper(?)", subraceName).executeSingle();
            subraceRoot = XmlUtils.getDocument(subrace.getXml()).getDocumentElement();
        }

        String ageDescr = getRaceProperty(raceRoot, subraceRoot, "age");
        if (ageDescr != null) {
            age_description.setText(ageDescr);
        }
        String heightDescr = getRaceProperty(raceRoot, subraceRoot, "height");
        if (heightDescr != null) {
            averageHeight.setText(heightDescr);
        }
        String weightDescr = getRaceProperty(raceRoot, subraceRoot, "weight");
        if (weightDescr != null) {
            averageWeight.setText(weightDescr);
        }

        configureRandomHeight(raceRoot, subraceRoot);
        configureRandomWeight(raceRoot, subraceRoot);

        updateViews();
    }

    protected void configureRandomHeight(Element raceRoot, Element subraceRoot) {
        final String baseHeight = getRaceProperty(raceRoot, subraceRoot, "baseHeight");
        final String heightModifier = getRaceProperty(raceRoot, subraceRoot, "heightModifier");
        if (baseHeight != null) {
            random_height_group.setVisibility(View.VISIBLE);
            base_height.setText(NumberUtils.formatLength(Integer.parseInt(baseHeight)));
            random_height_roll.setHint(heightModifier);

            random_height_roll.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int roll = 0;
                    if (s.toString().trim().length() > 0) {
                        try {
                            roll = Integer.parseInt(s.toString());
                        } catch (Exception e) {
                            // ignore excpetions, let it pass through as a 0
                        }
                    }
                    int resultHeight = Integer.parseInt(baseHeight) + roll;
                    result_height.setText(NumberUtils.formatLength(resultHeight));
                    dup_height_roll.setText(s);
                    random_weight_roll.setText(random_weight_roll.getText());
                }
            });
            height_roll_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int roll = getCharacter().evaluateFormula(heightModifier, null);
                    random_height_roll.setText(NumberUtils.formatNumber(roll));
                }
            });
        }
        acceptHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heightText.setText(result_height.getText());
            }
        });
    }

    protected void configureRandomWeight(Element raceRoot, Element subraceRoot) {
        final String baseWeight = getRaceProperty(raceRoot, subraceRoot, "baseWeight");
        final String weightModifier = getRaceProperty(raceRoot, subraceRoot, "weightMultiplier");
        if (baseWeight != null) {
            random_weight_group.setVisibility(View.VISIBLE);
            base_weight.setText(NumberUtils.formatNumber(Integer.parseInt(baseWeight)));
            random_weight_roll.setHint(weightModifier);

            random_weight_roll.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int roll = 0;
                    if (s.toString().trim().length() > 0) {
                        try {
                            roll = Integer.parseInt(s.toString());
                        } catch (Exception e) {
                            // ignore excpetions, let it pass through as a 0
                        }
                    }

                    int heightRoll = 0;
                    if (dup_height_roll.getText().toString().trim().length() > 0) {
                        try {
                            heightRoll = Integer.parseInt(dup_height_roll.getText().toString());
                        } catch (Exception e) {
                            // ignore excpetions, let it pass through as a 0
                        }
                    }
                    int resultWeight = Integer.parseInt(baseWeight) + roll * heightRoll;
                    result_weight.setText(NumberUtils.formatNumber(resultWeight));

                }
            });
            weight_roll_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int roll = getCharacter().evaluateFormula(weightModifier, null);
                    random_weight_roll.setText(NumberUtils.formatNumber(roll));
                }
            });
        }
        acceptWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightText.setText(result_weight.getText());
            }
        });

    }

    @Override
    public void onCharacterChanged(Character character) {
        super.onCharacterChanged(character);
        updateViews();
    }

    protected void updateViews() {
        Character character = getCharacter();

        String age;
        String weight;
        String height;
        String hair;
        String skin;
        String eyes;
        if (data != null) {
            age = data.get("age");
            weight = data.get("weight");
            height = data.get("height");
            hair = data.get("hair");
            skin = data.get("skin");
            eyes = data.get("eyes");
        } else {
            age = NumberUtils.formatNumber(character.getAge());
            weight = NumberUtils.formatNumber(character.getWeight());
            height = character.getHeight();
            hair = character.getHair();
            skin = character.getSkin();
            eyes = character.getEyes();
        }
        ageText.setText(age);
        weightText.setText(weight);
        heightText.setText(height);

        hairText.setText(hair);
        skinText.setText(skin);
        eyesText.setText(eyes);
    }

    public Map<String, String> getData() {
        data.put("age", ageText.getText().toString());
        data.put("weight", weightText.getText().toString());
        data.put("height", heightText.getText().toString());

        data.put("hair", hairText.getText().toString());
        data.put("skin", skinText.getText().toString());
        data.put("eyes", eyesText.getText().toString());

        return data;
    }

    @Override
    protected boolean onDone() {
        Character character = getCharacter();
        character.setAge(Integer.parseInt(ageText.getText().toString()));
        character.setWeight(Integer.parseInt(weightText.getText().toString()));
        character.setHeight(heightText.getText().toString());

        character.setHair(hairText.getText().toString());
        character.setSkin(skinText.getText().toString());
        character.setEyes(eyesText.getText().toString());

        return super.onDone();
    }


}
