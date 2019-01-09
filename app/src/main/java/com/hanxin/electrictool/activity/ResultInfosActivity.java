package com.hanxin.electrictool.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import com.hanxin.electrictool.R;
import com.hanxin.electrictool.R.layout;
import com.hanxin.electrictool.activity.TestElectricActivity.TimeThread;
import com.hanxin.electrictool.db.DBOpenHelper;
import com.hanxin.electrictool.entity.ElectricData;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResultInfosActivity extends Activity implements OnClickListener{
	
	private TextView dateTx,timeTx;
	private Button exitBt;
	private Timer timer;
	private ImageView backImg;
	private LinearLayout nextLl, previousLl;
	
	private DBOpenHelper dbOpenHelper = null;
	private List<ElectricData> listDatas = null;
	private Button upBt, downBt, getTimeBt, getResultBt;
	int counter = 0;
	int tempCounter = 0;
	private TextView recordNumTx,getTimeTx, getResultTx,getUserTx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_infos);	
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

		
		findViewById(R.id.next_result).setOnClickListener(this);
		findViewById(R.id.previous_result).setOnClickListener(this);
		
		backImg = (ImageView) findViewById(R.id.back);
		backImg.setOnClickListener(this);
		exitBt = (Button) findViewById(R.id.exit);
		exitBt.setOnClickListener(this);	
		
		getTimeTx = (TextView) findViewById(R.id.time1);
		getResultTx = (TextView) findViewById(R.id.result1);
		getUserTx = (TextView) findViewById(R.id.user1);
		recordNumTx = (TextView) findViewById(R.id.num1);
	}
	
	private void initData() {
		dbOpenHelper = new DBOpenHelper(getApplicationContext());
		
		// 初始化数据
		listDatas = dbOpenHelper.getAllElectricData();
		counter = listDatas.size();
		tempCounter = counter;
		if (counter > 0) {
			getTimeTx.setText(listDatas.get(counter - 1).begin_time);
			getResultTx.setText(listDatas.get(counter - 1).result);
			getUserTx.setText(listDatas.get(counter - 1).user);
			recordNumTx.setText(listDatas.get(counter - 1).id+"");			
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
		case R.id.previous_result:
			if (tempCounter <= 1) {
				Toast.makeText(ResultInfosActivity.this, "最后一个了",
						Toast.LENGTH_SHORT).show();
				return;
			}
			tempCounter--;

			getTimeTx.setText(listDatas.get(tempCounter - 1).begin_time);
			getResultTx.setText(listDatas.get(tempCounter - 1).result);
			getUserTx.setText(listDatas.get(tempCounter - 1).user);
			recordNumTx.setText(listDatas.get(tempCounter - 1).id+"");
			break;

		case R.id.next_result:
			if (tempCounter == counter) {
				Toast.makeText(ResultInfosActivity.this, "最后一个了",
						Toast.LENGTH_SHORT).show();
				return;
			}
			tempCounter++;
			getTimeTx.setText(listDatas.get(tempCounter - 1).begin_time);
			getResultTx.setText(listDatas.get(tempCounter - 1).result);
			getUserTx.setText(listDatas.get(tempCounter - 1).user);
			recordNumTx.setText(listDatas.get(tempCounter - 1).id+"");
			break;
			

		case R.id.back:
			finish();
			break;
		case R.id.exit:
			Intent intent1 = new Intent();
			intent1.setClass(ResultInfosActivity.this, MainActivity.class);
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
}
