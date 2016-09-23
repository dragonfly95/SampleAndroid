package org.onnuri.ncare.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class DivisionData implements Parcelable {

	public String code;
	public String title;
	public CampusData campus;

	
	public DivisionData() {
		campus = new CampusData();
	}

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
        dest.writeParcelable(campus, flags);
	}

	private DivisionData(Parcel source) {
		code = source.readString();
		title = source.readString();
		campus = source.readParcelable(CampusData.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<DivisionData> CREATOR = new Creator<DivisionData>() {
		public DivisionData createFromParcel(Parcel source) {
        	return new DivisionData(source);
		}
        
		public DivisionData[] newArray(int size) {
			return new DivisionData[size];
		}
	};
	
}
