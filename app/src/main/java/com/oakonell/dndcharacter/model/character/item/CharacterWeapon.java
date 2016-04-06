package com.oakonell.dndcharacter.model.character.item;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.DamageType;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.item.ItemType;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 12/8/2015.
 */
@Root(strict = false)
public class CharacterWeapon extends CharacterItem {
    @Element(required = false)
    private boolean isRanged;

    @Element(required = false)
    private String range;
    @ElementList(required = false)
    private Set<String> properties = new HashSet<>();

    @NonNull
    @ElementList(required = false)
    private List<DamageFormula> damageFormulas = new ArrayList<>();

    @ElementList(required = false)
    private List<AttackModifier> bonusModifiers = new ArrayList<>();

    @NonNull
    @ElementList(required = false)
    private List<DamageFormula> versatileDamageFormulas = new ArrayList<>();
    @Element(required = false)
    private String ammunition;
    @Element(required = false)
    private String specialComment;


    @NonNull
    public ItemType getItemType() {
        return ItemType.WEAPON;
    }


    public void addDamage(String formula, DamageType type) {
        DamageFormula damage = new DamageFormula(formula, type);
        damageFormulas.add(damage);
    }

    public void addVersatileDamage(String formula, DamageType type) {
        DamageFormula damage = new DamageFormula(formula, type);
        versatileDamageFormulas.add(damage);
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setProperties(@NonNull String[] properties) {
        this.properties = new HashSet<>();
        for (String each : properties) {
            this.properties.add(each.toUpperCase().trim());
        }
    }

    @NonNull
    public String getDamageString(@NonNull Resources resources) {
        final List<DamageFormula> damageFormulas = this.damageFormulas;
        return getDamagesString(damageFormulas, resources);
    }

    @NonNull
    public String getVersatileDamageString(@NonNull Resources resources) {
        final List<DamageFormula> damageFormulas = this.versatileDamageFormulas;
        return getDamagesString(damageFormulas, resources);
    }

    @NonNull
    private String getDamagesString(@NonNull List<DamageFormula> damageFormulas, @NonNull Resources resources) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (DamageFormula each : damageFormulas) {
            if (!isFirst) {
                builder.append(", ");
            }
            builder.append(each.damageFormula);
            builder.append(" ");
            builder.append(resources.getString(each.type.getStringResId()));
            isFirst = false;
        }
        return builder.toString();
    }

    @NonNull
    public List<DamageFormula> getDamages() {
        return damageFormulas;
    }

    @NonNull
    public List<DamageFormula> getVersatileDamages() {
        return versatileDamageFormulas;
    }


    public void setIsRanged(boolean ranged) {
        isRanged = ranged;
    }

    public boolean isRanged() {
        return isRanged;
    }

    public boolean isVersatile() {
        return properties != null && properties.contains("VERSATILE");
    }

    public void setIsVersatile(boolean checked) {
        if (checked) {
            properties.add("VERSATILE");
        } else {
            properties.remove("VERSATILE");
        }
    }


    public void setIsThrown(boolean checked) {
        if (checked) {
            properties.add("THROWN");
        } else {
            properties.remove("THROWN");
        }
    }

    public void setIsLoading(boolean checked) {
        if (checked) {
            properties.add("LOADING");
        } else {
            properties.remove("LOADING");
        }
    }

    public void setIsFinesse(boolean checked) {
        if (checked) {
            properties.add("FINESSE");
        } else {
            properties.remove("FINESSE");
        }
    }

    public void setIsTwoHanded(boolean checked) {
        if (checked) {
            properties.add("TWO-HANDED");
        } else {
            properties.remove("TWO-HANDED");
        }
    }

    public void setIsReach(boolean checked) {
        if (checked) {
            properties.add("REACH");
        } else {
            properties.remove("REACH");
        }
    }

    public void setIsHeavy(boolean checked) {
        if (checked) {
            properties.add("HEAVY");
        } else {
            properties.remove("HEAVY");
        }
    }

    public void setIsLight(boolean checked) {
        if (checked) {
            properties.add("LIGHT");
        } else {
            properties.remove("LIGHT");
        }
    }

    public void setIsSpecial(boolean checked) {
        if (checked) {
            properties.add("SPECIAL");
        } else {
            properties.remove("SPECIAL");
        }
    }

    public void setUsesAmmunition(boolean checked) {
        if (checked) {
            if (ammunition == null) {
                ammunition = "";
            }
        } else {
            ammunition = null;
        }
    }


