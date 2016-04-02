package com.oakonell.dndcharacter.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.BuildConfig;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.CharacterRow;
import com.oakonell.dndcharacter.views.background.BackgroundsListActivity;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.characters.CharactersListActivity;
import com.oakonell.dndcharacter.views.classes.ClassesListActivity;
import com.oakonell.dndcharacter.views.effect.EffectsListActivity;
import com.oakonell.dndcharacter.views.exports.ExportActivity;
import com.oakonell.dndcharacter.views.feat.FeatsListActivity;
import com.oakonell.dndcharacter.views.imports.ImportActivity;
import com.oakonell.dndcharacter.views.item.ItemsListActivity;
import com.oakonell.dndcharacter.views.race.RacesListActivity;
import com.oakonell.dndcharacter.views.spell.SpellsListActivity;

import java.util.List;

/**
 * Created by Rob on 11/6/2015.
 */
public abstract class AbstractBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int NUM_RECENT_CHARACTERS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
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
    }

    protected void configureCommon() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        populateRecentCharacters();
    }


    public void populateRecentCharacters() {
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AsyncTask<Void, Void, List<CharacterRow>> task = new AsyncTask<Void, Void, List<CharacterRow>>() {
            @Override
            protected List<CharacterRow> doInBackground(Void... params) {
                List<CharacterRow> characters = new Select()
                        .from(CharacterRow.class).orderBy("last_viewed desc").limit(NUM_RECENT_CHARACTERS).execute();
                return characters;
            }

            @Override
            protected void onPostExecute(@NonNull List<CharacterRow> characters) {
                Menu m = navigationView.getMenu();
                MenuItem item = m.findItem(R.id.nav_recent_characters);
                SubMenu sub = item.getSubMenu();

                // delete current items
                sub.clear();

                for (final CharacterRow each : characters) {
                    String name = each.getName();
                    if (name == null || name.trim().length() == 0) {
                        name = getString(R.string.unnamed_character);
                    }
                    String classesString = each.classesString;
                    if (classesString == null) {
                        each.classesString = getString(R.string.no_class);
                    }

                    MenuItem subitem = sub.add(name + "- " + classesString);
                    subitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            openCharacter(each.getId());
                            return true;
                        }
                    });
                }

                // hack, from http://stackoverflow.com/questions/30695038/how-to-programmatically-add-a-submenu-item-to-the-new-material-design-android-su
                MenuItem mi = m.getItem(m.size() - 1);
                mi.setTitle(mi.getTitle());
            }
        };
        task.execute();
    }

    public void openCharacter(long id) {
        Intent intent = new Intent(this, CharacterActivity.class);
        intent.putExtra(CharacterActivity.CHARACTER_ID, id);
        startActivity(intent);
    }


    // -------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void closeNavigationDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onResume() {
        populateRecentCharacters();
        super.onResume();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_character) {
            openNewCharacter();
        } else if (id == R.id.nav_characters) {
            Intent intent = new Intent(this, CharactersListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_effects) {
            Intent intent = new Intent(this, EffectsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_backgrounds) {
            Intent intent = new Intent(this, BackgroundsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_classes) {
            Intent intent = new Intent(this, ClassesListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_races) {
            Intent intent = new Intent(this, RacesListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_items) {
            Intent intent = new Intent(this, ItemsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_spells) {
            Intent intent = new Intent(this, SpellsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_feats) {
            Intent intent = new Intent(this, FeatsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_import) {
            Intent intent = new Intent(this, ImportActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(this, ExportActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void openNewCharacter() {
        Intent intent = new Intent(this, CharacterActivity.class);
        intent.putExtra(CharacterActivity.CREATE_CHARACTER, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
