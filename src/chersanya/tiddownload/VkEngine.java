package chersanya.tiddownload;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class VkEngine implements IEngine {

	private static class VkApp {
		public String mid;
		public String app_id;
		public String key;

		public VkApp(String mid, String app_id, String key, String s) {
			this.mid = mid;
			this.app_id = app_id;
			this.key = key;
		}

		@Override
		public String toString() {
			return "VkApp [mid=" + mid + ", app_id=" + app_id + ", key=" + key + "]";
		}

	}

	private Random rnd = new Random();
	private ArrayList<VkApp> apps = new ArrayList<VkApp>();
	private String lastAppKey = null;

	public VkEngine() {
		String s = "PLEASE DONT USE THESE CREDENTIALS! OTHERWISE THIS APPLICATION WON'T WORK, AND YOUR TOO!";
		apps.add(new VkApp("76347967", "1850196", "nk0n6I6vjQ", s));
		apps.add(new VkApp("11741544", "2055750", "XTl80Qvudw", s));
		apps.add(new VkApp("16748531", "2055756", "3fYVJO8qSw", s));
		apps.add(new VkApp("3378894", "2058398", "lOUc7ZWg6Q", s));
		apps.add(new VkApp("6868263", "2058415", "SvwfBTC3D7", s));
		apps.add(new VkApp("10424555", "2113895", "Wf8jZ8bXMV", s));
		// apps.add(new VkApp("12219697", "2113901", "t86pUKzc2T", s));
		apps.add(new VkApp("16892548", "2113904", "E99hC80RWk", s));
		apps.add(new VkApp("3204125", "2046774", "ovFsmm2mGS", s));
		apps.add(new VkApp("121919708", "2129681", "XNxCoJ5yFR", s));
		apps.add(new VkApp("121921731", "2129766", "mcIR5up136", s));
		apps.add(new VkApp("121922754", "2130936", "xjuYhmDELJ", s));
		apps.add(new VkApp("121923214", "2130972", "v2Ee6FApwC", s));
		apps.add(new VkApp("122349467", "2132057", "9daYYtcvEE", s));
		apps.add(new VkApp("122362866", "2132062", "9jIG8129o1", s));
		apps.add(new VkApp("122885173", "2134302", "JchJWUHpZ4", s));
		apps.add(new VkApp("122886170", "2134844", "kYAPKoxrhS", s));
		apps.add(new VkApp("123239579", "2135631", "UO0nmnBED0", s));
		apps.add(new VkApp("123239948", "2135962", "1PInxD2hBI", s));
		apps.add(new VkApp("123488580", "2136890", "Yrp2JsAvht", s));
		apps.add(new VkApp("123453669", "2137822", "G7Jidd6cIr", s));
		apps.add(new VkApp("123659133", "2138395", "IXf7OCBtnc", s));
		apps.add(new VkApp("124317574", "2141486", "hFTvvqsgbH", s));
		apps.add(new VkApp("124318920", "2141522", "20VxzrOywn", s));
		apps.add(new VkApp("124603710", "2142928", "ZdzSgpdxsn", s));
		apps.add(new VkApp("124604055", "2143315", "2clC3tUIyJ", s));
		apps.add(new VkApp("125030963", "2145048", "fIoxTHCDh0", s));
		apps.add(new VkApp("125029952", "2145131", "uFFu4jZlsr", s));
		apps.add(new VkApp("125289646", "2146268", "1GxFMTAQjS", s));
		apps.add(new VkApp("125290176", "2146597", "G0E5smGbi8", s));
		apps.add(new VkApp("125590444", "2149718", "2UUZsMOu3E", s));
		apps.add(new VkApp("125936279", "2155324", "AlmsaIX2jN", s));
		apps.add(new VkApp("127555795", "2156561", "6ngGsbZbum", s));
		apps.add(new VkApp("127984338", "2157895", "KeSXhQKBdc", s));
		apps.add(new VkApp("127985242", "2157910", "xkkeTLOx1N", s));
		apps.add(new VkApp("128555769", "2159570", "iZAxsVtehz", s));
		apps.add(new VkApp("128557685", "2159751", "BWAZX48DXV", s));
		apps.add(new VkApp("129062795", "2160974", "iqPwf8LacN", s));
		apps.add(new VkApp("129100216", "2165078", "JQxl06dzno", s));
		apps.add(new VkApp("130969813", "2165082", "pAczuowAdA", s));
	}

	private VkApp getRndApp() {
		int i;
		do {
			i = rnd.nextInt(apps.size());
		} while (apps.get(i).key.equals(lastAppKey));

		lastAppKey = apps.get(i).key;
		return apps.get(i);
	}

	private JSONObject request(Map<String, String> query) throws IOException, JSONException {
		VkApp app = getRndApp();
		query.put("api_id", app.app_id);

		String md5 = app.mid, queryStr = "";

		for (String key : new TreeSet<String>(query.keySet())) {
			String value = query.get(key);

			md5 += key + "=" + value;
			queryStr += key + "=" + URLEncoder.encode(value) + "&";
		}

		md5 += app.key;

		Log.i("query", queryStr);
		Log.i("md5source", md5);

		md5 = Utils.getMD5(md5);
		Log.i("md5", md5);

		String str = Utils.load("http://api.vkontakte.ru/api.php?" + queryStr + "sig=" + md5);
		Log.i("VK response", str);
		Log.i("AppInstance", app.toString());
		return new JSONObject(str);
	}

	public Song[] search(final String query) {
		try {
			Map<String, String> queryMap = new HashMap<String, String>() {
				{
					put("test_mode", "1");
					put("count", "100");
					put("sort", "2");
					put("format", "JSON");
					put("method", "audio.search");
					put("q", query.trim());
				}
			};

			JSONObject jobj = request(queryMap);
			if (jobj.has("error"))
				return new Song[] { new Song(CTX.get().getString(R.string.bad_response), jobj.getString("error"), null, -1) };

			JSONArray jarr = jobj.getJSONArray("response");

			ArrayList<Song> songs = new ArrayList<Song>();
			for (int i = 1; i < jarr.length(); i++) {
				try {
					JSONObject item = jarr.getJSONObject(i).getJSONObject("audio");
					Song song = new Song(item.optString("artist"), item.optString("title"), Uri.parse(item.getString("url")), item.getInt("duration"));
					songs.add(song);
				} catch (JSONException e) {
					Log.w("JSON", e);
				}
			}

			Song[] songsArr = new Song[songs.size()];
			songs.toArray(songsArr);
			return songsArr;
		} catch (IOException e) {
			return new Song[] { new Song(e.toString(), CTX.get().getString(R.string.no_internet), null, -1) };
		} catch (JSONException e) {
			Log.w("JSON", e);
			return new Song[] { new Song(e.toString(), CTX.get().getString(R.string.bad_response), null, -1) };
		}
	}
}
