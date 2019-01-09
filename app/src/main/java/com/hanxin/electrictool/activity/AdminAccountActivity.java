package com.hanxin.electrictool.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import com.hanxin.electrictool.R;
import com.hanxin.electrictool.R.id;
import com.hanxin.electrictool.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminAccountActivity extends Activity implements OnClickListener {

	private TextView dateTx,timeTx;
	private Button setupAccountBt, setupDetailsBt,exitBt;
	private ImageView backImg;
	private Timer timer;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_setting);
		initView();
		if (getActionBar() != null) {
			getActionBar().hide();
		}
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

		setupAccountBt = (Button) findViewById(R.id.setupAccount);
		setupAccountBt.setOnClickListener(this);
		setupDetailsBt = (Button) findViewById(R.id.setupDetails);
		setupDetailsBt.setOnClickListener(this);
		backImg = (ImageView) findViewById(R.id.back);
		backImg.setOnClickListener(this);
		exitBt = (Button) findViewById(R.id.exit);
		exitBt.setOnClickListener(this);
		 
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setupAccount:
			Intent intent0 = new Intent();
			intent0.setClass(AdminAccountActivity.this, AddSuperAccountActivity.class);
			startActivity(intent0);
			break;

		case R.id.setupDetails:
			Intent intent = new Intent();
			intent.setClass(AdminAccountActivity.this, AdminSettingsActivity.class);
			startActivity(intent);
			break;
			

		case R.id.back:
			finish();
			break;
		case R.id.exit:
			  Intent intent1 = new Intent(); 
			  intent1.setClass(AdminAccountActivity.this, MainActivity.class); 
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
		
		((TextView)findViewById(R.id.tx2)).setText(MainActivity.electric_type);
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
	
	
}
