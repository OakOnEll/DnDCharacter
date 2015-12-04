package com.oakonell.dndcharacter;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.CharacterRow;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.BindableRecyclerViewHolder;
import com.oakonell.dndcharacter.views.background.EditBackgroundDialogFragment;

/**
 * Created by Rob on 11/2/2015.
 */
public class CharactersListActivity extends AbstractComponentListActivity<CharacterRow> {
    @NonNull
    @Override
    protected Class<? extends CharacterRow> getComponentClass() {
        return CharacterRow.class;
    }

    @Override
    protected CharacterRow createNewRecord() {
        return new CharacterRow();
    }

    @Override
    protected void openRecord(long id) {
        openCharacter(id);
    }

    @Override
    protected String getRecordTypeName() {
        return "Character";
    }

    @Override
    protected String getSubtitle() {
        return "Characters";
    }

    @Override
    protected void deleteRow(long id) {
        CharacterRow.delete(CharacterRow.class, id);
    }

    @NonNull
    protected String getCursorSortBy() {
        return "last_updated desc";
    }


    protected static class CharacterRowViewHolder extends BindableRecyclerViewHolder {
        public TextView name;
        public TextView classes;

        public CharacterRowViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            classes = (TextView) itemView.findViewById(R.id.classes);
        }

        @Override
        public void bindTo(Cursor cursor, final AbstractComponentListActivity context, RecyclerView.Adapter adapter, IndexesByName indexesByName) {
            super.bindTo(cursor, context, adapter, indexesByName);

            final long id = cursor.getInt(indexesByName.getIndex(cursor, BaseColumns._ID));
            final String nameString = cursor.getString(indexesByName.getIndex(cursor, "name"));
            final String classesString = cursor.getString(indexesByName.getIndex(cursor, "classesString"));
            name.setText(nameString);
            classes.setText(classesString);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.openCharacter(id);
                }
            });
        }
    }

    @NonNull
    @Override
    protected CharacterRowViewHolder newRowViewHolder(View newView) {
        return new CharacterRowViewHolder(newView);
    }

    @Override
    protected int getListItemResource() {
        return R.layout.character_list_item;
    }

}
