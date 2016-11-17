package top.cokernut.sample;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.TextView;

import top.cokernut.sample.ui.activity.MainActivity;

@SuppressLint("NewApi")
public class MyAccessibilityService extends AccessibilityService {

	public int code = CLOSE;
	public static final int CLOSE = 0;
	public static final int CONFIRM = 1;

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
    	this.processAccessibilityEnvent(event);
	}

	/**
	 * 页面变化回调事件
	 * @param event event.getEventType() 当前事件的类型;
	 *              event.getClassName() 当前类的名称;
	 *              event.getSource() 当前页面中的节点信息；
	 *              event.getPackageName() 事件源所在的包名
	 */
	public void processAccessibilityEnvent(AccessibilityEvent event) {
		// 事件页面节点信息不为空
		if (event.getSource() != null) {
			// 判断事件页面所在的包名，这里是系统设置
			if (event.getPackageName().equals("com.android.settings")) {
				Log.d("test", "event = " + event.toString());
				switch (code) {
					case CLOSE:
						click(event, getResources().getString(R.string.text_settings_close), Button.class.getName());
						code = CONFIRM;
						break;
					case CONFIRM:
						click(event, getResources().getString(R.string.text_settings_confirm), Button.class.getName());
						code = CLOSE;
						break;
					default:
						break;
				}
			}
		} else {
			Log.d("test", "the source = null");
		}
	}

	/**
	 * 模拟点击
	 * @param event 事件
	 * @param text 按钮文字
	 * @param widgetType 按钮类型，如android.widget.Button，android.widget.TextView
	 */
	private void click(AccessibilityEvent event, String text, String widgetType) {
		// 事件页面节点信息不为空
		if (event.getSource() != null) {
			// 搜索所有符合条件（text）的节点
			List<AccessibilityNodeInfo> stop_nodes = event.getSource().findAccessibilityNodeInfosByText(text);
			// 遍历节点
			if (stop_nodes != null && !stop_nodes.isEmpty()) {
				AccessibilityNodeInfo node;
				for (int i = 0; i < stop_nodes.size(); i++) {
					node = stop_nodes.get(i);
					// 判断按钮类型
					if (node.getClassName().equals(widgetType)) {
						// 可用则模拟点击
						if (node.isEnabled()) {
							node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
							if (text.equals(getResources().getString(R.string.text_settings_confirm))) {
								MainActivity.mAtKill = true;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onInterrupt() {
		
	}

}
