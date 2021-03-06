package com.oakonell.dndcharacter.views.character;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.oakonell.dndcharacter.BuildConfig;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.DataImporter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterRow;
import com.oakonell.dndcharacter.views.AboutDialog;
import com.oakonell.dndcharacter.views.AbstractBaseActivity;
import com.oakonell.dndcharacter.views.character.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.character.classes.AddClassLevelDialogFragment;
import com.oakonell.dndcharacter.views.character.companion.CompanionsFragment;
import com.oakonell.dndcharacter.views.character.feature.FeaturesFragment;
import com.oakonell.dndcharacter.views.character.feature.SelectEffectDialogFragment;
import com.oakonell.dndcharacter.views.character.item.EquipmentFragment;
import com.oakonell.dndcharacter.views.character.persona.NotesFragment;
import com.oakonell.dndcharacter.views.character.persona.PersonaFragment;
import com.oakonell.dndcharacter.views.character.race.ApplyRaceDialogFragment;
import com.oakonell.dndcharacter.views.character.rest.longRest.LongRestDialogFragment;
import com.oakonell.dndcharacter.views.character.rest.shortRest.ShortRestDialogFragment;
import com.oakonell.dndcharacter.views.character.spell.SpellsFragment;
import com.oakonell.dndcharacter.views.character.stats.BaseStatsDialogFragment;
import com.oakonell.dndcharacter.views.imports.ImportActivity;
import com.oakonell.dndcharacter.views.settings.SettingsActivity;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import hugo.weaving.DebugLog;

public class CharacterActivity extends AbstractBaseActivity {
    public static final String CHARACTER_ID = "character_id";
    public static final String CREATE_CHARACTER = "create";

    public static final String ADD_EFFECT_DIALOG = "add_effect_frag";
    public static final String RACE_FRAG = "race";
    public static final String BACKGROUND_FRAG = "background";
    public static final String LEVEL_UP_FRAG = "level_up";
    public static final String BASE_STATS_FRAG = "base_stats";
    private static final String NAME_FRAG = "name_frag";

    public static final String MyPREFERENCES = "prefs";

    com.oakonell.dndcharacter.views.character.LicenseChecker checker = new com.oakonell.dndcharacter.views.character.LicenseChecker();

    long id = -1;
    @Nullable
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
    private final List<OnCharacterLoaded> onCharacterLoadListeners = new ArrayList<>();
    private BackgroundCharacterLoader characterLoader;
    private ContentLoadingProgressBar loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = (ContentLoadingProgressBar) findViewById(R.id.loading);

        checker.onCreate(this, new TheLicenseCheckerCallback());
        checker.doCheck(this);

        if (!DataImporter.hasAtLeastMinimalData()) {
            Intent intent = new Intent(this, ImportActivity.class);
            startActivity(intent);
        }

        if (savedInstanceState != null) {
            SelectEffectDialogFragment dpf = (SelectEffectDialogFragment) getSupportFragmentManager().findFragmentByTag(ADD_EFFECT_DIALOG);
            if (dpf != null) {
                dpf.setListener(new SelectEffectDialogFragment.AddEffectToCharacterListener(this));
            }

            String[] wizardFrags = new String[]{RACE_FRAG, LEVEL_UP_FRAG, BACKGROUND_FRAG};
            for (String eachFrag : wizardFrags) {
                ApplyAbstractComponentDialogFragment<?> wizardDialog = (ApplyAbstractComponentDialogFragment<?>) getSupportFragmentManager().findFragmentByTag(eachFrag);
                if (wizardDialog != null) {
                    wizardDialog.setDoneListener(new CreationWizardDoneListener());
                }
            }
            BaseStatsDialogFragment baseStatDialog = (BaseStatsDialogFragment) getSupportFragmentManager().findFragmentByTag(BASE_STATS_FRAG);
            if (baseStatDialog != null) {
                baseStatDialog.setDoneListener(new BaseStatsWizardDoneListener());
            }
        }

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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (characterLoader != null) {
            characterLoader.cancel(true);
        }
        outState.putLong(CHARACTER_ID, id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClassName(this, SettingsActivity.class.getName());
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_about) {
            final AboutDialog aboutDialog = AboutDialog.create();
            aboutDialog.show(getSupportFragmentManager(), "about");
            return true;
        }
        if (id == R.id.action_long_rest) {
            LongRestDialogFragment dialog = LongRestDialogFragment.createDialog();
            dialog.show(getSupportFragmentManager(), "short_rest_frag");
            return true;
        }
        if (id == R.id.action_add_effect) {
            SelectEffectDialogFragment dialog = SelectEffectDialogFragment.createDialog(new SelectEffectDialogFragment.AddEffectToCharacterListener(this));
            dialog.show(getSupportFragmentManager(), ADD_EFFECT_DIALOG);
            return true;
        }
        if (id == R.id.action_dice_roller) {
            DiceRollerFragment dialog = DiceRollerFragment.createDialog();
            dialog.show(getSupportFragmentManager(), "dice_roller");
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
        if (getSupportFragmentManager().getFragments() == null) return;
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
        if (character == null) return;
        BackgroundCharacterSaver characterSaver = new BackgroundCharacterSaver(post);
        characterSaver.execute();
    }

