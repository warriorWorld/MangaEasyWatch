package com.warrior.hangsu.administrator.mangaeasywatch.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * 本地存储工具
 * @author Yunlongx Luo
 * 
 */
public class SharedPreferencesUtils {

	/** SharedPreferences */
	private static SharedPreferences mSharedPreferences;

	
	public static void setSharedPreferencesData(Context mContext, String mKey,
			String mValue) {
		if (null == mSharedPreferences) {
			mSharedPreferences = mContext.getSharedPreferences(
					"zhongxin", Context.MODE_PRIVATE);
		}
		mSharedPreferences.edit().putString(mKey, mValue).commit();
	}

	public static void setSharedPreferencesData(Context mContext,String mKey,int mValue){
		if (null == mSharedPreferences) {
			mSharedPreferences = mContext.getSharedPreferences(
					"zhongxin", Context.MODE_PRIVATE);
		}
		mSharedPreferences.edit().putInt(mKey, mValue).commit();
	}
	
	
	public static String getSharedPreferencesData(Context mContext, String mKey) {
		if (null == mSharedPreferences) {
			mSharedPreferences = mContext.getSharedPreferences(
					"zhongxin", Context.MODE_PRIVATE);
		}
		return mSharedPreferences.getString(mKey, "");
	}
	
	public static Integer getIntSharedPreferencesData(Context mContext, String mKey) {
		if (null == mSharedPreferences) {
			mSharedPreferences = mContext.getSharedPreferences(
					"zhongxin", Context.MODE_PRIVATE);
		}
		return mSharedPreferences.getInt(mKey, 0);
	}

}
