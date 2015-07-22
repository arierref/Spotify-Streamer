package com.example.spotifystreamer.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;


public class Top10TracksActivity extends AppCompatActivity implements NowPlayingActivityFragment.PlayerCallback {

    private String mArtistName;
    private String mArtistId;
    private NowPlayingActivityFragment nowPlayingActivityFragment;
    private Top10TracksActivityFragment top10TracksActivityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        //Log.e("vendo o intent", String.valueOf(intent.getStringExtra("selectedArtistId")));
        if (intent != null) {
            mArtistName = intent.getStringExtra("artist");
            mArtistId = intent.getStringExtra("artistId");
            //Put the name of the Artist at the Action Bar, below the title.
            getSupportActionBar().setSubtitle(mArtistName);
        }

        setContentView(R.layout.activity_top10_tracks);

        if (savedInstanceState == null) {

            nowPlayingActivityFragment = new NowPlayingActivityFragment();
            top10TracksActivityFragment = new Top10TracksActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putString("artist", mArtistName);
            arguments.putString("artistId", mArtistId);

            top10TracksActivityFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_activity_top10, top10TracksActivityFragment, "Top10TracksActivityFragment")
                    .commit();
        } else {
            nowPlayingActivityFragment = (NowPlayingActivityFragment) getSupportFragmentManager().findFragmentByTag("NowPlayingActivityFragment");
            top10TracksActivityFragment = (Top10TracksActivityFragment) getSupportFragmentManager().findFragmentByTag("Top10TracksActivityFragment");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(ParcelableArray selectedTrack) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        Bundle bundle = new Bundle();
        bundle.putParcelable(NowPlayingActivityFragment.TRACK_INFO_KEY, selectedTrack);
        //bundle.putStringArrayList(NowPlayingActivityFragment.TRACK_INFO_KEY, selectedTrack);

        nowPlayingActivityFragment.setArguments(bundle);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, nowPlayingActivityFragment, "NowPlayingActivityFragment")
                .addToBackStack(null).commit();

    }


    @Override
    public void onNext() {
        int selectedTrack = top10TracksActivityFragment.loadNext();
        nowPlayingActivityFragment.onNext(selectedTrack);
    }

    @Override
    public void onPrevious() {
        int selectedTrack = top10TracksActivityFragment.loadPrevious();
        nowPlayingActivityFragment.onPrevious(selectedTrack);
    }

    public void play(View w) {
        nowPlayingActivityFragment.play(w);
    }

    public void previous(View w) {
        top10TracksActivityFragment.loadPrevious();
    }

    public void next(View w) {
        top10TracksActivityFragment.loadNext();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //nowPlayingActivityFragment.stop();
    }
}
