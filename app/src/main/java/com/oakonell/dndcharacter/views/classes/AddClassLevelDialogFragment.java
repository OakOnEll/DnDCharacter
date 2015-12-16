package com.oakonell.dndcharacter.views.classes;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterClass;
import com.oakonell.dndcharacter.model.RandomUtils;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.model.StatType;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.classes.ApplyClassToCharacterVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.ApplyAbstractComponentDialogFragment;
import com.oakonell.dndcharacter.views.md.ChooseMD;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class AddClassLevelDialogFragment extends ApplyAbstractComponentDialogFragment<AClass> {
    int classLevel;
    int hp;

    public static AddClassLevelDialogFragment createDialog(Character character, AClass aClass) {
        AddClassLevelDialogFragment newMe = new AddClassLevelDialogFragment();
        newMe.setModel(aClass);
        newMe.setCharacter(character);
        return newMe;
    }


    protected List<Page<AClass>> createPages() {
        List<Page<AClass>> pages = new ArrayList<>();
        if (getModel() == null) return pages;

        Element rootClassElement = XmlUtils.getDocument(getModel().getXml()).getDocumentElement();
        final Element levelElement = AClass.findLevelElement(rootClassElement, classLevel);

        final boolean isFirstLevel = getCharacter().getCharacterLevel() < 1;
        if (isFirstLevel) {
            // first page, show base skills, saving throws, hit dice..
            Page main = new Page<AClass>() {
                @Override
                public Map<String, ChooseMD> appendToLayout(AClass aClass, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                    AbstractComponentViewCreator visitor = new AbstractComponentViewCreator();
                    Element element = XmlUtils.getDocument(aClass.getXml()).getDocumentElement();
                    return visitor.appendToLayout(element, dynamic, backgroundChoices);
                }
            };
            pages.add(main);

            final int maxHp = AClass.getHitDieSides(rootClassElement);
            hp = maxHp;

            // page two, show equipment?

        } else {
            hp = 0;
        }

        // next page, show level specific stuff (if first, show hit die, or just show again)
        // TODO show when proficiency is increased!
        if (levelElement != null) {
            Page level = new Page<AClass>() {
                @Override
                public Map<String, ChooseMD> appendToLayout(AClass aClass, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                    AbstractComponentViewCreator visitor = new AbstractComponentViewCreator();
                    return visitor.appendToLayout(levelElement, dynamic, backgroundChoices);
                }
            };
            pages.add(level);
        }

        // final page, show Hit points- unless classLevel = 1
        if (!isFirstLevel) {
            Page hitPoints = new Page<AClass>() {
                @Override
                public Map<String, ChooseMD> appendToLayout(AClass aClass, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                    ViewGroup hpView = (ViewGroup) LayoutInflater.from(dynamic.getContext()).inflate(R.layout.level_hitpoints_layout, dynamic);
                    Button rollButton = (Button) hpView.findViewById(R.id.roll_button);
                    TextView hitDiceView = (TextView) hpView.findViewById(R.id.hit_dice);
                    final TextView hp_increase = (TextView) hpView.findViewById(R.id.hp_increase);
                    TextView con_mod = (TextView) hpView.findViewById(R.id.con_mod);
                    final EditText roll1 = (EditText) hpView.findViewById(R.id.roll1);

                    final int conModifier = getCharacter().getStatBlock(StatType.CONSTITUTION).getModifier();
                    con_mod.setText(conModifier + "");

                    Element rootClassElement = XmlUtils.getDocument(aClass.getXml()).getDocumentElement();
                    String hitDieString = XmlUtils.getElementText(rootClassElement, "hitDice");
                    hitDiceView.setText(hitDieString);
                    final int maxHp = AClass.getHitDieSides(rootClassElement);

                    rollButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int rollValue = RandomUtils.random(1, maxHp);
                            roll1.setText(rollValue + "");
                        }
                    });

                    roll1.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String string = s.toString();
                            int val = 0;
                            if (string != null && string.trim().length() > 0) {
                                val = Integer.parseInt(string);
                            }
                            hp = val;
                            hp_increase.setText((hp + conModifier) + "");
                        }
                    });

                    return Collections.emptyMap();
                }
            };
            pages.add(hitPoints);
        }

        return pages;
    }

    @Override
    protected void modelChanged() {
        Integer currentLevel = getCharacter().getClassLevels().get(getModel().getName());
        if (currentLevel == null) currentLevel = 0;
        classLevel = currentLevel + 1;
    }

    @Override
    protected void applyToCharacter(SavedChoices savedChoices, Map<String, String> customChoices) {
        ApplyClassToCharacterVisitor.addClassLevel(getModel(), savedChoices, customChoices, getCharacter(), classLevel, hp);
    }

    @NonNull
    @Override
    protected Class<? extends AClass> getModelClass() {
        return AClass.class;
    }

    @Override
    public String getModelSpinnerPrompt() {
        return "[Class]";
    }


    protected SavedChoices getSavedChoicesFromCharacter(Character character) {
        // a new class level won't have save choices
        return new SavedChoices();
    }

    protected String getCurrentName() {
        List<CharacterClass> classes = getCharacter().getClasses();
        if (classes.isEmpty()) return "";
        // continue with the last class
        return classes.get(classes.size() - 1).getName();
    }


}
