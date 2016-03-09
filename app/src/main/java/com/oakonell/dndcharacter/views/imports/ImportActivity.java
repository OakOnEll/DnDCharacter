package com.oakonell.dndcharacter.views.imports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.oakonell.dndcharacter.BuildConfig;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
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
import java.util.ArrayList;
import java.util.List;

public class ImportActivity extends AppCompatActivity {
    public static final String EXTRA_FILE_PATH = "filepath";
    private static final int FILE_IMPORT_REQUEST = 1;
    final List<ImportRow> importRows = new ArrayList<>();
    private ImageButton searchButton;
    private RecyclerView listView;
    private Button importRowsButton;
    @Nullable
    private String filename;
    private RecyclerView.Adapter<ImportRowViewHolder> listAdapter;
    private TextView filenameText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);


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
        listView = (RecyclerView) findViewById(R.id.list);
        importRowsButton = (Button) findViewById(R.id.import_rows);
        filenameText = (TextView) findViewById(R.id.filename);

        if (savedInstanceState != null) {
            filename = savedInstanceState.getString(EXTRA_FILE_PATH);
        } else {
            filename = getIntent().getExtras().getString(EXTRA_FILE_PATH);
        }

        listAdapter = new ImportRowAdapter(importRows);
        listView.setAdapter(listAdapter);
        listView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);

        if (filename != null) {
            filenameText.setText(filename);
            loadFile();
        }


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImportActivity.this, FilePickerActivity.class);
                startActivityForResult(intent, FILE_IMPORT_REQUEST);
            }
        });

        importRowsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importRows();
            }
        });

    }

    private static class ImportResult {
        int failed = 0;
        int updated = 0;
        int added = 0;

    }

    private void importRows() {
        Toast.makeText(this, "ToDo import the selected rows ", Toast.LENGTH_SHORT).show();

        final ProgressDialog barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("Importing ...");
        barProgressDialog.setMessage("Importing in progress ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(importRows.size());
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);

        AsyncTask<Void, Integer, ImportResult> task = new AsyncTask<Void, Integer, ImportResult>() {
            @Override
            protected ImportResult doInBackground(Void... params) {
                ImportResult result = new ImportResult();
                int position = 0;
                for (ImportRow each : importRows) {
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
                        } catch (Exception e) {
                            result.failed++;
                            each.message = "Error importing. " + e.getMessage();
                        }
                    }
                    final int finalPosition = position;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listAdapter.notifyItemChanged(finalPosition);
                        }
                    });
                    position++;
                    publishProgress(position);
                }
                return result;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                barProgressDialog.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(ImportResult importResult) {
                barProgressDialog.dismiss();
            }
        };
        task.execute();


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (filename != null) {
            outState.putString(EXTRA_FILE_PATH, filename);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_IMPORT_REQUEST && resultCode == RESULT_OK) {
//            String filePath = data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH);
            filename = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            loadFile();

            // Do anything with file
        }
    }

    private void loadFile() {
        importRows.clear();
        Document doc = readDocument();
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
        listAdapter.notifyDataSetChanged();
    }

    @Nullable
    private Document readDocument() {
        File file = new File(filename);
        FileInputStream in;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error getting file input stream", e);
        }
        return XmlUtils.getDocument(in);
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

    public static class ImportRow {
        public boolean imported;
        public String message;
        boolean shouldImport;
        String type;
        @Nullable
        String name;
        Element element;
    }

    public static class ImportRowAdapter extends RecyclerView.Adapter<ImportRowViewHolder> {
        private final List<ImportRow> rows;

        ImportRowAdapter(List<ImportRow> rows) {
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
            final ImportRow row = getItem(position);
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
                }
            });
        }

        public ImportRow getItem(int position) {
            return rows.get(position);
        }

        @Override
        public int getItemCount() {
            return rows.size();
        }
    }
}
