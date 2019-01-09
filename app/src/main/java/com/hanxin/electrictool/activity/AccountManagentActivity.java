package com.hanxin.electrictool.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import com.hanxin.electrictool.R;
import com.hanxin.electrictool.R.layout;
import com.hanxin.electrictool.activity.SetupPhoneActivity.TimeThread;
import com.hanxin.electrictool.db.UserDBOpenHelper;
import com.hanxin.electrictool.entity.UserData;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AccountManagentActivity extends Activity implements
		OnClickListener {

	private TextView dateTx, timeTx;
	private Button setupAccountBt, setupDetailsBt, exitBt;
	private ImageView backImg;
	private Timer timer;
	private EditText inputPasswordEt, inputUserNameEt;
	private Button saveCommonBt;
	private Button delUserBt[] = new Button[10];
	private TextView[] userNameTx = new TextView[10];
	private TextView[] passwordTx = new TextView[10];

	private List<String> newUserName = new ArrayList<String>();
	private List<String> newUserPass = new ArrayList<String>();

	int[] userNameIds = new int[] { R.id.newUserName1, R.id.newUserName2,
			R.id.newUserName3, R.id.newUserName4, R.id.newUserName5,
			R.id.newUserName6, R.id.newUserName7, R.id.newUserName8,
			R.id.newUserName9, R.id.newUserName10 };
	int[] userPassIds = new int[] { R.id.newUserPass1, R.id.newUserPass2,
			R.id.newUserPass3, R.id.newUserPass4, R.id.newUserPass5,
			R.id.newUserPass6, R.id.newUserPass7, R.id.newUserPass8,
			R.id.newUserPass9, R.id.newUserPass10 };
	int[] userDeleteIds = new int[] { R.id.delnewUser1, R.id.delnewUser2,
			R.id.delnewUser3, R.id.delnewUser4, R.id.delnewUser5,
			R.id.delnewUser6, R.id.delnewUser7, R.id.delnewUser8,
			R.id.delnewUser9, R.id.delnewUser10 };
	private UserDBOpenHelper userDBOpenHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_managent);
		initView();
		if (getActionBar() != null) {
			getActionBar().hide();
		}
		userDBOpenHelper = new UserDBOpenHelper(getApplicationContext());
		initData();
	}

	private void initView() {
		timeTx = (TextView) findViewById(R.id.showTime);
		dateTx = (TextView) findViewById(R.id.showDate);
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日/HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		String currentDate = str.substring(0, str.indexOf("/"));
		String currentTime = str.substring(str.indexOf("/") + 1);
		dateTx.setText(currentDate);
		timeTx.setText(currentTime);
		new TimeThread().start(); // 启动新的线程

		backImg = (ImageView) findViewById(R.id.back);
		backImg.setOnClickListener(this);
		exitBt = (Button) findViewById(R.id.exit);
		exitBt.setOnClickListener(this);
		inputUserNameEt = (EditText) findViewById(R.id.inputCommonName);
		inputPasswordEt = (EditText) findViewById(R.id.inputCommonPassword);
		saveCommonBt = (Button) findViewById(R.id.saveCommAccount);
		saveCommonBt.setOnClickListener(this);


		for (int i = 0; i <= 9; i++) {
			userNameTx[i] = (TextView) findViewById(userNameIds[i]);
			passwordTx[i] = (TextView) findViewById(userPassIds[i]);
			delUserBt[i] = (Button) findViewById(userDeleteIds[i]);
			delUserBt[i].setOnClickListener(this);
		}

	}

	private void initData() {
		List<UserData> listUsers = new ArrayList<UserData>();
		listUsers = userDBOpenHelper.getCommonUserDatas();
		for (int i = 0; i < listUsers.size(); i++) {
			newUserName.add(listUsers.get(i).userName);
			newUserPass.add(listUsers.get(i).passWord);
			userNameTx[i].setText(listUsers.get(i).userName);
			passwordTx[i].setText(listUsers.get(i).passWord);
		}

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


	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日/HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String str = formatter.format(curDate);
				String currentDate = str.substring(0, str.indexOf("/"));
				String currentTime = str.substring(str.indexOf("/") + 1);
				dateTx.setText(currentDate);
				timeTx.setText(currentTime);
				// 更新时间
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.saveCommAccount:
			saveCommonAccount();
			break;
		case R.id.delnewUser1:
			delCommonAccount();
			break;

		case R.id.back:
			finish();
			break;
		case R.id.exit:
			Intent intent1 = new Intent();
			intent1.setClass(AccountManagentActivity.this, MainActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(intent1);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void saveCommonAccount() {
		
		String userName = inputUserNameEt.getText().toString();
		if (TextUtils.isEmpty(userName)) {
			Toast.makeText(getApplicationContext(), "请输入用户名",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String password = inputPasswordEt.getText().toString();
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (newUserName.size() < 10) {
			newUserName.add(userName);
			newUserPass.add(password);
		}

		
		 for (int j = 0; j < newUserName.size(); j++) {
		 userNameTx[j].setText(newUserName.get(j));
		 passwordTx[j].setText(newUserPass.get(j)); 
		 }
		
		userDBOpenHelper.save(userName, password, "common");
		Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT)
				.show();

	}

	void delCommonAccount(int i) {
		userNameTx[i].setText("");
		passwordTx[i].setText("");
		userDBOpenHelper.deleteUserData(newUserName.get(i));
		newUserName.remove(i);
		newUserPass.remove(i);
		for (int j = 0; j < newUserName.size(); j++) {
			userNameTx[i].setText(newUserName.get(j));
			passwordTx[i].setText(newUserPass.get(j));
		}
		Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT)
				.show();
	}

	void delCommonAccount() {
		userNameTx[0].setText("");
		passwordTx[0].setText("");
		userDBOpenHelper.deleteUserData(newUserName.get(0));
		newUserName.remove(0);
		newUserPass.remove(0);
		Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT)
				.show();
	}

}
