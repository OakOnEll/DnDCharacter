package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.spell.SpellSchool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 2/20/2016.
 */
public class EnumHelper {

    public static <T extends Enum<T>> List<T> commaListToEnum(String input, Class<T> clazz) {
        List<T> enums = null;
        if (input != null && input.trim().length() > 0) {
            String[] strings = input.split(",");
            for (String eachString : strings) {
                if (eachString.trim().length() == 0) continue;
                if (enums == null) enums = new ArrayList<>();
                enums.add(stringToEnum(eachString, clazz));
            }
        }
        return enums;
    }


    public static <T extends Enum<T>> T stringToEnum(String input, Class<T> clazz) {
        input = input.replaceAll(" ", "_");
        input = input.toUpperCase();
        return SkillType.valueOf(clazz, input);
    }

}
