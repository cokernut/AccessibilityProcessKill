package top.cokernut.sample.util;

import java.util.List;

import top.cokernut.sample.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class AppUtil {

	public static String getPackageNameByUid(PackageManager pm, int uid) {
		String[] pkgNames = pm.getPackagesForUid(uid);
		if (pkgNames != null) {
			return pkgNames[0];
		}
		return null;
	}

	public static long getRunningAppProcessMemory(ActivityManager am,
			RunningAppProcessInfo processInfo) {
		Debug.MemoryInfo memoryInfo = am
				.getProcessMemoryInfo(new int[] { processInfo.pid })[0];
		return memoryInfo.getTotalPss() + memoryInfo.getTotalPrivateDirty();
	}

	public static void goToAppDetailSettings(Context ctx, String packageName) {
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.fromParts("package", packageName, null));
		ctx.startActivity(intent);
	}

	public static boolean isSystemApp(PackageManager pm, String packageName) {
		try {
			PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
			return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Drawable getIcon(Context ctx, String packageName) {
		PackageManager pm = ctx.getPackageManager();
		try {
			return pm.getApplicationIcon(packageName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ctx.getResources().getDrawable(R.mipmap.ic_launcher);
	}

	public static String getLabel(Context ctx, String packageName) {
		try {
			PackageManager pm = ctx.getPackageManager();
			ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
			CharSequence label = appInfo.loadLabel(pm);
			return label != null ? label.toString() : appInfo.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getVersion(PackageManager pm, String packageName) {
		try {
			PackageInfo pkgInfo = pm.getPackageInfo(packageName, 0);
			return pkgInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setReceiverEnabled(Context ctx, Class<?> receiverClazz,
			boolean enabled) {
		ctx.getPackageManager().setComponentEnabledSetting(
				new ComponentName(ctx, receiverClazz),
				enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
						: PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}
	public static boolean isRunningBackground(Context context,
			String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo rapi : infos) {
			if (rapi.processName.equals(packageName)) {
				boolean isBackground = rapi.importance == IMPORTANCE_FOREGROUND
						&& rapi.importance != IMPORTANCE_VISIBLE;
				// boolean isLockedState =
				// keyguardManager.inKeyguardRestrictedInputMode();
				if (isBackground)
					return true;
			}
			return true;
		}
		return false;
	}

	public static boolean isRunningForeground(Context ctx, String packageName) {
		String topActivityClassName = getTopActivityName(ctx);
		System.out.println("packageName=" + packageName
				+ ",topActivityClassName=" + topActivityClassName);
		if (packageName != null && topActivityClassName != null
				&& topActivityClassName.startsWith(packageName)) {
			Log.d("main", "---> isRunningForeGround");
			return true;
		} else {
			Log.d("main", "---> isRunningBackGround");
			return false;
		}
	}

	public static String getTopActivityName(Context context) {
		String topActivityClassName = null;
		ActivityManager activityManager = (ActivityManager) (context
				.getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningTaskInfo> runningTaskInfos = activityManager
				.getRunningTasks(1);
		if (runningTaskInfos != null) {
			ComponentName f = runningTaskInfos.get(0).topActivity;
			topActivityClassName = f.getClassName();
		}
		return topActivityClassName;
	}
	
	
	public static void killApp(String pkgName,Context ctx){
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(pkgName); 
	}
	
	public static boolean isRunning(Context context,String packageName){
	    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
	    for(RunningAppProcessInfo rapi : infos){
	        if(rapi.processName.equals(packageName)){
	        	boolean isBackground = rapi.importance != IMPORTANCE_FOREGROUND
						&& rapi.importance != IMPORTANCE_VISIBLE;
	        	if(isBackground)
	            return true;
	        }
	        }
	    return false;
	}
}
