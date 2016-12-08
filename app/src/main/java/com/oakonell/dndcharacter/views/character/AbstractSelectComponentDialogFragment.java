package com.oakonell.dndcharacter.views.character;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractDescriptionComponentModel;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.CursorBindableRecyclerViewHolder;
import com.oakonell.dndcharacter.views.CursorIndexesByName;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.ItemTouchHelperViewHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 1/24/2016.
 */
public abstract class AbstractSelectComponentDialogFragment<V extends AbstractSelectComponentDialogFragment.RowViewHolder> extends AbstractDialogFragment {
    private RecyclerView listView;
    private CursorComponentListAdapter<V> adapter;
    private int loaderId;
    @Nullable
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;


    @Nullable
    @Override
    protected abstract String getTitle();

//    @Override
//    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
//        super.onCharacterLoaded(character);
//        if (searchWhenCharacterAvailable) {
//            search();
//        }
//    }

    @Nullable
    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(getDialogResource(), container);

        listView = (RecyclerView) view.findViewById(R.id.list);

        listView.setHasFixedSize(true);
        listView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity()));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        listView.addItemDecoration(itemDecoration);

        adapter = new CursorComponentListAdapter<>(this, getListItemResource());
        listView.setAdapter(adapter);

        final EditText searchTerms = (EditText) view.findViewById(R.id.search_terms);

        loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            long start;

            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int theLoaderId, @Nullable Bundle cursor) {
                loaderId = theLoaderId;
                start = System.nanoTime();

                String searchTermString = searchTerms.getText().toString().trim();
                if (searchTermString.length() == 0) {
                    return new CursorLoader(getActivity(),
                            getContentProviderUri(),
                            null, getSelection(), getSelectionArgs(), getCursorSortBy());
                }
                searchTermString = "%" + searchTermString + "%";
                String selection = getSelection();
                if (selection == null) {
                    selection = "upper(name) like upper(?)";
                } else {
                    selection = "upper(name) like upper(?) AND (" + selection + ")";
                }

                String[] selectionArgs = getSelectionArgs();
                if (selectionArgs == null) {
                    selectionArgs = new String[]{searchTermString};
                } else {
                    String[] extraArgs = selectionArgs;
                    selectionArgs = new String[selectionArgs.length + 1];
                    selectionArgs[0] = searchTermString;
                    System.arraycopy(extraArgs, 0, selectionArgs, 1, extraArgs.length + 1 - 1);
                }
                return new CursorLoader(getActivity(),
                        getContentProviderUri(),
                        null, selection, selectionArgs, getCursorSortBy()
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, @Nullable Cursor cursor) {
                long totalTime = System.nanoTime() - start;
                start = 0;
                adapter.swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                adapter.swapCursor(null);
            }
        };
        if (canSearch()) {
            search();
        }


        searchTerms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });


        return view;
    }

    protected Uri getContentProviderUri() {
        return ContentProvider.createUri(getComponentClass(), null);
    }

    protected boolean canSearch() {
        return true;
    }

    protected void search() {
        if (!canSearch()) {
            listView.setVisibility(View.GONE);
            return;
        }
        listView.setVisibility(View.VISIBLE);
        if (loaderId > 0) {
            getLoaderManager().getLoader(loaderId).forceLoad();
        } else {
            getLoaderManager().restartLoader(loaderId, null, loaderCallbacks);
        }
    }

    @Nullable
    protected String[] getSelectionArgs() {
        return null;
    }

    @Nullable
    protected String getSelection() {
        return null;
    }

    protected int getDialogResource() {
        return R.layout.component_search_dialog;
    }


    @Override
    public void onResume() {
        if (loaderId > 0) {
            getLoaderManager().getLoader(loaderId).forceLoad();
        }
        super.onResume();
    }

    @NonNull
    public abstract Class<? extends AbstractDescriptionComponentModel> getComponentClass();

    @SuppressWarnings("SameReturnValue")
    protected int getListItemResource() {
        return R.layout.component_description_list_item;
    }

    @NonNull
    @SuppressWarnings("SameReturnValue")
    public String getCursorSortBy() {
        return "name";
    }


    public static class RowViewHolder extends CursorBindableRecyclerViewHolder<AbstractSelectComponentDialogFragment, CursorComponentListAdapter> implements ItemTouchHelperViewHolder {
        @NonNull
        final TextView name;
        //TextView description;
        private Drawable originalBackground;

        @NonNull
        private final TextView description;
        @NonNull
        private final ImageView expand;


        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            expand = (ImageView) itemView.findViewById(R.id.expand);
        }

        @Override
        public void bindTo(final Cursor cursor, final AbstractSelectComponentDialogFragment context, final CursorComponentListAdapter adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);

            final long id = cursor.getInt(cursorIndexesByName.getIndex(cursor, getIdColumnName()));
            final String nameString = cursor.getString(cursorIndexesByName.getIndex(cursor, "name"));
            name.setText(nameString);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context.applyAction(id)) {
                        context.dismiss();
                    }
                }
            });

            String text = cursor.getString(cursorIndexesByName.getIndex(cursor, "description"));
            if (text == null) text = "";
            description.setText(text);

            if (text.trim().equals("")) {
                expand.setVisibility(View.GONE);
            } else {
                expand.setVisibility(View.VISIBLE);
                final int position = cursor.getPosition();
                boolean isExpanded = adapter.isExpanded(cursor.getPosition());
                if (isExpanded) {
                    description.setEllipsize(null);
                    description.setLines(5);
                    description.setMaxLines(Integer.MAX_VALUE);
                    expand.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.setExpanded(position, false);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    description.setEllipsize(TextUtils.TruncateAt.END);
                    description.setLines(1);
                    description.setMaxLines(1);
                    expand.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.setExpanded(position, true);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }

        @NonNull
        protected String getIdColumnName() {
            return BaseColumns._ID;
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

    abstract protected boolean applyAction(long id);

    public static class CursorComponentListAdapter<V extends AbstractSelectComponentDialogFragment.RowViewHolder> extends RecyclerView.Adapter<V> {
        private final AbstractSelectComponentDialogFragment context;
        private final int layout;
        Cursor cursor;
        @NonNull
        CursorIndexesByName cursorIndexesByName = new CursorIndexesByName();

        public CursorComponentListAdapter(AbstractSelectComponentDialogFragment context, int layout) {
            this.context = context;
            this.layout = layout;
        }

        public Cursor swapCursor(Cursor newCursor) {
            cursor = newCursor;
            notifyDataSetChanged();
            cursorIndexesByName = new CursorIndexesByName();
            return cursor;
        }


        @NonNull
        @Override
        public V onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return (V) this.context.newRowViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull V holder, int position) {
            cursor.moveToPosition(position);
            holder.bindTo(cursor, context, this, cursorIndexesByName);
        }

        @Override
        public int getItemCount() {
            if (cursor == null) return 0;
            return cursor.getCount();
        }


        private Map<Integer, Boolean> expandedPositions = new HashMap<Integer, Boolean>();

        public boolean isExpanded(int position) {
            Boolean result = expandedPositions.get(position);
            if (result == null) return false;
            return result;
        }

        public void setExpanded(int position, boolean expanded) {
            if (!expanded) {
                expandedPositions.remove(position);
            } else {
                expandedPositions.put(position, true);
            }
        }


    }

    @NonNull
    public V newRowViewHolder(@NonNull View newView) {
        return (V) new RowViewHolder(newView);
    }


}
