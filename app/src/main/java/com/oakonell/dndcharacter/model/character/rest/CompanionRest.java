package com.oakonell.dndcharacter.model.character.rest;

/**
 * Created by Rob on 12/26/2016.
 */

public interface CompanionRest extends RestRequest {
    String getName();

    void setName(String name);

    boolean shouldReset();

    void shouldReset(boolean isChecked);

    String getDescription();

    void setDescription(String description);

    void setCompanionIndex(int index);

    int getCompanionIndex();
}
