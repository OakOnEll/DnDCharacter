package com.oakonell.dndcharacter.views;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;
import com.oakonell.dndcharacter.AbstractBaseActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 11/20/2015.
 */
public abstract class AbstractComponentListActivity<M extends AbstractComponentModel> extends AbstractBaseActivity {
    private RecyclerView listView;
    private ComponentListAdapter adapter;
    private int loaderId;
    private Map<Long, Long> recordsBeingDeleted = new HashMap<Long, Long>();
    private static final int UNDO_DELAY = 5000;

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

        getSupportActionBar().setSubtitle(getSubtitle());


        listView = (RecyclerView) findViewById(R.id.listView);

        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        listView.addItemDecoration(itemDecoration);

        adapter = new ComponentListAdapter(this, getListItemResource());
        listView.setAdapter(adapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(listView);


        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int theLoaderId, Bundle cursor) {
                loaderId = theLoaderId;
                Toast.makeText(AbstractComponentListActivity.this, "Loader created- cursor " + (cursor == null ? "is null!" : " bundled"), Toast.LENGTH_SHORT).show();
                return new CursorLoader(AbstractComponentListActivity.this,
                        ContentProvider.createUri(getComponentClass(), null),
                        null, null, null, getCursorSortBy()
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
                Toast.makeText(AbstractComponentListActivity.this, "Load finished- cursor " + (cursor == null ? "is null!" : cursor.getCount()), Toast.LENGTH_SHORT).show();
                ((ComponentListAdapter) adapter).swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                Toast.makeText(AbstractComponentListActivity.this, "Loader rest ", Toast.LENGTH_SHORT).show();
                ((ComponentListAdapter) adapter).swapCursor(null);
            }
        });


    }

    @NonNull
    protected String getCursorSortBy() {
        return "name";
    }


    private void createNewRecord(View view) {
        Model race = createNewRecord();
        long id = race.save();
        adapter.notifyDataSetChanged();

        openRecord(id);
    }

    protected int getListItemResource() {
        return R.layout.component_list_item;
    }

    @NonNull
    abstract protected Class<? extends M> getComponentClass();


    protected abstract M createNewRecord();

    abstract protected void openRecord(long id);

    abstract protected String getRecordTypeName();

    protected abstract String getSubtitle();

    @Override
    protected void onResume() {
        if (loaderId > 0) {
            getLoaderManager().getLoader(loaderId).forceLoad();
        }
        super.onResume();
    }


    public static class DeleteRowViewHolder extends BindableRecyclerViewHolder {
        TextView name;
        Button undo;

        public DeleteRowViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            undo = (Button) itemView.findViewById(R.id.undo);
        }

        @Override
        public void bindTo(Cursor cursor, final AbstractComponentListActivity context, final RecyclerView.Adapter adapter, IndexesByName indexesByName) {
            super.bindTo(cursor, context, adapter, indexesByName);
            final String nameString = cursor.getString(indexesByName.getIndex(cursor, "name"));
            final long id = cursor.getInt(indexesByName.getIndex(cursor, BaseColumns._ID));

            name.setText(nameString);
            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.recordsBeingDeleted.remove(id);
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }

    public static class RowViewHolder extends BindableRecyclerViewHolder {
        TextView name;
        TextView description;

        public RowViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bindTo(Cursor cursor, final AbstractComponentListActivity context, RecyclerView.Adapter adapter, IndexesByName indexesByName) {
            super.bindTo(cursor, context, adapter, indexesByName);

            final long id = cursor.getInt(indexesByName.getIndex(cursor, BaseColumns._ID));
            final String nameString = cursor.getString(indexesByName.getIndex(cursor, "name"));
            final int position = cursor.getPosition();
            name.setText(nameString);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.openRecord(id);
                }
            });
        }
    }

    public static class IndexesByName {
        private Map<String, Integer> cursorIndexesByName = new HashMap<>();

        public int getIndex(Cursor cursor, String name) {
            Integer result = cursorIndexesByName.get(name);
            if (result != null) return result;
            result = cursor.getColumnIndex(name);
            cursorIndexesByName.put(name, result);
            return result;
        }
    }

    public static class ComponentListAdapter extends RecyclerView.Adapter<BindableRecyclerViewHolder> implements ItemTouchHelperAdapter {
        private final AbstractComponentListActivity context;
        private final int layout;
        Cursor cursor;
        IndexesByName indexesByName = new IndexesByName();

        public ComponentListAdapter(AbstractComponentListActivity context, int layout) {
            this.context = context;
            this.layout = layout;
        }

        public Cursor swapCursor(Cursor newCursor) {
            cursor = newCursor;
            notifyDataSetChanged();
            indexesByName = new IndexesByName();
            return cursor;
        }

        public int getItemViewType(int position) {
            cursor.moveToPosition(position);
            long id = cursor.getLong(indexesByName.getIndex(cursor, BaseColumns._ID));
            if (context.recordsBeingDeleted.containsKey(id)) {
                return 1;
            }
            return 0;
        }

        @Override
        public BindableRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_deleted_item, parent, false);
                DeleteRowViewHolder holder = new DeleteRowViewHolder(newView);
                return holder;
            }
            View newView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            BindableRecyclerViewHolder holder = this.context.newRowViewHolder(newView);
            return holder;
        }

        @Override
        public void onBindViewHolder(BindableRecyclerViewHolder holder, int position) {
            cursor.moveToPosition(position);
            holder.bindTo(cursor, context, this, indexesByName);
        }

        @Override
        public int getItemCount() {
            if (cursor == null) return 0;
            return cursor.getCount();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            // not implemented
        }

        @Override
        public void onItemDismiss(final int position) {
            cursor.moveToPosition(position);
            final long id = cursor.getInt(indexesByName.getIndex(cursor, BaseColumns._ID));

            if (context.recordsBeingDeleted.containsKey(id)) {
                // actually delete the record, now
                context.deleteRow(id);
                context.recordsBeingDeleted.remove(id);
                notifyItemRemoved(position);
            }

            context.recordsBeingDeleted.put(id, System.currentTimeMillis());
            notifyItemChanged(position);

            context.listView.postDelayed(new Runnable() {
                public void run() {
                    // may have been deleted, undone, and then redeleted
                    Long deletedTime = (Long) context.recordsBeingDeleted.get(id);
                    if (deletedTime == null) return;
                    if (System.currentTimeMillis() - deletedTime >= UNDO_DELAY) {
                        // actually delete the record, now
                        context.deleteRow(id);
                        context.recordsBeingDeleted.remove(id);
                        notifyItemRemoved(position);
                    }
                }
            }, UNDO_DELAY);

        }
    }

    @NonNull
    protected BindableRecyclerViewHolder newRowViewHolder(View newView) {
        return new RowViewHolder(newView);
    }

    private void promptToDelete(final int position, final long rowId, String name, final ComponentListAdapter componentListAdapter) {
        String recordTypeName = getRecordTypeName();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete " + recordTypeName + " " + name + "(id=" + rowId + ", position=" + position + ")")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteRow(rowId);
                        componentListAdapter.notifyItemRemoved(position);

                        //componentListAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        // restore the view?
                        componentListAdapter.notifyDataSetChanged();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected abstract void deleteRow(long id);


    public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private final ItemTouchHelperAdapter mAdapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            //int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            // not supported, do nothing
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

    }


    public interface ItemTouchHelperAdapter {

        void onItemMove(int fromPosition, int toPosition);

        void onItemDismiss(int position);
    }
}
