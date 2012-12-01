package net.coocood.oomresearch;


import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingActivity extends Activity {
	private PreferenceHelper helper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper = new PreferenceHelper(this);
		setContentView(R.layout.setting);
		CheckBox recreate = (CheckBox)findViewById(R.id.recreate);
		recreate.setChecked(helper.isRecreateAfterRotation());
		recreate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				helper.setRecreateAfterRotation(isChecked);
			}
		});
		SeekBar clickSize = (SeekBar)findViewById(R.id.clickSize);
		clickSize.setProgress(helper.getClickSize());
		clickSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				helper.setClickSize(progress);
			}
		});
		SeekBar activitySize = (SeekBar)findViewById(R.id.acitivySize);
		activitySize.setProgress(helper.getActivityPreloadDataSize());
		activitySize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				helper.setActivityPreloadDataSize(progress);
			}
		});
		SeekBar runningTime = (SeekBar)findViewById(R.id.runningTime);
		runningTime.setProgress(helper.getTaskRunningTime());
		runningTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				helper.setTaskRunningTime(progress);
			}
		});
	}
}
