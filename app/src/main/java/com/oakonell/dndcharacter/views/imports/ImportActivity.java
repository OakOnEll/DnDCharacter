package com.oakonell.dndcharacter.views.imports;

import android.content.Intent;
import android.os.Bundle;
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
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.race.Race;
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
    private ImageButton searchButton;
    private RecyclerView listView;
    private Button importRowsButton;
    private String filename;

    private RecyclerView.Adapter<ImportRowViewHolder> listAdapter;
    List<ImportRow> importRows = new ArrayList<>();

    private static final int FILE_IMPORT_REQUEST = 1;
    private TextView filenameText;

    public static class ImportRowViewHolder extends RecyclerView.ViewHolder {
        CheckBox should_import;
        TextView type;
        TextView name;
        ImageView imported;
        TextView error;

        public ImportRowViewHolder(View itemView) {
            super(itemView);
            should_import = (CheckBox) itemView.findViewById(R.id.should_import);
            name = (TextView) itemView.findViewById(R.id.name);
            type = (TextView) itemView.findViewById(R.id.type);
            error = (TextView) itemView.findViewById(R.id.error);
            imported = (ImageView) itemView.findViewById(R.id.imported);
        }
    }


    public static class ImportRow {
        boolean shouldImport;
        String type;
        String name;
        Element element;
        public boolean imported;
        public String message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

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

    private void importRows() {
        Toast.makeText(this, "ToDo import the selected rows ", Toast.LENGTH_SHORT).show();

        int failed = 0;
        int updated = 0;
        int added = 0;

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
                default:
                    each.message = "Not imported. Type not handled";
                    modelClass = null;
                    failed++;
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
                    model.setDocument(each.element);
                    model.save();
                    if (isUpdated) {
                        updated++;
                    } else {
                        added++;
                    }
                    each.imported = true;
                } catch (Exception e) {
                    failed++;
                    each.message = "Error importing. " + e.getMessage();
                }
            }
            listAdapter.notifyItemChanged(position);
            position++;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (filename != null) {
            outState.putString(EXTRA_FILE_PATH, filename);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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


    public static class ImportRowAdapter extends RecyclerView.Adapter<ImportRowViewHolder> {
        private List<ImportRow> rows;

        ImportRowAdapter(List<ImportRow> rows) {
            this.rows = rows;
        }

        @Override
        public ImportRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.import_row_item, parent, false);
            return new ImportRowViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(ImportRowViewHolder holder, int position) {
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
