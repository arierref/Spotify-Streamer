package com.example.spotifystreamer.spotifystreamer;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class NowPlayingActivityFragment extends DialogFragment implements View.OnClickListener {

    private int mFileDuration;

    private static final String LOG_TAG = NowPlayingActivityFragment.class.getSimpleName();

    public static final String TRACK_INFO_KEY = "selectedTrack";

    private ParcelableArray trackToPlay;

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

    @InjectView(R.id.timeStart)
    TextView timeStart;

    @InjectView(R.id.timeEnd)
    TextView timeEnd;

    //private int trackProgress = 0;

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

    public void stop() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(MediaPlayerService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updateUI(Intent intent) {
        int mPlayerTrackPosition = intent.getIntExtra("mPlayerTrackPosition", 0);
        int mPlayerTrackDuration = intent.getIntExtra("mPlayerTrackDuration", 0);

        //Log.e("mPlayerTrackDuration", String.valueOf(mPlayerTrackDuration));

        scrubBar.setProgress(mPlayerTrackPosition / 300);
        String musicDuration = String.valueOf((mPlayerTrackDuration - mPlayerTrackPosition)/1000);
        String maxDuration = String.valueOf((mPlayerTrackDuration)/1000);
        if ((mPlayerTrackDuration - mPlayerTrackPosition)/1000 < 10){
            timeStart.setText("0:0" + musicDuration);
        }else if ((mPlayerTrackDuration - mPlayerTrackPosition)/1000 < 500) {
            timeStart.setText("0:" + musicDuration);
        }else{
            timeStart.setText("0:00");
        }
        if ((mPlayerTrackDuration)/1000 < 999) {
            timeEnd.setText("0:" + maxDuration);
        }else{
            timeEnd.setText("0:00");
        }
        //Log.e("mPlayerTrackDuration", String.valueOf(musicDuration));

        if (MediaPlayerService.getInstance().isPlaying()) {
            playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);
        } else {
            playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_play, 0, 0, 0);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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

        //if (MediaPlayerService.getInstance().isPlaying()) {
            //MediaPlayerService.getInstance().stopService(new Intent(getActivity(), MediaPlayerService.class));
        //}

        playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_pause, 0, 0, 0);
        previousButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_previous, 0, 0, 0);
        nextButton.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_media_next, 0, 0, 0);

        MediaPlayerService.setSong(trackToPlay.previewUrl, trackToPlay.mTrack, trackToPlay.imageUrl);
        getActivity().startService(new Intent("PLAY"));

        scrubBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MediaPlayerService.getInstance().seekMusicTo(300 * progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

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
        savedInstanceState.putParcelable(TRACK_INFO_KEY, trackToPlay);
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
