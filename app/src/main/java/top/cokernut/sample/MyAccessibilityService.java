package top.cokernut.sample;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import top.cokernut.sample.ui.activity.MainActivity;

@SuppressLint("NewApi")
public class MyAccessibilityService extends AccessibilityService {

	public static int INVOKE_TYPE = 0;
	public static final int TYPE_KILL_APP = 1;
	public static final int TYPE_INSTALL_APP = 2;
	public static final int TYPE_UNINSTALL_APP = 3;
	
	public static void reset() {
		INVOKE_TYPE = 0;
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
    	this.processAccessibilityEnvent(event);
	}
	
	private void processAccessibilityEnvent(AccessibilityEvent event) {
		if (event.getSource() == null) {
			Log.d("test", "the source = null");
		} else {
			if (event.getPackageName().equals("com.android.settings")) {
				Log.d("test", "event = " + event.toString());
				processKillApplication(event);
			}
		}
	}

	private void processKillApplication(AccessibilityEvent event) {
		
		if (event.getSource() != null) {
			if (event.getPackageName().equals("com.android.settings")) {
				List<AccessibilityNodeInfo> stop_nodes = event.getSource()
						.findAccessibilityNodeInfosByText(getResources().getString(R.string.text_settings_close));
				if (stop_nodes!=null && !stop_nodes.isEmpty()) {
					AccessibilityNodeInfo node;
					for(int i=0; i<stop_nodes.size(); i++){
						node = stop_nodes.get(i);
						if (node.getClassName().equals("android.widget.Button")) {
							if(node.isEnabled()){
							   node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
							}
						}
					}
				}

				List<AccessibilityNodeInfo> ok_nodes = event.getSource()
						.findAccessibilityNodeInfosByText(getResources().getString(R.string.text_settings_confirm));
				if (ok_nodes!=null && !ok_nodes.isEmpty()) {
					AccessibilityNodeInfo node;
					for(int i=0; i<ok_nodes.size(); i++){
						node = ok_nodes.get(i);
						if (node.getClassName().equals("android.widget.Button")) {
							node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
							MainActivity.mAtKill = true; 
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
