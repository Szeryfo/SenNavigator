package com.example.sennavigator;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class DataList implements Parcelable {

    private String nazwa;

    private LatLng pozycja;

    protected DataList(Parcel in) {
        nazwa = in.readString();
        pozycja = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<DataList> CREATOR = new Creator<DataList>() {
        @Override
        public DataList createFromParcel(Parcel in) {
            return new DataList(in);
        }

        @Override
        public DataList[] newArray(int size) {
            return new DataList[size];
        }
    };

    public DataList(String nazwa, LatLng pozycja) {
        this.nazwa = nazwa;
        this.pozycja = pozycja;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public LatLng getPozycja() {
        return pozycja;
    }

    public void setPozycja(LatLng pozycja) {
        this.pozycja = pozycja;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nazwa);
        dest.writeParcelable(pozycja, flags);
    }
}
