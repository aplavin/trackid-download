package chersanya.tiddownload;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Utils {

	public static String load(String url) throws IOException {
		URL address = new URL(url);
		URLConnection conn = address.openConnection();
		InputStream is = conn.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayBuffer baf = new ByteArrayBuffer(64);
		int current = 0;
		while ((current = bis.read()) != -1) {
			baf.append((byte) current);
		}
		return new String(baf.toByteArray());
	}

	public static String getMD5(String s) {
		try {
			return Utils.ByteToHexString(MessageDigest.getInstance("MD5").digest(s.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			Log.wtf("MD5", e);
			return null;
		}
	}

	public static String ByteToHexString(byte bytes[]) {
		StringBuffer sb = new StringBuffer(2 * bytes.length);
		for (byte b : bytes) {
			if ((0xff & b) < 16) {
				sb.append("0");
			}
			sb.append(Long.toString(0xff & b, 16));
		}
		return sb.toString();
	}

	public static void report(final String action, final String song, final String data) {
		new Thread(new Runnable() {

			public void run() {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://chersanya.appspot.com/receive_trackiddownloader");

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

					TelephonyManager telephonyManager = (TelephonyManager) CTX.get().getSystemService(Context.TELEPHONY_SERVICE);
					String id = telephonyManager.getDeviceId();
					if (id == null) {
						id = "n/a";
					}

					try {
						String version = String.valueOf(CTX.get().getPackageManager().getPackageInfo(CTX.get().getPackageName(), 0).versionCode);
						nameValuePairs.add(new BasicNameValuePair("version", version));
					} catch (NameNotFoundException e) {
						nameValuePairs.add(new BasicNameValuePair("version", "n/a"));
					}

					nameValuePairs.add(new BasicNameValuePair("id", id));
					nameValuePairs.add(new BasicNameValuePair("action", action));
					nameValuePairs.add(new BasicNameValuePair("song", song));
					nameValuePairs.add(new BasicNameValuePair("data", data));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					httpclient.execute(httppost);
				} catch (IOException e) {
					TrackIDDownloadActivity.exceptionReporter.reportException(Thread.currentThread(), e);
				}
			}
		}).start();
	}
}
