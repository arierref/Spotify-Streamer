package com.example.spotifystreamer.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback, NowPlayingActivityFragment.PlayerCallback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;
    private NowPlayingActivityFragment newFragment;
    private Top10TracksActivityFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_top10_detail) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_top10_detail, new Top10TracksActivityFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        MainActivityFragment mainActivityFragment =  ((MainActivityFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_artist));

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
    public void onItemSelected(ParcelableArray selectedTrack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        newFragment = new NowPlayingActivityFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(NowPlayingActivityFragment.TRACK_INFO_KEY, selectedTrack);

        newFragment.setArguments(bundle);

        if (mTwoPane) {
            // The device is using a large layout, so show the topTenTracksFragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the topTenTracksFragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the topTenTracksFragment, which is always the root view for the activity
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
