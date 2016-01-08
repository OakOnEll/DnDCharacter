package com.oakonell.dndcharacter.views.character;


import android.widget.Toast;

import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.views.character.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.character.classes.EditClassLevelDialogFragment;
import com.oakonell.dndcharacter.views.character.race.ApplyRaceDialogFragment;

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
            case FEATURE:
                Toast.makeText(context, "Opening feature source not supported yet", Toast.LENGTH_SHORT).show();
                break;
            case EFFECT:
                // is this one needed?
                Toast.makeText(context, "Opening effect source not supported yet", Toast.LENGTH_SHORT).show();
                break;
            case ITEM:
                Toast.makeText(context, "Opening item source not supported yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
