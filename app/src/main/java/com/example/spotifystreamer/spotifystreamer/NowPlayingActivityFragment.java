package com.example.spotifystreamer.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class NowPlayingActivityFragment extends Fragment {

    private SimpleAdapter mTrackAdapter;
    private String mArtistId;
    private ArrayList tracksResult = new ArrayList<Hashtable<String, Object>>();


    public NowPlayingActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the searched artist in case of a screen rotation for example.
        savedInstanceState.putStringArrayList("track", tracksResult);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mArtistId = intent.getStringExtra(Intent.EXTRA_TEXT);
            //Log.e("DebugmArtist", mArtistId);
        }
        if (savedInstanceState == null) {
            FetchTop10Task Track = new FetchTop10Task();
            Track.execute(mArtistId);
            //Log.e("PointOfStop", "PointOfStop");
        } else {
            tracksResult = savedInstanceState.getStringArrayList("track");
        }

        return rootView;
    }

    public class FetchTop10Task extends AsyncTask<String, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            Tracks trackList;
            String id = params[0];

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();
            Map map = new HashMap();
            map.put("country", "US");
            try {
                trackList = spotifyService.getArtistTopTrack(id, map);
            } catch (Exception e) {
                return null;
            }

            return getResultFromTrackList(trackList);
        }

        //Return an array with the top10 tracks, album and imagesURL
        private ArrayList getResultFromTrackList(Tracks trackList) {
            int numOfTrack = trackList.tracks.size();

            if(numOfTrack < 1){
                return null;
            } else {
                ArrayList<Hashtable<String, Object>> top10List = new ArrayList<Hashtable<String, Object>>();
                for (int i = 0; i < numOfTrack; i++) {
                    Hashtable<String, Object> trackTable = new Hashtable<String, Object>();
                    trackTable.put("album", trackList.tracks.get(i).album.name);
                    trackTable.put("track", trackList.tracks.get(i).name);
                    trackTable.put("id", trackList.tracks.get(i).id);
                    trackTable.put("href", trackList.tracks.get(i).href);
                    trackTable.put("uri", trackList.tracks.get(i).uri);
                    if (trackList.tracks.get(i).album.images.isEmpty()) {
                        trackTable.put("image", "");
                    } else {
                        trackTable.put("image", trackList.tracks.get(i).album.images.get(0).url);
                    }
                    top10List.add(trackTable);
                }
                //Log.e("top10ListAgain", String.valueOf(top10List));
                return top10List;
            }
        }

        @Override
        protected void onPostExecute(ArrayList resultList) {
            if (resultList != null) {
                tracksResult.clear();
                tracksResult.addAll(resultList);
                //mTop10Adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "No Track Found.Please Select Another Artist!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
