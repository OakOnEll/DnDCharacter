package com.oakonell.dndcharacter.views.race;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
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

import com.activeandroid.content.ContentProvider;
import com.oakonell.dndcharacter.AbstractBaseActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.views.background.EditBackgroundDialogFragment;

/**
 * Created by Rob on 11/2/2015.
 */
public class RacesListActivity extends AbstractBaseActivity {
    private ListView listView;
    private CursorAdapter adapter;
    private int loaderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backgrounds);
        configureCommon();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRace(view);
            }
        });


        listView = (ListView) findViewById(R.id.listView);

//        adapter = new SimpleCursorAdapter(this,
//                android.R.layout.simple_expandable_list_item_1,
//                null,
//                new String[]{"name"},
//                new int[]{android.R.id.text1},
//                0);

        adapter = new RaceListAdapter(this, R.layout.race_list_item, null);
        listView.setAdapter(adapter);
        //listView.setActivated(true);

        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int theLoaderId, Bundle cursor) {
                loaderId = theLoaderId;
                Toast.makeText(RacesListActivity.this, "Loader created- cursor " + (cursor == null ? "is null!" : " bundled"), Toast.LENGTH_SHORT).show();
                return new CursorLoader(RacesListActivity.this,
                        ContentProvider.createUri(Race.class, null),
                        null, null, null, "name"
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
                Toast.makeText(RacesListActivity.this, "Load finished- cursor " + (cursor == null ? "is null!" : cursor.getCount()), Toast.LENGTH_SHORT).show();
                ((ResourceCursorAdapter) adapter).swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                Toast.makeText(RacesListActivity.this, "Loader rest ", Toast.LENGTH_SHORT).show();
                ((ResourceCursorAdapter) adapter).swapCursor(null);
            }
        });

    }

    @Override
    protected void onResume() {
        if (loaderId > 0) {
            getLoaderManager().getLoader(loaderId).forceLoad();
        }
        super.onResume();
    }

    static class ViewHolder {
        TextView name;
        TextView description;
        ImageView deleteButton;
    }

    static class RaceListAdapter extends ResourceCursorAdapter {
        private final RacesListActivity context;
        int nameIndex = -1;
        int idIndex = -1;
        int classesIndex = -1;

        public RaceListAdapter(RacesListActivity context, int layout, Cursor c, boolean autoRequery) {
            super(context, layout, c, autoRequery);
            this.context = context;
        }

        public RaceListAdapter(RacesListActivity context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
            this.context = context;
        }

        public RaceListAdapter(RacesListActivity context, int layout, Cursor c) {
            super(context, layout, c);
            this.context = context;
        }

        @Override
        public Cursor swapCursor(Cursor newCursor) {
            nameIndex = -1;
            classesIndex = -1;
            idIndex = -1;
            return super.swapCursor(newCursor);
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                assignViewHolder(view);
            }
            if (nameIndex < 0) {
                idIndex = cursor.getColumnIndex(BaseColumns._ID);
                nameIndex = cursor.getColumnIndex("name");
                //classesIndex = cursor.getColumnIndex("classesString");
            }
            final long id = cursor.getInt(idIndex);
            final String name = cursor.getString(nameIndex);
            //final String classes = cursor.getString(classesIndex);
            holder.name.setText(name);
            //holder.description.setText(classes);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           RaceListAdapter.this.context.promptToDelete(id, name, "");
                                                       }
                                                   }
            );
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RaceListAdapter.this.context.openRace(id);
                }
            });
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View newView = super.newView(context, cursor, parent);
            assignViewHolder(newView);
            return newView;
        }

        private void assignViewHolder(View newView) {
            ViewHolder holder = new ViewHolder();
            holder.name = (TextView) newView.findViewById(R.id.name);
            //holder.classes = (TextView) newView.findViewById(R.id.classes);
            holder.deleteButton = (ImageView) newView.findViewById(R.id.action_delete);
            newView.setTag(holder);
        }
    }


    private void promptToDelete(final long charId, String name, String classes) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Race " + name)
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

    protected void openRace(long id) {
        Race race = Race.load(Race.class, id);

        EditRaceDialogFragment dialog = EditRaceDialogFragment.create(race);
        dialog.show(getSupportFragmentManager(), "race_edit");
    }

    private void createRace(View view) {
        Race race = new Race();
        long id = race.save();
        adapter.notifyDataSetInvalidated();

        openRace(id);

    }

}
