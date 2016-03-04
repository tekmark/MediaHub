package com.example.chao.mediahub;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements PlaylistsTabFragment.OnInteractionListener,
        PlaylistOptionsDialog.OnInteractionListener,
        LibraryTabFragment.OnInteractionListener,
        MusicFileOptionsDialog.OnInteractionListener,
        AddToPlaylistDialog.OnInteractionListener {

    final private static String TAG = "MainActivity";

    //2 tabs, library_tab and playlists_tab
    final private static int NUM_OF_TABS = 2;
    final private static int LIBRARY_TAB_POSITION = 0;
    final private static int PLAYLISTS_TAB_POSITION = 1;

    final private static String LIBRARY_TAB_TITLE = "Library";
    final private static String PLAYLISTS_TAB_TITLE = "Playlists";

    final private static String TAG_PLAYLIST_OPTIONS_FRAGMENT = "PlaylistOptionsDialog";
    private static final String TAG_MUSIC_FILE_OPTIONS_FRAGMENT = "MusicFileOptionsDialog";

    private static final String STATE_CURRENT_TAB_POSITION = "StateCurrentTabPosition";


    final static public String EXTRA_AGR_PLAYLIST_ID = "PlaylistsTabFragment.PLAYLIST_ID";
    final static public String EXTRA_AGR_PLAYLIST_NAME = "PlaylistsTabFragment.PLAYLIST_NAME";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private TabsFragment mSectionsPagerAdapter;

    private PlaylistsTabFragment mPlaylistTab;
    private LibraryTabFragment mLibraryTab;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new TabsFragment(getSupportFragmentManager());

        int current_tab_position = 0;
        if (savedInstanceState == null) {
            Log.d(TAG, "No saved Instance State");
        } else {
            current_tab_position = savedInstanceState.getInt(STATE_CURRENT_TAB_POSITION, 0);
            Log.d(TAG, "Saved Current Tab Position is " + current_tab_position);
        }
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Toast.makeText(MainActivity.this,
                        "Selected page position: " + position, Toast.LENGTH_SHORT).show();
                if (position == PLAYLISTS_TAB_POSITION) {

                } else if (position == LIBRARY_TAB_POSITION) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_PLAYLIST_OPTIONS_FRAGMENT);
                    if (fragment != null) {
                        //onFragmentClose();
                    }
                } else {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(current_tab_position);

        MediaManager.scan(getApplicationContext(), null);
        //MediaManager.getAllMusicFiles(getApplicationContext());
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
        Intent intent = new Intent(this, MusicPlaybackService.class);
        startService(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //save current state
        Log.d(TAG, "onSaveInstanceState(), current item : " + mViewPager.getCurrentItem());
        savedInstanceState.putInt(STATE_CURRENT_TAB_POSITION, mViewPager.getCurrentItem());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onPause() {
        removeDialogByTag(Tags.Fragments.DIALOG_PLAYLIST_OPTIONS, 0);
        removeDialogByTag(Tags.Fragments.DIALOG_MUSIC_FILE_OPTIONS, 0);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        if (mPlaylistTab != null) {
            mPlaylistTab.updateUI();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void playlistOnClick(int playlistId, String playlistName) {
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra(EXTRA_AGR_PLAYLIST_ID, playlistId);
        intent.putExtra(EXTRA_AGR_PLAYLIST_NAME, playlistName);
        startActivity(intent);
    }

    @Override
    public void musicfileOptionsOnClick(int audioId) {
        Log.d(TAG, "Iteraction, audio id : " + audioId);
        MusicFileOptionsDialog dialog = (MusicFileOptionsDialog) getSupportFragmentManager()
                .findFragmentByTag(Tags.Fragments.DIALOG_MUSIC_FILE_OPTIONS);
        if (dialog == null) {
            dialog = MusicFileOptionsDialog.newInstance(audioId);
            addDialog(dialog, Tags.Fragments.DIALOG_MUSIC_FILE_OPTIONS, R.anim.slide_in_up);
        } else {
            if (dialog.isHidden()) {
                Log.d(TAG, "Dialog exists, but hidden");
                showDialog(dialog, R.anim.slide_in_up);
            } else {
                int currentId = dialog.getCurrentAudioId();
                if (currentId != audioId) {
                    hideDialog(dialog, R.anim.slide_out_down);
                    dialog.updateAudioId(audioId);
                    showDialog(dialog, R.anim.slide_in_up);
                }
            }
        }
    }

    @Override
    public void playlistOptionsOnClick(int playlistId) {
        Log.d(TAG, "Interaction, playlist id : " + playlistId);
        PlaylistOptionsDialog dialog = (PlaylistOptionsDialog) getSupportFragmentManager()
                .findFragmentByTag(Tags.Fragments.DIALOG_PLAYLIST_OPTIONS);
        if (dialog == null) {
            dialog = PlaylistOptionsDialog.newInstance(playlistId);
            addDialog(dialog, Tags.Fragments.DIALOG_PLAYLIST_OPTIONS, R.anim.slide_in_up);
        } else {
            if (dialog.isHidden()) {
                Log.d(TAG, "Dialog exists, but hidden");
                showDialog(dialog, R.anim.slide_in_up);
            } else {
                int currentId = dialog.getCurrentPlaylistId();
                if (currentId != playlistId) {
                    hideDialog(dialog, R.anim.slide_out_down);
                    dialog.updatePlaylistId(playlistId);
                    showDialog(dialog, R.anim.slide_in_up);
                }
            }
        }
    }

    @Override
    public void onPlaylistDelete(final int playlistId) {
        //build a alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("delete?").setTitle("empty");

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.d(TAG, "OK");
                MediaManager.deletePlaylist(MainActivity.this, playlistId);
                hideDialogByTag(Tags.Fragments.DIALOG_PLAYLIST_OPTIONS, R.anim.slide_out_down);
                mPlaylistTab.updateUI();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.d(TAG, "Cancel");
            }
        });
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void musicfileOnClick(int audioId) {
        Log.d(TAG, "Music file on Click");
    }

    @Override
    public void onDialogClose(String tag) {
        hideDialogByTag(tag, R.anim.slide_out_down);
    }

    private void hideDialog(Fragment dialog, int animExit) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, animExit);
        ft.hide(dialog);
        ft.commit();
        Log.d(TAG, "Dialog Tag : " + dialog.getTag() + " -> hide");
    }

    private void hideDialogByTag(String tag, int animExit) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment dialog = fm.findFragmentByTag(tag);
        if (dialog != null) {
            hideDialog(dialog, animExit);
        } else {
            Log.w(TAG, "hideDialogByTag() - no fragment with tag: " + tag);
        }
    }

    private void showDialog(Fragment dialog, int animEnter) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(animEnter, 0);
        ft.show(dialog);
        ft.commit();
        Log.d(TAG, "Dialog Tag : " + dialog.getTag() + " -> show");
    }

    private void showDialogByTag(String tag, int anim) {
        Fragment dialog = getSupportFragmentManager().findFragmentByTag(tag);
        showDialog(dialog, 0);
    }

    private void addDialog(Fragment dialog, String tag, int animEnter) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(animEnter, 0);
        ft.add(R.id.main_content, dialog, tag);
        ft.commit();
        Log.d(TAG, "Add dialog with tag: " + tag);
    }

    private void removeDialogByTag(String tag, int animEnter) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment dialog = fm.findFragmentByTag(tag);
        if (dialog != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(dialog);
            ft.commit();
            Log.d(TAG, "Dialog Tag: " + tag + " -> remove");
        }
    }

    @Override
    public void onMusicFileOptionsAddToPlaylist(int audioId) {
        //hide options dialog;
        hideDialogByTag(Tags.Fragments.DIALOG_MUSIC_FILE_OPTIONS, 0);
        //show add_to_playlist dialog
        AddToPlaylistDialog dialog = AddToPlaylistDialog.newInstance(audioId);
        addDialog(dialog, Tags.Fragments.DIALOG_ADD_TO_PLAYLIST, R.anim.slide_in_up);
    }

    @Override
    public void playlistOnSelect(final int audioId, final Playlist playlist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("confirm ?").setTitle("empty");

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.d(TAG, "OK, playlistId: " + playlist.getId() + " audioId: " + audioId);
                MediaManager.addMusicFileToPlaylist(MainActivity.this, audioId, playlist.getId());
                //remove dialog
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fm.findFragmentByTag(Tags.Fragments.DIALOG_ADD_TO_PLAYLIST);
                ft.remove(fragment);
                ft.commit();
                //update playlist tab fragment
                mPlaylistTab.updateUI();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.d(TAG, "Cancel");
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class TabsFragment extends FragmentPagerAdapter {

        public TabsFragment(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "GetItem() at " + position);
            if (position == LIBRARY_TAB_POSITION) {
                return LibraryTabFragment.newInstance("0", "0");
            } else if (position == PLAYLISTS_TAB_POSITION) {
                return PlaylistsTabFragment.newInstance("0", "0");
            } else {
                Log.e(TAG, "Error. Page position: " + position + " doesn't exist.");
                return new Fragment();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            switch (position) {
                case LIBRARY_TAB_POSITION:
                    Log.d(TAG, "Library Tag: " + createdFragment.getTag());
                    break;
                case PLAYLISTS_TAB_POSITION:
                    Log.d(TAG, "Playlist Tag: " + createdFragment.getTag());
                    mPlaylistTab = (PlaylistsTabFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return NUM_OF_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case LIBRARY_TAB_POSITION:
                    return LIBRARY_TAB_TITLE;
                case PLAYLISTS_TAB_POSITION:
                    return PLAYLISTS_TAB_TITLE;
                default:
                    return null;
            }
        }
    }
}
