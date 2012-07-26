package chersanya.tiddownload;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

public class LocalEngine {
	public static class LocalSong {
		String artist, title, display_name;
		Uri uri;
		int size, duration;
		Date date_added;

		@Override
		public String toString() {
			return display_name;
		}

		public String getDurationStr() {
			int min = duration / 60;
			int sec = duration % 60;
			return min + ":" + (sec < 10 ? "0" : "") + sec;
		}
	}

	public static LocalSong[] search(String query) {
		List<LocalSong> allSongs = getAll();

		List<Pair<Integer, LocalSong>> withScores = new ArrayList<Pair<Integer, LocalSong>>();

		Set<String> queryNgrams = new HashSet<String>(ngrams(3, query));
		for (LocalSong song : allSongs) {
			List<String> songNgrams = ngrams(3, song.artist + " a a a " + song.title);
			int score = common(queryNgrams, songNgrams);
			if (score <= 0) {
				continue;
			}
			withScores.add(Pair.create(score, song));
		}

		Collections.sort(withScores, new Comparator<Pair<Integer, LocalSong>>() {

			public int compare(Pair<Integer, LocalSong> lhs, Pair<Integer, LocalSong> rhs) {
				return -lhs.first.compareTo(rhs.first);
			}
		});

		int resLen = Math.min(withScores.size(), 3);
		LocalSong[] res = new LocalSong[resLen];
		for (int i = 0; i < resLen; i++) {
			res[i] = withScores.get(i).second;
		}
		return res;
	}

	private static List<LocalSong> getAll() {
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

		String[] projection = { MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED };

		Cursor cursor = CTX.get().managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);

		List<LocalSong> songs = new ArrayList<LocalSong>();

		while (cursor.moveToNext()) {
			LocalSong song = new LocalSong();

			song.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			song.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			song.display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			song.uri = Uri.fromFile(new File(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))));
			song.size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
			song.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) / 1000;
			song.date_added = new Date(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)));

			songs.add(song);
		}

		return songs;
	}

	private static List<String> ngrams(int n, String str) {
		List<String> ngrams = new ArrayList<String>();
		Matcher matcher = Pattern.compile("[a-zà-ÿ]+").matcher(str.toLowerCase());

		List<String> words = new ArrayList<String>();
		while (matcher.find()) {
			words.add(matcher.group());
		}

		for (int i = 0; i < words.size() - n + 1; i++) {
			Log.i("NGRAM", concat(words, i, i + n));
			ngrams.add(concat(words, i, i + n));
		}
		return ngrams;
	}

	private static String concat(List<String> words, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < end; i++) {
			sb.append((i > start ? " " : "") + words.get(i));
		}
		return sb.toString();
	}

	private static int common(Set<String> queryNgrams, List<String> ngrams) {
		int cnt = 0;
		for (String s : ngrams) {
			if (queryNgrams.contains(s)) {
				cnt++;
			}
		}
		return cnt;
	}
}
