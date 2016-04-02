package com.oakonell.dndcharacter.views.exports;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oakonell.dndcharacter.BuildConfig;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.CharacterExporter;
import com.oakonell.dndcharacter.model.character.CharacterRow;
import com.oakonell.dndcharacter.utils.FileUtils;
import com.oakonell.dndcharacter.views.AbstractBaseActivity;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Rob on 3/29/2016.
 */
public class ExportActivity extends AbstractBaseActivity {
    public static final String CHECKED_IDS = "CHECKED_IDS";
    private static final int SAVE_FILE_RESULT_CODE = 1010;
    private RecyclerView listView;
    private Button exportRowsButton;
    private ExportRowAdapter listAdapter;

    CharacterExporter exporter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        configureCommon();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(R.string.export_title);
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

        listView = (RecyclerView) findViewById(R.id.list);
        exportRowsButton = (Button) findViewById(R.id.export_rows);

        exporter = new CharacterExporter();

        Button done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AsyncTask<Void, Integer, List<CharacterExporter.ExportRow>> getExportRows = new AsyncTask<Void, Integer, List<CharacterExporter.ExportRow>>() {
            @Override
            protected List<CharacterExporter.ExportRow> doInBackground(Void[] params) {
                long[] checkedIds = savedInstanceState == null ? null : savedInstanceState.getLongArray(CHECKED_IDS);
                return exporter.fetchExportRows(checkedIds);
            }

            @Override
            protected void onPostExecute(List<CharacterExporter.ExportRow> o) {
                listAdapter.rows.addAll(o);
                listAdapter.notifyDataSetChanged();
                updateViews();
            }
        };
        getExportRows.execute();

        listAdapter = new ExportRowAdapter(this, new ArrayList<CharacterExporter.ExportRow>());
        listView.setAdapter(listAdapter);
        listView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        listView.addItemDecoration(itemDecoration);

        exportRowsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportRows();
            }
        });

        updateViews();

    }

    private void exportRows() {
        List<CharacterExporter.ExportRow> toExport = new ArrayList<>();
        for (CharacterExporter.ExportRow each : exporter.exportRows) {
            if (!each.shouldExport) continue;

            toExport.add(each);
        }

        String fileName = "characters_dnd.xml";
        if (toExport.size() == 1) {
            fileName = toExport.get(0).name + "_dnd.xml";
        }

        File charactersPath = FileUtils.getCharactersDirectory(this);
        File newFile = new File(charactersPath, fileName);
        // write to the file here...
        if (newFile.exists()) {
            newFile.delete();
        }
        FileOutputStream out = null;
        try {
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();
            out = new FileOutputStream(newFile);
        } catch (FileNotFoundException e) {
            Fabric.getLogger().e("ExportActivity", "File not found exception", e);
            new AlertDialog.Builder(this).setTitle(R.string.error_exporting).setMessage(e.getMessage()).show();
            return;
        } catch (IOException e) {
            Fabric.getLogger().e("ExportActivity", "IOException", e);
            new AlertDialog.Builder(this).setTitle(R.string.error_exporting).setMessage(e.getMessage()).show();
            return;
        }
        BufferedOutputStream bOut = new BufferedOutputStream(out);
        PrintWriter writer = new PrintWriter(bOut);
        writer.println("<characters>");
        // TODO offload this to an asyncTask..
        for (CharacterExporter.ExportRow each : toExport) {
            CharacterRow row = CharacterRow.load(CharacterRow.class, each.id);
            writer.println(row.getXml());
            each.exported = true;
        }
        writer.println("</characters>");
        writer.close();

        // let the FileProvider generate an URI for this private file
        Uri contentUri = FileProvider.getUriForFile(this, FileUtils.getFileProviderName(), newFile);
        // create an intent, so the user can choose which application he/she wants to use to share this file
        final Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("text/xml")
                .setSubject(fileName)
                .setStream(contentUri)
                .setChooserTitle(getString(R.string.share_characters))
                .createChooserIntent()
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }

    private void updateViews() {
        exportRowsButton.setEnabled(exporter.getNumberToExport() > 0);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveCheckedIds(outState);
    }

    private void saveCheckedIds(Bundle outState) {
        List<Long> checkedIds = new ArrayList<>();
        for (CharacterExporter.ExportRow each : listAdapter.rows) {
            if (each.shouldExport) {
                checkedIds.add(each.id);
            }
        }
        long[] checkedIdsArray = new long[checkedIds.size()];
        int i = 0;
        for (Long each : checkedIds) {
            checkedIdsArray[i] = each;
            i++;
        }
        outState.putLongArray(CHECKED_IDS, checkedIdsArray);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case SAVE_FILE_RESULT_CODE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    String theFilePath = data.getData().getPath();
                    Toast.makeText(ExportActivity.this, "Write to file " + theFilePath, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static class ExportRowViewHolder extends BindableComponentViewHolder<CharacterExporter.ExportRow, ExportActivity, ExportRowAdapter> {
        @NonNull
        final CheckBox should_export;
        @NonNull
        final TextView name;
        @NonNull
        private final ImageView exported;
        private final TextView race_display_name;
        private final TextView classes;

        public ExportRowViewHolder(@NonNull View itemView) {
            super(itemView);
            should_export = (CheckBox) itemView.findViewById(R.id.should_export);
            name = (TextView) itemView.findViewById(R.id.name);
            classes = (TextView) itemView.findViewById(R.id.classes);
            race_display_name = (TextView) itemView.findViewById(R.id.race_display_name);

            exported = (ImageView) itemView.findViewById(R.id.exported);

        }

        @Override
        public void bind(final ExportActivity context, ExportRowAdapter adapter, final CharacterExporter.ExportRow row) {
            name.setText(row.name);
            classes.setText(row.classes);
            race_display_name.setText(race_display_name.getText());

            should_export.setOnCheckedChangeListener(null);
            should_export.setChecked(row.shouldExport);
            if (row.exported) {
                should_export.setVisibility(View.GONE);
                exported.setVisibility(View.VISIBLE);
            } else {
                should_export.setVisibility(View.VISIBLE);
                exported.setVisibility(View.GONE);
            }

            should_export.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    row.shouldExport = isChecked;
                    context.exportRowsButton.setEnabled(context.exporter.getNumberToExport() > 0);
                }
            });
        }
    }


    public static class ExportRowAdapter extends RecyclerView.Adapter<ExportRowViewHolder> {
        private final List<CharacterExporter.ExportRow> rows;
        private final ExportActivity activity;

        ExportRowAdapter(ExportActivity activity, List<CharacterExporter.ExportRow> rows) {
            this.activity = activity;
            this.rows = rows;
        }

        @NonNull
        @Override
        public ExportRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.export_row_item, parent, false);
            return new ExportRowViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull ExportRowViewHolder holder, int position) {
            final CharacterExporter.ExportRow row = getItem(position);
            holder.bind(activity, this, row);

        }

        public CharacterExporter.ExportRow getItem(int position) {
            return rows.get(position);
        }

        @Override
        public int getItemCount() {
            return rows.size();
        }
    }


}
