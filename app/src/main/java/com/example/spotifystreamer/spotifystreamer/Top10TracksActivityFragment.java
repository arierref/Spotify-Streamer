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
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class Top10TracksActivityFragment extends Fragment {

    private static final String LOG_TAG = Top10TracksActivityFragment.class.getSimpleName();
    private TracksAdapter mTracksAdapter;
    //private String mArtist;
    private ArrayList<ParcelableArray> trackArrayList;
    private int mPositionID;

    private SimpleAdapter mTop10Adapter;
    private String mArtist;
    private ArrayList tracksResult = new ArrayList<Hashtable<String, Object>>();

    public Top10TracksActivityFragment() {
    }

    /*@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the searched artist in case of a screen rotation for example.
        savedInstanceState.putStringArrayList("trackList", tracksResult);
        super.onSaveInstanceState(savedInstanceState);
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        Log.e("AllArguments", String.valueOf(arguments));
        if (arguments != null) {
            mArtist = arguments.getString("artistId");
        }

        if (savedInstanceState != null) {
        } else {
            FetchTop10Task FetchTop10Task = new FetchTop10Task();
            FetchTop10Task.execute(mArtist);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.e("Entidades Salvas:", String.valueOf(savedInstanceState));
        savedInstanceState.putParcelableArrayList("TopTenTracks", trackArrayList);
        savedInstanceState.putInt("artistId", Integer.parseInt(mArtist));
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top10_tracks, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("artistId")) {
            mArtist = intent.getStringExtra("artistId");
        }
        if (savedInstanceState == null) {
            FetchTop10Task top10Tracks = new FetchTop10Task();
            top10Tracks.execute(mArtist);
            //Log.e("PointOfStop", "PointOfStop");
        } else {
            tracksResult = savedInstanceState.getParcelableArrayList("TopTenTracks");
            mPositionID = savedInstanceState.getInt("artistId");
        }

        //if (savedInstanceState != null) {
            //trackArrayList = savedInstanceState.getParcelableArrayList("TopTenTracks");
            //mPositionID = savedInstanceState.getInt("artistId");

        //} else {
            //trackArrayList = new ArrayList<ParcelableArray>();
        //}

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

        mTracksAdapter = new TracksAdapter(getActivity(), R.layout.list_item_top10, trackArrayList);

        //inflating listview with the adapter
        ListView listView = (ListView) rootView.findViewById(R.id.listview_top10);
        listView.setAdapter(mTop10Adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Lunch parcelable for Now Playing carrying tracks list
                mPositionID = position;
                ParcelableArray selectedTrack = mTracksAdapter.getItem(mPositionID);
                ((PlayerFragment.PlayerCallback) getActivity())
                        .onItemSelected(selectedTrack);
            }
        });

        return rootView;
    }

    public ParcelableArray loadNext() {
        ParcelableArray selectedTrack = null;
        if (mPositionID < mTracksAdapter.getCount() - 1) {
            mPositionID = mPositionID + 1;
            selectedTrack = mTracksAdapter.getItem(mPositionID);
        }else{
            selectedTrack = mTracksAdapter.getItem(mPositionID);
            Toast.makeText(getActivity(), "No Next Track found. Click on Previous", Toast.LENGTH_LONG).show();
        }
        return selectedTrack;
    }

    public ParcelableArray loadPrevious() {
        ParcelableArray selectedTrack = null;
        if (mPositionID != 0) {
            mPositionID = mPositionID - 1;
            selectedTrack = mTracksAdapter.getItem(mPositionID);
        }else{
            selectedTrack = mTracksAdapter.getItem(mPositionID);
            Toast.makeText(getActivity(), "No Previous Track found. Click on Next", Toast.LENGTH_LONG).show();
        }
        return selectedTrack;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public class FetchTop10Task extends AsyncTask<String, Void, ArrayList> {

        private RetrofitError retrofitError;

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
                //Log.e("trackList", String.valueOf(trackList));
            } catch (RetrofitError e) {
                retrofitError = e;
                Log.e("Exception", String.valueOf(e));
                return null;
            }

            return getResultFromTrackList(trackList);
            //return trackList;
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

                trackArrayList.clear();
                String imageUrl = "";

                /*for (ArrayList resultList : trackList.tracks) {
                    if (!track.album.images.isEmpty()) {
                        imageUrl = track.album.images.get(0).url;
                    }
                    StringBuilder builder = new StringBuilder();
                    for (ArtistSimple artist : track.artists) {
                        if (builder.length() > 0) builder.append(", ");
                        builder.append(artist.name);
                    }
                    ParcelableArray ParcelableArray = new ParcelableArray(
                            track.name,
                            track.album.name,
                            builder.toString(),
                            imageUrl,
                            track.preview_url);
                    trackArrayList.add(ParcelableArray);
                }*/
            } else {
                Toast.makeText(getActivity(), "No Track Found.Please Select Another Artist!", Toast.LENGTH_SHORT).show();
            }
        }

        /*@Override
        protected void onPostExecute(Tracks result) {
            if (result != null) {
                if (result.tracks.isEmpty()) {
                    Toast.makeText(getActivity(), "Tracks not found, please search again.", Toast.LENGTH_LONG).show();
                } else {
                    mTracksAdapter.clear();
                    String imageUrl = "";
                    for (Track track : result.tracks) {
                        if (!track.album.images.isEmpty()) {
                            imageUrl = track.album.images.get(0).url;
                        }
                        StringBuilder builder = new StringBuilder();
                        for (ArtistSimple artist : track.artists) {
                            if (builder.length() > 0) builder.append(", ");
                            builder.append(artist.name);
                        }
                        ParcelableArray ParcelableArray = new ParcelableArray(
                                track.name,
                                track.album.name,
                                builder.toString(),
                                imageUrl,
                                track.preview_url);
                        mTracksAdapter.add(ParcelableArray);
                        //mTracksAdapter.notifyDataSetChanged();

                    }
                }
            } else {
                Toast.makeText(getActivity(), "Error: " + retrofitError.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }*/
    }

}
