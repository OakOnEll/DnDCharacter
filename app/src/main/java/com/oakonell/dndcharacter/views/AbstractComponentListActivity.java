package com.oakonell.dndcharacter.views;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 11/20/2015.
 */
public abstract class AbstractComponentListActivity<M extends AbstractComponentModel> extends AbstractBaseActivity {
    private static final int UNDO_DELAY = 5000;
    private ComponentListAdapter adapter;
    private int loaderId;

    final Map<Long, Long> recordsBeingDeleted = new HashMap<>();
    RecyclerView listView;

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
                new SimpleItemTouchHelperCallback(adapter, false, true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(listView);


        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int theLoaderId, @Nullable Bundle cursor) {
                loaderId = theLoaderId;
                return new CursorLoader(AbstractComponentListActivity.this,
                        ContentProvider.createUri(getComponentClass(), null),
                        null, null, null, getCursorSortBy()
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, @Nullable Cursor cursor) {
                adapter.swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                adapter.swapCursor(null);
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


    @NonNull
    protected abstract M createNewRecord();

    abstract protected void openRecord(long id);

    @NonNull
    protected abstract String getSubtitle();

    @Override
    protected void onResume() {
        if (loaderId > 0) {
            getLoaderManager().getLoader(loaderId).forceLoad();
        }
        super.onResume();
    }

    @NonNull
    protected CursorBindableRecyclerViewHolder newRowViewHolder(@NonNull View newView) {
        return new RowViewHolderCursor(newView);
    }

    protected abstract void deleteRow(long id);

    public static class DeleteRowViewHolderCursor<C extends AbstractComponentListActivity> extends CursorBindableRecyclerViewHolder<C, ComponentListAdapter> {
        @NonNull
        final TextView name;
        @NonNull
        final Button undo;

        public DeleteRowViewHolderCursor(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            undo = (Button) itemView.findViewById(R.id.undo);
        }

        @Override
        public void bindTo(@NonNull Cursor cursor, @NonNull final C context, @NonNull final ComponentListAdapter adapter, @NonNull CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);
            final String nameString = cursor.getString(cursorIndexesByName.getIndex(cursor, "name"));
            final long id = cursor.getInt(cursorIndexesByName.getIndex(cursor, BaseColumns._ID));

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

    public static class RowViewHolderCursor<M extends AbstractComponentModel> extends CursorBindableRecyclerViewHolder<AbstractComponentListActivity, ComponentListAdapter<AbstractComponentListActivity<M>, M>> implements ItemTouchHelperViewHolder {
        @NonNull
        final TextView name;
        //TextView description;
        private Drawable originalBackground;

        public RowViewHolderCursor(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bindTo(Cursor cursor, final AbstractComponentListActivity context, ComponentListAdapter<AbstractComponentListActivity<M>, M> adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);

            final long id = cursor.getInt(cursorIndexesByName.getIndex(cursor, BaseColumns._ID));
            final String nameString = cursor.getString(cursorIndexesByName.getIndex(cursor, "name"));
            name.setText(nameString);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.openRecord(id);
                }
            });
        }

        @Override
        public void onItemSelected() {
            originalBackground = itemView.getBackground();
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundDrawable(originalBackground);
        }
    }

    public static class ComponentListAdapter<C extends AbstractComponentListActivity<M>, M extends AbstractComponentModel> extends RecyclerView.Adapter<CursorBindableRecyclerViewHolder<C, ComponentListAdapter<C, M>>> implements ItemTouchHelperAdapter {
        private final C context;
        private final int layout;
        Cursor cursor;
        @NonNull
        CursorIndexesByName cursorIndexesByName = new CursorIndexesByName();

        public ComponentListAdapter(C context, int layout) {
            this.context = context;
            this.layout = layout;
        }

        public Cursor swapCursor(Cursor newCursor) {
            cursor = newCursor;
            notifyDataSetChanged();
            cursorIndexesByName = new CursorIndexesByName();
            return cursor;
        }

        public int getItemViewType(int position) {
            cursor.moveToPosition(position);
            long id = cursor.getLong(cursorIndexesByName.getIndex(cursor, BaseColumns._ID));
            if (context.recordsBeingDeleted.containsKey(id)) {
                return 1;
            }
            return 0;
        }

        @NonNull
        @Override
        public CursorBindableRecyclerViewHolder<C, ComponentListAdapter<C, M>> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_deleted_item, parent, false);
                return new DeleteRowViewHolderCursor(newView);
            }
            View newView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return this.context.newRowViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(CursorBindableRecyclerViewHolder<C, ComponentListAdapter<C, M>> holder, int position) {
            cursor.moveToPosition(position);
            holder.bindTo(cursor, context, this, cursorIndexesByName);
        }

        @Override
        public int getItemCount() {
            if (cursor == null) return 0;
            return cursor.getCount();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            // not implemented
            return false;
        }

        @Override
        public void onItemDismiss(final int position) {
            cursor.moveToPosition(position);
            final long id = cursor.getInt(cursorIndexesByName.getIndex(cursor, BaseColumns._ID));

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
                    Long deletedTime = context.recordsBeingDeleted.get(id);
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


}
