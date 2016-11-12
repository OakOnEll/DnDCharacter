package com.oakonell.dndcharacter.model;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.character.CharacterRow;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.effect.Effect;
import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.utils.ProgressData;
import com.oakonell.dndcharacter.utils.ProgressReportingInputStream;
import com.oakonell.dndcharacter.utils.ProgressUpdater;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 3/11/2016.
 */
public class DataImporter {
    public static final int BYTE_BUFFER_SIZE = 4096;
    private final Context context;
    private final List<DataImporter.ImportRow> importRows = new ArrayList<>();

    boolean fileRead;

    public DataImporter(Context context) {
        this.context = context;
    }

    public static class DataImportException extends RuntimeException {
        private final int messageResource;
        private final String[] arguments;

        DataImportException(Exception e, int messageResource, String... arguments) {
            super(e);
            this.messageResource = messageResource;
            this.arguments = arguments;
        }

        DataImportException(@SuppressWarnings("SameParameterValue") int messageResource, String... arguments) {
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

    public void loadUrl(@NonNull Context context, @NonNull URL url, @NonNull final ProgressUpdater progress) {
        fileRead = false;
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
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
                    ProgressData progressData = new ProgressData();
                    progressData.progress = total;
                    progressData.total = fileLength;
                    progress.progress(progressData);
                }
                output.write(data, 0, count);
            }


            loadFile(Uri.fromFile(outputFile), progress);
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

    @NonNull
    public List<ImportRow> getImportRows() {
        return importRows;
    }

    public static class ImportResult extends ProgressData {
        public int updated = 0;
        public int added = 0;

        public boolean needToUpdateCharacters = false;
        public Map<String, List<ImportRow>> rowsImportedByType = new HashMap<>();


        public CharacterUpdateProgress characterResult;
    }

    public static class CharacterUpdateProgress extends ProgressData {
        public List<String> errors = new ArrayList<>();
    }

    public static class ImportRow {
        public boolean imported;
        public String message;
        public String info;
        public boolean shouldImport;
        public String type;
        @Nullable
        public String name;
        public Element element;

        public long importToId;
    }


    public void loadFile(@NonNull Uri uri, ProgressUpdater progress) {
        fileRead = false;
        importRows.clear();
        Document doc = readDocument(uri, progress);
        Element root = doc.getDocumentElement();
        List<Element> children = XmlUtils.getChildElements(root);
        for (Element each : children) {
            String type = each.getNodeName();
            String name = XmlUtils.getElementText(each, "name");
            ImportRow row = new ImportRow();
            row.name = name;
            row.type = type;
            if (row.type.toLowerCase().equals("character")) {
                CharacterRow model = new Select().from(CharacterRow.class).where("name = ?", row.name).executeSingle();
                row.shouldImport = model == null;
                if (model != null) {
                    row.info = context.getString(R.string.character_already_exists);
                }
            } else {
                row.shouldImport = true;
            }
            row.element = each;
            importRows.add(row);
        }
        fileRead = true;
    }

    @Nullable
    private Document readDocument(@NonNull Uri uri, ProgressUpdater progress) {
        InputStream in = null;
        try {
            in = new ProgressReportingInputStream(context.getContentResolver().openInputStream(uri), progress);
        } catch (IOException e) {
            throw new DataImportException(e, R.string.error_reading_uri, uri.toString());
        } catch (ProgressReportingInputStream.InputStreamCanceled e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    Log.e("DataImporter", "Error while closing a canceled input stream read", e);
                }
            }
            return null;
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
    public ImportResult importRows(@Nullable ProgressUpdater progress) {
        DataImporter.ImportResult result = new DataImporter.ImportResult();

        for (DataImporter.ImportRow each : importRows) {
            if (!each.shouldImport) continue;
            result.total++;
        }
        result.needToUpdateCharacters = false;
        for (DataImporter.ImportRow each : importRows) {
            if (!each.shouldImport) continue;

            if (progress.isCancelled()) {
                return result;
            }

            boolean success = true;
            Class<? extends AbstractComponentModel> modelClass;

            final String type = each.type.toLowerCase();
            switch (type) {
                case "background":
                    modelClass = Background.class;
                    result.needToUpdateCharacters = true;
                    break;
                case "class":
                    modelClass = AClass.class;
                    result.needToUpdateCharacters = true;
                    break;
                case "race":
                    modelClass = Race.class;
                    result.needToUpdateCharacters = true;
                    break;
                case "item":
                    modelClass = ItemRow.class;
                    result.needToUpdateCharacters = true;
                    break;
                case "effect":
                    modelClass = Effect.class;
                    result.needToUpdateCharacters = true;
                    break;
                case "spell":
                    modelClass = Spell.class;
                    result.needToUpdateCharacters = true;
                    break;
                case "feat":
                    modelClass = Feat.class;
                    result.needToUpdateCharacters = true;
                    break;
                case "character":
                    // TODO warn about character overwrites..
                    modelClass = CharacterRow.class;
                    break;
                default:
                    each.message = "Not imported. Type not handled";
                    modelClass = null;
                    result.error++;
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
                    model.setDocumentAndSave(context, each.element);
                    if (isUpdated) {
                        result.updated++;
                    } else {
                        result.added++;
                    }
                    result.progress++;
                    each.imported = true;
                    each.importToId = model.getId();
                    List<ImportRow> importedRows = result.rowsImportedByType.get(type);
                    if (importedRows == null) {
                        importedRows = new ArrayList<>();
                        result.rowsImportedByType.put(type, importedRows);
                    }
                    importedRows.add(each);
                    each.shouldImport = false;
                } catch (Exception e) {
                    result.error++;
                    result.progress++;
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
