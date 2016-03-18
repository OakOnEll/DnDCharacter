package com.oakonell.dndcharacter.model;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.effect.Effect;
import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Rob on 3/11/2016.
 */
public class DataImporter {
    public static final int BYTE_BUFFER_SIZE = 4096;
    boolean fileRead;

    public static class DataImportException extends RuntimeException {
        private final int messageResource;
        private final String[] arguments;

        DataImportException(Exception e, int messageResource, String... arguments) {
            super(e);
            this.messageResource = messageResource;
            this.arguments = arguments;
        }

        DataImportException(int messageResource, String... arguments) {
            this.messageResource = messageResource;
            this.arguments = arguments;
        }

        public int getMessageResource() {
            return messageResource;
        }

        public String[] getArguments() {
            return arguments;
        }

        @NonNull
        public String getMessageText(@NonNull Resources resources) {
            return resources.getString(messageResource, arguments);
        }
    }

    public boolean fileRead() {
        return fileRead;
    }

    public void clear() {
        importRows.clear();
    }

    public interface LoadProgress {
        void progress(long bytes, long total);

        boolean isCancelled();
    }

    public void loadUrl(@NonNull Context context, @NonNull URL url, @NonNull LoadProgress progress) {
        fileRead = false;
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new DataImportException(R.string.bad_http_return_code, "" + connection.getResponseCode(), connection.getResponseMessage());
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();

            File outputDir = context.getCacheDir(); // context being the Activity pointer
            File outputFile = File.createTempFile("import", "xml", outputDir);
            output = new FileOutputStream(outputFile);

            byte data[] = new byte[BYTE_BUFFER_SIZE];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (progress.isCancelled()) {
                    input.close();
                    return;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) {
                    // only if total length is known
                    progress.progress(total, fileLength);
                }
                output.write(data, 0, count);
            }
            loadFile(outputFile);
            // delete the file when no longer needed..
            outputFile.delete();
        } catch (IOException e) {
            throw new DataImportException(e, R.string.io_exception, e.getMessage());
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public interface ImportProgress {
        void progress(ImportResult progress);
    }

    public static boolean hasAtLeastMinimalData() {
        Cursor c = ActiveAndroid.getDatabase().rawQuery("SELECT COUNT(*) as total FROM race", null);
        try {
            if (!c.moveToFirst()) return false;
            int total = c.getInt(c.getColumnIndex("total"));
            return total > 0;
        } finally {
            if (c != null) c.close();
        }

    }

    private final List<DataImporter.ImportRow> importRows = new ArrayList<>();

    @NonNull
    public List<ImportRow> getImportRows() {
        return importRows;
    }

    public static class ImportResult {
        public int failed = 0;
        public int updated = 0;
        public int added = 0;

        public UpdateCharacters.CharacterUpdateResult characterResult;
    }

    public static class ImportRow {
        public boolean imported;
        public String message;
        public boolean shouldImport;
        public String type;
        @Nullable
        public String name;
        public Element element;
    }

    public void loadFile(@NonNull String filename) {
        File file = new File(filename);
        loadFile(file);
    }


    protected void loadFile(@NonNull File file) {
        fileRead = false;
        importRows.clear();
        Document doc = readDocument(file);
        Element root = doc.getDocumentElement();
        List<Element> children = XmlUtils.getChildElements(root);
        for (Element each : children) {
            String type = each.getNodeName();
            String name = XmlUtils.getElementText(each, "name");
            ImportRow row = new ImportRow();
            row.name = name;
            row.type = type;
            row.shouldImport = true;
            row.element = each;
            importRows.add(row);
        }
        fileRead = true;
    }

    @Nullable
    private Document readDocument(@NonNull File file) {
        FileInputStream in;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new DataImportException(e, R.string.file_not_found);
        }
        return XmlUtils.getDocument(in);
    }

    public int getNumberToImport() {
        int num = 0;
        for (ImportRow each : importRows) {
            if (each.shouldImport) num++;
        }
        return num;
    }

    @NonNull
    public ImportResult importRows(@Nullable ImportProgress progress) {
        DataImporter.ImportResult result = new DataImporter.ImportResult();

        for (DataImporter.ImportRow each : importRows) {
            if (!each.shouldImport) continue;

            boolean success = true;
            Class<? extends AbstractComponentModel> modelClass;

            switch (each.type.toLowerCase()) {
                case "background":
                    modelClass = Background.class;
                    break;
                case "class":
                    modelClass = AClass.class;
                    break;
                case "race":
                    modelClass = Race.class;
                    break;
                case "item":
                    modelClass = ItemRow.class;
                    break;
                case "effect":
                    modelClass = Effect.class;
                    break;
                case "spell":
                    modelClass = Spell.class;
                    break;
                case "feat":
                    modelClass = Feat.class;
                    break;
                default:
                    each.message = "Not imported. Type not handled";
                    modelClass = null;
                    result.failed++;
                    success = false;
            }

            if (success) {
                try {
                    AbstractComponentModel model = new Select().from(modelClass).where("name = ?", each.name).executeSingle();
                    // TODO is it faster to skip when the xml is unchanged?
                    boolean isUpdated = true;
                    if (model == null) {
                        model = modelClass.newInstance();
                        isUpdated = false;
                    }
                    model.setDocumentAndSave(each.element);
                    if (isUpdated) {
                        result.updated++;
                    } else {
                        result.added++;
                    }
                    each.imported = true;
                    each.shouldImport = false;
                } catch (Exception e) {
                    result.failed++;
                    each.message = "Error importing. " + e.getMessage();
                }
            }
            if (progress != null) {
                progress.progress(result);
            }
        }

        // cleanup the imported rows..
        for (Iterator<ImportRow> iter = importRows.iterator(); iter.hasNext(); ) {
            ImportRow each = iter.next();
            if (each.imported) {
                iter.remove();
            }
        }

        return result;
    }

}
