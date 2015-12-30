package com.oakonell.dndcharacter.views.background;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.model.background.ApplyBackgroundToCharacterVisitor;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.ApplyAbstractComponentDialogFragment;
import com.oakonell.dndcharacter.views.md.ChooseMDTreeNode;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyBackgroundDialogFragment extends ApplyAbstractComponentDialogFragment<Background> {

    public static ApplyBackgroundDialogFragment createDialog() {
        return new ApplyBackgroundDialogFragment();
    }


    @Override
    public void onCharacterLoaded(Character character) {
        Background background = new Select().from(Background.class).where("name = ?", character.getBackgroundName()).executeSingle();
        setModel(background);

        super.onCharacterLoaded(character);
    }

    protected List<Page<Background>> createPages() {
        List<Page<Background>> pages = new ArrayList<>();
        Page main = new Page<Background>() {
            @Override
            public ChooseMDTreeNode appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                AbstractComponentViewCreator visitor = new AbstractComponentViewCreator();
                Element element = XmlUtils.getDocument(background.getXml()).getDocumentElement();
                return visitor.appendToLayout(element, dynamic, backgroundChoices);
            }
        };

        Page trait = new Page<Background>() {
            @Override
            public ChooseMDTreeNode appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                return visitor.appendToLayout(background, dynamic, backgroundChoices, customChoices, "traits");
            }
        };
        Page ideal = new Page<Background>() {
            @Override
            public ChooseMDTreeNode appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                return visitor.appendToLayout(background, dynamic, backgroundChoices, customChoices, "ideals");
            }
        };
        Page bond = new Page<Background>() {
            @Override
            public ChooseMDTreeNode appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                return visitor.appendToLayout(background, dynamic, backgroundChoices, customChoices, "bonds");
            }
        };
        Page flaw = new Page<Background>() {
            @Override
            public ChooseMDTreeNode appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                return visitor.appendToLayout(background, dynamic, backgroundChoices, customChoices, "flaws");
            }
        };
        Page extra = null;
        if (getModel() != null) {
            Element documentElement = XmlUtils.getDocument(getModel().getXml()).getDocumentElement();
            if (!XmlUtils.getChildElements(documentElement, "specialties").isEmpty()) {
                extra = new Page<Background>() {
                    @Override
                    public ChooseMDTreeNode appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                        BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                        return visitor.appendToLayout(background, dynamic, backgroundChoices, customChoices, "specialties");
                    }
                };
            }
        }
        pages.add(main);
        if (extra != null) {
            pages.add(extra);
        }
        pages.add(trait);
        pages.add(ideal);
        pages.add(bond);
        pages.add(flaw);
        return pages;
    }

    @Override
    protected void applyToCharacter(SavedChoices savedChoices, Map<String, String> customChoices) {
        ApplyBackgroundToCharacterVisitor.applyToCharacter(getModel(), savedChoices, customChoices, getCharacter());
    }

    @NonNull
    @Override
    protected Class<? extends Background> getModelClass() {
        return Background.class;
    }

    @Override
    public String getModelSpinnerPrompt() {
        return "[Background]";
    }


    @NonNull
    protected Map<String, String> getCustomChoicesFromCharacter(Character character) {
        SavedChoices savedChoices = character.getBackgroundChoices();
        Map<String, String> customChoices = new HashMap<>();
        if (savedChoices.getChoicesFor("traits").contains("custom")) {
            customChoices.put("traits", character.getPersonalityTrait());
        }
        if (savedChoices.getChoicesFor("ideals").contains("custom")) {
            customChoices.put("ideals", character.getIdeals());
        }
        if (savedChoices.getChoicesFor("bonds").contains("custom")) {
            customChoices.put("bonds", character.getBonds());
        }
        if (savedChoices.getChoicesFor("flaws").contains("custom")) {
            customChoices.put("flaws", character.getFlaws());
        }
        return customChoices;
    }

    protected SavedChoices getSavedChoicesFromCharacter(Character character) {
        return character.getBackgroundChoices();
    }

    protected String getCurrentName() {
        return getCharacter().getBackgroundName();
    }
}