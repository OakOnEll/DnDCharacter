package com.oakonell.dndcharacter.views.race;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.race.ApplyRaceToCharacterVisitor;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.ApplyAbstractComponentDialogFragment;
import com.oakonell.dndcharacter.views.md.ChooseMD;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyRaceDialogFragment extends ApplyAbstractComponentDialogFragment<Race> {
    public static ApplyRaceDialogFragment createDialog(Character character, Race race) {
        ApplyRaceDialogFragment newMe = new ApplyRaceDialogFragment();
        newMe.setModel(race);
        newMe.setCharacter(character);
        return newMe;
    }
    protected List<Page<Race>> createPages() {
        List<Page<Race>> result = new ArrayList<Page<Race>>();
        Page main = new Page<Race>() {
            @Override
            public Map<String, ChooseMD> appendToLayout(Race model, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                AbstractComponentViewCreator visitor = new AbstractComponentViewCreator();
                Element element = XmlUtils.getDocument(model.getXml()).getDocumentElement();
                return visitor.appendToLayout(element, dynamic, backgroundChoices);
            }
        };


        result.add(main);
        return result;
    }

    protected void applyToCharacter(SavedChoices savedChoices, Map<String, String> customChoices) {
        ApplyRaceToCharacterVisitor.applyToCharacter(getModel(), savedChoices, customChoices, getCharacter());
    }


    @NonNull
    protected Class<? extends Race> getModelClass() {
        return Race.class;
    }

    protected SavedChoices getSavedChoicesFromCharacter(com.oakonell.dndcharacter.model.Character character) {
        return character.getRaceChoices();
    }

     protected String getCurrentName() {
        return getCharacter().getRaceName();
    }

}