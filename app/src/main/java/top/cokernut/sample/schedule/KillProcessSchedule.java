package top.cokernut.sample.schedule;

import top.cokernut.sample.Const;
import top.cokernut.sample.service.KillProcessService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class KillProcessSchedule {

	private Context mContext;
	private AlarmManager mAlarmManager;

	public KillProcessSchedule(Context ctx) {
		mContext = ctx;
		mAlarmManager = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
	}

	public void schedule() {
		Intent intent = new Intent(mContext, KillProcessService.class);
		intent.setAction(Const.INTENT_ACTION_KILL_PROCESS);
		PendingIntent operation = PendingIntent.getService(mContext, 0, intent,
											PendingIntent.FLAG_UPDATE_CURRENT);
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 1000 * 60, 1000 * 60 * 5, operation);
	}
}
