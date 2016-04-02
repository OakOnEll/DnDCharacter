package com.oakonell.dndcharacter.model;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.character.CharacterRow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 3/29/2016.
 */
public class CharacterExporter {
    public List<ExportRow> exportRows = new ArrayList<>();

    public List<ExportRow> fetchExportRows(long[] checkedIdsArray) {
        exportRows = new ArrayList<>();
        Set<Long> checkedIds = new HashSet<>();
        if (checkedIdsArray != null) {
            for (long each : checkedIdsArray) {
                checkedIds.add(each);
            }
        }
        final List<CharacterRow> models = new Select().from(CharacterRow.class).execute();
        for (CharacterRow each : models) {
            String name = each.getName();
            ExportRow exportRow = new ExportRow();
            exportRow.id = each.getId();

            exportRow.name = name;
            exportRow.classes = each.classesString;
            exportRow.race_display_name = each.race_display_name;

            if (checkedIds.contains(exportRow.id)) {
                exportRow.shouldExport = true;
            }
            exportRows.add(exportRow);
        }
        return exportRows;
    }

    public int getNumberToExport() {
        int num = 0;
        for (ExportRow each : exportRows) {
            if (each.shouldExport) num++;
        }
        return num;
    }

    public static class ExportRow {
        public long id;

        public String name;
        public String classes;
        public String race_display_name;

        public boolean shouldExport;
        public boolean exported;
    }


}
