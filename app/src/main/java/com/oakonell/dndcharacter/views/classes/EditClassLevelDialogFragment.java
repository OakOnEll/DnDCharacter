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

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterClass;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.model.StatType;
import com.oakonell.dndcharacter.model.background.Background;
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
import java.util.Random;

/**
 * Created by Rob on 11/9/2015.
 */
public class EditClassLevelDialogFragment extends ApplyAbstractComponentDialogFragment<AClass> {
    int hp;
    CharacterClass characterClass;
    private int classIndex;
    Runnable onChange;

    public static EditClassLevelDialogFragment createDialog(Character character, CharacterClass characterClass, int classIndex, Runnable onChange) {
        EditClassLevelDialogFragment newMe = new EditClassLevelDialogFragment();
        AClass aClass = new Select().from(Background.class).where("name = ?", characterClass.getName()).executeSingle();
        newMe.setModel(aClass);
        newMe.setClassIndex(classIndex);
        newMe.setCharacterClass(characterClass);
        newMe.setCharacter(character);
        newMe.onChange = onChange;
        return newMe;
    }

    protected boolean allowMainComponentChange() {
        return false;
    }

    protected List<Page<AClass>> createPages() {
        List<Page<AClass>> pages = new ArrayList<>();
        if (getModel() == null) return pages;

        Element rootClassElement = XmlUtils.getDocument(getModel().getXml()).getDocumentElement();
        final Element levelElement = AClass.findLevelElement(rootClassElement, characterClass.getLevel());

        final boolean isFirstLevel = getCharacter().getClasses().get(0) == characterClass;
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
            hp = characterClass.getHpRoll();
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
                    final Random rand = new Random();

                    // set the current values
                    roll1.setText((hp) + "");
                    hp_increase.setText((hp + conModifier) + "");

                    rollButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int rollValue = rand.nextInt(maxHp - 1) + 1;
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
    protected void applyToCharacter(SavedChoices savedChoices, Map<String, String> customChoices) {
        ApplyClassToCharacterVisitor.updateClassLevel(getModel(), savedChoices, customChoices, getCharacter(), classIndex, characterClass.getLevel(), hp);
        if (onChange != null) onChange.run();
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
        return characterClass.getSavedChoices();
    }

    protected String getCurrentName() {
        return characterClass.getName();
    }


    public void setCharacterClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    public int getClassIndex() {
        return classIndex;
    }
}
