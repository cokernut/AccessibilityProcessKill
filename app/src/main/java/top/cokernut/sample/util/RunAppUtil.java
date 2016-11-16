package top.cokernut.sample.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import top.cokernut.sample.model.AndroidAppProcess;
import top.cokernut.sample.model.RunningAppInfo;

public class RunAppUtil {
	private Context mContext;
	private PackageManager mPackageManager;

	public RunAppUtil(Context mContext) {
		super();
		this.mContext = mContext;
		mPackageManager = mContext.getPackageManager();
	}

	public List<RunningAppInfo> getRunApps() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			return getRunAppsOld();
		} else {
			return getRunAppsNew();
		}
	}

	//API20及以下
	public List<RunningAppInfo> getRunAppsOld() {
		// 查询所有已经安装的应用程序
		List<ApplicationInfo> listAppcations = mPackageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(mPackageManager));

		Map<String, ActivityManager.RunningAppProcessInfo> pkgProcessMap = new HashMap<String, ActivityManager.RunningAppProcessInfo>();
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		// 获得系统里所有正在运行的进程
		List<ActivityManager.RunningAppProcessInfo> appProcessList = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
			String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的所有应用程序包
			for (String pkgName : pkgNameList) {
				pkgProcessMap.put(pkgName, appProcess);
			}
		}
		// 保存所有正在运行的应用程序信息
		List<RunningAppInfo> runningAppInfos = new ArrayList<>();

		for (ApplicationInfo app : listAppcations) {
			// 判断是否为系统程序 否则该包名存在 则构造一个RunningAppInfo对象
			if (!(((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) || ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0))) {
				continue;
			} else if (pkgProcessMap.containsKey(app.packageName)) {
				if (app.packageName.equals(mContext.getPackageName())) {
					continue;
				}
				int pid = pkgProcessMap.get(app.packageName).pid;
				String processName = pkgProcessMap.get(app.packageName).processName;
				runningAppInfos.add(getAppInfo(app, pid, processName));
			}
		}
		return runningAppInfos;
	}

	//API21及以上
	public List<RunningAppInfo> getRunAppsNew() {
		// 获得系统里所有正在运行的进程
		List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
		// 保存所有正在运行的应用程序信息
		List<RunningAppInfo> runningAppInfos = new ArrayList<>();

		for (int i = 0; i < processes.size(); i++) {
			// 判断是否为系统程序 否则该包名存在 则构造一个RunningAppInfo对象
			ApplicationInfo app = null;
			try {
				app = processes.get(i).getPackageInfo(mContext, i).applicationInfo;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			if (app != null) {
				if (!(((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) || ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0))) {
					continue;
				} else if (app.packageName.equals(mContext.getPackageName())) {
					continue;
				} else {
					int pid = processes.get(i).pid;
					String processName = processes.get(i).name;
					runningAppInfos.add(getAppInfo(app, pid, processName));
				}
			}
		}
		return runningAppInfos;
	}

	private RunningAppInfo getAppInfo(ApplicationInfo app, int pid, String processName) {
		RunningAppInfo appInfo = new RunningAppInfo();
		appInfo.appLabel = (String) app.loadLabel(mPackageManager);
		appInfo.appIcon = app.loadIcon(mPackageManager);
		appInfo.pkgName = app.packageName;
		appInfo.pid = pid;
		appInfo.processName = processName;
		return appInfo;
	}
}
