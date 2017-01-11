package com.oakonell.dndcharacter.model;

import android.content.Context;
import android.util.Log;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.background.ApplyBackgroundToCharacterVisitor;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.CharacterRow;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.classes.ApplyClassToCharacterVisitor;
import com.oakonell.dndcharacter.model.race.ApplyRaceToCharacterVisitor;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.ProgressUpdater;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by Rob on 3/17/2016.
 */
public class UpdateCharacters {

    public static int getNumberCharacters() {
        return new Select().from(CharacterRow.class).count();
    }

    public static DataImporter.CharacterUpdateProgress updateCharacters(Context context, ProgressUpdater progress) {
        DataImporter.CharacterUpdateProgress result = new DataImporter.CharacterUpdateProgress();
        final List<CharacterRow> list = new Select().from(CharacterRow.class).orderBy("last_updated").execute();
        Serializer serializer = new Persister();
        result.total = list.size();

        for (CharacterRow each : list) {
            if (progress.isCancelled()) {
                return result;
            }
            try {
                ByteArrayInputStream input = new ByteArrayInputStream(each.xml.getBytes());
                Character character = serializer.read(Character.class, input);

                // apply background
                String backgroundName = character.getBackgroundName();
                if (backgroundName != null) {
                    Background background = new Select().from(Background.class).where("name = ?", backgroundName).executeSingle();
                    if (background != null) {
                        ApplyBackgroundToCharacterVisitor.applyToCharacter(context, background, character.getBackgroundChoices(), null, character, true);
                    } else {
                        Log.d("UpdateCharacters", "Character " + character.getName() + " has no background");
                    }
                }
                // apply race
                String raceName = character.getRaceName();
                if (raceName != null) {
                    Race race = new Select().from(Race.class).where("name = ? ", character.getRaceName()).executeSingle();
                    if (race != null) {
                        String subraceName = character.getSubRaceName();
                        Race subrace = subraceName == null ? null : (Race) new Select().from(Race.class).where("name = ? ", subraceName).executeSingle();
                        ApplyRaceToCharacterVisitor.applyToCharacter(context, race, character.getRaceChoices(), null, subrace, character.getSubRaceChoices(), null, character, true);
                    } else {
                        Log.d("UpdateCharacters", "Character " + character.getName() + " has no race");
                    }
                }
                // apply classes
                int classIndex = 0;
                for (CharacterClass charClass : character.getClasses()) {
                    // concern with item deletion... need to prevent
                    AClass aClass = new Select().from(AClass.class).where("name = ?", charClass.getName()).executeSingle();
                    String subclassName = charClass.getSubclassName();
                    AClass aSubClass = subclassName == null ? null : (AClass) new Select().from(AClass.class).where("name = ?", charClass.getSubclassName()).executeSingle();
                    ApplyClassToCharacterVisitor.updateClassLevel(context, aClass, charClass.getSavedChoices(), null, aSubClass, charClass.getSubClassChoices(), character, classIndex, charClass.getLevel(), charClass.getHpRoll(), true);
                    classIndex++;
                }

                // apply spells..?

                // apply items... needed because we aren't redoing items for the class

                // apply feats- or was this from class?

                String newXml = CharacterRow.characterToXmlString(serializer, character);
                if (!newXml.equals(each.xml)) {
                    Log.d("UpdateCharacters", "Character " + character.getName() + " was updated on import");
                    CharacterRow.saveCharacter(context, serializer, character, each.getId());
                }
            } catch (Exception e) {
                Log.e("UpdateCharacters", "Error updating character " + each.getName() + ": " + e.getMessage(), e);
                result.errors.add("Error updating character " + each.getName() + ": " + e.getMessage());
                result.error++;
            }
            result.progress++;
            progress.progress(result);
        }
        return result;
    }
}
