package top.cokernut.sample.service;

import top.cokernut.sample.R;
import top.cokernut.sample.util.Math;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

@SuppressLint("ClickableViewAccessibility")
public class FloatWindowService extends Service {

	private WindowManager.LayoutParams mParams;
	private WindowManager mWindowManager;
	private ImageView mImgIcon;
	private Point mWindow = new Point();
	private GestureDetector mGestureDetector;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();

		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mWindowManager.getDefaultDisplay().getSize(mWindow);
		} else {
			int w = mWindowManager.getDefaultDisplay().getWidth();
			int h = mWindowManager.getDefaultDisplay().getHeight();
			mWindow.set(w, h);
		}

		mGestureDetector = new GestureDetector(this, new GestListener());

		mImgIcon = new ImageView(this);
		mImgIcon.setImageResource(R.mipmap.img_icon);
		mImgIcon.setOnTouchListener(new View.OnTouchListener() {
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mGestureDetector.onTouchEvent(event);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					initialX = mParams.x;
					initialY = mParams.y;
					initialTouchX = event.getRawX();
					initialTouchY = event.getRawY();
					return true;
				case MotionEvent.ACTION_UP:
					resetPosition();
					return true;
				case MotionEvent.ACTION_MOVE:
					mParams.x = initialX
							+ (int) (event.getRawX() - initialTouchX);
					mParams.y = initialY
							+ (int) (event.getRawY() - initialTouchY);
					mWindowManager.updateViewLayout(mImgIcon, mParams);
					if (mParams.y > mWindow.y * 0.7) {
						mImgIcon.setImageResource(R.mipmap.img_rockets);
					}
					return true;
				}
				return false;
			}
		});
		mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		mParams.gravity = Gravity.TOP | Gravity.LEFT;
		mParams.x = 0;
		mParams.y = 100;

		mWindowManager.addView(mImgIcon, mParams);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mImgIcon != null)
			mWindowManager.removeView(mImgIcon);
	}

	private void resetPosition() {
		int w = mImgIcon.getWidth();
		if (mParams.x == 0 || mParams.x == mWindow.x - w) {

		} else if (mParams.y > mWindow.y * 0.7) {
			moveToTop();
		} else if (mParams.x + w / 2 <= mWindow.x / 2) {
			moveToLeft();
		} else if (mParams.x + w / 2 > mWindow.x / 2) {
			moveToRight();
		}
	}

	private void moveToLeft() {
		final int x = mParams.x;
		new CountDownTimer(500, 5) {
			public void onTick(long t) {
				long step = (500 - t) / 5;
				mParams.x = (int) (double) Math.bounceValue(step, x);
				mWindowManager.updateViewLayout(mImgIcon, mParams);
			}

			public void onFinish() {
				mParams.x = 0;
				mWindowManager.updateViewLayout(mImgIcon, mParams);
			}
		}.start();
	}

	private void moveToRight() {
		final int x = mParams.x;
		new CountDownTimer(500, 5) {
			public void onTick(long t) {
				long step = (500 - t) / 5;
				mParams.x = mWindow.x
						+ (int) (double) Math.bounceValue(step, x)
						- mImgIcon.getWidth();
				mWindowManager.updateViewLayout(mImgIcon, mParams);
			}

			public void onFinish() {
				mParams.x = mWindow.x - mImgIcon.getWidth();
				mWindowManager.updateViewLayout(mImgIcon, mParams);
			}
		}.start();
	}

	private void moveToTop() {
		mImgIcon.setImageResource(R.mipmap.img_rockets);
		final int y = mParams.y;
		mParams.x = mWindow.x / 2;
		new CountDownTimer(500, 5) {
			public void onTick(long t) {
				long step = (500 - t) / 5;
				mParams.y = (int) (double) Math.bounceValue(step, y);
				mWindowManager.updateViewLayout(mImgIcon, mParams);
				if(mParams.y <50){
					mImgIcon.setVisibility(View.GONE);
				}
			}

			public void onFinish() {
				mParams.y = 150;
				mParams.x = 0;
				mWindowManager.updateViewLayout(mImgIcon, mParams);
				mImgIcon.setImageResource(R.mipmap.img_icon);
				mImgIcon.setVisibility(View.VISIBLE);
			}
		}.start();
	}

	private class GestListener implements GestureDetector.OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			return false;
		}

	}

}
