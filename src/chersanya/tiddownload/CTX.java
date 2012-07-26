package chersanya.tiddownload;

import android.app.Activity;

public class CTX {
	private static Activity context;

	public static Activity get() {
		return context;
	}

	public static void set(Activity context) {
		CTX.context = context;
	}
}
