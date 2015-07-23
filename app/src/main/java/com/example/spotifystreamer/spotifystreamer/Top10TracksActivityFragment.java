package com.example.spotifystreamer.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class Top10TracksActivityFragment extends Fragment {

    private ParcelableArray selectedTrack;
    private int mPositionID;
    private TracksAdapter mTop10Adapter;
    private String mArtist;
    private String mNameArtist;
    private ArrayList<ParcelableArray> tracksResult;

    public Top10TracksActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("TopTenTracks", tracksResult);
        savedInstanceState.putInt("selectedTrackId", mPositionID);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        if (arguments != null) {
            mArtist = arguments.getString("artistId");
        }

        if (savedInstanceState != null) {
            tracksResult = savedInstanceState.getParcelableArrayList("TopTenTracks");
        } else {
            FetchTop10Task FetchTop10Task = new FetchTop10Task();
            FetchTop10Task.execute(mArtist);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top10_tracks, container, false);


        if (savedInstanceState != null) {
            tracksResult = savedInstanceState.getParcelableArrayList("TopTenTracks");
            mPositionID = savedInstanceState.getInt("selectedTrackId");

        } else {
            tracksResult = new ArrayList<ParcelableArray>();
        }

        /*Intent intent = getActivity().getIntent();
        if (intent != null) {
            mArtist = intent.getStringExtra("artistId");
            mNameArtist = intent.getStringExtra("artist");
        }
        if (savedInstanceState == null) {
            FetchTop10Task top10Tracks = new FetchTop10Task();
            top10Tracks.execute(mArtist);
        } else {
            tracksResult = savedInstanceState.getParcelableArrayList("TopTenTracks");
        }*/

        /*mTop10Adapter = new SimpleAdapter(
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
        };*/

        mTop10Adapter = new TracksAdapter(getActivity(), R.layout.list_item_top10, tracksResult);

        //inflating listview with the adapter
        ListView listView = (ListView) rootView.findViewById(R.id.listview_top10);
        listView.setAdapter(mTop10Adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Lunch parcelable for Now Playing carrying tracks list
                mPositionID = position;
                ParcelableArray selectedTrack = mTop10Adapter.getItem(mPositionID);
                ((NowPlayingActivityFragment.PlayerCallback) getActivity())
                        .onItemSelected(selectedTrack);
                /*Intent intent = new Intent(getActivity(), NowPlayingActivity.class);
                    intent.putExtra("artistSelected", mPositionID);
                    intent.putExtra("Top10Tracks", tracksResult);
                    intent.putExtra("nameArtist", mNameArtist);
                startActivity(intent);*/
            }
        });

        return rootView;
    }

    public ParcelableArray loadNext() {
        ParcelableArray selectedTrack = null;
        if (mPositionID < mTop10Adapter.getCount() - 1) {
            mPositionID = mPositionID + 1;
            selectedTrack = mTop10Adapter.getItem(mPositionID);
        } else {
            selectedTrack = mTop10Adapter.getItem(mPositionID);
            Toast.makeText(getActivity(), "No Next Track found. Click on Previous", Toast.LENGTH_LONG).show();
        }

        return selectedTrack;
    }

    public ParcelableArray loadPrevious() {
        ParcelableArray selectedTrack = null;
        if (mPositionID != 0) {
            mPositionID = mPositionID - 1;
            selectedTrack = mTop10Adapter.getItem(mPositionID);
        } else {
            selectedTrack = mTop10Adapter.getItem(mPositionID);
            Toast.makeText(getActivity(), "No Previous Track found. Click on Next", Toast.LENGTH_LONG).show();
        }
        return selectedTrack;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public class FetchTop10Task extends AsyncTask<String, Void, Tracks> {

        private RetrofitError retrofitError;

        @Override
        protected Tracks doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            //Tracks trackList;
            String id = params[0];

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();
            Map map = new HashMap();
            map.put("country", "US");

            Tracks trackList;
            try {
                trackList = spotifyService.getArtistTopTrack(id, map);
            } catch (RetrofitError e) {
                retrofitError = e;
                Log.e("Exception", String.valueOf(e));
                return null;
            }
            return trackList;
        }

        //Return an array with the top10 tracks, album and imagesURL
        /*private ArrayList getResultFromTrackList(Tracks trackList) {
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
                    trackTable.put("id", trackList.tracks.get(i).id);
                    trackTable.put("href", trackList.tracks.get(i).href);
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
        }*/

        @Override
        protected void onPostExecute(Tracks resultList) {
            if (resultList != null) {
                /*tracksResult.clear();
                tracksResult.addAll(resultList);
                mTop10Adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(getActivity(), "No Track Found.Please Select Another Artist!", Toast.LENGTH_SHORT).show();
            }*/
                if (resultList.tracks.isEmpty()) {
                    Toast.makeText(getActivity(), "Track not found, please refine your search", Toast.LENGTH_LONG).show();
                } else {
                    mTop10Adapter.clear();
                    String ImageUrl = "";
                    for (Track track : resultList.tracks) {
                        if (!track.album.images.isEmpty()) {
                            ImageUrl = track.album.images.get(0).url;
                        }
                        StringBuilder builder = new StringBuilder();
                        for (ArtistSimple artist : track.artists) {
                            if (builder.length() > 0) builder.append(", ");
                            builder.append(artist.name);
                        }
                        ParcelableArray parcelableArray = new ParcelableArray(
                                track.name,
                                track.album.name,
                                builder.toString(),
                                ImageUrl,
                                track.preview_url);
                        mTop10Adapter.add(parcelableArray);
                        mTop10Adapter.notifyDataSetChanged();
                    }
                }
            } else {
                Toast.makeText(getActivity(), "We've gotten an error: " + retrofitError.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
