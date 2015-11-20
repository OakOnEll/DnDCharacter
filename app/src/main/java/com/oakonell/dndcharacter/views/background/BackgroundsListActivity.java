package com.oakonell.dndcharacter.views.background;

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
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.race.EditRaceDialogFragment;

/**
 * Created by Rob on 11/2/2015.
 */
public class BackgroundsListActivity extends AbstractComponentListActivity {
    @NonNull
    @Override
    protected Class<? extends Model> getComponentClass() {
        return Background.class;
    }

    @Override
    protected Background createNewRecord() {
        return new Background();
    }

    @Override
    protected void openRecord(long id) {
        Background background = Background.load(Background.class, id);

        EditBackgroundDialogFragment dialog = EditBackgroundDialogFragment.create(background);
        dialog.show(getSupportFragmentManager(), "background_edit");
    }

    @Override
    protected String getRecordTypeName() {
        return "Background";
    }
}
