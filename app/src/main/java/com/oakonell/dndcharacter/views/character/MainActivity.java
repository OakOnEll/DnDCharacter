package com.oakonell.dndcharacter.views.character;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterRow;
import com.oakonell.dndcharacter.views.AbstractBaseActivity;
import com.oakonell.dndcharacter.views.character.classes.AddClassLevelDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.AddEffectDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.FeaturesFragment;
import com.oakonell.dndcharacter.views.character.item.EquipmentFragment;
import com.oakonell.dndcharacter.views.character.persona.NotesFragment;
import com.oakonell.dndcharacter.views.character.persona.PersonaFragment;
import com.oakonell.dndcharacter.views.character.rest.LongRestDialogFragment;
import com.oakonell.dndcharacter.views.character.rest.ShortRestDialogFragment;
import com.oakonell.dndcharacter.views.character.spell.SpellsFragment;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import hugo.weaving.DebugLog;

public class MainActivity extends AbstractBaseActivity {
    public static final String CHARACTER_ID = "character_id";
    private final String MyPREFERENCES = "prefs";
    long id = -1;
    private Character character = null;
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private List<OnCharacterLoaded> onCharacterLoadListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureCommon();

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
        loadCharacter(savedInstanceState);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (character != null) {
            saveCharacter();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        loadCharacter(savedInstanceState);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        loadCharacter(-1);
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(CHARACTER_ID, id);
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
            LongRestDialogFragment dialog = LongRestDialogFragment.createDialog();
            dialog.show(getSupportFragmentManager(), "short_rest_frag");
            return true;
        }
        if (id == R.id.action_add_effect) {
            AddEffectDialogFragment dialog = AddEffectDialogFragment.createDialog();
            dialog.show(getSupportFragmentManager(), "add_effect_frag");
            return true;
        }
        if (id == R.id.action_short_rest) {
            ShortRestDialogFragment dialog = ShortRestDialogFragment.createDialog();
            dialog.show(getSupportFragmentManager(), "short_rest_frag");

            return true;
        }
        if (id == R.id.action_delete) {
            Toast.makeText(this, "Clicked delete character ", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_level_up) {
            AddClassLevelDialogFragment dialog = AddClassLevelDialogFragment.createDialog();
            dialog.show(getSupportFragmentManager(), "level_up");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @DebugLog
    public void updateViews() {
        // loop over fragments and check for character change listeners
        for (Fragment each : getSupportFragmentManager().getFragments()) {
            if (each instanceof CharacterChangedListener) {
                ((CharacterChangedListener) each).onCharacterChanged(character);
            }
        }
    }

    public void saveCharacter() {
        saveCharacter(null);
    }

    @DebugLog
    public void saveCharacter(Runnable post) {
        BackgroundCharacterSaver characterSaver = new BackgroundCharacterSaver(post);
        characterSaver.execute();
    }

    public class BackgroundCharacterSaver extends AsyncTask<Void, Void, Void> {
        private final Runnable post;

        public BackgroundCharacterSaver(Runnable post) {
            this.post = post;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Serializer serializer = new Persister();
            OutputStream out;
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
            row.last_updated = new Date();

            id = row.save();

            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(CHARACTER_ID, id);
            editor.commit();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Character '" + character.getName() + "' saved", Toast.LENGTH_SHORT).show();
            if (post != null) {
                post.run();
            }
        }
    }


    @DebugLog
    private void loadCharacter(Bundle savedInstanceState) {
        long savedId = -1;
        // try to get a character id from
        // 1.   the saved bundle
        if (savedInstanceState != null) {
            savedId = savedInstanceState.getLong(CHARACTER_ID, -1);
        }
        // 2.   the passed intent
        if (savedId == -1 && getIntent().getExtras() != null) {
            savedId = getIntent().getExtras().getLong(CHARACTER_ID);
        }

        loadCharacter(savedId);

    }

    private void loadCharacter(long savedId) {
        // 3.   find the last viewed character
        if (savedId == -1) {
            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            savedId = sharedpreferences.getLong(CHARACTER_ID, -1);
        }

        BackgroundCharacterLoader characterLoader = new BackgroundCharacterLoader();
        characterLoader.execute(savedId);
    }

    public class BackgroundCharacterLoader extends AsyncTask<Long, String, Void> {
        @Override
        protected Void doInBackground(Long... savedIds) {
            long savedId = savedIds[0];
            if (savedId == -1) {
                // otherwise if no character, launch either wizard (no characters in list) or character list to choose
                publishProgress("Making a new Character");
                character = new Character(true);
            } else {
                id = savedId;
                //publishProgress("Loading an existing Character id=" + id);
                CharacterRow characterRow = CharacterRow.load(CharacterRow.class, id);
                if (characterRow == null) {
                    //  Toast.makeText(this, "Making a new Character", Toast.LENGTH_SHORT).show();
                    characterRow = new CharacterRow();
                    id = characterRow.save();
                }
                characterRow.last_viewed = new Date();
                characterRow.save();
                populateRecentCharacters();

                if (characterRow.xml == null || characterRow.xml.trim().length() == 0) {
                    character = new Character(true);
                } else {

                    Serializer serializer = new Persister();
                    InputStream input;
                    try {
                        // offload this to a new thread
                        input = new ByteArrayInputStream(characterRow.xml.getBytes());
                        character = serializer.read(Character.class, input);
                        input.close();
                    } catch (Exception e) {
                        publishProgress("Error loading a Character, making a new one for now: " + e.getMessage());
                        Log.e(MainActivity.class.getName(), "Error loading character", e);
                        character = new Character(true);
                        //throw new RuntimeException("Error loading xml", e);
                    }
                }
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(MainActivity.this, values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Character '" + character.getName() + "' loaded", Toast.LENGTH_SHORT).show();
            for (Iterator<OnCharacterLoaded> iter = onCharacterLoadListeners.iterator(); iter.hasNext(); ) {
                OnCharacterLoaded listener = iter.next();
                listener.onCharacterLoaded(character);
                iter.remove();
            }
            updateViews();
        }
    }


    public Character getCharacter() {
        return character;
    }

    public void addCharacterLoadLister(OnCharacterLoaded onCharacterLoad) {
        if (character != null) {
            onCharacterLoad.onCharacterLoaded(character);
            return;
        }
        onCharacterLoadListeners.add(onCharacterLoad);
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

        public PlaceholderFragment() {
        }

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

        @Override
        public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            superCreateViews(rootView);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

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
                return new MainFragment();
            }
            if (position == 1) {
                return new FeaturesFragment();
            }
            if (position == 2) {
                return new EquipmentFragment();
            }
            if (position == 3) {
                return new SpellsFragment();
            }
            if (position == 4) {
                return new PersonaFragment();

            }
            if (position == 5) {
                return new NotesFragment();
            }
            throw new RuntimeException("Not a valid tab");
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
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

    public void openCharacter(final long id) {
        saveCharacter(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                intent.putExtra(MainActivity.CHARACTER_ID, id);

                loadCharacter(id);
                closeNavigationDrawer();
            }
        });
    }
}
