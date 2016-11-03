package com.example.diaryapp;

import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

public class ModelDiary implements Parcelable{

	public int id;
	public String description;
	public double latitude;
	public double longitude;
	public String imagePath;
	public String city;
	public String address;
	public Date mDate;
	public MoodModel mood;

	public ModelDiary(){
		super();
		this.mDate = new Date();
	}
	
	private ModelDiary(Parcel in){
		super();
		this.id = in.readInt();
		this.description = in.readString();
		this.mDate = new Date(in.readLong());
		this.address = in.readString();
		this.city = in.readString();
		this.imagePath = in.readString();
		this.mood = in.readParcelable(MoodModel.class.getClassLoader());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}
	
	public MoodModel getMood() {
		return mood;
	}

	public void setMood(MoodModel mood) {
		this.mood = mood;
	}
	

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stub
		parcel.writeInt(getId());
		parcel.writeString(getDescription());
		parcel.writeLong(getDate().getTime());
		parcel.writeString(getAddress());
		parcel.writeString(getCity());
		parcel.writeString(getImagePath());
		parcel.writeParcelable(getMood(), flags);
	}
	
	public static final Parcelable.Creator<ModelDiary> CREATOR = new Parcelable.Creator<ModelDiary>() {
		public ModelDiary createFromParcel(Parcel in) {
			return new ModelDiary(in);
		}

		public ModelDiary[] newArray(int size) {
			return new ModelDiary[size];
		}
	};
	
}
