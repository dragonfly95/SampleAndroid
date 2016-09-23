package org.onnuri.ncare.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class ClassData implements Parcelable {

	public String code;
	public String title;
	public GradeData grade;
	
	public ClassData() {
		grade = new GradeData();
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
        dest.writeParcelable(grade, flags);
	}

	private ClassData(Parcel source) {
		code = source.readString();
		title = source.readString();
		grade = source.readParcelable(GradeData.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<ClassData> CREATOR = new Creator<ClassData>() {
		public ClassData createFromParcel(Parcel source) {
        	return new ClassData(source);
		}
        
		public ClassData[] newArray(int size) {
			return new ClassData[size];
		}
	};
	
}
