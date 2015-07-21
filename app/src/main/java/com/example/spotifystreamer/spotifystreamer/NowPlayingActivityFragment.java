package com.example.spotifystreamer.spotifystreamer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class NowPlayingActivityFragment extends Fragment implements View.OnClickListener {

    public static final String TRACK_INFO_KEY = "selectedTrack";

    //private SimpleAdapter mTrackAdapter;
    private int mArtistSelected;
    private int position;
    private ArrayList tracksResult = new ArrayList<Hashtable<String, Object>>();
    private String trackToPlay;
    private String mArtistName;
    private String[] id;
    private String[] track;
    private String[] album;
    private String[] href;
    private String[] image;
    MediaPlayer mediaPlayer;

    @InjectView(R.id.artistImage)
    ImageView artistImage;

    @InjectView(R.id.playButton)
    Button playButton;

    @InjectView(R.id.nextButton)
    Button nextButton;

    @InjectView(R.id.previousButton)
    Button previousButton;

    @InjectView(R.id.artistName)
    TextView artistName;

    @InjectView(R.id.albumName)
    TextView albumName;

    @InjectView(R.id.trackName)
    TextView trackName;

    @InjectView(R.id.seekBar)
    SeekBar seekBar;

    private int trackProgress = 0;

    public NowPlayingActivityFragment() {
    }

    public interface PlayerCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        //public void onItemSelected(ParcelableArray selectedTrack);
        public void onItemSelected(int selectedTrack);

        public void onNext();

        public void onPrevious();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the searched artist in case of a screen rotation for example.
        savedInstanceState.putInt("artistSelected", mArtistSelected);
        savedInstanceState.putString("nameArtist", mArtistName);
        savedInstanceState.putStringArrayList("Top10Tracks", tracksResult);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initializeMediaPlayer() {

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mArtistSelected = intent.getIntExtra("artistSelected", position);
            mArtistName = intent.getStringExtra("nameArtist");
            tracksResult = intent.getStringArrayListExtra("Top10Tracks");
        }
        trackToPlay = tracksResult.get(mArtistSelected).toString();
        id = trackToPlay.split(",")[0].split("=");
        track = trackToPlay.split(",")[1].split("=");
        image = trackToPlay.split(",")[2].split("=");
        href = trackToPlay.split(",")[3].split("=");
        album = trackToPlay.split(",")[4].split("=");

        if (href[1] != null) {
            String url = href[1];
            mediaPlayer = new MediaPlayer();
            mediaPlayer.seekTo(300 * trackProgress);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(url);
                linkScrubBarToMediaPlayer();
                mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    private void linkScrubBarToMediaPlayer() {

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (mp.isPlaying() && seekBar != null) {
                    seekBar.setProgress(mp.getCurrentPosition() / 300);
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onCompletion(MediaPlayer mp) {
                playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
                seekBar.setProgress(0);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);

        ButterKnife.inject(this, rootView);

        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);

        /*Intent intent = getActivity().getIntent();
        if (intent != null) {
            mArtistSelected = intent.getIntExtra("artistSelected", position);
            mArtistName = intent.getStringExtra("nameArtist");
            tracksResult = intent.getStringArrayListExtra("Top10Tracks");
        }*/
        if (savedInstanceState != null) {
            mArtistSelected = savedInstanceState.getInt("artistSelected");
            //mArtistName = intent.getStringExtra("nameArtist");
            tracksResult = savedInstanceState.getStringArrayList("Top10Tracks");
            int positionMusic = mArtistSelected;
            trackToPlay = tracksResult.get(mArtistSelected).toString();
            //trackProgress = savedInstanceState.getInt("Progress");
            //seekBar.setProgress(trackProgress);
        } else{
            int Test = getArguments().getInt("selectedTrack");
            //trackToPlay = getArguments().getInt("selectedTrack");
            Log.e("trackToPlay", String.valueOf(Test));

            int positionMusic = mArtistSelected;
            trackToPlay = null;
            trackToPlay = tracksResult.get(mArtistSelected).toString();
            id = trackToPlay.split(",")[0].split("=");
            track = trackToPlay.split(",")[1].split("=");
            image = trackToPlay.split(",")[2].split("=");
            href = trackToPlay.split(",")[3].split("=");
            album = trackToPlay.split(",")[4].split("=");

            //trackToPlay = (Hashtable) tracksResult.get(positionMusic);
        }

        if (image != null) {
            Picasso.with(getActivity()).load(image[1]).into(artistImage);
        }

        if (mArtistName != null) {
            artistName.setText(mArtistName);
        }

        if (album != null) {
            albumName.setText(album[1]);
        }

        if (track != null) {
            trackName.setText(track[1]);
        }

        if (mediaPlayer == null) {
            initializeMediaPlayer();
        } else {
            if (href[1] != null)
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(href[1]);
                    linkScrubBarToMediaPlayer();
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (mediaPlayer.isPlaying()) {
                playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);
            } else {
                playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
            }
            linkScrubBarToMediaPlayer();
        }

        //MediaPlayerService.setSong(href[1], track[1], image[1]);
        //getActivity().startService(new Intent("PLAY"));

        playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);
        previousButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_previous, 0, 0, 0);
        nextButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_next, 0, 0, 0);

        return rootView;
    }

    public void onNext(int mArtistSelected) {

        int positionMusic = mArtistSelected;
        trackToPlay = tracksResult.get(mArtistSelected).toString();
        id = trackToPlay.split(",")[0].split("=");
        track = trackToPlay.split(",")[1].split("=");
        image = trackToPlay.split(",")[2].split("=");
        href = trackToPlay.split(",")[3].split("=");
        album = trackToPlay.split(",")[4].split("=");

        if (image != null) {
            Picasso.with(getActivity()).load(image[1]).into(artistImage);
        }

        if (mArtistName != null) {
            artistName.setText(mArtistName);
        }

        if (album != null) {
            albumName.setText(album[1]);
        }

        if (track != null) {
            trackName.setText(track[1]);
        }

        seekBar.setProgress(0);

        MediaPlayerService.getInstance().stopService(new Intent(getActivity(), MediaPlayerService.class));
        MediaPlayerService.setSong(href[1], track[1], image[1]);
        getActivity().startService(new Intent("PLAY"));



    }

    public void onPrevious(int mArtistSelected) {

        int positionMusic = mArtistSelected;
        trackToPlay = tracksResult.get(mArtistSelected).toString();
        id = trackToPlay.split(",")[0].split("=");
        track = trackToPlay.split(",")[1].split("=");
        image = trackToPlay.split(",")[2].split("=");
        href = trackToPlay.split(",")[3].split("=");
        album = trackToPlay.split(",")[4].split("=");

        if (image != null) {
            Picasso.with(getActivity()).load(image[1]).into(artistImage);
        }

        if (mArtistName != null) {
            artistName.setText(mArtistName);
        }

        if (album != null) {
            albumName.setText(album[1]);
        }

        if (track != null) {
            trackName.setText(track[1]);
        }

        seekBar.setProgress(0);

        MediaPlayerService.getInstance().stopService(new Intent(getActivity(), MediaPlayerService.class));
        MediaPlayerService.setSong(href[1], track[1], image[1]);
        getActivity().startService(new Intent("PLAY"));

    }

    public void stop() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void play(View w) {
        playButton = (Button) w;
        if (MediaPlayerService.getInstance().isPlaying()) {
            playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
            MediaPlayerService.getInstance().pauseMusic();
        } else {
            playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);
            MediaPlayerService.getInstance().startMusic();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButton:
                play(v);
                break;
            case R.id.previousButton:
                ((PlayerCallback)getActivity()).onPrevious();
                break;
            case R.id.nextButton:
                ((PlayerCallback)getActivity()).onNext();
                break;
            default:
                break;
        }
    }
}
