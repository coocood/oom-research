package net.coocood.oomresearch;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static ArrayList<IntegerArray> dataArray = new ArrayList<IntegerArray>();
	private static ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
	private static int runningTask = 0;
	
	private Button addData, addBitmap, removeData, removeBitmap, gc;
	private TextView max, vmHeap, vmAllocated, nativeHeap, nativeAllocated,totalAllocated,
			dataSize, bitmapSize, tasks;
	protected boolean quit = false;
	private ArrayList<IntegerArray> preloadData = new ArrayList<IntegerArray>();
	private Timer timer;
	private TimerTask timerTask;
	private PreferenceHelper helper;
	private boolean firstResume = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper = new PreferenceHelper(this);
		initViews();
	}
	private void initViews(){
		setContentView(R.layout.activity_main);
		long maxMemory = Runtime.getRuntime().maxMemory();
		max = (TextView) findViewById(R.id.max);
		max.setText("VM Max " + (maxMemory / 1024 / 1024) + "MB");
		vmHeap = (TextView) findViewById(R.id.vmHeap);
		vmAllocated = (TextView) findViewById(R.id.vmAllocated);
		nativeHeap = (TextView) findViewById(R.id.nativeHeap);
		nativeAllocated = (TextView) findViewById(R.id.nativeAllocated);
		totalAllocated = (TextView)findViewById(R.id.totalAllocated);
		dataSize = (TextView) findViewById(R.id.dataSize);
		bitmapSize = (TextView) findViewById(R.id.bitmapSize);
		tasks = (TextView)findViewById(R.id.tasks);
		addData = (Button) findViewById(R.id.addData);
		addData.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					dataArray.add(new IntegerArray(new int[helper.getClickSize() * 256]));
				} catch (OutOfMemoryError e) {
					showErrorToast(e);
				}
				updateTextVeiw();
			}
		});
		addBitmap = (Button) findViewById(R.id.addBitmap);
		addBitmap.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					Bitmap bitmap = Bitmap.createBitmap(helper.getClickSize(), 256, Config.ARGB_8888);
					bitmapArray.add(bitmap);
				} catch (OutOfMemoryError e) {
					showErrorToast(e);
				}
				updateTextVeiw();
			}
		});
		removeData = (Button) findViewById(R.id.removeData);
		removeData.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (dataArray.size() > 0) {
					dataArray.remove(dataArray.size()-1);
				}
				updateTextVeiw();
			}
		});
		removeBitmap = (Button) findViewById(R.id.removeBitmap);
		removeBitmap.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (bitmapArray.size() > 0) {
					bitmapArray.remove(bitmapArray.size()-1);
					
				}
				updateTextVeiw();
			}
		});
		gc = (Button) findViewById(R.id.gc);
		gc.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				System.gc();
				updateTextVeiw();
			}
		});
		
		updateTextVeiw();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		timer = new Timer();
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					public void run() {
						updateTextVeiw();
					}
				});
			}
		};
		timer.schedule(timerTask, 1000, 1000);
		if(firstResume){
			firstResume = false;
			try{
				for(int i = 0; i < 4 ; i ++ ){
					preloadData.add(new IntegerArray(new int[helper.getActivityPreloadDataSize()*64]));
				}
			}catch (OutOfMemoryError e) {
				showErrorToast(e);
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);		
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.start_new){
			if(helper.isRecreateAfterRotation()){
				startActivity(new Intent(this,RecreatingActivity.class));
			}else{
				startActivity(new Intent(this,NewActivity.class));
			}
			
		}else if(id == R.id.menu_settings){
			startActivity(new Intent(this,SettingActivity.class));
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void updateTextVeiw() {
		Runtime rt = Runtime.getRuntime();
		long vmAlloc =  rt.totalMemory() - rt.freeMemory();
		long nativeAlloc = Debug.getNativeHeapAllocatedSize();
		vmHeap.setText(formatMemoeryText(rt.totalMemory()));
		vmAllocated.setText(formatMemoeryText(vmAlloc));
		nativeAllocated.setText(formatMemoeryText(nativeAlloc));
		totalAllocated.setText(formatMemoeryText(nativeAlloc+vmAlloc));
		nativeHeap.setText(formatMemoeryText(Debug.getNativeHeapSize()));
		int dataSizeNumber = 0;
		for(IntegerArray intArray:dataArray){
			dataSizeNumber+=intArray.array.length;
		}
		dataSize.setText(formatMemoeryText(dataSizeNumber*4));
		int bitmapSizeNumber = 0;
		for(Bitmap bitmap:bitmapArray){
			bitmapSizeNumber += bitmap.getRowBytes() * bitmap.getHeight();
		}
		bitmapSize.setText(formatMemoeryText(bitmapSizeNumber));
		tasks.setText(""+runningTask);
	}

	private String formatMemoeryText(long memory) {
		float memoryInMB = memory * 1f / 1024 / 1024;
		return String.format("%.1f MB", memoryInMB);
	}

	private void showErrorToast(OutOfMemoryError error) {
		String message = error.getMessage();
		if(message==null||message.equals("null")){
			message = "";
		}
		Toast.makeText(this, "OutOfMemoryError:" + message,
				Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initViews();
	}
	
	@SuppressLint({ "NewApi"})
	@Override
	protected void onPause() {
		super.onPause();
		timer.cancel();
		timer.purge();
		if(isFinishing()){
			//We have to kill the process to get back to the initial VM Heap size, because the Heap size will never shrink.
			//And we should kill process onDestroy, or there would be no transition animation.
			quit = true;
		}else{
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
				@Override
				protected void onPreExecute() {
					runningTask++;
				}
				protected Void doInBackground(Void... params) {
					SystemClock.sleep(helper.getTaskRunningTime());
					return null;
				};
				@Override
				protected void onPostExecute(Void result) {
					runningTask--;
				}
			};
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				task.execute();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(quit){
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
	
	static class IntegerArray{
		public IntegerArray(int[] array) {
			this.array = array;
		}
		int[] array;
	}
}
