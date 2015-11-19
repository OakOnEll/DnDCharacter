package com.oakonell.dndcharacter;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.views.background.BackgroundsListActivity;
import com.oakonell.dndcharacter.model.CharacterRow;

import java.util.List;

/**
 * Created by Rob on 11/6/2015.
 */
public abstract class AbstractBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int NUM_RECENT_CHARACTERS = 3;

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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        List<CharacterRow> characters = new Select()
                .from(CharacterRow.class).orderBy("last_viewed desc").limit(NUM_RECENT_CHARACTERS).execute();

        Menu m = navigationView.getMenu();
        MenuItem item = m.findItem(R.id.nav_recent_characters);
        SubMenu sub = item.getSubMenu();

        // delete current items
        sub.clear();

        for (final CharacterRow each : characters) {
            MenuItem subitem = sub.add(each.name + "- " + each.classesString);
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

    protected void openCharacter(long id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.CHARACTER_ID, id);
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

    @Override
    protected void onResume() {
        populateRecentCharacters();
        super.onResume();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_characters) {
            Intent intent = new Intent(this, CharactersListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_backgrounds) {
            Intent intent = new Intent(this, BackgroundsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_classes) {
            Toast.makeText(this, "Clicked classes ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_races) {
            Toast.makeText(this, "Clicked races ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_items) {
            Toast.makeText(this, "Clicked items ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_feats) {
            Toast.makeText(this, "Clicked feats ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
