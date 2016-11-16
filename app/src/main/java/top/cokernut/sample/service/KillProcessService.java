package top.cokernut.sample.service;

import java.util.List;
import top.cokernut.sample.model.RunningAppInfo;
import top.cokernut.sample.util.RunAppUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;

public class KillProcessService extends Service {

	private List<RunningAppInfo> mApps;
	private Context mContext;

	public KillProcessService() {
		super();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mContext = getApplicationContext();
		mApps = new RunAppUtil(mContext).getRunApps();
		new Thread() {
			public void run() {
				for (RunningAppInfo info : mApps) {
					String pkgName = info.pkgName;
						Intent killIntent = new Intent(
								Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						Uri packageURI = Uri.parse("package:" + pkgName + "");
						killIntent.setData(packageURI);
						mContext.startActivity(killIntent);
				}
			};
		}.start();

		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stopSelf();
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
