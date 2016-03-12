package com.oakonell.dndcharacter.views.character;

import android.support.annotation.NonNull;

/**
 * Created by Rob on 12/26/2015.
 */
public interface CharacterChangedListener {
    void onCharacterChanged(@NonNull com.oakonell.dndcharacter.model.character.Character character);
}
