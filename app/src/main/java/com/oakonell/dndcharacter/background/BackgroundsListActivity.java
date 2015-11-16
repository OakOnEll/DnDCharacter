package com.oakonell.dndcharacter.background;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.oakonell.dndcharacter.AbstractBaseActivity;
import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.storage.CharacterRow;

/**
 * Created by Rob on 11/2/2015.
 */
public class BackgroundsListActivity extends AbstractBaseActivity {
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
                createBackground(view);
            }
        });


        listView = (ListView) findViewById(R.id.listView);

//        adapter = new SimpleCursorAdapter(this,
//                android.R.layout.simple_expandable_list_item_1,
//                null,
//                new String[]{"name"},
//                new int[]{android.R.id.text1},
//                0);

        adapter = new BackgroundListAdapter(this, R.layout.background_list_item, null);
        listView.setAdapter(adapter);
        //listView.setActivated(true);

        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int theLoaderId, Bundle cursor) {
                loaderId = theLoaderId;
                Toast.makeText(BackgroundsListActivity.this, "Loader created- cursor " + (cursor == null ? "is null!" : " bundled"), Toast.LENGTH_SHORT).show();
                return new CursorLoader(BackgroundsListActivity.this,
                        ContentProvider.createUri(Background.class, null),
                        null, null, null, "name"
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
                Toast.makeText(BackgroundsListActivity.this, "Load finished- cursor " + (cursor == null ? "is null!" : cursor.getCount()), Toast.LENGTH_SHORT).show();
                ((ResourceCursorAdapter) adapter).swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                Toast.makeText(BackgroundsListActivity.this, "Loader rest ", Toast.LENGTH_SHORT).show();
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.characters, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Toast.makeText(this, "Clicked settings ", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//
//        return super.onOptionsItemSelected(item);
//    }


    static class ViewHolder {
        TextView name;
        TextView description;
        ImageView deleteButton;
    }

    static class BackgroundListAdapter extends ResourceCursorAdapter {
        private final BackgroundsListActivity context;
        int nameIndex = -1;
        int idIndex = -1;
        int classesIndex = -1;

        public BackgroundListAdapter(BackgroundsListActivity context, int layout, Cursor c, boolean autoRequery) {
            super(context, layout, c, autoRequery);
            this.context = context;
        }

        public BackgroundListAdapter(BackgroundsListActivity context, int layout, Cursor c, int flags) {
            super(context, layout, c, flags);
            this.context = context;
        }

        public BackgroundListAdapter(BackgroundsListActivity context, int layout, Cursor c) {
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
                                                           BackgroundListAdapter.this.context.promptToDelete(id, name, "");
                                                       }
                                                   }
            );
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BackgroundListAdapter.this.context.openBackground(id);
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
        builder.setMessage("Delete Background " + name)
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

    protected void openBackground(long id) {
        Background background = Background.load(Background.class, id);

        EditBackgroundDialogFragment dialog = EditBackgroundDialogFragment.create(background);
        dialog.show(getSupportFragmentManager(), "background_edit");
    }

    private void createBackground(View view) {
        Background background = new Background();
        long id = background.save();
        adapter.notifyDataSetInvalidated();

        openBackground(id);


/*
                int num = new Select()
                .from(CharacterRow.class).count();


        List<CharacterRow> characters = new Select()
                .from(CharacterRow.class).execute();
        StringBuilder builder = new StringBuilder();
        for (CharacterRow each : characters) {
            builder.append(each + "\n");
        }
        Toast.makeText(CharactersListActivity.this, builder.toString(), Toast.LENGTH_SHORT).show();

        Snackbar.make(view, "Added a character '" + charRow.name + "'", Snackbar.LENGTH_LONG)
                .setAction("Create a new character", null).show();
                */
    }

}
