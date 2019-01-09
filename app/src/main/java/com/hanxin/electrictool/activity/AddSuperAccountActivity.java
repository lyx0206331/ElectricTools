package com.hanxin.electrictool.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import com.hanxin.electrictool.R;
import com.hanxin.electrictool.R.layout;
import com.hanxin.electrictool.db.UserDBOpenHelper;
import com.hanxin.electrictool.entity.UserData;

import android.app.Activity;
import android.content.Intent;
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

public class AddSuperAccountActivity extends Activity implements
		OnClickListener {

	private TextView dateTx, timeTx;
	private Button setupAccountBt, setupDetailsBt, exitBt;
	private ImageView backImg;
	private Timer timer;
	private Button saveSuperAccountBt, saveUnlockAccountBt, delSuperAccountBt,
			delUnlockAccountBt;
	private TextView superNameTx, superPassTx, unlockNameTx, unlockPassTx;
	private EditText superNameEt, superPassEt, unlockNameEt, unlockPassEt;
	private UserDBOpenHelper userDBOpenHelper ;
	private UserData superUserData, unlockUserData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_super_account);
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

		saveSuperAccountBt = (Button) findViewById(R.id.saveSuperAccount);
		saveSuperAccountBt.setOnClickListener(this);
		saveUnlockAccountBt = (Button) findViewById(R.id.saveUnlockAccount);
		saveUnlockAccountBt.setOnClickListener(this);
		delSuperAccountBt = (Button) findViewById(R.id.delSuperAccountBt);
		delSuperAccountBt.setOnClickListener(this);
		delUnlockAccountBt = (Button) findViewById(R.id.delUnlockAccountBt);
		delUnlockAccountBt.setOnClickListener(this);
		superNameTx = (TextView) findViewById(R.id.superAccountName);
		superPassTx = (TextView) findViewById(R.id.superAccountPass);
		unlockNameTx = (TextView) findViewById(R.id.unlockAccountName);
		unlockPassTx = (TextView) findViewById(R.id.unlockAccountPass);
		superNameEt = (EditText) findViewById(R.id.inputSuperName);
		superPassEt = (EditText) findViewById(R.id.inputSuperPassword);
		unlockNameEt = (EditText) findViewById(R.id.inputUnlockUserName);
		unlockPassEt = (EditText) findViewById(R.id.inputUnlockPassword);
	}
	
	private void initData() {
		superUserData = userDBOpenHelper.queryUserDataByType("super");
		unlockUserData = userDBOpenHelper.queryUserDataByType("unlock");
		if (superUserData!=null) {
			superNameTx.setText(superUserData.userName);
			superPassTx.setText(superUserData.passWord);
		}
		if (unlockUserData!=null) {
			unlockNameTx.setText(unlockUserData.userName);			
			unlockPassTx.setText(unlockUserData.passWord);
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

	// 在主线程里面处理消息并更新UI界面

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

		case R.id.saveSuperAccount:
			saveSuper();
			break;

		case R.id.saveUnlockAccount:
			saveUnlock();
			break;
		case R.id.delSuperAccountBt:
			delSuper();
			break;

		case R.id.delUnlockAccountBt:
			delUnlock();
			break;

		case R.id.back:
			finish();
			break;
		case R.id.exit:
			Intent intent1 = new Intent();
			intent1.setClass(AddSuperAccountActivity.this, MainActivity.class);
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

	private void saveSuper() {

		if (superUserData!=null) {
			return;
		}
		String userName = superNameEt.getText().toString();
		String passWord = superPassEt.getText().toString();
		if (TextUtils.isEmpty(userName)) {
			Toast.makeText(getApplicationContext(), "请输入用户名",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(passWord)) {
			Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		// 初始超级用户密码
		
		userDBOpenHelper.save(userName, passWord, "super");

		superNameTx.setText(userName);
		superPassTx.setText(passWord);		
		superUserData=new UserData(userName, passWord, "super");
		Toast.makeText(getApplicationContext(), "保存成功",
				Toast.LENGTH_SHORT).show();
	}
	
	private void saveUnlock() {
	
		if (unlockUserData!=null) {
			return;
		}
		String userName = unlockNameEt.getText().toString();
		String passWord = unlockPassEt.getText().toString();
		if (TextUtils.isEmpty(userName)) {
			Toast.makeText(getApplicationContext(), "请输入用户名",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(passWord)) {
			Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		// 初始超级用户密码
		
		userDBOpenHelper.save(userName, passWord, "unlock");
		unlockNameTx.setText(userName);
		unlockPassTx.setText(passWord);	
		unlockUserData=new UserData(userName, passWord, "unlock");
		Toast.makeText(getApplicationContext(), "保存成功",
				Toast.LENGTH_SHORT).show();
	}
	
	void delSuper(){
		if (superUserData==null) {
			return;
		}		
		userDBOpenHelper.deleteUserDataByType("super");
		superNameTx.setText("");
		superPassTx.setText("");	
		superUserData=null;
		Toast.makeText(getApplicationContext(), "删除成功",
				Toast.LENGTH_SHORT).show();
	}
	
	void delUnlock(){
		if (unlockUserData==null) {
			return;
		}		
		userDBOpenHelper.deleteUserDataByType("unlock");
		unlockNameTx.setText("");
		unlockPassTx.setText("");	
		unlockUserData=null;
		Toast.makeText(getApplicationContext(), "删除成功",
				Toast.LENGTH_SHORT).show();
	}
}
