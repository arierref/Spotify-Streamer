package com.example.spotifystreamer.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback, NowPlayingActivityFragment.PlayerCallback {

    private boolean mTwoPane;
    private NowPlayingActivityFragment newFragment;
    private Top10TracksActivityFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_top10_detail) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_top10_detail, new Top10TracksActivityFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    @Override
    public void onItemSelected(String spotifyId, String name) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString("artistId", spotifyId);
            args.putString("artist", name);

            fragment = new Top10TracksActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_top10_detail, fragment, "TopTenTracksFragment")
                    .commit();
        } else {
            Intent intent = new Intent(this, Top10TracksActivity.class);
            intent.putExtra("artistId",spotifyId);
            intent.putExtra("artist",name);
            startActivity(intent);
        }
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

    @Override
    public void onItemSelected(ParcelableArray selectedTrack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        newFragment = new NowPlayingActivityFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(NowPlayingActivityFragment.TRACK_INFO_KEY, selectedTrack);

        newFragment.setArguments(bundle);

        if (mTwoPane) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(android.R.id.content, newFragment)
                    .addToBackStack(null).commit();
        }

    }

    @Override
    public void onNext() {
        ParcelableArray track = fragment.loadNext();
        newFragment.onNext(track);

    }

    @Override
    public void onPrevious() {
        ParcelableArray track = fragment.loadPrevious();
        newFragment.onPrevious(track);
    }

    public void play(View w) {
        newFragment.play(w);
    }
}
