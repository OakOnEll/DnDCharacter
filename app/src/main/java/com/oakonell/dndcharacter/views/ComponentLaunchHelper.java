package com.oakonell.dndcharacter.views;


import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.oakonell.dndcharacter.model.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.CharacterClass;
import com.oakonell.dndcharacter.views.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.classes.EditClassLevelDialogFragment;
import com.oakonell.dndcharacter.views.race.ApplyRaceDialogFragment;

/**
 * Created by Rob on 12/16/2015.
 */
public final class ComponentLaunchHelper {
    public static void editComponent(FragmentActivity context, com.oakonell.dndcharacter.model.Character character, BaseCharacterComponent source, OnDialogDone onDone, boolean includeClassHP) {
        switch (source.getType()) {
            case BACKGROUND:
                ApplyBackgroundDialogFragment dialog = ApplyBackgroundDialogFragment.createDialog(character, onDone);
                dialog.show(context.getSupportFragmentManager(), "background");
                break;
            case CLASS:
                CharacterClass charClass = (CharacterClass) source;
                int position = character.getClasses().indexOf(charClass);
                EditClassLevelDialogFragment classDialog = EditClassLevelDialogFragment.createDialog(character, charClass, position, onDone, includeClassHP);
                classDialog.show(context.getSupportFragmentManager(), "class");
                break;
            case RACE:
                ApplyRaceDialogFragment raceDialog = ApplyRaceDialogFragment.createDialog(character, onDone);
                raceDialog.show(context.getSupportFragmentManager(), "race");
                break;
            case ITEM:
                break;
        }
    }

    public interface OnDialogDone {
        public void done(boolean changed);
    }

}
