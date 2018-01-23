package services.genii.reactnative.rnsharefile;

import android.app.Activity;
import android.content.Intent;
import android.content.ComponentName;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.os.Environment;
import android.util.Log;
import android.net.Uri;

import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Arrays;
import java.lang.SecurityException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import okio.Okio;
import okio.BufferedSink;
import okio.BufferedSource;

public class RNShareFileModule extends ReactContextBaseJavaModule {

	private static final String TAG = RNShareFileModule.class.getSimpleName();

	private static final String TEXT_PLAIN = "text/plain";
	private static final String TEXT_HTML = "text/html";

	private ReactApplicationContext reactContext;

	public RNShareFileModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
	}

	@Override
	public String getName() {
		return "RNShareFile";
	}

	@Override
	public Map<String, Object> getConstants() {
		final Map<String, Object> constants = new HashMap<>();
		constants.put("TEXT_PLAIN", TEXT_PLAIN);
		constants.put("TEXT_HTML", TEXT_HTML);
		return constants;
	}

	private Intent getSendIntent(String text, String type) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.setType(type);
		sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		return sendIntent;
	}

	@ReactMethod
	public void isAppInstalled(String packageName, final Promise promise) {
		Intent sendIntent = this.reactContext.getPackageManager().getLaunchIntentForPackage(packageName);
		if (sendIntent == null) {
			promise.resolve(false);
			return;
		}

		promise.resolve(true);
	}

	@ReactMethod
	public void installRemoteApp(final String uri, final String saveAs, final Promise promise) {
		final File file = new File(this.reactContext.getExternalFilesDir(null), saveAs);

		final Request request = new Request.Builder().url(uri).build();
		new OkHttpClient()
		.newCall(request)
		.enqueue(new okhttp3.Callback() {
			@Override
			public void onFailure(final Call call, final IOException e) {
				e.printStackTrace();
				promise.resolve(false);
			}

			private void saveFile(final ResponseBody body) throws IOException, FileNotFoundException {
				final BufferedSource source = body.source();
				final BufferedSink sink = Okio.buffer(Okio.sink(file));

				sink.writeAll(source);

				sink.flush();
				sink.close();
				source.close();
			}

			@Override
			public void onResponse(final Call call, final Response response) {
				if(!response.isSuccessful()) {
					promise.resolve(false);
					return;
				}

				try (final ResponseBody body = response.body()) {
					saveFile(body);

					final Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

					reactContext.startActivity(intent);

					promise.resolve(true);
				}
				catch (final Exception e) {
					e.printStackTrace();
					promise.resolve(false);
				}
			}
		});
	}

	@ReactMethod
	public void openApp(String packageName, ReadableMap extras, final Promise promise) {
		Intent sendIntent = this.reactContext.getPackageManager().getLaunchIntentForPackage(packageName);
		if (sendIntent == null) {
			promise.resolve(false);
			return;
		}

		final ReadableMapKeySetIterator it = extras.keySetIterator();
		while(it.hasNextKey()) {
			final String key = it.nextKey();
			final String value = extras.getString(key);
			sendIntent.putExtra(key, value);
		}

		sendIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		this.reactContext.startActivity(sendIntent);
		promise.resolve(true);
	}

	@ReactMethod
	public void share(ReadableMap options) {

		Intent intent = new Intent(Intent.ACTION_SEND);
		String title = options.hasKey("title") ? options.getString("title") : "Share the file";

		if (options.hasKey("subject")) {
			intent.putExtra(Intent.EXTRA_SUBJECT, options.getString("subject"));
		}

		if (options.hasKey("text")) {
			intent.putExtra(Intent.EXTRA_TEXT, options.getString("text"));
		}

		if (options.hasKey("url")) {
			String url = options.getString("url");
			Uri uri = Uri.parse(url);
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			String type = options.hasKey("type") ? options.getString("type") : "*/*";
			intent.setType(type);
		}
		else if (options.hasKey("imageUrl")) {
			Uri uri = Uri.parse(options.getString("imageUrl"));
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			intent.setType("image/*");
		} 
		else if (options.hasKey("videoUrl")) {
			File media = new File(options.getString("videoUrl"));
			Uri uri = Uri.fromFile(media);
			if(!options.hasKey("subject")) {
				intent.putExtra(Intent.EXTRA_SUBJECT,"Untitled_Video");
			}
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			intent.setType("video/*");
		} 
		else {
			intent.setType("text/plain");
		}

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Activity currentActivity = getCurrentActivity();
		if (currentActivity != null) {
			currentActivity.startActivity(Intent.createChooser(intent, title));
		}
	}
}
