package chersanya.tiddownload;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SongDialog {

	private class SizeLoadTask extends AsyncTask<Song, Void, Song> {
		private View layout;

		public SizeLoadTask(View layout) {
			this.layout = layout;
		}

		@Override
		protected Song doInBackground(Song... params) {
			Song song = params[0];
			song.getSize();
			return song;
		}

		@Override
		protected void onPostExecute(Song result) {
			TextView infoTv = (TextView) layout.findViewById(R.id.sd_info);
			infoTv.setText(String.format("%s / %s (≈%s)", result.getSizeStr(), result.getDurationStr(), result.getBitrateStr()));
		}
	}

	private class DownloadTask extends AsyncTask<Void, Integer, Void> {
		private String url, destination;

		public DownloadTask(String url, String destination) {
			this.url = url;
			this.destination = destination;
		}

		@Override
		protected Void doInBackground(Void... voids) {
			try {
				URL url = new URL(this.url);
				URLConnection connection = url.openConnection();
				connection.connect();
				// this will be useful so that you can show a typical 0-100%
				// progress bar
				int fileLength = connection.getContentLength();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream());
				Log.i("DEST", destination);
				OutputStream output = new FileOutputStream(destination);

				byte data[] = new byte[1 << 12];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					output.write(data, 0, count);
					total += count;
					publishProgress((int) (total * 100 / fileLength));
				}

				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			progressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			Toast.makeText(CTX.get(), String.format(CTX.get().getString(R.string.downloaded_s), Uri.parse(destination).getPath()), Toast.LENGTH_SHORT).show();
		}
	}

	private ProgressDialog progressDialog;
	private Song song;

	public SongDialog(Song song) {
		this.song = song;

		progressDialog = new ProgressDialog(CTX.get());
		progressDialog.setMessage(CTX.get().getString(R.string.downloading));
		progressDialog.setIndeterminate(false);
		progressDialog.setMax(100);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}

	public void show() {
		LayoutInflater inflater = (LayoutInflater) CTX.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.song_details_dialog, (ViewGroup) CTX.get().findViewById(R.id.song_details_root));

		TextView titleTv = (TextView) layout.findViewById(R.id.sd_title);
		titleTv.setText(song.title);
		TextView artistTv = (TextView) layout.findViewById(R.id.sd_artist);
		artistTv.setText(song.artist);
		TextView infoTv = (TextView) layout.findViewById(R.id.sd_info);
		infoTv.setText(R.string.info_loading);

		new SizeLoadTask(layout).execute(song);

		AlertDialog.Builder builder = new AlertDialog.Builder(CTX.get());
		builder.setView(layout).setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// dialog to choose filename format
				final String[] filenames = new String[] { TrackIDDownloadActivity.searchString + ".mp3", song.artist + " - " + song.title + ".mp3" };

				AlertDialog.Builder builder = new AlertDialog.Builder(CTX.get());
				builder.setTitle(R.string.select_filename).setItems(filenames, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Utils.report("download", TrackIDDownloadActivity.searchString, String.valueOf(Preferences.isInternalDownloader()));

						// now really download
						Uri dest = Uri.withAppendedPath(Preferences.getDownloadFolder(), filenames[which]);

						if (Preferences.isInternalDownloader()) {
							try {
								DownloadTask dt = new DownloadTask(song.uri.toString(), dest.toString());
								dt.execute();
							} catch (NullPointerException e) {
								TrackIDDownloadActivity.exceptionReporter.reportException(Thread.currentThread(), e, "dest: " + dest + "; song.uri: " + song.uri.toString());
							}
						} else {
							Request r = new Request(song.uri);
							r.setDestinationUri(dest);
							DownloadManager dm = (DownloadManager) CTX.get().getSystemService(Context.DOWNLOAD_SERVICE);
							dm.enqueue(r);
						}
					}
				});

				AlertDialog d = builder.create();
				d.show();
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog dialog = builder.create();

		dialog.show();
	}
}
