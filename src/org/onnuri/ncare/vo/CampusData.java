package org.onnuri.ncare.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class CampusData implements Parcelable {

	public String code;
	public String title;
	
	public CampusData() {}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(code);
        dest.writeString(title);
	}

	private CampusData(Parcel source) {
		code = source.readString();
		title = source.readString();
	}
	
	public static final Parcelable.Creator<CampusData> CREATOR = new Creator<CampusData>() {
		public CampusData createFromParcel(Parcel source) {
        	return new CampusData(source);
		}
        
		public CampusData[] newArray(int size) {
			return new CampusData[size];
		}
	};
	
}
