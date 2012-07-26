package chersanya.tiddownload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.net.Uri;
import android.util.Log;

public class MusicSharaEngine implements IEngine {

	public Song[] search(String query) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Log.wtf("search", e);
		}

		Document dom = null;
		try {
			dom = builder.parse("http://api.musicshara.ru/index.php?mod=vk&q=" + query);
		} catch (SAXException e) {
			return new Song[] { new Song(CTX.get().getString(R.string.bad_response), e.toString(), null, -1) };
		} catch (IOException e) {
			return new Song[] { new Song(CTX.get().getString(R.string.no_internet), e.toString(), null, -1) };
		}

		Element root = dom.getDocumentElement();

		ArrayList<Song> songs = new ArrayList<Song>();

		NodeList items = root.getElementsByTagName("link");
		for (int i = 0; i < items.getLength(); i++) {
			HashMap<String, String> data = new HashMap<String, String>();

			NodeList properties = items.item(i).getChildNodes();
			for (int j = 0; j < properties.getLength(); j++) {
				Node property = properties.item(j);
				data.put(property.getNodeName(), property.getTextContent());
			}

			String[] ds = data.get("duration").split(":");
			int duration;
			try {
				duration = Integer.parseInt(ds[0]) * 60 + Integer.parseInt(ds[1]);
			} catch (NumberFormatException e) {
				duration = -1;
			}
			Song song = new Song(data.get("artist"), data.get("s_name"), Uri.parse(data.get("slink")), duration);
			songs.add(song);

			Log.i("S", song.artist + " - " + song.title);
		}

		Song[] res = new Song[songs.size()];
		songs.toArray(res);
		return res;
	}
}
