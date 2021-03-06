package com.oakonell.dndcharacter.model.item;

import android.content.Context;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 12/29/2015.
 */
public class CreateCharacterArmorVisitor extends AbstractArmorVisitor {
    CharacterArmor armor;

    private CreateCharacterArmorVisitor(CharacterArmor armor) {
        this.armor = armor;
    }

    @NonNull
    public static CharacterArmor createArmor(@NonNull Context context, @NonNull ItemRow row, @NonNull AbstractCharacter character) {
        CharacterArmor armor = new CharacterArmor();

        final Element root = XmlUtils.getDocument(row.getXml()).getDocumentElement();
        ApplyChangesToGenericComponent.applyToCharacter(context, root, null, armor, character, false);

        final String ac = XmlUtils.getElementText(root, "ac");
        armor.setAcFormula(ac);

        final String condition = XmlUtils.getElementText(root, "condition");
        armor.setActiveFormula(condition);


        CreateCharacterArmorVisitor newMe = new CreateCharacterArmorVisitor(armor);
        newMe.visit(row);

        if (character != null) {
            character.addArmor(armor);
        }

        return armor;
    }


    @Override
    protected void visitCategory(@NonNull Element element) {
        String category = element.getTextContent();
        armor.setCategory(category);
    }

    @Override
    protected void visitStrengthMin(@NonNull Element element) {
        super.visitStrengthMin(element);
    }

    @Override
    protected void visitDisadvantage(@NonNull Element element) {
        super.visitDisadvantage(element);
    }

}
