package org.onnuri.ncare.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class GradeData implements Parcelable {

	public String code;
	public String title;
	public DivisionData division;

	
	public GradeData() {
		division = new DivisionData();
	}

	public GradeData(String code, String title) {
		this.code = code;
		this.title = title;
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
        dest.writeParcelable(division, flags);
	}

	private GradeData(Parcel source) {
		code = source.readString();
		title = source.readString();
		division = source.readParcelable(DivisionData.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<GradeData> CREATOR = new Creator<GradeData>() {
		public GradeData createFromParcel(Parcel source) {
        	return new GradeData(source);
		}
        
		public GradeData[] newArray(int size) {
			return new GradeData[size];
		}
	};
	
}
