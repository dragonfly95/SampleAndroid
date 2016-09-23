package org.onnuri.ncare.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentData implements Parcelable {

	public String name;
	public String birth;
	public String user_cd;
	public String dlb;
	public String soon;
	public String pass_yn;
	public String pass_day;
	public String commcode;
	public String user_id;
	public String leadername;
	public String wdiv;
	public String ban_seq;
	public String attend_yn;
	public String simbang_yn;
	public String moimsum;
	public String seq;
	public String sau;
	
	public ClassData classInfo;

	
	public StudentData() {
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(birth);
		dest.writeString(user_cd);
		dest.writeString(dlb);
		dest.writeString(soon);
		dest.writeString(pass_yn);
		dest.writeString(pass_day);
		dest.writeString(commcode);
		dest.writeString(user_id);
		dest.writeString(leadername);
		dest.writeString(wdiv);
		dest.writeString(ban_seq);
		dest.writeString(attend_yn);
		dest.writeString(simbang_yn);
		dest.writeString(moimsum);
		dest.writeString(seq);
		dest.writeString(sau);
        dest.writeParcelable(classInfo, flags);
	}

	private StudentData(Parcel source) {
		name      = source.readString();
		birth     = source.readString();
		user_cd   = source.readString();
		dlb       = source.readString();
		soon      = source.readString();
		pass_yn   = source.readString();
		pass_day  = source.readString();
		commcode  = source.readString();
		user_id   = source.readString();
		leadername = source.readString();
		wdiv      = source.readString();
		ban_seq   = source.readString();
		attend_yn = source.readString();
		simbang_yn = source.readString();
		moimsum   = source.readString();
		seq       = source.readString();
		sau       = source.readString();
		classInfo = source.readParcelable(ClassData.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<StudentData> CREATOR = new Creator<StudentData>() {
		public StudentData createFromParcel(Parcel source) {
        	return new StudentData(source);
		}
        
		public StudentData[] newArray(int size) {
			return new StudentData[size];
		}
	};
	
}
