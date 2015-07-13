package com.example.spotifystreamer.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableArray implements Parcelable {

    public String mTrack;
    public String mAlbum;
    public String mArtistName;
    public String imageUrl;
    public String previewUrl;

    public ParcelableArray(String mTrack, String mAlbum, String mArtistName, String imageUrl, String previewUrl) {
        this.mTrack = mTrack;
        this.mAlbum = mAlbum;
        this.mArtistName = mArtistName;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
    }

    private ParcelableArray(Parcel in) {
        mTrack = in.readString();
        mAlbum = in.readString();
        mArtistName = in.readString();
        imageUrl = in.readString();
        previewUrl = in.readString();
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mTrack);
        parcel.writeString(this.mAlbum);
        parcel.writeString(this.mArtistName);
        parcel.writeString(this.imageUrl);
        parcel.writeString(this.previewUrl);
    }

    public static final Creator<ParcelableArray> CREATOR = new Creator<ParcelableArray>() {
        @Override
        public ParcelableArray createFromParcel(Parcel parcel) { return new ParcelableArray(parcel); }

        @Override
        public ParcelableArray[] newArray(int i) { return new ParcelableArray[i]; }
    };
}

