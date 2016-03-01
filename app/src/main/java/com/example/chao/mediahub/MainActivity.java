package com.example.chao.mediahub;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
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
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements PlaylistsTabFragment.OnInteractionListener,
        PlaylistMgmtFragment.OnInteractionListener {

    final private static String TAG = "MainActivity";

    final private static String PLAYLIST_OPTIONS_FRAGMENT_TAG = "PlaylistOptionsFragment";

    final private static int LIBRARY_PAGE_POS = 0;
    final private static int PLAYLISTS_PAGE_POS = 1;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private LibraryFragment mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new LibraryFragment(getSupportFragmentManager());

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
                if (position == PLAYLISTS_PAGE_POS)  {

                } else if (position == LIBRARY_PAGE_POS) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(PLAYLIST_OPTIONS_FRAGMENT_TAG);
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

        MediaManager.scan(getApplicationContext(), null);
        //MediaManager.getAllMusicFiles(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                showDialog();
                //showPlaylistMgmtDialog();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicPlaybackService.class);
        startService(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //save current state
        Log.d(TAG, "onSaveInstanceState(), current item : " + mViewPager.getCurrentItem());
        savedInstanceState.putInt("CurrentPagePosition", mViewPager.getCurrentItem());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showDialog() {
        Log.d(TAG, "show dialog");
        Fragment dialog = AddToPlaylistFragment.newInstance(1);

//        dialog.show(getSupportFragmentManager(), "ADD TO PLAYLIST");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.main_content, dialog, "TAG");
        ft.commit();
    }

    private void showPlaylistMgmtDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment dialog = PlaylistMgmtFragment.newInstance("", "");
        ft.add(R.id.main_content, dialog, "OptionsFragment");
        ft.commit();
    }

    @Override
    public void onPlaylistOptionsClick(int playlistId) {
        Log.d(TAG, "Interaction, playlist id : " + playlistId);
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment dialog = PlaylistMgmtFragment.newInstance(Integer.toString(playlistId), "");
//        ft.add(R.id.main_content, dialog, "OptionsFragment");
//        ft.commit();
        updatePlaylistOptionsFragment(playlistId);
    }

    @Override
    public void onFragmentClose() {
        Log.d(TAG, "onFragmentClose");
        Fragment options = getSupportFragmentManager().findFragmentByTag(PLAYLIST_OPTIONS_FRAGMENT_TAG);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(options);
        ft.commit();
    }

    @Override
    public void onDeleteDialog() {
        Log.d(TAG, "onDeleteDialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("delete?").setTitle("empty");

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.d(TAG, "OK");
                PlaylistMgmtFragment fragment = (PlaylistMgmtFragment) getSupportFragmentManager().findFragmentByTag("OptionsFragment");
                fragment.deletePlaylist();
//                finish();
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
        Fragment fragment = fm.findFragmentByTag(PLAYLIST_OPTIONS_FRAGMENT_TAG);

        if (fragment != null) {
//            ft.setCustomAnimations(R.anim.slide_out_no_anim, R.anim.slide_out_down);
            ft.remove(fragment);
        }

        fragment = PlaylistMgmtFragment.newInstance(Integer.toString(playlistId), "");
//        ft.setCustomAnimations(R.anim.slide_in_up, 0);
        ft.add(R.id.main_content, fragment, PLAYLIST_OPTIONS_FRAGMENT_TAG);
        ft.commit();
    };


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class LibraryFragment extends FragmentPagerAdapter {

        public LibraryFragment(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            if (position == LIBRARY_PAGE_POS) {
                return LibraryTabFragment.newInstance("0", "0");
            } else if (position == PLAYLISTS_PAGE_POS) {
                return PlaylistsTabFragment.newInstance("0", "0");
            } else {
                Log.e(TAG, "page section error");
                return new Fragment();
            }
            //return LibraryTabFragment.newInstance("0", "0");
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case LIBRARY_PAGE_POS:
                    return "Library";
                case PLAYLISTS_PAGE_POS:
                    return "Playlists";
            }
            return null;
        }
    }
}
