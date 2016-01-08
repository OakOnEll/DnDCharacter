package com.oakonell.dndcharacter.views.character;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.Model;
import com.activeandroid.content.ContentProvider;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.effect.AddEffectToCharacterVisitor;
import com.oakonell.dndcharacter.model.effect.Effect;
import com.oakonell.dndcharacter.views.CursorBindableRecyclerViewHolder;
import com.oakonell.dndcharacter.views.CursorIndexesByName;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.ItemTouchHelperViewHolder;

import org.solovyev.android.views.llm.LinearLayoutManager;

/**
 * Created by Rob on 1/3/2016.
 */
public class AddEffectDialogFragment extends AbstractCharacterDialogFragment {
    private RecyclerView listView;
    private ComponentListAdapter adapter;
    private int loaderId;


    public static AddEffectDialogFragment createDialog() {
        return new AddEffectDialogFragment();
    }

    @Override
    protected String getTitle() {
        return "Add Effect";
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.effect_search_dialog, container);

        listView = (RecyclerView) view.findViewById(R.id.list);

        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        listView.addItemDecoration(itemDecoration);

        adapter = new ComponentListAdapter(this, getListItemResource());
        listView.setAdapter(adapter);

        final EditText searchTerms = (EditText) view.findViewById(R.id.search_terms);

        final LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int theLoaderId, Bundle cursor) {
                loaderId = theLoaderId;
                Toast.makeText(getActivity(), "Loader created- cursor " + (cursor == null ? "is null!" : " bundled"), Toast.LENGTH_SHORT).show();

                String searchTermString = searchTerms.getText().toString().trim();
                if (searchTermString.length() == 0) {
                    return new CursorLoader(getActivity(),
                            ContentProvider.createUri(getComponentClass(), null),
                            null, null, null, getCursorSortBy());
                }
                searchTermString += "%";
                return new CursorLoader(getActivity(),
                        ContentProvider.createUri(getComponentClass(), null),
                        null, "upper(name) like upper(?)", new String[]{searchTermString}, getCursorSortBy()
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
                Toast.makeText(getActivity(), "Load finished- cursor " + (cursor == null ? "is null!" : cursor.getCount()), Toast.LENGTH_SHORT).show();
                adapter.swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                Toast.makeText(getActivity(), "Loader rest ", Toast.LENGTH_SHORT).show();
                adapter.swapCursor(null);
            }
        };
        getLoaderManager().initLoader(0, null, loaderCallbacks);


        searchTerms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getLoaderManager().restartLoader(loaderId, null, loaderCallbacks);
            }
        });


        return view;
    }


    @Override
    public void onResume() {
        if (loaderId > 0) {
            getLoaderManager().getLoader(loaderId).forceLoad();
        }
        super.onResume();
    }

    public Class<? extends Model> getComponentClass() {
        return Effect.class;
    }

    protected int getListItemResource() {
        return R.layout.component_list_item;
    }

    public String getCursorSortBy() {
        return "name";
    }


    public static class RowViewHolder extends CursorBindableRecyclerViewHolder<AddEffectDialogFragment> implements ItemTouchHelperViewHolder {
        TextView name;
        //TextView description;
        private Drawable originalBackground;

        public RowViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bindTo(Cursor cursor, final AddEffectDialogFragment context, RecyclerView.Adapter adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);

            final long id = cursor.getInt(cursorIndexesByName.getIndex(cursor, BaseColumns._ID));
            final String nameString = cursor.getString(cursorIndexesByName.getIndex(cursor, "name"));
            name.setText(nameString);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.applyAction(id);
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

    private void applyAction(long id) {
        Effect effect = Effect.load(Effect.class, id);

        final String name = effect.getName();
        final CharacterEffect existingEffect = getCharacter().getEffectNamed(name);
        if (existingEffect != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Character already has effect '" + name + "'");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            dismiss();
            return;
        }

        CharacterEffect charEffect = AddEffectToCharacterVisitor.applyToCharacter(effect, getCharacter());
        getMainActivity().updateViews();
        getMainActivity().saveCharacter();

        dismiss();
    }

    public static class ComponentListAdapter extends RecyclerView.Adapter<CursorBindableRecyclerViewHolder<AddEffectDialogFragment>> {
        private final AddEffectDialogFragment context;
        private final int layout;
        Cursor cursor;
        CursorIndexesByName cursorIndexesByName = new CursorIndexesByName();

        public ComponentListAdapter(AddEffectDialogFragment context, int layout) {
            this.context = context;
            this.layout = layout;
        }

        public Cursor swapCursor(Cursor newCursor) {
            cursor = newCursor;
            notifyDataSetChanged();
            cursorIndexesByName = new CursorIndexesByName();
            return cursor;
        }


        @Override
        public CursorBindableRecyclerViewHolder<AddEffectDialogFragment> onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return this.context.newRowViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(CursorBindableRecyclerViewHolder<AddEffectDialogFragment> holder, int position) {
            cursor.moveToPosition(position);
            holder.bindTo(cursor, context, this, cursorIndexesByName);
        }

        @Override
        public int getItemCount() {
            if (cursor == null) return 0;
            return cursor.getCount();
        }


    }

    private CursorBindableRecyclerViewHolder<AddEffectDialogFragment> newRowViewHolder(View newView) {
        return new RowViewHolder(newView);
    }



}
