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

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements PlaylistsTabFragment.OnInteractionListener,
        PlaylistOptionsDialog.OnInteractionListener,
        LibraryTabFragment.OnInteractionListener,
        MusicFileOptionsFragment.OnInteractionListener {

    final private static String TAG = "MainActivity";

    //2 tabs, library_tab and playlists_tab
    final private static int NUM_OF_TABS = 2;
    final private static int LIBRARY_TAB_POSITION = 0;
    final private static int PLAYLISTS_TAB_POSITION = 1;

    final private static String LIBRARY_TAB_TITLE = "Library";
    final private static String PLAYLISTS_TAB_TITLE = "Playlists";

    final private static String TAG_PLAYLIST_OPTIONS_FRAGMENT = "PlaylistOptionsDialog";
    private static final String TAG_MUSIC_FILE_OPTIONS_FRAGMENT = "MusicFileOptionsFragment";

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
                        onFragmentClose();
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

    void showDialog() {
        Log.d(TAG, "show dialog");
        AddToPlaylistDialog dialog = AddToPlaylistDialog.newInstance(1);
        dialog.showDialog(getSupportFragmentManager(), "ADD_TO_PLAYLIST");
//        dialog.show(getSupportFragmentManager(), "ADD TO PLAYLIST");
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.main_content, dialog, "TAG");
//        ft.commit();
    }

//    private void showPlaylistMgmtDialog() {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment dialog = PlaylistOptionsDialog.newInstance("", "");
//        ft.add(R.id.main_content, dialog, "OptionsFragment");
//        ft.commit();
//    }
    @Override
    public void playlistOnClick(int playlistId, String playlistName) {
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra(EXTRA_AGR_PLAYLIST_ID, playlistId);
        intent.putExtra(EXTRA_AGR_PLAYLIST_NAME, playlistName);
        startActivity(intent);
    }
    @Override
    public void playlistOptionsOnClick(int playlistId) {
        Log.d(TAG, "Interaction, playlist id : " + playlistId);
        PlaylistOptionsDialog dialog = (PlaylistOptionsDialog) getSupportFragmentManager()
                .findFragmentByTag("PlaylistOptionsDialog");
        if (dialog == null) {
            dialog = PlaylistOptionsDialog.newInstance(playlistId);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_up, 0);
            ft.add(R.id.main_content, dialog, "PlaylistOptionsDialog" );
            ft.commit();
        } else {
            int currentId = dialog.getCurrentPlaylistId();
            Log.d(TAG, "Dialog exists. Current playlist Id: " + currentId);
            if (currentId != playlistId) {
                dialog.updatePlaylistId(playlistId);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(0, R.anim.slide_out_down);
                ft.hide(dialog);
                ft.commit();
                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_up, 0);
                ft.show(dialog);
                ft.commit();
            }
        }
    }

    @Override
    public void onFragmentClose() {
        Log.d(TAG, "onFragmentClose");
//        Fragment options = getSupportFragmentManager().findFragmentByTag(TAG_PLAYLIST_OPTIONS_FRAGMENT);
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.remove(options);
//        ft.commit();
    }

    @Override
    public void onPlaylistDelete() {
        if (mPlaylistTab == null) {
            Log.e(TAG, "Playlist Tab is null");
        } else {
            mPlaylistTab.updateUI();
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
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("PlaylistOptionsDialog");
                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.hide(fragment);
                    ft.commit();
                } else {
                    Log.e(TAG, "ERROR! ");
                }
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


    private void updatePlaylistOptionsFragment (int playlistId) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(TAG_PLAYLIST_OPTIONS_FRAGMENT);

        if (fragment != null) {
//            ft.setCustomAnimations(R.anim.slide_out_no_anim, R.anim.slide_out_down);
            ft.remove(fragment);
        }

        fragment = PlaylistOptionsDialog.newInstance(playlistId);
//        ft.setCustomAnimations(R.anim.slide_in_up, 0);
        ft.add(R.id.main_content, fragment, TAG_PLAYLIST_OPTIONS_FRAGMENT);
        ft.commit();
    };

    @Override
    public void onClickMoreOptions(int audioId) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(TAG_MUSIC_FILE_OPTIONS_FRAGMENT);

        if (fragment != null) {
            ft.remove(fragment);
        }
        Log.d(TAG, "audioId: " + audioId);
        fragment = MusicFileOptionsFragment.newInstance(Integer.toString(audioId), "");
        ft.add(R.id.main_content, fragment, TAG_MUSIC_FILE_OPTIONS_FRAGMENT);
        ft.commit();
}

    @Override
    public void onMusicFileOptionsClose() {
        Fragment options = getSupportFragmentManager().findFragmentByTag(TAG_MUSIC_FILE_OPTIONS_FRAGMENT);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(options);
        ft.commit();
    }

    @Override
    public void onMusicFileOptionsAddToPlaylist(int audioId) {
        showDialog();
//        Log.d(TAG, "show dialog");
//        Fragment fragment = AddToPlaylistDialog.newInstance(1);
//
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.main_content, fragment, "TAG");
//        ft.commit();
//        DialogFragment dialog = AddToPlaylistDialog.newInstance(1);
//        dialog.show(getSupportFragmentManager(), "ADD_TO_PLAYLIST");
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
