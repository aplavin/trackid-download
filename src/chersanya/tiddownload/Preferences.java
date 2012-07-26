package chersanya.tiddownload;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preferences extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	public static Uri getDownloadFolder() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CTX.get());
		return Uri.parse("file://" + prefs.getString("download_folder", "/mnt/sdcard/"));
	}

	public static boolean isInternalDownloader() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CTX.get());
		return prefs.getBoolean("use_internal_downloader", false);
	}
}
