package com.example.spotifystreamer.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Hashtable;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private SimpleAdapter mArtistAdapter;
    private ArrayList artistsResult = new ArrayList<Hashtable<String, Object>>();

    private ArrayList<ParcelableArray> artistArrayList;

    public MainActivityFragment() {
    }

    public interface Callback {
        public void onItemSelected(String id, String name);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the searched artist in case of a screen rotation for example.
        savedInstanceState.putStringArrayList("artistList", artistsResult);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (null != savedInstanceState) {
            artistsResult = savedInstanceState.getStringArrayList("artistList");
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //inflating listview with the adapter
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        //listView.setAdapter(mArtistAdapter);

        EditText editText = (EditText) rootView.findViewById(R.id.searchArtist);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                String artista = textView.getText().toString();
                if (artista != null) {
                    //call AsyncTask to load the artist
                    FetchArtistTask artistTask = new FetchArtistTask();
                    artistTask.execute(artista);
                    //Log.e("Nome do Artista:", artista);
                } else {
                    return false;
                }
                return true;
            }
        });

        mArtistAdapter = new SimpleAdapter(
                getActivity(),
                artistsResult,
                R.layout.list_item_artist,
                new String[]{"name", "image"},
                new int[]{R.id.textViewArtist, R.id.imageViewArtist}) {
            @Override
            public void setViewImage(ImageView imageView, String urlImage) {
                if (urlImage.isEmpty()) {
                    Picasso.with(getActivity()).load(R.drawable.no_image).fit().into(imageView);
                } else {
                    Picasso.with(getActivity()).load(urlImage).fit().into(imageView);
                }
            }
        };
        listView.setAdapter(mArtistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Hashtable<String, Object> selectedArtist = (Hashtable<String, Object>) mArtistAdapter.getItem(position);
                //Log.e("Artista selecionado", String.valueOf(selectedArtist));
                String selectedArtistId = (String) selectedArtist.get("id");
                String selectedArtistName = (String) selectedArtist.get("name");
                ((Callback) getActivity()).onItemSelected(selectedArtistId, selectedArtistName);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public class FetchArtistTask extends AsyncTask<String, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(String... params) {

            ArtistsPager artistList;

            String artista = params[0];
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();

            try {
                artistList = spotifyService.searchArtists(artista);
            } catch (Exception e) {
                return null;
            }

            int numOfArtists = artistList.artists.items.size();

            ArrayList<Hashtable<String, Object>> artistsList = new ArrayList<Hashtable<String, Object>>();

            for (int i = 0; i < numOfArtists; i++) {
                Hashtable<String, Object> artistsTable = new Hashtable<String, Object>();
                artistsTable.put("id", artistList.artists.items.get(i).id);
                artistsTable.put("name", artistList.artists.items.get(i).name);

                if (artistList.artists.items.get(i).images.isEmpty()) {
                    artistsTable.put("image", "");
                } else {
                    artistsTable.put("image", artistList.artists.items.get(i).images.get(0).url);
                }
                artistsList.add(artistsTable);
            }
            //Log.e("Lista de artistas", String.valueOf(artistsList));
            return artistsList;
        }

        @Override
        protected void onPostExecute(ArrayList resultList) {
            if (resultList != null) {
                artistsResult.clear();
                artistsResult.addAll(resultList);
                mArtistAdapter.notifyDataSetChanged();
                if (resultList.size() == 0) {
                    Toast.makeText(getActivity(), "No artist found, please search again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Please Type The Artist's Name or check your network connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
