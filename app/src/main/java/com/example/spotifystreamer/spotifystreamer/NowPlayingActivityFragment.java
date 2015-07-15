package com.example.spotifystreamer.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Hashtable;


/**
 * A placeholder fragment containing a simple view.
 */
public class NowPlayingActivityFragment extends Fragment implements View.OnClickListener {

    //private SimpleAdapter mTrackAdapter;
    private int mArtistSelected;
    private int position;
    private ArrayList tracksResult = new ArrayList<Hashtable<String, Object>>();
    private ArrayList trackToPlay;

    public NowPlayingActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the searched artist in case of a screen rotation for example.
        savedInstanceState.putInt("artistSelected", mArtistSelected);
        savedInstanceState.putStringArrayList("Top10Tracks", tracksResult);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mArtistSelected = intent.getIntExtra("artistSelected", position);
            tracksResult = intent.getStringArrayListExtra("Top10Tracks");
        }
        if (savedInstanceState != null) {
            mArtistSelected = savedInstanceState.getInt("artistSelected");
            tracksResult = savedInstanceState.getStringArrayList("Top10Tracks");
            int positionMusic = mArtistSelected;
            trackToPlay = tracksResult;
            //trackProgress = savedInstanceState.getInt("Progress");
            //seekBar.setProgress(trackProgress);
        } else{
            int positionMusic = mArtistSelected;
            //trackToPlay = (Hashtable) tracksResult.get(positionMusic);
        }


        //MediaPlayerService.setSong(trackToPlay.previewUrl, trackToPlay.mTrack, trackToPlay.imageUrl);
        //getActivity().startService(new Intent("PLAY"));


        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}
