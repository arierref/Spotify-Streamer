package com.example.spotifystreamer.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TracksAdapter extends ArrayAdapter<ParcelableArray> {


    static class ViewHolder {
        @InjectView(R.id.textViewTrack)
        public TextView name;
        @InjectView(R.id.imageViewArtist)
        public ImageView image;
        @InjectView(R.id.textViewAlbum)
        public TextView album;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_top10, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ParcelableArray track = getItem(position);

        viewHolder.name.setText(track.mTrack);
        viewHolder.album.setText(track.mAlbum);

        if (!track.imageUrl.isEmpty()) {
            Picasso.with(parent.getContext()).load(track.imageUrl).into(viewHolder.image);
        } else {
            Picasso.with(parent.getContext()).load(R.drawable.no_image).into(viewHolder.image);
        }

        return convertView;
    }

    public TracksAdapter(Context context, int resource, List<ParcelableArray> tracks) {
        super(context, resource, tracks);
    }
}
