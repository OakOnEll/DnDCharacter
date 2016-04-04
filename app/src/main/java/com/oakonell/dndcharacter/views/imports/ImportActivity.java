package com.oakonell.dndcharacter.views.imports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.oakonell.dndcharacter.BuildConfig;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.DataImporter;
import com.oakonell.dndcharacter.model.UpdateCharacters;
import com.oakonell.dndcharacter.utils.ProgressData;
import com.oakonell.dndcharacter.utils.ProgressUpdater;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.AbstractBaseActivity;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.characters.CharactersListActivity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImportActivity extends AbstractBaseActivity {
    public static final String EXTRA_FILE_PATH = "filepath";
    public static final String EXTRA_URL_PATH = "url";
    private static final int FILE_IMPORT_REQUEST = 1;

    private static final String LAST_IMPORTED_URL_PREF = "lastImportedUrl";

    private static final int FILE_TYPE_INDEX = 0;
    private static final int URL_TYPE_INDEX = 1;
    private static final String PREF_LAST_IMPORT_TYPE = "lastImportType";

    @NonNull
    private DataImporter importer = new DataImporter(this);

    private Spinner importTypeSpinner;
    private TextView filenameText;
    private ImageButton searchButton;
    private ImageButton downloadButton;
    ProgressDialog mUrlDownloadProgressDialog;
    private TextView import_summary;
    private TextView character_update_summary;
    private CheckBox select_all;
    private boolean changingSelectState;

    private RecyclerView listView;
    private Button importRowsButton;
    private RecyclerView.Adapter<ImportRowViewHolder> listAdapter;
    private int lastTypePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        configureCommon();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(R.string.import_title);
        }


        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
//                        .detectDiskReads()
                    .penaltyLog()
                    .penaltyFlashScreen()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
