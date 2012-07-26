package chersanya.tiddownload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SongsAdapter extends ArrayAdapter<Song> {

	private Context context;
	private Song[] songs;

	private View.OnClickListener clickListener = new View.OnClickListener() {

		public void onClick(View v) {
			Song song = (Song) v.getTag();
			new SongDialog(song).show();
		}
	};

	public SongsAdapter(Context context, int textViewResourceId, Song[] songs) {
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
		Song song = songs[position];

		TextView titleTv = (TextView) view.findViewById(R.id.title);
		TextView artistTv = (TextView) view.findViewById(R.id.artist);
		TextView durationTv = (TextView) view.findViewById(R.id.duration);

		titleTv.setText(song.title);
		artistTv.setText(song.artist);
		durationTv.setText(song.getDurationStr());

		view.setTag(song);
		view.setOnClickListener(clickListener);

		return view;
	}
}