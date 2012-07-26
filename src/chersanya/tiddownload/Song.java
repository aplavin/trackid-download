package chersanya.tiddownload;

import java.net.URL;
import java.text.DecimalFormat;

import android.net.Uri;

public class Song {
	public String artist, title;
	public Uri uri;
	public int duration;
	private int size = -1;

	public Song(String artist, String title, Uri uri, int duration) {
		this.artist = artist;
		this.title = title;
		this.uri = uri;
		this.duration = duration;
	}

	public String getDurationStr() {
		int min = duration / 60;
		int sec = duration % 60;
		return min + ":" + (sec < 10 ? "0" : "") + sec;
	}

	public int getSize() {
		if (size != -1)
			return size;
		try {
			return (size = new URL(uri.toString()).openConnection().getContentLength());
		} catch (Exception e) {
			return -1;
		}
	}

	public String getSizeStr() {
		int size = getSize();
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public int getBitrate() {
		return getSize() * 8 / duration;
	}

	public String getBitrateStr() {
		int bitrate = getBitrate();
		if (bitrate <= 0)
			return "0";
		final String[] units = new String[] { "bps", "kbps", "mbps" };
		int digitGroups = (int) (Math.log10(bitrate) / Math.log10(1024));
		return new DecimalFormat("#,##0").format(bitrate / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
}