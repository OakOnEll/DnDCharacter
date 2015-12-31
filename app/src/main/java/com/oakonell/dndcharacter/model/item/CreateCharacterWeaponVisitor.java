package com.oakonell.dndcharacter.model.item;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.CharacterWeapon;
import com.oakonell.dndcharacter.model.DamageType;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 12/29/2015.
 */
public class CreateCharacterWeaponVisitor extends AbstractWeaponVisitor {
    private final CharacterWeapon weapon;

    public CreateCharacterWeaponVisitor(CharacterWeapon weapon) {
        this.weapon = weapon;
    }

    public static CharacterWeapon createWeapon(ItemRow row, com.oakonell.dndcharacter.model.Character character) {
        CharacterWeapon weapon = new CharacterWeapon();

        ApplyChangesToGenericComponent.applyToCharacter(XmlUtils.getDocument(row.getXml()).getDocumentElement(), null, weapon, character, false);

        CreateCharacterWeaponVisitor newMe = new CreateCharacterWeaponVisitor(weapon);
        newMe.visit(row);

        character.addWeapon(weapon);

        return weapon;
    }

    @Override
    protected void visitDamage(Element element) {
        String amount = XmlUtils.getElementText(element, "amount");
        String typeString = XmlUtils.getElementText(element, "damageType");
        DamageType type = DamageType.valueOf(typeString.substring(0, 1).toUpperCase() + typeString.substring(1).toLowerCase());
        weapon.addDamage(amount, type);
    }

    @Override
    protected void visitVersatileDamage(Element element) {
        String amount = XmlUtils.getElementText(element, "amount");
        String typeString = XmlUtils.getElementText(element, "damageType");
        DamageType type = DamageType.valueOf(typeString.substring(0, 1).toUpperCase() + typeString.substring(1).toLowerCase());
        weapon.addVersatileDamage(amount, type);
    }

    @Override
    protected void visitRange(Element element) {
        String range = element.getTextContent();
        weapon.setRange(range);
    }

    @Override
    protected void visitProperties(Element element) {
        String propertiesString = element.getTextContent();
        final String[] properties = propertiesString.split(",");
        weapon.setProperties(properties);
    }

    @Override
    protected void visitCategory(Element element) {
        String category = element.getTextContent();
        weapon.setCategory(category);
    }

    @Override
    protected void visitRanged(Element element) {
        if ("true".equals(element.getTextContent())) {
            weapon.setIsRanged(true);
        }
    }
}
