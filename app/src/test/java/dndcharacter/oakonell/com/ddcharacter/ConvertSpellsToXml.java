package dndcharacter.oakonell.com.ddcharacter;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.spell.CastingTimeType;
import com.oakonell.dndcharacter.model.character.spell.SpellAttackType;
import com.oakonell.dndcharacter.model.character.spell.SpellDurationType;
import com.oakonell.dndcharacter.model.character.spell.SpellRange;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rob on 2/21/2016.
 */
public class ConvertSpellsToXml {
    @Test
    public void convertSpellsToXml() throws IOException {
        //INSERT INTO spell (_id,name,school,level,casting_time,range,components,duration,description,description_high,book,favorite,bard,cleric,druid,paladin,ranger,sorcerer,warlock,wizard) VALUES
        File inputFile = new File("E:\\projects\\DnDCharacter\\data\\spells\\import3.sql");
        assert (inputFile.exists());
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String prefix = null;

        File dir = new File(new File("E:\\projects\\DnDCharacter\\data\\spells\\"), "converted");
        deleteFolder(dir);
        assert dir.mkdir();

        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            String[] split = line.split("VALUES");
            assert (split.length == 2);
            if (prefix == null) {
                prefix = split[0];
            } else {
                assert prefix.equals(split[0]);
            }
            String columnsString = split[1].substring(split[1].indexOf('(') + 1, split[1].lastIndexOf(')'));
            convertRow(dir, columnsString);


            count++;
        }
        reader.close();
        System.out.println("Read " + count + " rows");

