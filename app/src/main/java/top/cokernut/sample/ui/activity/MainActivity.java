package top.cokernut.sample.ui.activity;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import top.cokernut.sample.R;
import top.cokernut.sample.model.RunningAppInfo;
import top.cokernut.sample.util.RunAppUtil;

public class MainActivity extends Activity {
	private ListView mAutoAppLv;
	private final static int KILL_NO 	= 0; //不停止
	private final static int KILL_ONE 	= 1; //停止选中
	private final static int KILL_ALL 	= 2; //停止全部
	private int mkill_flag 	= 0;
	private int mkill_num 	= 0;
	public static boolean mAtKill = false; //是否在停止进程
	private Button mBtnOneKey, mBtnBack;
	private ImageView mKillAppIconIv;
	private AutoAppAdapter mAdapter;
	private List<RunningAppInfo> mApps; //正在运行的App
	private View mTopWindow;	//用于隐藏操作的Window

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		setListener();
	}

	/**
	 * 判断自己的应用的MyAccessibilityService是否在运行
	 * @return
     */
	private boolean serviceIsRunning() {
		ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(Short.MAX_VALUE);
		for (RunningServiceInfo info : infos) {
			if (info.service.getClassName().equals(getPackageName() + ".MyAccessibilityService")) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onStart() {
		setAdapter();
		super.onStart();
	}

	class MyThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				if (mAtKill) {
					if (mkill_flag == KILL_ONE) {
						clearTopWindow(mTopWindow);
						Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
						startActivity(intent2);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								setAdapter();
							}
						});
						mkill_flag = KILL_NO;
						mAtKill = false;
						break;
					} else if (mkill_flag == KILL_ALL) {
						mkill_num++;
						if (mkill_num == mApps.size()) {
							clearTopWindow(mTopWindow);
							Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
							startActivity(intent2);
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									setAdapter();
								}
							});
							mkill_num = 0;
							mkill_flag = KILL_NO;
							mAtKill = false;
							break;
						} else {
							final Drawable icon = mApps.get(mkill_num).appIcon;
							String pkgName = mApps.get(mkill_num).pkgName;
							mkill_flag = KILL_ALL;
							Intent killIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							Uri packageURI = Uri.parse("package:" + pkgName);
							killIntent.setData(packageURI);
							startActivity(killIntent);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mKillAppIconIv.setImageDrawable(icon);
								}
							});
							mAtKill = false;
						}
					}
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void startThread() {
		MyThread thread = new MyThread();
		new Thread(thread).start();
	}

	private void setListener() {
		mBtnOneKey.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (serviceIsRunning()) {
					if (mApps.size() == 0) {
						Toast.makeText(getApplicationContext(), "已经没有在运行的程序了！",
								Toast.LENGTH_SHORT).show();
						return;
					}
					startThread();
					showTopWindow(mTopWindow);
					Drawable icon = mApps.get(mkill_num).appIcon;
					String pkgName = mApps.get(mkill_num).pkgName;
					mkill_flag = KILL_ALL;
					Intent killIntent = new Intent(
							Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					Uri packageURI = Uri.parse("package:" + pkgName);
					killIntent.setData(packageURI);
					startActivity(killIntent);
					mKillAppIconIv.setImageDrawable(icon);
				} else {
					startAccessibilityService();
				}
			}
		});

		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearTopWindow(mTopWindow);
				startActivity(new Intent(MainActivity.this, MainActivity.class));
			}
		});

	}

	private void setAdapter() {
		mApps = new RunAppUtil(getApplicationContext()).getRunApps();
		mAdapter = new AutoAppAdapter(this, mApps);
		mAutoAppLv.setAdapter(mAdapter);
	}

	private void initView() {
		mAutoAppLv = (ListView) findViewById(R.id.listView1);
		mBtnOneKey = (Button) findViewById(R.id.btn_one_key);
		mTopWindow = View.inflate(getApplicationContext(),
				R.layout.popupwindow, null);
		mBtnBack = (Button) mTopWindow.findViewById(R.id.unfoldButton);
		mTopWindow = View.inflate(getApplicationContext(),
				R.layout.popupwindow, null);
		mBtnBack = (Button) mTopWindow.findViewById(R.id.unfoldButton);
		mTopWindow = View.inflate(getApplicationContext(),
				R.layout.popupwindow, null);
		mBtnBack = (Button) mTopWindow.findViewById(R.id.unfoldButton);
		mKillAppIconIv = (ImageView) mTopWindow
				.findViewById(R.id.iv_kill_app_icon);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showTopWindow(View view) {
		WindowManager windowManager = (WindowManager) getApplicationContext()
				.getSystemService(WINDOW_SERVICE);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();

		LayoutParams params = new LayoutParams();
		params.type = LayoutParams.TYPE_SYSTEM_ALERT;
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		params.width = screenWidth;
		params.height = screenHeight;
		params.x = 0;
		params.y = 0;
		windowManager.addView(view, params);
	}

	public void clearTopWindow(View view) {
		if (view != null && view.isShown()) {
			WindowManager windowManager = (WindowManager) getApplicationContext()
					.getSystemService(WINDOW_SERVICE);
			windowManager.removeView(view);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			clearTopWindow(mTopWindow);
			Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
			startActivity(intent2);
			setAdapter();
		}
		return super.onKeyDown(keyCode, event);
	}

	private class AutoAppAdapter extends BaseAdapter {

		private Context mContext;

		private List<RunningAppInfo> mList;

		public AutoAppAdapter(Context context, List<RunningAppInfo> mList) {
			this.mContext = context;
			this.mList = mList;
		}

		@Override
		public int getCount() {
			return mList == null ? 0 : mList.size();
		}

		@Override
		public RunningAppInfo getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(mContext, R.layout.item_auto_app,null);
				holder.mIcon = (ImageView) convertView.findViewById(R.id.iv_item_auto_app_icon);
				holder.mName = (TextView) convertView.findViewById(R.id.tv_item_auto_app_lable);
				holder.mPkgName = (TextView) convertView.findViewById(R.id.tv_item_auto_app_desc);
				holder.mKill = (Button) convertView.findViewById(R.id.btn_item_auto_app_kill);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			RunningAppInfo info = mList.get(position);
			final Drawable icon = info.appIcon;
			holder.mIcon.setImageDrawable(info.appIcon);
			holder.mName.setText(info.appLabel);
			final String pkgName = info.pkgName;
			holder.mPkgName.setText(pkgName);
			holder.mKill.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (serviceIsRunning()) {
						startThread();
						showTopWindow(mTopWindow);
						Intent killIntent = new Intent(
								Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						Uri packageURI = Uri.parse("package:" + pkgName);
						killIntent.setData(packageURI);
						mContext.startActivity(killIntent);
						mKillAppIconIv.setImageDrawable(icon);
						mkill_flag = KILL_ONE;
					} else {
						startAccessibilityService();
					}
				};
			});

			return convertView;
		}

		private class ViewHolder {
			ImageView mIcon;
			TextView mName, mPkgName;
			Button mKill;
		}
	}

	private void startAccessibilityService() {
		new AlertDialog.Builder(this)
				.setTitle("开启辅助功能")
				.setIcon(R.mipmap.ic_launcher)
				.setMessage("使用此项功能需要您开启辅助功能")
				.setPositiveButton("立即开启",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent killIntent = new Intent(
										Settings.ACTION_ACCESSIBILITY_SETTINGS);
								startActivity(killIntent);
							}
						}).create().show();
	}

}
