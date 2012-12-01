package net.coocood.oomresearch;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {
	private static final String RECREATE2 = "recreate";
	private static final String CLICK_SIZE = "clickSize";
	private static final String ACTIVITY_SIZE = "acitivitySize";
	private static final String TASK_RUNNING_TIME = "taskRunningTime";
	private SharedPreferences preferences;
	public PreferenceHelper(Context context) {
		preferences = context.getSharedPreferences("preferences", 0);
	}
	public int getTaskRunningTime(){
		return preferences.getInt(TASK_RUNNING_TIME, 20000);
	}
	public void setTaskRunningTime(int runningTime){
		preferences.edit().putInt(TASK_RUNNING_TIME, runningTime).commit();
	}
	public int getActivityPreloadDataSize(){
		return preferences.getInt(ACTIVITY_SIZE, 2048);
	}
	public void setActivityPreloadDataSize(int size){
		preferences.edit().putInt(ACTIVITY_SIZE, size).commit();
	}
	public int getClickSize(){
		return preferences.getInt(CLICK_SIZE, 2048);
	}
	public void setClickSize(int size){
		if(size<100){
			size = 100;
		}
		preferences.edit().putInt(CLICK_SIZE, size).commit();
	}
	public boolean isRecreateAfterRotation(){
		return preferences.getBoolean(RECREATE2, false);
	}
	public void setRecreateAfterRotation(boolean recreate){
		preferences.edit().putBoolean(RECREATE2, recreate).commit();
	}
}
