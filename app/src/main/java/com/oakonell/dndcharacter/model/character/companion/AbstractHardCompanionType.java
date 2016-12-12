package com.oakonell.dndcharacter.model.character.companion;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rob on 12/10/2016.
 */

public abstract class AbstractHardCompanionType extends AbstractCompanionType {

    public abstract int getStringResId();

    public abstract int getDescriptionResId();

    public abstract int getShortDescriptionResId();

    public String getName(Resources resources) {
        return resources.getString(getStringResId());
    }

    public String getDescription(Resources resources) {
        return resources.getString(getDescriptionResId());
    }

    public String getShortDescription(Resources resources) {
        return resources.getString(getShortDescriptionResId());
    }

    // ---------------- all types, as classes for now

    static final List<AbstractCompanionType> types;

    static {
        List<AbstractCompanionType> typesWork = new ArrayList<>();
//        typesWork.add(new CompanionTypeFamiliar());
        typesWork.add(new CompanionTypeMount());
        typesWork.add(new CompanionTypePet());
        typesWork.add(new CompanionTypePolymorph());
//        typesWork.add(new CompanionTypeUndead());
//        typesWork.add(new CompanionTypeWildShape());
//        typesWork.add(new CompanionTypeRangerCompanion());
        types = Collections.unmodifiableList(typesWork);
    }

    public static List<AbstractCompanionType> values() {
        return types;
    }


    public static AbstractCompanionType fromString(String typeString) {
        for (AbstractCompanionType each : values()) {
            if (each.getType().equals(typeString)) return each;
        }
        throw new RuntimeException("Unknown companion type: " + typeString);
    }

}
