package com.example.spotifystreamer.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class Top10TracksActivityFragment extends Fragment {

    private SimpleAdapter mTop10Adapter;
    private String mArtist;
    private ArrayList tracksResult = new ArrayList<Hashtable<String, Object>>();

    public Top10TracksActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the searched artist in case of a screen rotation for example.
        savedInstanceState.putStringArrayList("trackList", tracksResult);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top10_tracks, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mArtist = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.e("DebugmArtist", mArtist);
        }
        if (savedInstanceState == null) {
            FetchTop10Task top10Tracks = new FetchTop10Task();
            top10Tracks.execute(mArtist);
            Log.e("PointOfStop", "PointOfStop");
        } else {
            tracksResult = savedInstanceState.getStringArrayList("trackList");
        }

        //inflating listview with the adapter
        ListView listView = (ListView) rootView.findViewById(R.id.listview_top10);

        mTop10Adapter = new SimpleAdapter(
                getActivity(),
                tracksResult,
                R.layout.list_item_top10,
                new String[]{"track", "album", "image"},
                new int[]{R.id.textViewTrack, R.id.textViewAlbum, R.id.imageViewArtist}) {
            @Override
            public void setViewImage(ImageView imageView, String urlImage) {
                if (urlImage.isEmpty()) {
                    Picasso.with(getActivity()).load(R.drawable.no_image).fit().into(imageView);
                } else {
                    Picasso.with(getActivity()).load(urlImage).fit().into(imageView);
                }
            }
        };
        listView.setAdapter(mTop10Adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Lunch explicit intent for Now Playing carrying track ID
                Hashtable<String, Object> selectedTrack = (Hashtable<String, Object>) mTop10Adapter.getItem(position);
                //String selectedTrackID = (String) selectedTrack.get("href");
                Intent intent = new Intent(getActivity(), NowPlayingActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, mArtist);
                startActivity(intent);
            }
        });

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
            //Log.e("idDoArtista", id);

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();
            Map map = new HashMap();
            map.put("country", "US");
            try {
                trackList = spotifyService.getArtistTopTrack(id, map);
                Log.e("trackList", String.valueOf(trackList));
            } catch (Exception e) {
                Log.e("Exception", String.valueOf(e));
                return null;
            }

            return getResultFromTrackList(trackList);
        }

        //Return an array with the top10 tracks, album and imagesURL
        private ArrayList getResultFromTrackList(Tracks trackList) {
            int numOfTrack = trackList.tracks.size();
            Log.e("numOfTrack", String.valueOf(numOfTrack));

            if(numOfTrack < 1){
                return null;
            } else {
                ArrayList<Hashtable<String, Object>> top10List = new ArrayList<Hashtable<String, Object>>();
                for (int i = 0; i < numOfTrack; i++) {
                    Hashtable<String, Object> trackTable = new Hashtable<String, Object>();
                    trackTable.put("album", trackList.tracks.get(i).album.name);
                    trackTable.put("track", trackList.tracks.get(i).name);
                    if (trackList.tracks.get(i).album.images.isEmpty()) {
                        trackTable.put("image", "");
                    } else {
                        trackTable.put("image", trackList.tracks.get(i).album.images.get(0).url);
                    }
                    top10List.add(trackTable);
                }
                //Log.e("top10List", String.valueOf(top10List));
                return top10List;
            }
        }

        @Override
        protected void onPostExecute(ArrayList resultList) {
            if (resultList != null) {
                tracksResult.clear();
                tracksResult.addAll(resultList);
                mTop10Adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "No Track Found.Please Select Another Artist!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
