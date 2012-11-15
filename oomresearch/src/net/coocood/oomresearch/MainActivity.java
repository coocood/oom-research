package net.coocood.oomresearch;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Debug;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button addData, addBitmap, removeData, removeBitmap, gc;
	private TextView max, vmHeap, vmAllocated, nativeHeap, nativeAllocated,totalAllocated,
			dataSize, bitmapSize;
	private ArrayList<Integer[]> dataArray = new ArrayList<Integer[]>();
	private ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		max = (TextView) findViewById(R.id.max);
		long maxMemory = Runtime.getRuntime().maxMemory();
		max.setText("VM Max " + (maxMemory / 1024 / 1024) + "MB");
		vmHeap = (TextView) findViewById(R.id.vmHeap);
		vmAllocated = (TextView) findViewById(R.id.vmAllocated);
		nativeHeap = (TextView) findViewById(R.id.nativeHeap);
		nativeAllocated = (TextView) findViewById(R.id.nativeAllocated);
		totalAllocated = (TextView)findViewById(R.id.totalAllocated);
		dataSize = (TextView) findViewById(R.id.dataSize);
		bitmapSize = (TextView) findViewById(R.id.bitmapSize);
		addData = (Button) findViewById(R.id.addData);
		addData.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					dataArray.add(new Integer[512 * 512]);
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
					bitmapArray.add(Bitmap.createBitmap(512, 512,
							Config.ARGB_8888));
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
					dataArray.remove(0);
				}
				updateTextVeiw();
			}
		});
		removeBitmap = (Button) findViewById(R.id.removeBitmap);
		removeBitmap.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (bitmapArray.size() > 0) {
					bitmapArray.remove(0);
					
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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
		dataSize.setText("" + dataArray.size() + ".0 MB");
		bitmapSize.setText("" + bitmapArray.size() + ".0 MB");
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
}
