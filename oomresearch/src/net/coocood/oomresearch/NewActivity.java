package net.coocood.oomresearch;

public class NewActivity extends MainActivity {
	@Override
	protected void onDestroy() {
		quit = false;
		super.onDestroy();
	}
}
