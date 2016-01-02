package com.oakonell.dndcharacter.views;


import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.model.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.CharacterClass;
import com.oakonell.dndcharacter.views.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.classes.EditClassLevelDialogFragment;
import com.oakonell.dndcharacter.views.race.ApplyRaceDialogFragment;

/**
 * Created by Rob on 12/16/2015.
 */
public final class ComponentLaunchHelper {
    public static void editComponent(MainActivity context, BaseCharacterComponent source, boolean includeClassHP) {
        switch (source.getType()) {
            case BACKGROUND:
                ApplyBackgroundDialogFragment dialog = ApplyBackgroundDialogFragment.createDialog();
                dialog.show(context.getSupportFragmentManager(), "background");
                break;
            case CLASS:
                CharacterClass charClass = (CharacterClass) source;
                int position = context.getCharacter().getClasses().indexOf(charClass);
                EditClassLevelDialogFragment classDialog = EditClassLevelDialogFragment.createDialog(position, includeClassHP);
                classDialog.show(context.getSupportFragmentManager(), "class");
                break;
            case RACE:
                ApplyRaceDialogFragment raceDialog = ApplyRaceDialogFragment.createDialog();
                raceDialog.show(context.getSupportFragmentManager(), "race");
                break;
            case ITEM:
                break;
        }
    }



}
