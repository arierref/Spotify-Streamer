package com.example.spotifystreamer.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;


/**
 * A placeholder fragment containing a simple view.
 */
public class NowPlayingActivityFragment extends Fragment {

    private SimpleAdapter mTrackAdapter;
    private String mTrack;
    private ArrayList trackResult = new ArrayList<Hashtable<String, Object>>();


    public NowPlayingActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the searched artist in case of a screen rotation for example.
        savedInstanceState.putStringArrayList("track", trackResult);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mTrack = intent.getStringExtra(Intent.EXTRA_TEXT);
            //Log.e("DebugmArtist", mTrack);
        }
        if (savedInstanceState == null) {
            FetchTrackTask Track = new FetchTrackTask();
            Track.execute(mTrack);
            //Log.e("PointOfStop", "PointOfStop");
        } else {
            trackResult = savedInstanceState.getStringArrayList("track");
        }

        return rootView;
    }

    public class FetchTrackTask extends AsyncTask<String, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            Track trackResult;
            String id = params[0];
            //Log.e("idDoArtista", id);

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();
            Map map = new HashMap();
            map.put("country", "US");
            try {
                trackResult = spotifyService.getTrack(id, map);
                //trackList = spotifyService.getArtistTopTrack(id, map);
                Log.e("trackList", String.valueOf(trackResult));
            } catch (Exception e) {
                Log.e("Exception", String.valueOf(e));
                return null;
            }
            return null;
            //return getResultFromTrack(trackResult);
        }

        //Return an array with the top10 tracks, album and imagesURL
        //private ArrayList getResultFromTrack(Track trackResult) {

            //if(trackResult == null){
                //return null;
            //} else {
                //Array trackDetail;
                    //Hashtable<String, Object> trackTable = new Hashtable<String, Object>();
                    //trackTable.put("album", trackResult.track.get(i).album.name);
                    //trackTable.put("track", trackResult.track.get(i).name);
                    //trackTable.put("id", trackResult.tracks.get(i).id);
                    //if (trackResult.tracks.get(i).album.images.isEmpty()) {
                        //trackTable.put("image", "");
                    //} else {
                        //trackTable.put("image", trackResult.tracks.get(i).album.images.get(0).url);
                    //}
                    //trackDetail.add(trackTable);
                //Log.e("top10List", String.valueOf(top10List));
                //return top10List;
            //}
        //}

        //@Override
        //protected void onPostExecute(ArrayList resultList) {
            //if (resultList != null) {
               // trackResult.clear();
                //trackResult.addAll(resultList);
                //mTrackAdapter.notifyDataSetChanged();
            //} else {
                //Toast.makeText(getActivity(), "No Track Found.Please Select Another Artist!", Toast.LENGTH_SHORT).show();
            //}
        //}
    }
}
