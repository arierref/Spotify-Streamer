package com.example.spotifystreamer.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Top10TracksActivity extends AppCompatActivity implements PlayerFragment.PlayerCallback {

    private String mArtistName;
    private String mArtistId;
    private PlayerFragment playerFragment;
    private Top10TracksActivityFragment top10TracksActivityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top10_tracks);

        /*Intent intent = getIntent();
        //Log.e("vendo o intent", String.valueOf(intent.getStringExtra("selectedArtistId")));
        if (intent != null) {
            mArtistName = intent.getStringExtra("artist");
            mArtistId = intent.getStringExtra("artistId");
            //Put the name of the Artist at the Action Bar, below the title.
            getSupportActionBar().setSubtitle(mArtistName);
        }



        if (savedInstanceState == null) {

            playerFragment = new PlayerFragment();
            top10TracksActivityFragment = new Top10TracksActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putString("artist", mArtistName);
            arguments.putString("artistId", mArtistId);

            top10TracksActivityFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_activity_top10, top10TracksActivityFragment, "Top10TracksActivityFragment")
                    .commit();
        } else {
            playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("PlayerFragment");
            top10TracksActivityFragment = (Top10TracksActivityFragment) getSupportFragmentManager().findFragmentByTag("Top10TracksActivityFragment");
        }*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top10_tracks, menu);
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

    public void onItemSelected(ParcelableArray selectedTrack) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        Bundle bundle = new Bundle();
        bundle.putParcelable(PlayerFragment.TRACK_INFO_KEY, selectedTrack);

        playerFragment.setArguments(bundle);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // For a little polish, specify a transition animation
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, playerFragment, "PlayerFragment")
                .addToBackStack(null).commit();


    }

    @Override
    public void onNext() {
        ParcelableArray selectedTrack = top10TracksActivityFragment.loadNext();
        playerFragment.onNext(selectedTrack);
    }

    @Override
    public void onPrevious() {
        ParcelableArray selectedTrack = top10TracksActivityFragment.loadPrevious();
        playerFragment.onPrevious(selectedTrack);
    }

    public void play(View w) {
        playerFragment.play(w);
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
        playerFragment.stop();
    }
}
