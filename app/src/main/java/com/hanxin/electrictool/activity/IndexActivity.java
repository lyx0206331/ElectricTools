package com.hanxin.electrictool.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hanxin.electrictool.R;

import com.hanxin.electrictool.R.color;
import com.hanxin.electrictool.R.id;
import com.hanxin.electrictool.R.layout;
import com.hanxin.electrictool.db.DBOpenHelper;
import com.hanxin.electrictool.entity.ElectricData;
import com.hanxin.electrictool.utils.Constants;
import com.hanxin.electrictool.utils.DataProcess;
import com.hanxin.electrictool.utils.HandleMsg;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IndexActivity extends Activity implements OnClickListener {

	private TextView dateTx,timeTx, showStatusTx;
	private boolean result = true;
	private Button checkElectricBt, showResultBt,exitBt;
	private Timer timer;
	private int counter = 0;
	private LinearLayout lLayout;
	private DBOpenHelper dbOpenHelper = null;
	private ElectricData lastElectricData = null;
	private ImageView backImg;
	public static DataProcess dataProcess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 从savedInstanceState中恢复数据, 如果没有数据需要恢复savedInstanceState为null  
        if (savedInstanceState != null) {  
            String temp = savedInstanceState.getString("temp");  
            System.out.println("onCreate: temp = " + temp);  
        }  
        
		setContentView(R.layout.activity_index1);
		initView();
		if (getActionBar() != null) {
			getActionBar().hide();
		}
		dbOpenHelper = new DBOpenHelper(getApplicationContext());
		initData();			
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

		showStatusTx = (TextView) findViewById(R.id.recentCheck);
		showStatusTx.setOnClickListener(this);
		showResultBt = (Button) findViewById(R.id.history1);
		showResultBt.setOnClickListener(this);
		checkElectricBt = (Button) findViewById(R.id.checkElectric1);
		checkElectricBt.setOnClickListener(this);
		exitBt = (Button) findViewById(R.id.exit);
		exitBt.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		
		((TextView)findViewById(R.id.tx2)).setText(MainActivity.electric_type);
	}
	


	@SuppressLint("NewApi")
	private void initData() { // 查询历史验电数据
		long recent = dbOpenHelper.getCount();
		int last = (int)recent;//(recent-1);
		if (recent > 0) {
			lastElectricData = dbOpenHelper.queryElectricDataById(last);
			String time = lastElectricData.begin_time;
			int year = Integer.valueOf(time.substring(0, 4)); // 年份
			//int month = Integer.valueOf(time.substring(5, 7)); // 月份
			int month = Integer.valueOf(time.substring(5, time.indexOf("月"))); // 月份
			//int dayOfMonth = Integer.valueOf(time.substring(8, 10)); // 日期
			int dayOfMonth = Integer.valueOf(time.substring(time.indexOf("月")+1, time.indexOf("日"))); // 日期
			Log.i("index", year+month+dayOfMonth+"");
			Date date2 = addDate2(year, month, dayOfMonth);	
        	Date date1 = new Date();	
        	
        	int days = getIntervalDays(date1,date2);
        	if (days<0) {
        		result=false;
			} else {
				result=true;
			}
		}
		if (result) {
			checkElectricBt.setEnabled(true);
			checkElectricBt.setBackground(getResources().getDrawable(R.drawable.check_bt_bg));
			findViewById(R.id.tx3).setVisibility(View.INVISIBLE);
		} else {
			
			checkElectricBt.setEnabled(false);
			checkElectricBt.setBackground(getResources().getDrawable(R.drawable.uncheck_bt_bg));
			findViewById(R.id.tx3).setVisibility(View.VISIBLE);
			
		}
	}
	


	public static void showMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
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
			case 2:
				counter++;
				if (counter % 2 == 1) {
					showStatusTx.setBackgroundColor(Color.RED);
				} else {
					showStatusTx.setBackgroundColor(getResources().getColor(
							R.color.status_bg_normal));
				}

				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent0 = new Intent();
		switch (v.getId()) {
		
		case R.id.recentCheck:
			
			intent0.setClass(IndexActivity.this, CheckInfoActivity.class);
			startActivity(intent0);
			break;

		case R.id.checkElectric1:
			if (MainActivity.isConnect(this) == false) { //联网检查
				Dialog dialod = new AlertDialog.Builder(this)
						.setTitle("网络错误")
						.setMessage("网络连接失败，请确认网络连接")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
									}
								}).show();	
				//return;
			}else {
				Intent intent = new Intent();
				intent.setClass(IndexActivity.this,TestElectricActivity.class);
				startActivity(intent);
			}
			
			break;
		case R.id.history1:			
			intent0.setClass(IndexActivity.this, ResultInfosActivity.class);
			startActivity(intent0);
			break;


		case R.id.back:
			finish();
			break;
		case R.id.exit:
			Intent intent1 = new Intent();
			intent1.setClass(IndexActivity.this, MainActivity.class);
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
		initData();
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
		System.exit(0);
	}
	
	
    @Override  
    protected void onSaveInstanceState(Bundle outState) {  
        super.onSaveInstanceState(outState);  
        outState.putString("temp", "test");  
    }  

	private Date addDate2(int year,int month,int dayOfMonth) {
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.add(Calendar.MONTH, 12);
		Date date=cal.getTime();
		return date;
	}

	public int getIntervalDays(Date startday,Date endday){        
    
        long sl=startday.getTime();
        long el=endday.getTime();       
        long ei=el-sl;           
        return (int)(ei/(1000*60*60*24));
    }

}