        System.out.println(actions.size() + " unique actions");
        for (String each : actions) {
            System.out.println(each);
        }
    }

    Set<String> actions = new HashSet<>();

    protected void convertRow(File dir, String columnsString) throws IOException {

        //                                                              favorite,bard,cleric,druid,paladin,ranger,sorcerer,warlock,wizard
        //      (1,'Abi-Dalzim''s Horrid Wilting','8th-level necromancy',8,'1 action','150 feet','V, S, M (a bit of sponge)','Instantaneous','descr','-','ee 15',0,0,0,0,0,0,1,0,1)

        StringReader stringReader = new StringReader(columnsString);
        // skip Id
        readNumberColumnValue(stringReader);
        String name = readColumnValue(stringReader);
        String schoolLevel = readColumnValue(stringReader);
        int level = readNumberColumnValue(stringReader);
        String action = readColumnValue(stringReader);
        String range = readColumnValue(stringReader);
        String componentsString = readColumnValue(stringReader);
        String duration = readColumnValue(stringReader);
        String description = readColumnValue(stringReader);
        String higherLevelDescription = readColumnValue(stringReader);
        String source = readColumnValue(stringReader);
        // skip favorite
        readBoooleanColumnValue(stringReader);
        SpellClasses spellClasses = new SpellClasses();
        spellClasses.readFrom(stringReader);


        String schoolName;
        if (schoolLevel.contains("level")) {
            // eg: 2nd level Transmutation
            schoolName = schoolLevel.substring(schoolLevel.lastIndexOf(' ') + 1);
        } else {
            // cantrip: Conjuration Cantrip
            schoolName = schoolLevel.substring(0, schoolLevel.indexOf(' '));
        }


        //SpellAttackType;
        SpellAttackType attackType = getSpellAttackType(name, description);

        // process duration
        DurationInfo durationInfo = getDurationInfo(duration);

        ActionInfo actionInfo = getActionInfo(action);

        ComponentInfo componentInfo = getComponentInfo(name, description, componentsString);

        // process range
        RangeInfo rangeInfo = getRangeInfo(range);


        boolean isRitual = name.contains("(Ritual)");

        assert readColumnValue(stringReader) == null;

        if (higherLevelDescription.trim().equals("-")) {
            higherLevelDescription = "";
        }

        writeXml(dir, name, schoolName, level, actionInfo, attackType, rangeInfo, componentInfo, durationInfo, description, higherLevelDescription, source, spellClasses, isRitual);

    }

    private RangeInfo getRangeInfo(String range) {
        RangeInfo info = new RangeInfo();
        range = range.trim();

        if (range.equals("Sight")) {
            info.rangeType = SpellRange.SIGHT;
        } else if (range.equals("Touch")) {
            info.rangeType = SpellRange.TOUCH;
        } else if (range.equals("Special")) {
            info.rangeType = SpellRange.SPECIAL;
        } else if (range.contains("Self")) {
            if (range.equals("Self")) {
                info.rangeType = SpellRange.SELF;
            } else {
                final Pattern pattern = Pattern.compile("(\\d+)[- ](\\w+)([- ]radius)?( (\\w+))?");
                final Matcher matcher = pattern.matcher(range);
                if (matcher.find()) {
                    int numeric = Integer.parseInt(matcher.group(1));
                    String unitString = matcher.group(2);
                    String radiusString = matcher.group(3);
                    String shape = matcher.group(5);

                    if ("cone".equals(shape)) {
                        if (!unitString.equals("foot")) {
                            throw new RuntimeException("Unexpected unit for cone:" + unitString);
                        }
                        info.rangeType = SpellRange.SELF_CONE_FEET;
                        info.value = numeric;
                    } else if ("cube".equals(shape)) {
                        if (!unitString.equals("foot")) {
                            throw new RuntimeException("Unexpected unit for cube:" + unitString);
                        }
                        info.rangeType = SpellRange.SELF_CUBE_FEET;
                        info.value = numeric;
                    } else if ("line".equals(shape)) {
                        if (!unitString.equals("foot")) {
                            throw new RuntimeException("Unexpected unit for line:" + unitString);
                        }
                        info.rangeType = SpellRange.SELF_LINE_FEET;
                        info.value = numeric;
                    } else if ("sphere".equals(shape) || "radius".equals(shape) || (radiusString != null && shape == null)) {
                        if (unitString.equals("foot")) {
                            info.rangeType = SpellRange.SELF_SPHERE_FEET;
                            info.value = numeric;
                        } else if (unitString.equals("mile")) {
                            info.rangeType = SpellRange.SELF_SPHERE_MILE;
                            info.value = numeric;
                        } else {
                            throw new RuntimeException("Unexpected unit: " + unitString);
                        }
                    } else if ("hemisphere".equals(shape)) {
                        if (!unitString.equals("foot")) {
                            throw new RuntimeException("Unexpected unit for hemisphere:" + unitString);
                        }
                        info.rangeType = SpellRange.SELF_HEMISPHERE_FEET;
                        info.value = numeric;
                    } else {
                        throw new RuntimeException("Unexpected range shape: " + range);
                    }
                } else {
                    throw new RuntimeException("Unexpected range shape: " + range);
                }
            }
        } else if (range.equals("Unlimited")) {
            info.rangeType = SpellRange.UNLIMITED;
        } else if (range.contains("feet")) {
            info.rangeType = SpellRange.FEET;
            info.value = Integer.parseInt(range.substring(0, range.indexOf("feet")).trim());
        } else if (range.contains("mile")) {
            info.rangeType = SpellRange.MILES;
            info.value = Integer.parseInt(range.substring(0, range.indexOf("mile")).trim());
        } else {
            throw new RuntimeException("Unexpected range shape: " + range);
        }

        return info;
    }

    private ComponentInfo getComponentInfo(String name, String description, String componentsString) {
        ComponentInfo info = new ComponentInfo();
        int indexOfParen = componentsString.indexOf("(");
        String material = null;
        if (indexOfParen >= 0) {
            int lastIndexOfCloseParen = componentsString.lastIndexOf(")");
            material = componentsString.substring(indexOfParen + 1, lastIndexOfCloseParen);
            componentsString = componentsString.substring(0, indexOfParen);
        }
        final String[] strings = componentsString.split(",");

        for (int index = 0; index < strings.length; index++) {
            String string = strings[index].trim();
            if ("V".equals(string)) {
                info.usesVerbal = true;
            } else if ("S".equals(string)) {
                info.usesSomatic = true;
            } else if (string.startsWith("M")) {
                info.usesMaterial = true;
                if (material.contains("gp")) {
                    info.specialMaterial = material;
                } else {
                    info.material = material;
                }
            } else {
                throw new RuntimeException("Unexpected component for " + name + ":" + componentsString + "=>" + string);
            }
        }

//        if (!info.usesMaterial && material != null) {
//            actions.add(name + ": " + componentsString);
//        }
//
//        if (!info.usesVerbal && !info.usesSomatic && !info.usesMaterial) {
//            actions.add(name + ": " + componentsString);
//        }

        return info;
    }

    private SpellAttackType getSpellAttackType(String name, String description) {
        if (name.equals("Contagion")) {
            //  Your touch inflicts disease.
            //     Make a melee spell attack against a creature within your reach.
            //     On a hit, you afflict the creature with a disease of your choice from any of the ones described below.
            //     At the end of each of the target''s turns, it must make a Constitution saving throw.
            //       After failing three of these saving throws, the disease''s effects last for the duration, and the creature stops making these saves.
            //       After succeeding on three of these saving throws, the creature recovers from the disease, and the spell ends. Since this spell induces a natural disease in its target, any effect that removes a disease or otherwise ameliorates a disease''s effects apply to it. <br><b><i>Blinding Sickness.</b></i> Pain grips the creature''s mind, and its eyes turn milky white. The creature has disadvantage on Wisdom checks and Wisdom saving throws and is blinded. <br><b><i>Filth Fever.</b></i> A raging fever sweeps through the creature''s body. The creature has disadvantage on Strength checks, Strength saving throws, and attack rolls that use Strength. <br><b><i>Flesh Rot.</b></i> The creature''s flesh decays. The creature has disadvantage on Charisma checks and vulnerability to all damage. <br><b><i>Mindfire.</b></i> The creature''s mind becomes feverish. The creature has disadvantage on Intelligence checks and Intelligence saving throws, and the creature behaves as if under the effects of the confusion spell during combat. <br><b><i>Seizure. </b></i>The creature is overcome with shaking. The creature has disadvantage on Dexterity checks, Dexterity saving throws, and attack rolls that use Dexterity. <br><b><i>Slimy Doom.</b></i> The creature begins to bleed uncontrollably. The creature has disadvantage on Constitution checks and Constitution saving throws. In addition, whenever the creature takes damage, it is stunned until the end of its next turn.
            return SpellAttackType.MELEE_ATTACK;
        }
        if (name.equals("Dispel Evil and Good")) {
            // Shimmering energy surrounds and protects you from fey, undead, and creatures originating from beyond the Material Plane.
            // For the duration, celestials, elementals, fey, fiends, and undead have disadvantage on attack rolls against you.
            // You can end the spell early by using either of the following special functions.
            // <br><b><i>Break  Enchantment.</b></i> As your action, you touch a creature you can reach that is charmed, frightened, or possessed by a celestial, an elemental, a fey, a fiend, or an undead.
            //       The creature you touch is no longer charmed, frightened, or possessed by such creatures.
            // <br><b><i>Dismissal. </b></i>As your action, make a melee spell attack against a celestial, an elemental, a fey, a fiend, or an undead you can reach.
            //       On a hit, you attempt to drive the creature back to its home plane. The creature must succeed on a Charisma saving throw or be sent back to its home plane (if it isn't there already). If they aren't on their home plane, undead are sent to the Shadowfell, and fey are sent to the Feywild.
            return SpellAttackType.AUTOMATIC;
        }
        if (name.equals("Ice Knife")) {
            // You create a shard of ice and fling it at one creature within range.
            // Make a ranged spell attack against the target.
            // On a hit, the target takes 1d10 piercing damage. Hit or miss, the shard then explodes.
            // The target and each creature within 5 feet of the point where the ice exploded must succeed on a Dexterity saving throw or take 2d6 cold damage.
            return SpellAttackType.MELEE_ATTACK;
        }
        if (name.equals("Plane Shift")) {
            // You and up to eight willing creatures who link hands in a circle are transported to a different plane of existence.
            // You can specify a target destination in general terms, such as the City of Brass on the Elemental Plane of Fire or the palace of Dispater on the second level of the Nine Hells,
            // and you appear in or near that destination.
            // If you are trying to reach the City of Brass, for example, you might arrive in its Street of Steel, before its Gate of Ashes,
            //    or looking at the city from across the Sea of Fire, at the DM's discretion.
            // Alternatively, if you know the sigil sequence of a teleportation circle on another plane of existence,
            //    this spell can take you to that circle.
            //   If the teleportation circle is too small to hold all the creatures you transported, they appear in the closest unoccupied spaces next to the circle.
            //    You can use this spell to banish an unwilling creature to another plane.
            //    Choose a creature within your reach and make a melee spell attack against it.
            //    On a hit, the creature must make a Charisma saving throw. If the creature fails the save, it is transported to a random location on the plane of existence you specify. A creature so transported must find its own way back to your current plane of existence.
            return SpellAttackType.MELEE_ATTACK;
        }
        if (name.equals("Ray of Enfeeblement") || name.equals("Ray of Sickness")) {
            // A black beam of enervating energy springs from your finger toward a creature within range.
            // Make a ranged spell attack against the target.
            // On a hit, the target deals only half damage with weapon attacks that use Strength until the spell ends.
            // At the end of each of the target's turns, it can make a Constitution saving throw against the spell. On a success, the spell ends.
            return SpellAttackType.RANGED_ATTACK;
        }
        // ranged spell attack
        // melee spell attack
        // saving throw
        boolean mentionsSavingthrow = false;
        if (description.contains("saving throw")) {
            mentionsSavingthrow = true;
        }
        boolean mentionsRangedAttack = false;
        if (description.contains("ranged spell attack")) {
            mentionsRangedAttack = true;
        }
        boolean mentionsMeleeAttack = false;
        if (description.contains("melee spell attack")) {
            mentionsMeleeAttack = true;
        }

        if (mentionsMeleeAttack && mentionsRangedAttack) {
            throw new RuntimeException(name + " mentioned ranged and melee: " + description);
        }
        if (mentionsMeleeAttack && mentionsSavingthrow) {
            throw new RuntimeException(name + " mentioned melee and saving throw: " + description);
        }
        if (mentionsRangedAttack && mentionsSavingthrow) {
            throw new RuntimeException(name + " mentioned ranged and saving throw: " + description);
        }

        if (mentionsSavingthrow) {
            // 173 saving throw spells
            return SpellAttackType.SAVING_THROW;
        }
        if (mentionsMeleeAttack) {
            // 8 melee attacks
/*
Spiritual Weapon: You create a floating, spectral weapon within range that lasts for the duration or until you cast this spell again. When you cast the spell, you can make a melee spell attack against a creature within 5 feet of the weapon. On a hit, the target takes force damage equal to 1d8 + your spellcasting ability modifier. As a bonus action on your turn, you can move the weapon up to 20 feet and repeat the attack against a creature within 5 feet of it. The weapon can take whatever form you choose. Clerics of deities who are associated with a particular weapon (as St. Cuthbert is known for his mace and Thor for his hammer) make this spell's effect resemble that weapon.
Bigby's Hand: You create a Large hand of shimmering, translucent force in an unoccupied space that you can see within range. The hand lasts for the spell's duration, and it moves at your command, mimicking the movements of your own hand. The hand is an object that has AC 20 and hit points equal to your hit point maximum. If it drops to 0 hit points, the spell ends. It has a Strength of 26 (+8) and a Dexterity of 10 (+0). The hand doesn't fill its space. When you cast the spell and as a bonus action on your subsequent turns, you can move the hand up to 60 feet and then cause one of the following effects with it. <br><b><i>Clenched Fist.</b></i> The hand strikes one creature or object within 5 feet of it. Make a melee spell attack for the hand using your game statistics. On a hit, the target takes 4d8 force damage. <br><b><i>Forceful Hand. </b></i>The hand attempts to push a creature within 5 feet of it in a direction you choose. Make a check with the hand's Strength contested by the Strength (Athletics) check of the target. If the target is Medium or smaller, you have advantage on the check. If you succeed, the hand pushes the target up to 5 feet plus a number of feet equal to five times your spellcasting ability modifier. The hand moves with the target to remain within 5 feet of it. <br><b><i>Grasping Hand.</b></i> The hand attempts to grapple a Huge or smaller creature within 5 feet of it. You use the hand's Strength score to resolve the grapple. If the target is Medium or smaller, you have advantage on the check. While the hand is grappling the target, you can use a bonus action to have the hand crush it. When you do so, the target takes bludgeoning damage equal to 2d6 + your spellcasting ability modifier. <br><b><i>Interposing Hand.</b></i> The hand interposes itself between you and a creature you choose until you give the hand a different command. The hand moves to stay between you and the target, providing you with half cover against the target. The target can't move through the hand's space if its Strength score is less than or equal to the hand's Strength score. If its Strength score is higher than the hand's Strength score, the target can move toward you through the hand's space, but that space is difficult terrain for the target.
Mordenkainen's Sword: You create a sword-shaped plane of force that hovers within range. It lasts for the duration. When the sword appears you make a melee spell attack against a target of your choice within 5 feet of the sword. On a hit. the target takes 3d10 force damage. Until the spell ends you can use a bonus action on each of your turns to move the sword up to 20 feet to a spot you can see and repeat this attack against the same target or a different one.
Inflict Wounds: Make a melee spell attack against a creature you can reach. On a hit, the target takes 3d10 necrotic damage.
Vampiric Touch: The touch of your shadow-wreathed hand can siphon force from others to heal your wounds. Make a melee spell attack against a creature within your reach. On a hit, the target takes 3d6 necrotic damage, and you regain hit points equal to half the amount of necrotic damage dealt. Until the spell ends, you can make the attack again on each of your turns as an action.
Flame Blade: You evoke a fiery blade in your free hand. The blade is similar in size and shape to a scimitar, and it lasts for the duration. If you let go of the blade, it disappears, but you can evoke the blade again as a bonus action. <br>You can use your action to make a melee spell attack with the fiery blade. On a hit, the target takes 3d6 fire damage. <br>The flaming blade sheds bright light in a 10-foot radius and dim light for an additional 10 feet.
Thorn Whip: You create a long, vine-like whip covered in thorns that lashes out at your command toward a creature in range. Make a melee spell attack against the target. If the attack hits, the creature takes 1d6 piercing damage, and if the creature is Large or smaller, you pull the creature up to 10 feet closer to you. This spell's damage increases by 1d6 when you reach 5th level (2d6), 11th level (3d6), and 17th level (4d6).
Shocking Grasp: Lightning springs from your hand to deliver a shock to a creature you try to touch. Make a melee spell attack against the target. You have advantage on the attack roll if the target is wearing armor made of metal. On a hit, the target takes 1d8 lightning damage, and it can't take reactions until the start of its next turn. The spell's damage increases by 1d8 when you reach 5th level (2d8), 11th level (3d8), and 17th level (4d8).
             */
            return SpellAttackType.MELEE_ATTACK;
        }
        if (mentionsRangedAttack) {
/*  11 ranged attack spells ?
            Witch Bolt: A beam of crackling, blue energy lances out toward a creature within range, forming a sustained arc of lightning between you and the target. Make a ranged spell attack against that creature. On a hit, the target takes 1d12 lightning damage, and on each of your turns for the duration, you can use your action to deal 1d12 lightning damage to the target automatically. The spell ends if you use your action to do anything else. The spell also ends if the target is ever outside the spell's range or if it has total cover from you.
Magic Stone: You touch one to three pebbles and imbue them with magic. You or someone else can make a ranged spell attack with one of the pebbles by throwing it or hurling it with a sling. If thrown, it has a range of 60 feet. If someone else attacks with the pebble, that attacker adds your spellcasting ability modifier, not the attacker's, to the attack roll. On a hit, the target takes bludgeoning damage equal to 1d6 + your spellcasting ability modifier. Hit or miss, the spell then ends on the stone. If you cast this spell again, the spell ends early on any pebbles still affected by it.
Chromatic Orb: You hurl a 4-inch-diameter sphere of energy at a creature that you can see within range. You choose acid, cold, fire, lightning, poison, or thunder for the type of orb you create, and then make a ranged spell attack against the target. If the attack hits, the creature takes 3d8 damage of the type you chose.
Melf's Acid Arrow: A shimmering green arrow streaks toward a target within range and bursts in a spray of acid. Make a ranged spell attack against the target. On a hit, the target takes 4d4 acid damage immediately and 2d4 acid damage at the end of its next turn. On a miss, the arrow splashes the target with acid for half as much of the initial damage and no damage at the end of its next turn.
Chill Touch: You create a ghostly, skeletal hand in the space of a creature within range. Make a ranged spell attack against the creature to assail it with the chill of the grave. On a hit, the target takes 1d8 necrotic damage, and it can't regain hit points until the start of your next turn. Until then, the hand clings to the target.<br>If you hit an undead target, it also has disadvantage on attack rolls against you until the end of your next turn. This spell's damage increases by 1d8 when you reach 5th level (2d8), 11th level (3d8), and 17th level (4d8).
Produce Flame: A flickering flame appears in your hand. The flame remains there for the duration and harms neither you nor your equipment. The flame sheds bright light in a 10-foot radius and dim light for an additional 10 feet. The spell ends if you dismiss it as an action or if you cast it again. You can also attack with the flame, although doing so ends the spell. When you cast this spell, or as an action on a later turn, you can hurl the flame at a creature within 30 feet of you. Make a ranged spell attack. On a hit, the target takes 1d8 fire damage. This spell's damage increases by 1d8 when you reach 5th level (2d8),  11th level (3d8), and 17th level (4d8).
Guiding Bolt: A flash of light streaks toward a creature of your choice within range. Make a ranged spell attack against the target. On a hit, the target takes 4d6 radiant damage, and the next attack roll made against this target before the end of your next turn has advantage, thanks to the mystical dim light glittering on the target until then.
Eldritch Blast: A beam of crackling energy streaks toward a creature within range. Make a ranged spell attack against the target. On a hit, the target takes 1d10 force damage. <br>The spell creates more than one beam when you reach higher levels: two beams at 5th level, three beams at 11th level, and four beams at 17th level. You can direct the beams at the same target or at different ones. Make a separate attack roll for each beam.
Scorching Ray: You create three rays of fire and hurl them at targets within range. You can hurl them at one target or several. Make a ranged spell attack for each ray. On a hit, the target takes 2d6 fire damage.
Ray of Frost: A frigid beam of blue-white light streaks toward a creature within range. Make a ranged spell attack against the target. On a hit, it takes 1d8 cold damage, and its speed is reduced by 10 feet until the start of your next turn. The spell's damage increases by 1d8 when you reach 5th level (2d8), 11th level (3d8), and 17th level (4d8).
Fire Bolt: You hurl a mote of fire at a creature or object within range. Make a ranged spell attack against the target. On a hit, the target takes 1d10 fire damage. A flammable object hit by this spell ignites if it isn't being worn or carried. <br>This spell's damage increases by 1d10 when you reach 5th level (2d10), 11th level (3d10), and 17th level (4d10).

             */
            return SpellAttackType.RANGED_ATTACK;
        }
        // 206 "automatic" spells
        return SpellAttackType.AUTOMATIC;
    }


    class SpellClasses {
        private boolean isBard;
        private boolean isCleric;
        private boolean isDruid;
        private boolean isPaladin;
        private boolean isRanger;
        private boolean isSorcerer;
        private boolean isWarlock;
        private boolean isWizard;

        public void readFrom(StringReader stringReader) throws IOException {
            isBard = readBoooleanColumnValue(stringReader);
            isCleric = readBoooleanColumnValue(stringReader);
            isDruid = readBoooleanColumnValue(stringReader);
            isPaladin = readBoooleanColumnValue(stringReader);
            isRanger = readBoooleanColumnValue(stringReader);
            isSorcerer = readBoooleanColumnValue(stringReader);
            isWarlock = readBoooleanColumnValue(stringReader);
            isWizard = readBoooleanColumnValue(stringReader);
        }
    }


    private ActionInfo getActionInfo(String action) {
        ActionInfo info = new ActionInfo();
        if (action.contains("reaction")) {
            info.castingType = CastingTimeType.REACTION;
            info.amount = Integer.parseInt(action.substring(0, action.indexOf(" ")));
            if (info.amount != 1) {
                throw new RuntimeException("Unexpected");
            }
        } else if (action.contains("bonus action")) {
            info.castingType = CastingTimeType.BONUS_ACTION;
            info.amount = Integer.parseInt(action.substring(0, action.indexOf(" ")));
            if (info.amount != 1) {
                throw new RuntimeException("Unexpected");
            }
        } else if (action.contains(" action")) {
            info.castingType = CastingTimeType.ACTION;
            info.amount = Integer.parseInt(action.substring(0, action.indexOf(" ")));
        } else if (action.contains("minute")) {
            info.castingType = CastingTimeType.MINUTE;
            info.amount = Integer.parseInt(action.substring(0, action.indexOf(" ")));
        } else if (action.contains("hour")) {
            info.castingType = CastingTimeType.HOUR;
            info.amount = Integer.parseInt(action.substring(0, action.indexOf(" ")));
        } else {
            throw new RuntimeException("Unexpected");
        }
        return info;
    }

    @NonNull
    private DurationInfo getDurationInfo(String duration) {
        DurationInfo durationInfo = new DurationInfo();
        durationInfo.requiresConcentration = duration.contains("Concentration");
        if (durationInfo.requiresConcentration) {
            final String upToString = "up to ";
            int upToIndex = duration.indexOf(upToString);
            duration = duration.substring(upToIndex + upToString.length());
        }
        // a few spells are "Up to".. just make duration the max limit?
        final String upToString = "Up to ";
        int upToIndex = duration.indexOf(upToString);
        if (upToIndex >= 0) {
            duration = duration.substring(upToIndex + upToString.length());
        }
        if (duration.equals("Instantaneous")) {
            durationInfo.durationType = SpellDurationType.INSTANTANEOUS;
        } else if (duration.equals("Special")) {
            durationInfo.durationType = SpellDurationType.SPECIAL;
        } else if (duration.equals("Until dispelled")) {
            durationInfo.durationType = SpellDurationType.UNTIL_DISPELLED;
        } else if (duration.equals("Until dispelled or triggered")) {
            durationInfo.durationType = SpellDurationType.UNTIL_DISPELLED_OR_TRIGGERED;
        } else if (duration.equals("Instantaneous or 1 hour (see below)")) {
            durationInfo.durationType = SpellDurationType.SPECIAL;
        } else if (duration.contains("day")) {
            durationInfo.durationType = SpellDurationType.DAY;
            durationInfo.durationAmount = Integer.parseInt(duration.substring(0, duration.indexOf(" ")));
        } else if (duration.contains("hour")) {
            durationInfo.durationType = SpellDurationType.HOUR;
            durationInfo.durationAmount = Integer.parseInt(duration.substring(0, duration.indexOf(" ")));
        } else if (duration.contains(" min")) {
            durationInfo.durationType = SpellDurationType.MINUTE;
            durationInfo.durationAmount = Integer.parseInt(duration.substring(0, duration.indexOf(" ")));
        } else if (duration.contains("min")) {
            durationInfo.durationType = SpellDurationType.MINUTE;
            durationInfo.durationAmount = Integer.parseInt(duration.substring(0, duration.indexOf("min")));
        } else if (duration.contains("round")) {
            durationInfo.durationType = SpellDurationType.ROUND;
            durationInfo.durationAmount = Integer.parseInt(duration.substring(0, duration.indexOf(" ")));
        } else {
            throw new RuntimeException("Unexpected");
        }
        return durationInfo;
    }

    class DurationInfo {
        boolean requiresConcentration;
        SpellDurationType durationType;
        int durationAmount;
    }

    class ComponentInfo {
        boolean usesVerbal;
        boolean usesSomatic;
        boolean usesMaterial;
        String material;
        String specialMaterial;

    }

    private void writeXml(File dir, String name, String schoolName, int level, ActionInfo action, SpellAttackType attackType, RangeInfo rangeInfo, ComponentInfo components, DurationInfo duration, String description, String higherLevelDescription, String source, SpellClasses spellClasses, boolean isRitual) throws IOException {
        System.out.println(level + " - " + name);
        String filename = name.replace(" ", "_").replace("'", "").replace("\\", "_").replace("/", "_");
        File outFile = new File(dir, filename + ".xml");
        //assert outFile.canWrite();
        FileWriter writer = new FileWriter(outFile);
        writer.append("<spell>\n");

        writer.append("    <name>");
        writer.append(name);
        writer.append("</name>\n");

        writer.append("    <ref>");
        writer.append(source.toUpperCase());
        writer.append("</ref>\n");


        writer.append("    <school>");
        writer.append(schoolName.trim());
        writer.append("</school>\n");

        writer.append("    <level>");
        writer.append(Integer.toString(level < 0 ? 0 : level));
        writer.append("</level>\n");

        writer.append("    <classes>\n");
        //boolean isBard, boolean isCleric, boolean isDruid, boolean isPaladin, boolean isRanger, boolean isSorceror, boolean isWarlock, boolean isWizard
        if (spellClasses.isBard) {
            writer.append("        <class>Bard</class>\n");
        }
        if (spellClasses.isCleric) {
            writer.append("        <class>Cleric</class>\n");
        }
        if (spellClasses.isDruid) {
            writer.append("        <class>Druid</class>\n");
        }
        if (spellClasses.isPaladin) {
            writer.append("        <class>Paladin</class>\n");
        }
        if (spellClasses.isRanger) {
            writer.append("        <class>Ranger</class>\n");
        }
        if (spellClasses.isSorcerer) {
            writer.append("        <class>Sorcerer</class>\n");
        }
        if (spellClasses.isWarlock) {
            writer.append("        <class>Warlock</class>\n");
        }
        if (spellClasses.isWizard) {
            writer.append("        <class>Wizard</class>\n");
        }
        writer.append("    </classes>\n");

        writer.append("    <durationType>");
        writer.append(duration.durationType.name());
        writer.append("</durationType>\n");
        if (duration.durationType.hasValue()) {
            writer.append("    <duration>");
            writer.append(duration.durationAmount + "");
            writer.append("</duration>\n");
        }

        if (duration.requiresConcentration) {
            writer.append("   <concentration>true</concentration>\n");
        }

        if (attackType != SpellAttackType.AUTOMATIC) {
            writer.append("    <attackType>");
            writer.append(attackType.name());
            writer.append("</attackType>\n");
        }

        writer.append("    <rangeType>");
        writer.append(rangeInfo.rangeType.name());
        writer.append("</rangeType>\n");
        if (rangeInfo.value > 0) {
            writer.append("    <range>");
            writer.append(rangeInfo.value + "");
            writer.append("</range>\n");
        }

        if (isRitual) {
            writer.append("    <ritual>true</ritual>\n");
        }


        writer.append("    <castingTimeType>");
        writer.append(action.castingType.name());
        writer.append("</castingTimeType>\n");
        writer.append("    <castingTime>");
        writer.append(action.amount + "");
        writer.append("</castingTime>\n");

        writer.append("    <components>\n");
        if (components.usesVerbal) {
            writer.append("      <verbal>true</verbal>\n");
        }
        if (components.usesSomatic) {
            writer.append("      <somatic>true</somatic>\n");
        }
        if (components.usesMaterial) {
            if (components.specialMaterial != null) {
                writer.append("      <specialMaterial>" + components.specialMaterial + "</specialMaterial>\n");
            }
            if (components.material != null) {
                writer.append("      <material>" + components.material + "</material>\n");
            }
        }
        writer.append("    </components>\n");

        writer.append("    <shortDescription>");
        writer.append(description.replaceAll("<", "&lt;") + "\n");
        writer.append("</shortDescription>\n");
        if (higherLevelDescription.trim().length() > 0) {
            writer.append("    <higherLevelDescription>");
            writer.append(higherLevelDescription.replaceAll("<", "&lt;"));
            writer.append("</higherLevelDescription>\n");
        }


        writer.append("</spell>\n");
        writer.close();

  /*
    <spell>
<name>Bless</name>
<school>Enchantment</school>
<level>1</level>
<classes>
<class>Paladin</class>
<class>Cleric</class>
</classes>
<duration>1 minute</duration>
<range>30 feet</range>
<castingTime>1 action</castingTime>
<components>V,M (a sprinkling of holy water)</components>
<shortDescription>Bless up to three creatures. Whenever targets make an attack roll or saving throw, can roll a d4 and add the result to the original roll.</shortDescription>
</spell>
     */
    }

    private boolean readBoooleanColumnValue(StringReader stringReader) throws IOException {
        Integer num = readNumberColumnValue(stringReader);
        if (num == null) return false;
        if (num != 0 && num != 1) throw new RuntimeException("Invalid 'boolean' numeric " + num);
        return num == 1;
    }

    private Integer readNumberColumnValue(StringReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        int c;
        while ((c = reader.read()) > 0) {
            if (c == ',') break;
            builder.append((char) c);
        }
        if (builder.length() == 0) return null;
        String column = builder.toString();
        return Integer.parseInt(column);
    }

    private String readColumnValue(StringReader reader) throws IOException {
        boolean inString = false;
        boolean stringDone = false;
        boolean pendingQuote = false;
        StringBuilder builder = null;
        int c;

        while ((c = reader.read()) > 0) {
            if (c == ',' && (stringDone || pendingQuote)) {
                if (pendingQuote) {
                    pendingQuote = false;
                    stringDone = true;
                    inString = true;
                }
                break;
            }
            if (c != ' ' && stringDone) {
                throw new RuntimeException("Unexpected character '" +
                        "" + (char) c + "' outside literal marks after " + builder.toString());
            }
            if (c == ' ' && !inString) continue;

            if (pendingQuote) {
                pendingQuote = false;
                if (c == '\'') {
                    builder.append((char) c);
                } else {
                    inString = false;
                    stringDone = true;
                }
                continue;
            }
            if (c == '\'') {
                if (inString) {
                    // might be an escaped '...
                    pendingQuote = true;
                    continue;
                } else {
                    if (stringDone) throw new RuntimeException("Second string starting?!");
                    // start of the string, but don't append the ' to the output
                    builder = new StringBuilder();
                    inString = true;
                }
            } else {
                builder.append((char) c);
            }

        }

        if (builder == null) return null;
        return builder.toString();
    }


    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    private class ActionInfo {
        public CastingTimeType castingType;
        public int amount;
    }

    private class RangeInfo {
        SpellRange rangeType;
        int value;
    }
}