//                        .detectLeakedSqlLiteObjects()
//                        .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
        searchButton = (ImageButton) findViewById(R.id.search);
        downloadButton = (ImageButton) findViewById(R.id.download);

        listView = (RecyclerView) findViewById(R.id.list);
        importRowsButton = (Button) findViewById(R.id.import_rows);
        filenameText = (TextView) findViewById(R.id.filename);
        import_summary = (TextView) findViewById(R.id.import_summary);
        character_update_summary = (TextView) findViewById(R.id.character_update_summary);

        select_all = (CheckBox) findViewById(R.id.select_all);

        importTypeSpinner = (Spinner) findViewById(R.id.import_type);
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (importTypeSpinner.getPrompt().length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, importTypeSpinner.getResources().getDisplayMetrics());
        importTypeSpinner.setMinimumWidth((int) minWidth);

        List<String> list = new ArrayList<>();
        list.add(getString(ImportType.FILE.getResourceId()));
        list.add(getString(ImportType.URL.getResourceId()));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        importTypeSpinner.setAdapter(dataAdapter);


        Button done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        listAdapter = new ImportRowAdapter(this, importer.getImportRows());
        listView.setAdapter(listAdapter);
        listView.setLayoutManager(UIUtils.createLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);


        Intent intent = getIntent();
        final Uri data = intent.getData();
        if (data != null) {
            Log.i("Import", "Importing from intent data..");
            final String scheme = data.getScheme();
            switch (scheme) {
                case "content":
                case "file": {
                    Log.i("Import", "Importing from 'file' data..");
                    int typeIndex = FILE_TYPE_INDEX;
                    importTypeSpinner.setSelection(typeIndex);
                    filenameText.setText(data.toString());
                    attemptToLoadFile(data);
                    importTypeSpinner.setEnabled(false);
                    searchButton.setVisibility(View.GONE);
                    break;
                }
                case "http": {
                    Log.i("Import", "Importing from http data..");
                    int typeIndex = URL_TYPE_INDEX;
                    importTypeSpinner.setSelection(typeIndex);
                    filenameText.setText(data.toString());
                    loadUrl(data.toString());
                    importTypeSpinner.setEnabled(false);
                    break;
                }
                default:
                    // report an error? shouldn't get here, based on AndroidManifest Intent filters
                    break;
            }
        } else {
            Log.i("Import", "Importing from saved inputs..");
            ImportInputs defaultedInputs = getDefaultedInputs(savedInstanceState);

            int typeIndex = defaultedInputs.type == ImportType.FILE ? 0 : 1;
            importTypeSpinner.setSelection(typeIndex);
            filenameText.setText(defaultedInputs.name);
            if (defaultedInputs.name != null && typeIndex == 0) {
                File file = new File(defaultedInputs.name);
                final Uri uri = Uri.fromFile(file);
                attemptToLoadFile(uri);
            }
        }


        select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (changingSelectState) return;

                for (DataImporter.ImportRow each : importer.getImportRows()) {
                    each.shouldImport = isChecked;
                }

                updateViews();
                listAdapter.notifyDataSetChanged();
            }
        });

        lastTypePosition = importTypeSpinner.getSelectedItemPosition();
        importTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (importTypeSpinner.getSelectedItemPosition() == lastTypePosition) return;

                import_summary.setText("");
                character_update_summary.setText("");
                importer.clear();
                listAdapter.notifyDataSetChanged();
                if (position == FILE_TYPE_INDEX) {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ImportActivity.this);
                    String filename = sharedPrefs.getString(EXTRA_FILE_PATH, null);
                    filenameText.setText(filename);
                    File file = new File(filename);
                    final Uri uri = Uri.fromFile(file);

                    if (filename != null) {
                        attemptToLoadFile(uri);
                    }
                } else {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ImportActivity.this);
                    String filename = sharedPrefs.getString(LAST_IMPORTED_URL_PREF, null);
                    filenameText.setText(filename);
                }
                updateViews();
                lastTypePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImportActivity.this, FilePickerActivity.class);
                startActivityForResult(intent, FILE_IMPORT_REQUEST);
            }
        });


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                import_summary.setText("");
                final String url = filenameText.getText().toString();
                loadUrl(url);
            }
        });

        importRowsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importRows();
            }
        });

        updateViews();
    }

    private void attemptToLoadFile(@NonNull final Uri uri) {
        attemptToLoadFile(uri, null);
    }

    private void attemptToLoadFile(@NonNull final Uri uri, @Nullable final Runnable uiContinuation) {
        final ProgressDialog barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle(getString(R.string.loading_file_title));
        barProgressDialog.setMessage(getString(R.string.loading_file));
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setIndeterminate(true);
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);

        AsyncTask<Void, ProgressData, String> task = new LoadFileTask(uri, barProgressDialog, uiContinuation);
        task.execute();
    }

    @NonNull
    private ImportInputs getDefaultedInputs(@Nullable Bundle savedInstanceState) {
        ImportInputs result = new ImportInputs();
        // look in saved state
        if (savedInstanceState != null) {
            result.name = savedInstanceState.getString(EXTRA_FILE_PATH);
            int typeIndex = savedInstanceState.getInt(PREF_LAST_IMPORT_TYPE);
            if (typeIndex == 0) {
                result.type = ImportType.FILE;
            } else {
                result.type = ImportType.URL;
            }
            return result;
        }

        // if not there, look on intent
        if (getIntent() != null && getIntent().getExtras() != null) {
            result.name = getIntent().getExtras().getString(EXTRA_FILE_PATH);
            if (result.name != null) {
                result.type = ImportType.FILE;
                return result;
            }
            result.name = getIntent().getExtras().getString(EXTRA_URL_PATH);
            if (result.name != null) {
                result.type = ImportType.URL;
                return result;
            }
        }

        // if not there, populate with last used value
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int typeIndex = sharedPrefs.getInt(PREF_LAST_IMPORT_TYPE, 0);
        if (typeIndex == 0) {
            result.type = ImportType.FILE;
        } else {
            result.type = ImportType.URL;
        }
        result.name = sharedPrefs.getString(EXTRA_FILE_PATH, "");
        return result;
    }

    private void loadUrl(String filename) {
        // instantiate it within the onCreate method
        mUrlDownloadProgressDialog = new ProgressDialog(this);
        mUrlDownloadProgressDialog.setMessage(getString(R.string.download_url_data));
        mUrlDownloadProgressDialog.setIndeterminate(true);
        mUrlDownloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mUrlDownloadProgressDialog.setCancelable(true);
        mUrlDownloadProgressDialog.show();

// execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute(filename);

        mUrlDownloadProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    private void updateViews() {
        int position = importTypeSpinner.getSelectedItemPosition();
        if (position == FILE_TYPE_INDEX) {
            filenameText.setEnabled(false);
            searchButton.setVisibility(View.VISIBLE);
            downloadButton.setVisibility(View.GONE);
        } else {
            filenameText.setEnabled(true);
            searchButton.setVisibility(View.GONE);
            downloadButton.setVisibility(View.VISIBLE);
        }

        importRowsButton.setEnabled(importer.fileRead() && importer.getNumberToImport() > 0);
        select_all.setEnabled(importer.fileRead() && importer.getImportRows().size() > 0);
    }


    private void importRows() {
        import_summary.setText("");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor edit = sharedPrefs.edit();
        edit.putInt(PREF_LAST_IMPORT_TYPE, importTypeSpinner.getSelectedItemPosition());
        edit.apply();

        final ProgressDialog barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle(getString(R.string.import_rows_progress_title));
        barProgressDialog.setMessage(getString(R.string.import_rows_message));
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(importer.getNumberToImport());
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);

        AsyncTask<Void, ProgressData, DataImporter.ImportResult> task = new ImportTask(barProgressDialog);
        task.execute();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String filename = filenameText.getText().toString();
        outState.putString(EXTRA_FILE_PATH, filename);
        outState.putInt(PREF_LAST_IMPORT_TYPE, importTypeSpinner.getSelectedItemPosition());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_IMPORT_REQUEST && resultCode == RESULT_OK) {
            final String filename = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            filenameText.setText(filename);
            Runnable uiContinuation = new Runnable() {
                @Override
                public void run() {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ImportActivity.this);
                    final SharedPreferences.Editor edit = sharedPrefs.edit();
                    edit.putString(EXTRA_FILE_PATH, filename);
                    edit.apply();
                }
            };
            File file = new File(filename);
            final Uri uri = Uri.fromFile(file);
            attemptToLoadFile(uri, uiContinuation);
            updateViews();

            // Do anything with file
        }
    }


    public static class ImportRowViewHolder extends BindableComponentViewHolder<DataImporter.ImportRow, ImportActivity, ImportRowAdapter> {
        @NonNull
        final CheckBox should_import;
        @NonNull
        final TextView type;
        @NonNull
        final TextView name;
        @NonNull
        final ImageView imported;
        @NonNull
        final TextView error;
        private final TextView info;

        public ImportRowViewHolder(@NonNull View itemView) {
            super(itemView);
            should_import = (CheckBox) itemView.findViewById(R.id.should_import);
            name = (TextView) itemView.findViewById(R.id.name);
            type = (TextView) itemView.findViewById(R.id.type);
            error = (TextView) itemView.findViewById(R.id.error);
            info = (TextView) itemView.findViewById(R.id.info);
            imported = (ImageView) itemView.findViewById(R.id.imported);

        }

        @Override
        public void bind(final ImportActivity activity, final ImportRowAdapter adapter, final DataImporter.ImportRow row) {
            name.setText(row.name);
            if (row.info != null) {
                info.setText(row.info);
                info.setVisibility(View.VISIBLE);
            } else {
                info.setVisibility(View.GONE);
                info.setText("");
            }
            type.setText(row.type);
            should_import.setOnCheckedChangeListener(null);
            should_import.setChecked(row.shouldImport);
            if (row.imported) {
                should_import.setVisibility(View.GONE);
                imported.setVisibility(View.VISIBLE);
            } else {
                should_import.setVisibility(View.VISIBLE);
                imported.setVisibility(View.GONE);
            }
            if (row.message != null) {
                error.setText(row.message);
                error.setVisibility(View.VISIBLE);
            } else {
                error.setVisibility(View.GONE);
                error.setText("");
            }

            should_import.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    row.shouldImport = isChecked;
                    activity.importRowsButton.setEnabled(activity.importer.getNumberToImport() > 0);

                    final boolean selectAllChecked = activity.select_all.isChecked();
                    if (selectAllChecked && !isChecked) {
                        activity.changingSelectState = true;
                        activity.select_all.setChecked(false);
                        activity.changingSelectState = false;
                    }


                }
            });
        }
    }


    public static class ImportRowAdapter extends RecyclerView.Adapter<ImportRowViewHolder> {
        private final List<DataImporter.ImportRow> rows;
        private final ImportActivity activity;

        ImportRowAdapter(ImportActivity activity, List<DataImporter.ImportRow> rows) {
            this.activity = activity;
            this.rows = rows;
        }

        @NonNull
        @Override
        public ImportRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.import_row_item, parent, false);
            return new ImportRowViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull ImportRowViewHolder holder, int position) {
            final DataImporter.ImportRow row = getItem(position);
            holder.bind(activity, this, row);
        }

        public DataImporter.ImportRow getItem(int position) {
            return rows.get(position);
        }

        @Override
        public int getItemCount() {
            return rows.size();
        }
    }


    // usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, ProgressData, String> {
        private Context context;
        private String urlString;

        // TODO use a wakelock.. ?   https://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
        public DownloadTask(Context context) {
            this.context = context;
        }

        @Nullable
        @Override
        protected String doInBackground(String... sUrl) {
            ProgressUpdater progress = new ProgressUpdater() {
                @Override
                public void progress(ProgressData data) {
                    publishProgress(data);
                }

                @Override
                public boolean isCancelled() {
                    return DownloadTask.this.isCancelled();
                }
            };

            try {
                urlString = sUrl[0];
                URL url = new URL(urlString);
                importer.loadUrl(context, url, progress);
                return null;
            } catch (MalformedURLException e) {
                return getString(R.string.invalid_url);
            } catch (DataImporter.DataImportException e) {
                return getString(e.getMessageResource(), e.getArguments());
            }
        }

        @Override
        protected void onProgressUpdate(ProgressData... progress) {
            // if we get here, length is known, now set indeterminate to false
            mUrlDownloadProgressDialog.setIndeterminate(false);

            // not going to be downloading gb files, I assume...
            int downloaded = (int) (long) progress[0].progress;
            int total = (int) (long) progress[0].total;

            mUrlDownloadProgressDialog.setMax(total);
            mUrlDownloadProgressDialog.setProgress(downloaded);

            final String message = progress[0].message;
            if (message != null && message.trim().length() > 0) {
                mUrlDownloadProgressDialog.setMessage(message);
            }
        }

        @Override
        protected void onPostExecute(@Nullable String result) {
            mUrlDownloadProgressDialog.dismiss();
            if (result != null) {
                import_summary.setText(getString(R.string.download_error) + result);
                return;
            }
            changingSelectState = true;
            select_all.setChecked(true);
            changingSelectState = false;

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor edit = sharedPrefs.edit();
            edit.putString(LAST_IMPORTED_URL_PREF, urlString);
            edit.apply();
            listAdapter.notifyDataSetChanged();
            updateViews();
        }
    }

    enum ImportType {
        FILE(R.string.import_from_file), URL(R.string.import_from_url);

        private final int resourceId;

        ImportType(int resourceId) {
            this.resourceId = resourceId;
        }

        public int getResourceId() {
            return resourceId;
        }
    }

    static class ImportInputs {
        ImportType type;
        @Nullable
        String name;
    }

    private final class LoadFileTask extends AsyncTask<Void, ProgressData, String> {
        private final ProgressDialog barProgressDialog;
        private final Uri uri;
        private final Runnable uiContinuation;

        LoadFileTask(Uri uri, ProgressDialog barProgressDialog, Runnable uiContinuation) {
            this.uri = uri;
            this.barProgressDialog = barProgressDialog;
            this.uiContinuation = uiContinuation;
        }

        @Nullable
        @Override
        protected String doInBackground(Void... params) {
            try {
                importer.loadFile(uri, new ProgressUpdater() {
                    @Override
                    public void progress(ProgressData progress) {
                        publishProgress(progress);
                    }

                    @Override
                    public boolean isCancelled() {
                        return LoadFileTask.this.isCancelled();
                    }
                });

            } catch (DataImporter.DataImportException e) {
                return e.getMessageText(getResources());

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ProgressData... values) {
            // TODO worry about size exceeding int storage bytes?
            barProgressDialog.setIndeterminate(false);
            barProgressDialog.setProgress((int) values[0].progress);
            barProgressDialog.setMax((int) values[0].total);
            final String message = values[0].message;
            if (message != null && message.trim().length() > 0) {
                barProgressDialog.setMessage(message);
            }
        }

        @Override
        protected void onPostExecute(@Nullable String result) {
            barProgressDialog.dismiss();
            if (result != null) {
                import_summary.setText(result);
            } else {
                changingSelectState = true;
                select_all.setChecked(true);
                changingSelectState = false;
                if (uiContinuation != null) {
                    uiContinuation.run();
                }
            }
            listAdapter.notifyDataSetChanged();
            updateViews();
        }
    }


    private class ImportTask extends AsyncTask<Void, ProgressData, DataImporter.ImportResult> {
        boolean scrollImportList = true;
        private final ProgressDialog barProgressDialog;

        ImportTask(ProgressDialog barProgressDialog) {
            this.barProgressDialog = barProgressDialog;
        }

        @NonNull
        @Override
        protected DataImporter.ImportResult doInBackground(Void... params) {
            ProgressUpdater progress = new ProgressUpdater() {
                @Override
                public void progress(@NonNull ProgressData progress) {
                    publishProgress(progress);
                }

                @Override
                public boolean isCancelled() {
                    return ImportTask.this.isCancelled();
                }
            };
            DataImporter.ImportResult result = importer.importRows(progress);
            if (result.needToUpdateCharacters && result.updated > 0) {
                // update any characters that may be affected by imported data
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        barProgressDialog.setMessage(getString(R.string.updating_characters));
                        barProgressDialog.setMax(UpdateCharacters.getNumberCharacters());

                    }
                });
                scrollImportList = false;
                ProgressData charResult = UpdateCharacters.updateCharacters(ImportActivity.this, progress);
                result.characterResult = charResult;
            }
            return result;
        }


        @Override
        protected void onProgressUpdate(ProgressData... values) {
            final ProgressData result = values[0];
            barProgressDialog.setProgress((int) result.progress);
            if (scrollImportList) {
                listView.smoothScrollToPosition((int) result.progress);
            }
            final String message = result.message;
            if (message != null && message.trim().length() > 0) {
                barProgressDialog.setMessage(message);
            }
        }

        @Override
        protected void onPostExecute(@NonNull DataImporter.ImportResult importResult) {
            import_summary.setText(getString(R.string.import_summary, importResult.added, importResult.updated, importResult.error));
            if (importResult.characterResult != null) {
                character_update_summary.setText(getString(R.string.character_update_summary, importResult.characterResult.progress, importResult.characterResult.error));
            }
            barProgressDialog.dismiss();
            listAdapter.notifyDataSetChanged();
            updateViews();

            if (importResult.error > 0) return;
            if (importResult.characterResult != null && importResult.characterResult.error > 0) {
                return;
            }

            // If only characters were imported, we can navigate to either character list, or the single character imported
            if (importResult.rowsImportedByType.size() != 1) return;
            final List<DataImporter.ImportRow> importedCharacters = importResult.rowsImportedByType.get("character");
            if (importedCharacters == null) return;
            if (importedCharacters.size() == 0) return;
            if (importedCharacters.size() > 1) {
                Intent intent = new Intent(ImportActivity.this, CharactersListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return;
            }
            final DataImporter.ImportRow importRow = importedCharacters.get(0);
            long characterId = importRow.importToId;

            openCharacter(characterId);
        }
    }

}