    public class BackgroundCharacterSaver extends AsyncTask<Void, Void, CharacterRow.SaveResult> {
        private final Runnable post;

        public BackgroundCharacterSaver(Runnable post) {
            this.post = post;
        }

        @Nullable
        @Override
        protected CharacterRow.SaveResult doInBackground(Void... params) {
            Serializer serializer = new Persister();
            if (character == null) {
                CharacterRow.SaveResult result = new CharacterRow.SaveResult();
                result.action = "not saved... nonexistent?";
                result.id = -1;
                return result;
            }
            CharacterRow.SaveResult saveResult = CharacterRow.saveCharacter(CharacterActivity.this, serializer, character, id);

            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(CHARACTER_ID, id);
            editor.apply();
            return saveResult;
        }


        @Override
        protected void onPostExecute(CharacterRow.SaveResult saveResult) {
            Toast.makeText(CharacterActivity.this, "Character '" + character.getName() + "' " + saveResult.action, Toast.LENGTH_SHORT).show();
            id = saveResult.id;
            populateRecentCharacters();
            if (post != null) {
                post.run();
            }
        }
    }


    @DebugLog
    private void loadCharacter(@Nullable Bundle savedInstanceState) {
        long savedId = -1;
        // try to get a character id from
        // 1.   the saved bundle
        if (savedInstanceState != null) {
            savedId = savedInstanceState.getLong(CHARACTER_ID, -1);
        }
        // 2.   the passed intent
        if (savedId == -1 && getIntent().getExtras() != null) {
            savedId = getIntent().getExtras().getLong(CHARACTER_ID, -1);
            if (savedId == -1) {
                if (getIntent().getExtras().getBoolean(CREATE_CHARACTER)) {
                    createNewCharacter();
                    loading.setVisibility(View.GONE);
                    return;
                }
            }
        }
        // 3.   find the last viewed character
        if (savedId == -1) {
            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            savedId = sharedpreferences.getLong(CHARACTER_ID, -1);
        }

        if (savedId == -1) {
            createNewCharacter();
            loading.setVisibility(View.GONE);
            return;
        }
        loadCharacter(savedId);

    }

    private void createNewCharacter() {
        id = -1;
        character = new Character();
        for (Iterator<OnCharacterLoaded> iter = onCharacterLoadListeners.iterator(); iter.hasNext(); ) {
            OnCharacterLoaded listener = iter.next();
            listener.onCharacterLoaded(character);
            iter.remove();
        }
        updateViews();
        launchCreationWizard();
    }

    private void launchCreationWizard() {
        if (character.getBackground() == null) {
            ApplyBackgroundDialogFragment dialog = ApplyBackgroundDialogFragment.createDialog();
            dialog.setDoneListener(new CreationWizardDoneListener());

            String doneLabelId = null;
            if (character.getRace() == null) {
                doneLabelId = getString(R.string.choose_a_race);
            } else if (character.getClassLevels().isEmpty()) {
                doneLabelId = getString(R.string.add_class_level);
            } else if (character.getBaseStats().isEmpty()) {
                doneLabelId = getString(R.string.choose_base_stats);
            } else if (character.getName() == null || character.getName().trim().length() == 0) {
                doneLabelId = getString(R.string.choose_name);
            }
            dialog.setDoneLabel(doneLabelId);
            dialog.show(getSupportFragmentManager(), BACKGROUND_FRAG);
            return;
        }
        if (character.getRace() == null) {
            ApplyRaceDialogFragment dialog = ApplyRaceDialogFragment.createDialog();
            dialog.setDoneListener(new CreationWizardDoneListener());

            String doneLabelId = null;
            if (character.getClassLevels().isEmpty()) {
                doneLabelId = getString(R.string.add_class_level);
            } else if (character.getBaseStats().isEmpty()) {
                doneLabelId = getString(R.string.choose_base_stats);
            } else if (character.getName() == null || character.getName().trim().length() == 0) {
                doneLabelId = getString(R.string.choose_name);
            }
            dialog.setDoneLabel(doneLabelId);
            dialog.show(getSupportFragmentManager(), RACE_FRAG);
            return;
        }

        if (character.getClassLevels().isEmpty()) {
            AddClassLevelDialogFragment dialog = AddClassLevelDialogFragment.createDialog();
            dialog.setDoneListener(new CreationWizardDoneListener());

            String doneLabelId = null;
            if (character.getBaseStats().isEmpty()) {
                doneLabelId = getString(R.string.choose_base_stats);
            } else if (character.getName() == null || character.getName().trim().length() == 0) {
                doneLabelId = getString(R.string.choose_name);
            }
            dialog.setDoneLabel(doneLabelId);
            dialog.show(getSupportFragmentManager(), LEVEL_UP_FRAG);
            return;
        }

        if (character.getBaseStats().isEmpty()) {
            BaseStatsDialogFragment dialog = BaseStatsDialogFragment.createDialog();
            dialog.setDoneListener(new BaseStatsWizardDoneListener());
            String doneLabelId = null;
            if (character.getName() == null || character.getName().trim().length() == 0) {
                doneLabelId = getString(R.string.choose_name);
            }
            dialog.setDoneLabel(doneLabelId);
            dialog.show(getSupportFragmentManager(), BASE_STATS_FRAG);
            return;
        }
        if (character.getName() == null || character.getName().trim().length() == 0) {
            NameDialog dialog = NameDialog.create();
            dialog.show(getSupportFragmentManager(), NAME_FRAG);

        }
    }

