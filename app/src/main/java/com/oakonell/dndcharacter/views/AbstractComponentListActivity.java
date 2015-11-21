package com.oakonell.dndcharacter.views;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;
import com.oakonell.dndcharacter.AbstractBaseActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.background.Background;

/**
 * Created by Rob on 11/20/2015.
 */
public abstract class AbstractComponentListActivity extends AbstractBaseActivity {
    private ListView listView;
    private CursorAdapter adapter;
    private int loaderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_list);
        configureCommon();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewRecord(view);
            }
        });


        listView = (ListView) findViewById(R.id.listView);

        adapter = new ComponentListAdapter(this, getListItemResource(), null);
        listView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int theLoaderId, Bundle cursor) {
                loaderId = theLoaderId;
                Toast.makeText(AbstractComponentListActivity.this, "Loader created- cursor " + (cursor == null ? "is null!" : " bundled"), Toast.LENGTH_SHORT).show();
                return new CursorLoader(AbstractComponentListActivity.this,
                        ContentProvider.createUri(getComponentClass(), null),
                        null, null, null, "name"
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
                Toast.makeText(AbstractComponentListActivity.this, "Load finished- cursor " + (cursor == null ? "is null!" : cursor.getCount()), Toast.LENGTH_SHORT).show();
                ((ResourceCursorAdapter) adapter).swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                Toast.makeText(AbstractComponentListActivity.this, "Loader rest ", Toast.LENGTH_SHORT).show();
                ((ResourceCursorAdapter) adapter).swapCursor(null);
            }
        });

    }

    private void createNewRecord(View view) {
        Model race = createNewRecord();
        long id = race.save();
        adapter.notifyDataSetInvalidated();

        openRecord(id);
    }

    protected int getListItemResource() {
        return R.layout.component_list_item;
    }

    @NonNull
    abstract protected Class<? extends Model> getComponentClass();


    protected abstract Model createNewRecord();

    abstract protected void openRecord(long id);

    abstract protected String getRecordTypeName();

    @Override
    protected void onResume() {
        if (loaderId > 0) {
            getLoaderManager().getLoader(loaderId).forceLoad();
        }
        super.onResume();
    }


    public static class RowViewHolder {
        TextView name;
        TextView description;
        ImageView deleteButton;
    }

    int nameIndex = -1;
    int idIndex = -1;

    static class ComponentListAdapter extends ResourceCursorAdapter {
        private final AbstractComponentListActivity context;

        public ComponentListAdapter(AbstractComponentListActivity context, int layout, Cursor c, boolean autoRequery) {
            super(context, layout, c, autoRequery);
            this.context = context;
        }

        public ComponentListAdapter(AbstractComponentListActivity context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
            this.context = context;
        }

        public ComponentListAdapter(AbstractComponentListActivity context, int layout, Cursor c) {
            super(context, layout, c);
            this.context = context;
        }

        @Override
        public Cursor swapCursor(Cursor newCursor) {
            return super.swapCursor(newCursor);
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            RowViewHolder holder = (RowViewHolder) view.getTag();
            if (holder == null) {
                assignViewHolder(view);
            }
            this.context.updateRowView(view, cursor, holder);
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View newView = super.newView(context, cursor, parent);
            assignViewHolder(newView);
            return newView;
        }

        private void assignViewHolder(View newView) {
            RowViewHolder holder = this.context.newRowViewHolder(newView);
            holder.name = (TextView) newView.findViewById(R.id.name);
            holder.deleteButton = (ImageView) newView.findViewById(R.id.action_delete);
            newView.setTag(holder);
        }
    }

    @NonNull
    protected  RowViewHolder newRowViewHolder(View newView) {
        return new RowViewHolder();
    }

    protected void updateRowView(View view, Cursor cursor, RowViewHolder holder) {
        if (nameIndex < 0) {
            idIndex = cursor.getColumnIndex(BaseColumns._ID);
            nameIndex = cursor.getColumnIndex("name");
        }
        final long id = cursor.getInt(idIndex);
        final String name = cursor.getString(nameIndex);
        holder.name.setText(name);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       AbstractComponentListActivity.this.promptToDelete(id, name, "");
                                                   }
                                               }
        );
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbstractComponentListActivity.this.openRecord(id);
            }
        });
    }

    private void promptToDelete(final long charId, String name, String classes) {
        String recordTypeName = getRecordTypeName();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete " + recordTypeName + " " + name)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Background.delete(Background.class, charId);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
