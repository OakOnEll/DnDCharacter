package com.oakonell.dndcharacter.model.item;

import android.content.Context;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.DamageType;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 12/29/2015.
 */
public class CreateCharacterWeaponVisitor extends AbstractWeaponVisitor {
    private final CharacterWeapon weapon;

    private CreateCharacterWeaponVisitor(CharacterWeapon weapon) {
        this.weapon = weapon;
    }

    @NonNull
    public static CharacterWeapon createWeapon(@NonNull Context context, @NonNull ItemRow row, @NonNull com.oakonell.dndcharacter.model.character.Character character) {
        CharacterWeapon weapon = new CharacterWeapon();

        ApplyChangesToGenericComponent.applyToCharacter(context, XmlUtils.getDocument(row.getXml()).getDocumentElement(), null, weapon, character, false);

        CreateCharacterWeaponVisitor newMe = new CreateCharacterWeaponVisitor(weapon);
        newMe.visit(row);

        character.addWeapon(weapon);

        return weapon;
    }

    @Override
    protected void visitAmmunition(@NonNull Element element) {
        String ammunition = element.getTextContent();
        weapon.setAmmunition(ammunition);
    }

    @Override
    protected void visitDamage(Element element) {
        String amount = XmlUtils.getElementText(element, "amount");
        String typeString = XmlUtils.getElementText(element, "damageType");
        if (typeString == null) {
            throw new RuntimeException("Weapon damage has no damage type!");
        }
        DamageType type = EnumHelper.stringToEnum(typeString, DamageType.class);
        weapon.addDamage(amount, type);
    }

    @Override
    protected void visitVersatileDamage(Element element) {
        String amount = XmlUtils.getElementText(element, "amount");
        String typeString = XmlUtils.getElementText(element, "damageType");
        if (typeString == null) {
            throw new RuntimeException("Weapon versatile damage has no damage type!");
        }
        DamageType type = EnumHelper.stringToEnum(typeString, DamageType.class);
        weapon.addVersatileDamage(amount, type);
    }

    @Override
    protected void visitRange(@NonNull Element element) {
        String range = element.getTextContent();
        weapon.setRange(range);
    }

    @Override
    protected void visitProperties(@NonNull Element element) {
        String propertiesString = element.getTextContent();
        final String[] properties = propertiesString.split(",");
        weapon.setProperties(properties);
    }

    @Override
    protected void visitCategory(@NonNull Element element) {
        String category = element.getTextContent();
        weapon.setCategory(category);
    }

    @Override
    protected void visitRanged(@NonNull Element element) {
        weapon.setIsRanged("true".equals(element.getTextContent()));
    }
}