    private void loadCharacter(long savedId) {
        if (savedId == -1) {
            Toast.makeText(CharacterActivity.this, "Invalid Character id requested to load", Toast.LENGTH_SHORT).show();
            createNewCharacter();
            loading.setVisibility(View.GONE);
            return;
        }
        characterLoader = new BackgroundCharacterLoader();
        loading.setVisibility(View.VISIBLE);
        characterLoader.execute(savedId);
    }

    public class BackgroundCharacterLoader extends AsyncTask<Long, String, Void> {
        @Nullable
        @Override
        protected Void doInBackground(Long... savedIds) {
            long savedId = savedIds[0];
            if (savedId == -1) {
                // otherwise if no character, launch either wizard (no characters in list) or character list to choose
                publishProgress("Should not get here!!");
                character = new Character();
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
                    character = new Character();
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
                        Log.e(CharacterActivity.class.getName(), "Error loading character", e);
                        character = new Character();
                        //throw new RuntimeException("Error loading xml", e);
                    }
                }
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(CharacterActivity.this, values[0], Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loading.setVisibility(View.GONE);
            if (isCancelled()) return;

            if (BuildConfig.DEBUG) {
                Toast.makeText(CharacterActivity.this, "Character '" + character.getName() + "' loaded", Toast.LENGTH_SHORT).show();
            }
            for (Iterator<OnCharacterLoaded> iter = onCharacterLoadListeners.iterator(); iter.hasNext(); ) {
                OnCharacterLoaded listener = iter.next();
                listener.onCharacterLoaded(character);
                iter.remove();
            }
            updateViews();
            launchCreationWizard();
        }
    }


    @Nullable
    public Character getCharacter() {
        return character;
    }

    public void addCharacterLoadLister(@NonNull OnCharacterLoaded onCharacterLoad) {
        if (character != null) {
            onCharacterLoad.onCharacterLoaded(character);
            return;
        }
        onCharacterLoadListeners.add(onCharacterLoad);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
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
                return new CompanionsFragment();
            }
            if (position == 6) {
                return new NotesFragment();
            }
            throw new RuntimeException("Not a valid tab");
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.main_tab_title);
                case 1:
                    return getString(R.string.features_tab_title);
                case 2:
                    return getString(R.string.equipment_tab_title);
                case 3:
                    return getString(R.string.spells_tab_title);
                case 4:
                    return getString(R.string.persona_tab_title);
                case 5:
                    return getString(R.string.companion_tab_title);
                case 6:
                    return getString(R.string.notes_tab_title);
            }
            return null;
        }
    }


    protected void openNewCharacter() {
        closeNavigationDrawer();
        saveCharacter(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                intent.putExtra(CharacterActivity.CHARACTER_ID, -1);
                intent.putExtra(CharacterActivity.CREATE_CHARACTER, true);

                createNewCharacter();
            }
        });
    }


    public void openCharacter(final long newId) {
        closeNavigationDrawer();
        saveCharacter(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                intent.putExtra(CharacterActivity.CHARACTER_ID, newId);

                loadCharacter(newId);
            }
        });
    }

    public class BaseStatsWizardDoneListener extends CreationWizardDoneListener {
        @Override
        public void onDone() {
            getCharacter().setHP(getCharacter().getMaxHP());
            super.onDone();
        }
    }

    public class CreationWizardDoneListener implements ApplyAbstractComponentDialogFragment.DoneListener {
        @Override
        public void onDone() {
            launchCreationWizard();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checker.onDestroy(this);
    }

    private class TheLicenseCheckerCallback implements com.oakonell.dndcharacter.views.character.LicenseChecker.LicenseCallback {
        @Override
        public void allow(int reason) {
            // we're good
        }

        @Override
        public void dontAllow(int reason) {

            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // TODO AlertDialog

            if (reason == LicenseChecker.RETRY_REASON) {
                // If the reason received from the policy is RETRY, it was probably
                // due to a loss of connection with the service, so we should give the
                // user a chance to retry. So show a dialog to retry.
//                showDialog(DIALOG_RETRY);
            } else {
                // Otherwise, the user is not licensed to use this app.
                // Your response should always inform the user that the application
                // is not licensed, but your behavior at that point can vary. You might
                // provide the user a limited access version of your app or you can
                // take them to Google Play to purchase the app.
//                showDialog(DIALOG_GOTOMARKET);
            }
        }

        @Override
        public void applicationError(int errorCode) {
            // TODO
        }
    }


}
