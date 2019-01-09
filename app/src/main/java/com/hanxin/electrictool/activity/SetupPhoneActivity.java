package com.hanxin.electrictool.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import com.hanxin.electrictool.R;

import com.hanxin.electrictool.R.layout;
import com.hanxin.electrictool.activity.AddSuperAccountActivity.TimeThread;

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

public class SetupPhoneActivity extends Activity implements OnClickListener {

	private TextView dateTx,timeTx;
	private Button setupAccountBt, setupDetailsBt,exitBt;
	private ImageView backImg;
	private Timer timer;
	private EditText inputPhoneEt,inputUserEt;
	private Button savePhoneBt,delPhone1Bt,delPhone2Bt;
	private TextView phone1Tx,phone2Tx,phoneUser1Tx,phoneUser2Tx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_phone);
		initView();
		if (getActionBar() != null) {
			getActionBar().hide();
		}
		initData();
		((TextView)findViewById(R.id.tx2)).setText(MainActivity.electric_type);
	}
	
	private void initView() {
		timeTx = (TextView) findViewById(R.id.showTime);
		dateTx = (TextView) findViewById(R.id.showDate);
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy年MM月dd日/HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		String currentDate =str.substring(0, str.indexOf("/"));
		String currentTime =str.substring(str.indexOf("/")+1);
		dateTx.setText(currentDate);
		timeTx.setText(currentTime);
		new TimeThread().start(); // 启动新的线程


		backImg = (ImageView) findViewById(R.id.back);
		backImg.setOnClickListener(this);
	   	inputPhoneEt=(EditText) findViewById(R.id.inputPhoneNum);    
	   	inputUserEt=(EditText) findViewById(R.id.inputPhoneUser);    
    	savePhoneBt=(Button) findViewById(R.id.savePhoneNum); 
    	delPhone1Bt=(Button) findViewById(R.id.delPhoneUser1); 
    	delPhone2Bt=(Button) findViewById(R.id.delPhoneUser2);
    	
    	delPhone1Bt.setOnClickListener(this); 
    	delPhone2Bt.setOnClickListener(this);
    	savePhoneBt.setOnClickListener(this);	    	
    	
		phone1Tx = (TextView) findViewById(R.id.phoneNum1);
		phone2Tx = (TextView) findViewById(R.id.phoneNum2);
		phoneUser1Tx = (TextView) findViewById(R.id.phoneUser1);
		phoneUser2Tx  = (TextView) findViewById(R.id.phoneUser2);
	}
	
	private void initData() {
		phone1Tx.setText(MainActivity.phone[0]);
		phone2Tx.setText(MainActivity.phone[1]);
		phoneUser1Tx.setText(MainActivity.phoneUsers[0]);
		phoneUser2Tx.setText(MainActivity.phoneUsers[1]);
	}
	
	class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(1000);
					Message msg = new Message();
					msg.what = 1; // 消息(一个整型值)
					mHandler.sendMessage(msg);// 每隔1秒发送一个USG给mHandler
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
				String currentDate =str.substring(0, str.indexOf("/"));
				String currentTime =str.substring(str.indexOf("/")+1);
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
		case R.id.savePhoneNum:
			savePhone();
			break;
		case R.id.delPhoneUser1:
			delPhone1();
			break;

		case R.id.delPhoneUser2:
			delPhone2();
			break;
			

		case R.id.back:
			finish();
			break;
		case R.id.exit:
			Intent intent1 = new Intent();
			intent1.setClass(SetupPhoneActivity.this, MainActivity.class);
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
	private void savePhone() {
		// 待加入 密码校验功能
		String phone = inputPhoneEt.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(getApplicationContext(), "请输入手机号",
					Toast.LENGTH_SHORT).show();
			return;
		}
		String user = inputUserEt.getText().toString();
		if (TextUtils.isEmpty(user)) {
			Toast.makeText(getApplicationContext(), "请输入用户名",
					Toast.LENGTH_SHORT).show();
			return;
		}
		SharedPreferences state   = this.getSharedPreferences("system", MODE_PRIVATE);
		if (TextUtils.isEmpty(MainActivity.phone[0])) {
				phone1Tx.setText(phone);	
				phoneUser1Tx.setText(user);	
		 		Editor et=state.edit();
		 		et.putString("phone1",phone);
		 		et.putString("phoneUser1",user);
		 		et.commit();
		 		MainActivity.phone[0]=phone;
		 		MainActivity.phoneUsers[0]=user;
			Toast.makeText(getApplicationContext(), "保存成功",
					Toast.LENGTH_SHORT).show();
		}else if (TextUtils.isEmpty(MainActivity.phone[1])) {
			phone2Tx.setText(phone);	
			phoneUser2Tx.setText(user);	
	 		Editor et=state.edit();
	 		et.putString("phone2",phone);
	 		et.putString("phoneUser2",user);
	 		et.commit();
	 		MainActivity.phone[1]=phone;
	 		MainActivity.phoneUsers[1]=user;
			Toast.makeText(getApplicationContext(), "保存成功",
					Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(getApplicationContext(), "未保存",
					Toast.LENGTH_SHORT).show();
		}

	}
	
	void delPhone1(){
		SharedPreferences state   = this.getSharedPreferences("system", MODE_PRIVATE);
		if (!TextUtils.isEmpty(MainActivity.phone[0])) {
				phone1Tx.setText("");
				phoneUser1Tx.setText("");	
				MainActivity.phone[0]="";
		 		Editor et=state.edit();
		 		et.putString("phone1","");
		 		et.putString("phoneUser1","");
		 		et.commit();
		Toast.makeText(getApplicationContext(), "删除成功",
				Toast.LENGTH_SHORT).show();
		}
	}
	
	void delPhone2(){
		SharedPreferences state   = this.getSharedPreferences("system", MODE_PRIVATE);
		if (!TextUtils.isEmpty(MainActivity.phone[1])) {
				phone2Tx.setText("");
				phoneUser2Tx.setText("");
				MainActivity.phone[1]="";
		 		Editor et=state.edit();
		 		et.putString("phone2","");
		 		et.putString("phoneUser2","");
		 		et.commit();
		Toast.makeText(getApplicationContext(), "删除成功",
				Toast.LENGTH_SHORT).show();
		}
	}
}
