package com.hanxin.electrictool.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanxin.electrictool.R;
import com.hanxin.electrictool.R.id;
import com.hanxin.electrictool.R.layout;
import com.hanxin.electrictool.db.DBOpenHelper;
import com.hanxin.electrictool.db.UserDBOpenHelper;
import com.hanxin.electrictool.entity.UserData;
import com.hanxin.electrictool.utils.Constants;
import com.hanxin.electrictool.utils.DataProcess;
import com.hanxin.electrictool.utils.HandleMsg;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends Activity implements OnClickListener, Callback{

	private Button loginBt;
	private EditText passwordEt,userNameEt;
	private TextView dateTx,timeTx, deviceIdTx, deviceTypeTx;

	public static DataProcess dataProcess;
	private CheckBox  notice_cBox;
	private UserDBOpenHelper userDBOpenHelper ;
	
	public static String userNick = null;
	public static String electric_type = "±1100kV直流架空输电线路验电系统";
	public static String device_id = "未知";
	public static String []phone = {"",""};
	public static String []phoneUsers = {"",""};
	
	// 展示所有下拉选项的listView
	private ListView listView = null;
	// 用来处理选中或者删除下拉项消息
	private Handler handler;
	// 是否初始化完成标志
	private boolean flag = false;
	
	// PopupWindow对象
		private PopupWindow selectPopupWindow = null;
		// 自定义Adapter
		private OptionsAdapter optionsAdapter = null;
		// 下拉框选项数据源
		private List<Map<String, Object>> datas;
		// 下拉框依附附件
		private LinearLayout parent;
		// 下拉框依附组件宽度，也将作为下拉框的宽度
		private int pwidth;
		
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		initView();
		if (getActionBar() != null) {
			getActionBar().hide();
		}		
		userDBOpenHelper = new UserDBOpenHelper(getApplicationContext());
		loadAdminConfigure();
		((TextView)findViewById(R.id.tx2)).setText(electric_type);
		((TextView)findViewById(R.id.textView9)).setText(getString(R.string.company_phone)+phone[0]);
	}

	private void initView() {
	
		passwordEt = (EditText) findViewById(R.id.inputPassword);
		userNameEt = (EditText) findViewById(R.id.inputUserName);
		notice_cBox= (CheckBox) findViewById(R.id.checkNotice);
		timeTx = (TextView) findViewById(R.id.showTime);
		dateTx = (TextView) findViewById(R.id.showDate);
		deviceTypeTx = (TextView) findViewById(R.id.showDeviceType);
		deviceIdTx = (TextView) findViewById(R.id.showDeviceId);
		loginBt = (Button) findViewById(R.id.login);
		loginBt.setOnClickListener(this);
		findViewById(R.id.down_user).setOnClickListener(this);
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日/HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		String currentDate =str.substring(0, str.indexOf("/"));
		String currentTime =str.substring(str.indexOf("/")+1);
		dateTx.setText(currentDate);
		timeTx.setText(currentTime);		
		new TimeThread().start(); // 启动新的线程
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent1 = new Intent();
		switch (v.getId()) {
		case R.id.login:
			String userName = userNameEt.getText().toString();
			String passWord = passwordEt.getText().toString();
			if (TextUtils.isEmpty(userName)) {
				Toast.makeText(getApplicationContext(), "请输入用户名",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (TextUtils.isEmpty(passWord)) {
				Toast.makeText(getApplicationContext(), "请输入密码",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!notice_cBox.isChecked()) {
				Toast.makeText(getApplicationContext(), "请勾选注意事项",
						Toast.LENGTH_SHORT).show();
				return;
			}
			//初始超级用户密码
				if (userName.equals("admin")&&passWord.equals("admin")) {
					//超级用户设置密码  用户界面
					userNick = userName;
					intent1.setClass(MainActivity.this, AdminAccountActivity.class);
					startActivity(intent1);
					return;
				}
			UserData userData = userDBOpenHelper.queryUserData(userName);
			if (userData==null) {
				Toast.makeText(getApplicationContext(), "用户不存在",
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			
			if (passWord.equals(userData.passWord)) {
				userNick = userName;
				//三种账户类型
				if (userData.type.equals("admin")) {
					intent1.setClass(MainActivity.this, AdminAccountActivity.class);
					startActivity(intent1);
				}else if (userData.type.equals("super")) {
					intent1.setClass(MainActivity.this, SuperAccountActivity.class);
					startActivity(intent1);					
				}else if (userData.type.equals("unlock")) {
					intent1.setClass(MainActivity.this, UnlockDevicesActivity.class);
					startActivity(intent1);				
				}else if (userData.type.equals("common")) {
					intent1.setClass(MainActivity.this, IndexActivity.class);
					startActivity(intent1);		
				}				
			} 
		    else{ 
				Toast.makeText(getApplicationContext(), "输入的密码错误",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.down_user:

			break;

		default:

			break;                                            
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(1000);
					Message msg = new Message();
					msg.what = 1; // 消息(一个整型值)
					mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (true);
		}
	}

	// 在主线程里面处理消息并更新UI界面
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				 // 更新时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日/HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String str = formatter.format(curDate);
				String currentDate =str.substring(0, str.indexOf("/"));
				String currentTime =str.substring(str.indexOf("/")+1);
				dateTx.setText(currentDate);
				timeTx.setText(currentTime);
				
				break;
			default:
				break;
			}
		}
	};


	public static boolean isConnect(Context context) {

		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return false;
	}

	public static void showMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

    
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
		if (isConnect(this) == false) {
			new AlertDialog.Builder(this)
					.setTitle("网络错误")
					.setMessage("网络连接失败，请确认网络连接")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
									System.exit(0);
								}
							}).show();
		}
    }
    
    

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();	
	}
    
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		while (!flag) {
			initWedget();
			flag = true;
		}
	}	
    
	/**
	 * 初始化界面
	 */
	private void initWedget() {
		handler = new Handler(MainActivity.this);
		parent = (LinearLayout)findViewById(R.id.tx5);
		int width = parent.getWidth();
		pwidth = width;
		initPopupWindow();
	}

	/**
	 * 初始化下拉框
	 */
	@SuppressWarnings("deprecation")
	private void initPopupWindow() {
		datas = initDatas();
		View loginwindow = (View) this.getLayoutInflater().inflate(
				R.layout.options, null);
		listView = (ListView) loginwindow.findViewById(R.id.list);
		optionsAdapter = new OptionsAdapter(this, handler, datas);
		listView.setAdapter(optionsAdapter);
		selectPopupWindow = new PopupWindow(loginwindow, pwidth,
				LayoutParams.WRAP_CONTENT, true);
		selectPopupWindow.setOutsideTouchable(true);
		selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	protected void PopupWindowShowing() {
		if (datas.size() > 0) {
			selectPopupWindow.showAsDropDown(parent, 0, -3);	
		 
		}
	}

	/**
	 * 下拉框数据
	 * 
	 * @return
	 */
	private List<Map<String, Object>> initDatas() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		List<String> listUsers = new ArrayList<String>();
		listUsers = userDBOpenHelper.getAllUserNames();
		listUsers.add("a");
		listUsers.add("b");
		listUsers.add("c");
		for (int i = 0; i < listUsers.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("number", listUsers.get(i));
			listItems.add(map);
		}
		return listItems;
	}

	public boolean handleMessage(Message message) {
		Bundle data = message.getData();
		switch (message.what) {
		case 3:
			int selIndex = data.getInt("selIndex");
			userNameEt.setText((CharSequence) datas.get(selIndex).get("number"));
			dismiss();
			break;
		}
		return false;
	}

	private void dismiss() {
		selectPopupWindow.dismiss();
	}  
	
	public void loadAdminConfigure()
    {
 	    SharedPreferences uiState   = this.getSharedPreferences("system", MODE_PRIVATE);
 	    phone[0]=uiState.getString("phone1", "");
 	    phone[1]=uiState.getString("phone2", "");
 	    phoneUsers[0]=uiState.getString("phoneUser1", "");
 	    phoneUsers[1]=uiState.getString("phoneUser2", "");
 	    electric_type=uiState.getString("device_type", "±1100kV直流架空输电线路验电系统");
 	    device_id=uiState.getString("device_id", ""); 	    
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((TextView)findViewById(R.id.tx2)).setText(MainActivity.electric_type);
		((TextView)findViewById(R.id.textView1)).setText("使用须知:本系统仅适用于"+MainActivity.electric_type.substring(0,MainActivity.electric_type.length()-4));
		loadAdminConfigure();
	}
}