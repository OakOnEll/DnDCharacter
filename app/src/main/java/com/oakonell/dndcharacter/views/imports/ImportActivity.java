package com.oakonell.dndcharacter.views.imports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.oakonell.dndcharacter.BuildConfig;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.DataImporter;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImportActivity extends AppCompatActivity {
    public static final String EXTRA_FILE_PATH = "filepath";
    public static final String EXTRA_URL_PATH = "url";
    private static final int FILE_IMPORT_REQUEST = 1;

    private static final String LAST_IMPORTED_URL_PREF = "lastImportedUrl";

    private static final int FILE_TYPE_INDEX = 0;
    private static final int URL_TYPE_INDEX = 1;
    private static final String PREF_LAST_IMPORT_TYPE = "lastImportType";

    @NonNull
    private DataImporter importer = new DataImporter();

    private Spinner importTypeSpinner;
    private TextView filenameText;
    private ImageButton searchButton;
    private ImageButton downloadButton;
    ProgressDialog mUrlDownloadProgressDialog;
    private TextView import_summary;
    private CheckBox select_all;
    private boolean changingSelectState;

    private RecyclerView listView;
    private Button importRowsButton;
    private RecyclerView.Adapter<ImportRowViewHolder> listAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

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

        select_all = (CheckBox) findViewById(R.id.select_all);

        importTypeSpinner = (Spinner) findViewById(R.id.import_type);
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (importTypeSpinner.getPrompt().length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, importTypeSpinner.getResources().getDisplayMetrics());
        importTypeSpinner.setMinimumWidth((int) minWidth);

        Button done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        List<String> list = new ArrayList<>();
        list.add(getString(ImportType.FILE.getResourceId()));
        list.add(getString(ImportType.URL.getResourceId()));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        importTypeSpinner.setAdapter(dataAdapter);


        listAdapter = new ImportRowAdapter(this, importer.getImportRows());
        listView.setAdapter(listAdapter);
        listView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);

        ImportInputs defaultedInputs = getDefaultedInputs(savedInstanceState);

        int typeIndex = defaultedInputs.type == ImportType.FILE ? 0 : 1;
        importTypeSpinner.setSelection(typeIndex);
        filenameText.setText(defaultedInputs.name);
        if (defaultedInputs.name != null && typeIndex == 0) {
            attemptToLoadFile(defaultedInputs.name);
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

        importTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                import_summary.setText("");
                importer.clear();
                listAdapter.notifyDataSetChanged();
                if (position == FILE_TYPE_INDEX) {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ImportActivity.this);
                    String filename = sharedPrefs.getString(EXTRA_FILE_PATH, null);
                    filenameText.setText(filename);
                    if (filename != null) {
                        attemptToLoadFile(filename);
                    }
                } else {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ImportActivity.this);
                    String filename = sharedPrefs.getString(LAST_IMPORTED_URL_PREF, null);
                    filenameText.setText(filename);
                }
                updateViews();
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

    private void attemptToLoadFile(@NonNull final String filename) {
        attemptToLoadFile(filename, null);
    }

    private void attemptToLoadFile(@NonNull final String filename, @Nullable final Runnable uiContinuation) {
        final ProgressDialog barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle(getString(R.string.loading_file_title));
        barProgressDialog.setMessage(getString(R.string.loading_file));
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setIndeterminate(true);
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Nullable
            @Override
            protected String doInBackground(Void... params) {
                try {
                    importer.loadFile(filename);

                } catch (DataImporter.DataImportException e) {
                    return e.getMessageText(getResources());

                }
                return null;
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
        };
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

        AsyncTask<Void, Integer, DataImporter.ImportResult> task = new AsyncTask<Void, Integer, DataImporter.ImportResult>() {
            @NonNull
            @Override
            protected DataImporter.ImportResult doInBackground(Void... params) {
                DataImporter.ImportProgress progress = new DataImporter.ImportProgress() {
                    @Override
                    public void progress(@NonNull DataImporter.ImportResult progress) {
                        publishProgress(progress.updated + progress.added, progress.failed);
                    }
                };
                return importer.importRows(progress);
            }


            @Override
            protected void onProgressUpdate(Integer... values) {
                barProgressDialog.setProgress(values[0]);
                listView.smoothScrollToPosition(values[0] + values[1]);
            }

            @Override
            protected void onPostExecute(@NonNull DataImporter.ImportResult importResult) {
                import_summary.setText(getString(R.string.import_summary, importResult.added, importResult.updated, importResult.failed));
                barProgressDialog.dismiss();
                listAdapter.notifyDataSetChanged();
                updateViews();
            }
        };
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
            attemptToLoadFile(filename, uiContinuation);
            updateViews();

            // Do anything with file
        }
    }


    public static class ImportRowViewHolder extends RecyclerView.ViewHolder {
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

        public ImportRowViewHolder(@NonNull View itemView) {
            super(itemView);
            should_import = (CheckBox) itemView.findViewById(R.id.should_import);
            name = (TextView) itemView.findViewById(R.id.name);
            type = (TextView) itemView.findViewById(R.id.type);
            error = (TextView) itemView.findViewById(R.id.error);
            imported = (ImageView) itemView.findViewById(R.id.imported);

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
            holder.name.setText(row.name);
            holder.type.setText(row.type);
            holder.should_import.setOnCheckedChangeListener(null);
            holder.should_import.setChecked(row.shouldImport);
            if (row.imported) {
                holder.should_import.setVisibility(View.GONE);
                holder.imported.setVisibility(View.VISIBLE);
            } else {
                holder.should_import.setVisibility(View.VISIBLE);
                holder.imported.setVisibility(View.GONE);
            }
            if (row.message != null) {
                holder.error.setText(row.message);
            } else {
                holder.error.setText("");
            }

            holder.should_import.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
    private class DownloadTask extends AsyncTask<String, Long, String> {
        private Context context;
        private String urlString;

        // TODO use a wakelock.. ?   https://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
        public DownloadTask(Context context) {
            this.context = context;
        }

        @Nullable
        @Override
        protected String doInBackground(String... sUrl) {
            DataImporter.LoadProgress progress = new DataImporter.LoadProgress() {
                @Override
                public void progress(long bytes, long total) {
                    publishProgress(bytes, total);
                }

                @Override
                public boolean isCancelled() {
                    return false;
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
        protected void onProgressUpdate(Long... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mUrlDownloadProgressDialog.setIndeterminate(false);

            // not going to be downloading gb files, I assume...
            int downloaded = (int) (long) progress[0];
            int total = (int) (long) progress[1];

            mUrlDownloadProgressDialog.setMax(total);
            mUrlDownloadProgressDialog.setProgress(downloaded);
        }

        @Override
        protected void onPostExecute(@Nullable String result) {
            mUrlDownloadProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
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

}