    public boolean isTwoHanded() {
        return properties != null && properties.contains("TWO-HANDED");
    }

    public boolean isFinesse() {
        return properties != null && properties.contains("FINESSE");
    }

    public boolean isThrown() {
        return properties != null && properties.contains("THROWN");
    }

    public boolean isLoading() {
        return properties != null && properties.contains("LOADING");
    }

    public boolean usesReach() {
        return properties != null && properties.contains("REACH");
    }

    public boolean isHeavy() {
        return properties != null && properties.contains("HEAVY");
    }

    public boolean isLight() {
        return properties != null && properties.contains("LIGHT");
    }

    public boolean isSpecial() {
        return properties != null && properties.contains("SPECIAL");
    }

    public String specialComment() {
        return specialComment;
    }

    public void setSpecialComment(String comment) {
        specialComment = comment;
    }

    @NonNull
    public String getDescriptionString() {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        if (properties != null) {
            for (String each : properties) {
                if (!isFirst) {
                    builder.append(", ");
                }
                String string = each.substring(0, 1) + each.substring(1).toLowerCase();
                builder.append(string);
                if (string.equals("Thrown")) {
                    builder.append("(");
                    builder.append(range);
                    builder.append(")");
                }
                isFirst = false;
            }
        }
        if (isRanged()) {
            if (!isFirst) {
                builder.append(", ");
            }
            builder.append("Ranged(");
            builder.append(range);
            builder.append(")");
            isFirst = false;
        }
        return builder.toString();
    }

    @NonNull
    public List<AttackModifier> getAttackModifiers(@NonNull com.oakonell.dndcharacter.model.character.Character character, boolean useFinesse) {
        StatBlock statBlock;
        if (useFinesse || isRanged()) {
            statBlock = character.getStatBlock(StatType.DEXTERITY);
        } else {
            statBlock = character.getStatBlock(StatType.STRENGTH);
        }

        int damageModifier = statBlock.getModifier();

        boolean isProficient = character.isProficientWith(this);
        int profBonus = isProficient ? character.getProficiency() : 0;

        List<AttackModifier> result = new ArrayList<>();
        result.add(new AttackModifier(damageModifier + profBonus, damageModifier));

        // bonus weapon modifiers- eg, magic weapon
        for (AttackModifier each : bonusModifiers) {
            result.add(each);
        }

        return result;
    }

    @NonNull
    public AttackModifier getBaseAttackModifier(@NonNull com.oakonell.dndcharacter.model.character.Character character, boolean useFinesse) {
        List<AttackModifier> modifiers = getAttackModifiers(character, useFinesse);
        int damage = 0;
        int attackBonus = 0;
        for (AttackModifier each : modifiers) {
            damage += each.damageModifier;
            attackBonus += each.attackBonus;
        }

        AttackModifier result = new AttackModifier(attackBonus, damage);
        return result;
    }

    public void setAmmunition(String ammunition) {
        this.ammunition = ammunition;
    }

    public String getAmmunition() {
        return ammunition;
    }

    public boolean usesAmmunition() {
        return getAmmunition() != null;
    }


    public static class DamageFormula {
        @Element(required = false)
        String damageFormula;
        @Element(required = false)
        DamageType type;

        public DamageFormula() {
            // for SimpleXML persistence FW
        }

        public DamageFormula(String formula, DamageType type) {
            this.damageFormula = formula;
            this.type = type;
        }

        public String getDamageFormula() {
            return damageFormula;
        }

        public DamageType getType() {
            return type;
        }

        public void setDamageFormula(String damageFormula) {
            this.damageFormula = damageFormula;
        }

        public void setType(DamageType type) {
            this.type = type;
        }
    }

    public static class AttackModifier {
        @Element(required = false)
        int attackBonus;
        @Element(required = false)
        int damageModifier;

        public AttackModifier() {
            // for Simple XML framework
        }

        public AttackModifier(int attackBonus, int damageModifier) {
            this.attackBonus = attackBonus;
            this.damageModifier = damageModifier;
        }

        public int getAttackBonus() {
            return attackBonus;
        }

        public int getDamageModifier() {
            return damageModifier;
        }

        public void setAttackBonus(int attackBonus) {
            this.attackBonus = attackBonus;
        }
    }

    public List<AttackModifier> getBonusModifiers() {
        return bonusModifiers;
    }

}
