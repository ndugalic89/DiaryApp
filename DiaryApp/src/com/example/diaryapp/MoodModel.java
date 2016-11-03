package com.example.diaryapp;
import android.os.Parcel;
import android.os.Parcelable;

public class MoodModel implements Parcelable {

	public int id;
	public String mood;

	public MoodModel() {
		super();
	}

	public MoodModel(String mood) {
		this.mood = mood;
	}
	
	public MoodModel(Parcel in){
		super();
		this.id = in.readInt();
		this.mood = in.readString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMood() {
		return mood;
	}

	public void setMood(String mood) {
		this.mood = mood;
	}

	@Override
	public String toString() {
		return mood;
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
		parcel.writeString(getMood());
	}
	
	public static final Parcelable.Creator<MoodModel> CREATOR = new Parcelable.Creator<MoodModel>() {
		public MoodModel createFromParcel(Parcel in) {
			return new MoodModel(in);
		}

		public MoodModel[] newArray(int size) {
			return new MoodModel[size];
		}
	};
}
