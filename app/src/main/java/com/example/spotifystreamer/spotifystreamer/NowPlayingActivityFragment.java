package com.example.spotifystreamer.spotifystreamer;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Hashtable;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class NowPlayingActivityFragment extends DialogFragment implements View.OnClickListener {

    public static final String TRACK_INFO_KEY = "selectedTrack";

    //private SimpleAdapter mTrackAdapter;
    private int mArtistSelected;
    private int position;
    private ArrayList tracksResult = new ArrayList<Hashtable<String, Object>>();
    private ParcelableArray trackToPlay;
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
    SeekBar scrubBar;

    private int trackProgress = 0;

    public NowPlayingActivityFragment() {
    }

    public interface PlayerCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        //public void onItemSelected(ParcelableArray selectedTrack);
        public void onItemSelected(ParcelableArray selectedTrack);

        public void onNext();

        public void onPrevious();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        return dialog;
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


        if (savedInstanceState == null) {
            trackToPlay = getArguments().getParcelable(TRACK_INFO_KEY);
        } else {
            trackToPlay = savedInstanceState.getParcelable(TRACK_INFO_KEY);
            trackProgress = savedInstanceState.getInt("Progress");
            scrubBar.setProgress(trackProgress);
        }

        if (!trackToPlay.imageUrl.isEmpty()) {
            Picasso.with(getActivity()).load(trackToPlay.imageUrl).into(artistImage);
        }

        if (!trackToPlay.mArtistName.isEmpty()) {
            artistName.setText(trackToPlay.mArtistName);
        }

        if (!trackToPlay.mAlbum.isEmpty()) {
            albumName.setText(trackToPlay.mAlbum);
        }

        if (!trackToPlay.mTrack.isEmpty()) {
            trackName.setText(trackToPlay.mTrack);
        }

        playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
        previousButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_previous, 0, 0, 0);
        nextButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_next, 0, 0, 0);

        MediaPlayerService.setSong(trackToPlay.previewUrl, trackToPlay.mTrack, trackToPlay.imageUrl);
        getActivity().startService(new Intent("PLAY"));

        return rootView;
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save the searched artist in case of a screen rotation for example.
        savedInstanceState.putInt("Progress", scrubBar.getProgress());
        savedInstanceState.putParcelable(TRACK_INFO_KEY, trackToPlay);

    }

    private void initializeMediaPlayer() {
        if (!trackToPlay.previewUrl.isEmpty()) {
            String url = trackToPlay.previewUrl;
        }
    }

    private void linkScrubBarToMediaPlayer() {

    }

    public void onNext(ParcelableArray mArtistSelected) {

        trackToPlay = mArtistSelected;

        if (!trackToPlay.imageUrl.isEmpty()) {
            Picasso.with(getActivity()).load(trackToPlay.imageUrl).into(artistImage);
        }

        if (!trackToPlay.mArtistName.isEmpty()) {
            artistName.setText(trackToPlay.mArtistName);
        }

        if (!trackToPlay.mAlbum.isEmpty()) {
            albumName.setText(trackToPlay.mAlbum);
        }

        if (!trackToPlay.mTrack.isEmpty()) {
            trackName.setText(trackToPlay.mTrack);
        }

        scrubBar.setProgress(0);

        MediaPlayerService.getInstance().stopService(new Intent(getActivity(), MediaPlayerService.class));
        MediaPlayerService.setSong(trackToPlay.previewUrl, trackToPlay.mTrack, trackToPlay.imageUrl);
        getActivity().startService(new Intent("PLAY"));

    }

    public void onPrevious(ParcelableArray mArtistSelected) {

        trackToPlay = mArtistSelected;

        if (!trackToPlay.imageUrl.isEmpty()) {
            Picasso.with(getActivity()).load(trackToPlay.imageUrl).into(artistImage);
        }

        if (!trackToPlay.mArtistName.isEmpty()) {
            artistName.setText(trackToPlay.mArtistName);
        }

        if (!trackToPlay.mAlbum.isEmpty()) {
            albumName.setText(trackToPlay.mAlbum);
        }

        if (!trackToPlay.mTrack.isEmpty()) {
            trackName.setText(trackToPlay.mTrack);
        }

        scrubBar.setProgress(0);

        MediaPlayerService.getInstance().stopService(new Intent(getActivity(), MediaPlayerService.class));
        MediaPlayerService.setSong(trackToPlay.previewUrl, trackToPlay.mTrack, trackToPlay.imageUrl);
        getActivity().startService(new Intent("PLAY"));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public void stop() {
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
