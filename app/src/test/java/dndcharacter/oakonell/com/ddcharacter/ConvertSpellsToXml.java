package dndcharacter.oakonell.com.ddcharacter;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

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
    }

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
        String components = readColumnValue(stringReader);
        String duration = readColumnValue(stringReader);
        String description = readColumnValue(stringReader);
        String higherLevelDescription = readColumnValue(stringReader);
        String source = readColumnValue(stringReader);
        // skip favorite?
        readBoooleanColumnValue(stringReader);
        boolean isBard = readBoooleanColumnValue(stringReader);
        boolean isCleric = readBoooleanColumnValue(stringReader);
        boolean isDruid = readBoooleanColumnValue(stringReader);
        boolean isPaladin = readBoooleanColumnValue(stringReader);
        boolean isRanger = readBoooleanColumnValue(stringReader);
        boolean isSorceror = readBoooleanColumnValue(stringReader);
        boolean isWarlock = readBoooleanColumnValue(stringReader);
        boolean isWizard = readBoooleanColumnValue(stringReader);

        boolean isRitual = name.contains("(Ritual)");

        assert readColumnValue(stringReader) == null;

        writeXml(dir, name, schoolLevel, level, action, range, components, duration, description, higherLevelDescription, source, isBard, isCleric, isDruid, isPaladin, isRanger, isSorceror, isWarlock, isWizard, isRitual);

    }

    private void writeXml(File dir, String name, String schoolLevel, int level, String action, String range, String components, String duration, String description, String higherLevelDescription, String source, boolean isBard, boolean isCleric, boolean isDruid, boolean isPaladin, boolean isRanger, boolean isSorceror, boolean isWarlock, boolean isWizard, boolean isRitual) throws IOException {
        System.out.println(level + " - " + name);
        String filename = name.replace(" ", "_").replace("'", "").replace("\\", "_").replace("/", "_");
        File outFile = new File(dir, filename + ".xml");
        //assert outFile.canWrite();
        FileWriter writer = new FileWriter(outFile);
        writer.append("<spell>\n");

        writer.append("    <name>");
        writer.append(name);
        writer.append("</name>\n");

        String schoolName;
        if (schoolLevel.contains("level")) {
            // eg: 2nd level Transmutation
            schoolName = schoolLevel.substring(schoolLevel.lastIndexOf(' ') + 1);
        } else {
            // cantrip: Conjuration Cantrip
            schoolName = schoolLevel.substring(0, schoolLevel.indexOf(' '));
        }
        writer.append("    <school>");
        writer.append(schoolName.trim());
        writer.append("</school>\n");

        writer.append("    <level>");
        writer.append(Integer.toString(level < 0 ? 0 : level));
        writer.append("</level>\n");

        writer.append("    <classes>\n");
        //boolean isBard, boolean isCleric, boolean isDruid, boolean isPaladin, boolean isRanger, boolean isSorceror, boolean isWarlock, boolean isWizard
        if (isBard) {
            writer.append("        <class>Bard</class>\n");
        }
        if (isCleric) {
            writer.append("        <class>Cleric</class>\n");
        }
        if (isDruid) {
            writer.append("        <class>Druid</class>\n");
        }
        if (isPaladin) {
            writer.append("        <class>Paladin</class>\n");
        }
        if (isRanger) {
            writer.append("        <class>Ranger</class>\n");
        }
        if (isSorceror) {
            writer.append("        <class>Sorcerer</class>\n");
        }
        if (isWarlock) {
            writer.append("        <class>Warlock</class>\n");
        }
        if (isWizard) {
            writer.append("        <class>Wizard</class>\n");
        }
        writer.append("    </classes>\n");

        writer.append("    <duration>");
        writer.append(duration);
        writer.append("</duration>\n");

        writer.append("    <range>");
        writer.append(range);
        writer.append("</range>\n");

        if (isRitual) {
            writer.append("    <ritual>true</ritual>\n");
        }


        writer.append("    <castingTime>");
        writer.append(action);
        writer.append("</castingTime>\n");

        writer.append("    <components>");
        writer.append(components);
        writer.append("</components>\n");

        writer.append("    <shortDescription>");
        writer.append(description.replaceAll("<", "&lt;") + "\n");
        writer.append(higherLevelDescription.replaceAll("<", "&lt;"));
        writer.append("</shortDescription>\n");


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
}
