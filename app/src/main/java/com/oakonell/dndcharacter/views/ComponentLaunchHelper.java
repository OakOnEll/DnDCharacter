package com.oakonell.dndcharacter.views;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.views.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.classes.EditClassLevelDialogFragment;
import com.oakonell.dndcharacter.views.race.ApplyRaceDialogFragment;

/**
 * Created by Rob on 12/16/2015.
 */
public final class ComponentLaunchHelper {
    public static void editComponent(AppCompatActivity context, com.oakonell.dndcharacter.model.Character character, BaseCharacterComponent source) {
        switch (source.getType()) {
            case BACKGROUND:
                ApplyBackgroundDialogFragment dialog = ApplyBackgroundDialogFragment.createDialog(character);
                dialog.show(context.getSupportFragmentManager(), "background");
                break;
            case CLASS:
                CharacterClass charClass = (CharacterClass) source;
                int position = character.getClasses().indexOf(charClass);
                EditClassLevelDialogFragment classDialog = EditClassLevelDialogFragment.createDialog(character, charClass, position, null);
                classDialog.show(context.getSupportFragmentManager(), "class");
                break;
            case RACE:
                ApplyRaceDialogFragment raceDialog = ApplyRaceDialogFragment.createDialog(character);
                raceDialog.show(context.getSupportFragmentManager(), "race");
                break;
            case ITEM:
                break;
        }
    }
}
