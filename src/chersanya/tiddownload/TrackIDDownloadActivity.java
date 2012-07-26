package chersanya.tiddownload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import chersanya.tiddownload.LocalEngine.LocalSong;
import de.quist.app.errorreporter.ExceptionReporter;

public class TrackIDDownloadActivity extends Activity {
	private String artist, title;
	public static String searchString;
	private IEngine engine = new VkEngine();
	public static ExceptionReporter exceptionReporter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		CTX.set(this);
		exceptionReporter = ExceptionReporter.register(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Uri uri = getIntent().getData();

		if (uri.getHost().endsWith("amazon.com")) {
			artist = "";
			title = uri.getQueryParameter("field-keywords");
		} else if (uri.getHost().endsWith("trackid.playfon.ru")) {
			artist = uri.getQueryParameter("artist");
			title = uri.getQueryParameter("title");
		} else if (uri.getHost().endsWith("7digital.com")) {
			artist = uri.getQueryParameter("artistName");
			title = uri.getQueryParameter("trackTitle");
		}

		searchString = "";
		if (artist.trim().length() > 0) {
			searchString = artist.trim();
		}
		if (artist.trim().length() > 0 && title.trim().length() > 0) {
			searchString += " - ";
		}
		if (title.trim().length() > 0) {
			searchString += title.trim();
		}

		ListView list = (ListView) findViewById(R.id.online_search_list);
		list.setEmptyView(findViewById(R.id.list_empty));

		list = (ListView) findViewById(R.id.local_search_list);
		list.setEmptyView(findViewById(R.id.list_empty));

		setTitle(String.format(getString(R.string.search_s), searchString));

		doSearch();

		Utils.report("search_start", searchString, uri.getHost());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.search_again);
		menu.add(Menu.NONE, 2, Menu.NONE, R.string.settings);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			doSearch();
			break;
		case 2:
			Intent intent = new Intent(this, Preferences.class);
			startActivityForResult(intent, 0);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void doSearch() {
		final ProgressDialog pd = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.searching));

		AsyncTask<String, Void, Song[]> task = new AsyncTask<String, Void, Song[]>() {

			@Override
			protected Song[] doInBackground(String... params) {
				Song[] songs = engine.search(params[0]);
				Utils.report("search_complete", params[0], String.valueOf(songs.length));
				return songs;
			}

			@Override
			protected void onPostExecute(Song[] songs) {
				SongsAdapter adapter = new SongsAdapter(TrackIDDownloadActivity.this, R.layout.song_item, songs);

				ListView list = (ListView) findViewById(R.id.online_search_list);
				list.setAdapter(adapter);

				try {
					pd.dismiss();
				} catch (IllegalArgumentException e) {
				}
			}
		};
		task.execute(searchString);

		LocalSongsAdapter adapter = new LocalSongsAdapter(this, R.layout.song_item, LocalEngine.search(searchString));
		ListView list = (ListView) findViewById(R.id.local_search_list);
		list.setAdapter(adapter);
	}

	private class LocalSongsAdapter extends ArrayAdapter<LocalSong> {

		private Context context;
		private LocalSong[] songs;

		public LocalSongsAdapter(Context context, int textViewResourceId, LocalSong[] songs) {
			super(context, textViewResourceId, songs);
			this.context = context;
			this.songs = songs;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.song_item, null);
			}
			LocalSong song = songs[position];

			TextView titleTv = (TextView) view.findViewById(R.id.title);
			TextView artistTv = (TextView) view.findViewById(R.id.artist);
			TextView durationTv = (TextView) view.findViewById(R.id.duration);

			titleTv.setText(song.title);
			artistTv.setText(song.artist);
			durationTv.setText(song.getDurationStr());

			view.setTag(song);

			return view;
		}
	}
}