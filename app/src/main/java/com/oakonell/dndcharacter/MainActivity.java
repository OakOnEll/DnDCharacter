package com.oakonell.dndcharacter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.storage.CharacterRow;
import com.oakonell.dndcharacter.views.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.SkillBlockView;
import com.oakonell.dndcharacter.views.StatBlockView;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String CHARACTER_ID = "character_id";

    long id = -1;
    com.oakonell.dndcharacter.model.Character character = null;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private MainFragment mainFragment;
    private FeaturesFragment featuresFragment;
    private NotesFragment notesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        // Load recent used character
        long savedId = -1;
        if (savedInstanceState != null) {
            savedId = savedInstanceState.getLong(CHARACTER_ID, -1);
        }
        if (savedId == -1 && getIntent().getExtras() != null) {
            savedId = getIntent().getExtras().getLong(CHARACTER_ID);
        }
        if (savedId == -1) {
            // otherwise if no character, launch either wizard (no characters in list) or character list to choose
            Toast.makeText(this, "Making a new Character", Toast.LENGTH_SHORT).show();
            character = new Character(true);
        } else {
            id = savedId;
            Toast.makeText(this, "Loading an existing Character id=" + id, Toast.LENGTH_SHORT).show();
            CharacterRow characterRow = CharacterRow.load(CharacterRow.class, id);

            if (characterRow.xml == null || characterRow.xml.trim().length() == 0) {
                character = new Character(true);
            } else {
                Serializer serializer = new Persister();
                InputStream input = null;
                try {
                    // offload this to a new thread
                    input = new ByteArrayInputStream(characterRow.xml.getBytes());
                    character = serializer.read(Character.class, input);
                    input.close();
                } catch (Exception e) {
                    throw new RuntimeException("Error loading xml", e);
                }
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (character != null) {
            Serializer serializer = new Persister();
            OutputStream out = null;
            try {
                out = new ByteArrayOutputStream();
                serializer.write(character, out);
                out.close();
            } catch (Exception e) {
                throw new RuntimeException("Error writing character xml", e);
            }
            String xml = out.toString();
            CharacterRow row;
            String action;
            if (id >= 0) {
                row = CharacterRow.load(CharacterRow.class, id);
                action = "Updated";
            } else {
                row = new CharacterRow();
                action = "Added";
            }
            row.classesString = character.getClassesString();
            row.name = character.getName();
            row.xml = xml;
            id = row.save();
            Toast.makeText(this, action + " character '" + row.name + "', id = " + id, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(CHARACTER_ID, id);
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Clicked settings ", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_long_rest) {
            Toast.makeText(this, "Clicked long rest ", Toast.LENGTH_SHORT).show();
            character.longRest();
            updateViews();
            return true;
        }
        if (id == R.id.action_short_rest) {
            Toast.makeText(this, "Clicked short rest ", Toast.LENGTH_SHORT).show();
            character.shortRest();
            updateViews();
            return true;
        }
        if (id == R.id.action_delete) {
            Toast.makeText(this, "Clicked delete character ", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_level_up) {
            Toast.makeText(this, "Clicked level-up character ", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateViews() {
        if (mainFragment != null) mainFragment.updateViews();
        if (featuresFragment != null) featuresFragment.updateViews();
        if (notesFragment != null) notesFragment.updateViews();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_characters) {
            Intent intent = new Intent(this, CharactersListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_backgrounds) {
            Toast.makeText(this, "Clicked backgrounds ", Toast.LENGTH_SHORT).show();
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mainFragment = new MainFragment();
                mainFragment.setCharacter(character);
                return mainFragment;
            }
            if (position == 1) {
                featuresFragment = new FeaturesFragment();
                featuresFragment.setCharacter(character);
                return featuresFragment;
            }
            if (position == 5) {
                notesFragment = new NotesFragment();
                notesFragment.setCharacter(character);
                return notesFragment;

            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Main";
                case 1:
                    return "Features";
                case 2:
                    return "Equipment";
                case 3:
                    return "Spells";
                case 4:
                    return "Persona";
                case 5:
                    return "Notes";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends AbstractSheetFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
