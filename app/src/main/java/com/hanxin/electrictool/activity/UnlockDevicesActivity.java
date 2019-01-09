package com.hanxin.electrictool.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import com.hanxin.electrictool.R;
import com.hanxin.electrictool.R.layout;
import com.hanxin.electrictool.activity.SuperAccountActivity.TimeThread;
import com.hanxin.electrictool.db.DBOpenHelper;
import com.hanxin.electrictool.entity.ElectricData;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UnlockDevicesActivity extends Activity implements OnClickListener{
	
	private TextView dateTx,timeTx;
	private Button setupAccountBt, setupPhoneBt,exitBt;
	private Timer timer;
	private ImageView backImg;
	private TextView changeDateBt;
	private DBOpenHelper dbOpenHelper = null;
	private ElectricData lastElectricData = null;
	private TextView deviceIdTx, perTimeTx, nextTimeTx, daysTx;
	private String newDate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unlock_devices);
		initView();
		if (getActionBar() != null) {
			getActionBar().hide();
		}
		dbOpenHelper = new DBOpenHelper(getApplicationContext());
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
		exitBt = (Button) findViewById(R.id.exit);
		exitBt.setOnClickListener(this);
		changeDateBt = (TextView) findViewById(R.id.changeDate);
		changeDateBt.setOnClickListener(this);
		findViewById(R.id.save).setOnClickListener(this);
		
		deviceIdTx = (TextView) findViewById(R.id.tx3);
		perTimeTx = (TextView) findViewById(R.id.lastDay);
		nextTimeTx = (TextView) findViewById(R.id.nextDay);
		daysTx = (TextView) findViewById(R.id.distance);
	}
	
	private void initData() {
		long recent = dbOpenHelper.getCount();
		int last = (int) recent;// (recent-1);
		if (recent > 0) {
			lastElectricData = dbOpenHelper.queryElectricDataById(last);
			String time = lastElectricData.begin_time;
			int year = Integer.valueOf(time.substring(0, 4)); // 年份
			// int month = Integer.valueOf(time.substring(5, 7)); // 月份
			int month = Integer.valueOf(time.substring(5, time.indexOf("月"))); // 月份
			// int dayOfMonth = Integer.valueOf(time.substring(8, 10)); // 日期
			int dayOfMonth = Integer.valueOf(time.substring(
					time.indexOf("月") + 1, time.indexOf("日"))); // 日期
			Log.i("index", year + month + dayOfMonth + "");

			perTimeTx.setText("上次检验时间：" + year + "年" + month + "月" + dayOfMonth
					+ "日");
        	newDate = year + "年" + month + "月" + dayOfMonth
					+ "日";
			// user_birthday= year+""+month+""+dayOfMonth+"";
			SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
			Date date2 = addDate2(year, month, dayOfMonth);
			Date date1 = new Date();

			int days = getIntervalDays(date1, date2);
			nextTimeTx.setText("下次检验时间：" + df.format(date2));
			daysTx.setText("距离下次检验时间还差  " + days + "" + " 天");
			if (days < 0) {
				daysTx.setTextColor(Color.RED);
			} else {
				daysTx.setTextColor(Color.GREEN);
			}
		}
	}
	
	private String addDate(int year, int month, int dayOfMonth) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		// cal.add(Calendar.DATE, -4);
		// Date date=cal.getTime();
		// System.out.println(df.format(date));
		cal.add(Calendar.MONTH, 12);
		Date date = cal.getTime();
		// System.out.println(df.format(date));
		return df.format(date);
	}

	private Date addDate2(int year, int month, int dayOfMonth) {
		// SimpleDateFormat df=new SimpleDateFormat("yyyy年MM月dd日");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		// cal.add(Calendar.DATE, -4);
		// Date date=cal.getTime();
		// System.out.println(df.format(date));
		cal.add(Calendar.MONTH, 12);
		Date date = cal.getTime();

		return date;
	}

	public int getIntervalDays(Date startday, Date endday) {

		long sl = startday.getTime();
		long el = endday.getTime();
		long ei = el - sl;
		return (int) (ei / (1000 * 60 * 60 * 24));
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

		case R.id.save:	
			saveElectricResult(newDate);    
			break;
		case R.id.changeDate:			
			Calendar c = Calendar.getInstance();
			Dialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
              	
                    public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                    	int month1 = month+1;  
                    	perTimeTx.setText("上次检验时间：" + year + "年" + month1 + "月" + dayOfMonth
            					+ "日");
                    	newDate = year + "年" + month1 + "月" + dayOfMonth
            					+ "日";

                    	SimpleDateFormat df=new SimpleDateFormat("yyyy年MM月dd日");
                    	Date date2 = addDate2(year, month, dayOfMonth);	
                    	Date date1 = new Date();	
                    	
                    	int days = getIntervalDays(date1,date2);

            			nextTimeTx.setText("下次检验时间：" + df.format(date2));
            			daysTx.setText("距离下次检验时间还差  " + days + "" + " 天");
            			if (days < 0) {
            				daysTx.setTextColor(Color.RED);
            			} else {
            				daysTx.setTextColor(Color.GREEN);
            			}           			               	
                    }                  
                }, 
                c.get(Calendar.YEAR), // 传入年份
                c.get(Calendar.MONTH), // 传入月份
                c.get(Calendar.DAY_OF_MONTH) // 传入天数
            );
			dialog.show();
			
			break;	

		case R.id.back:
			finish();
			break;
		case R.id.exit:
			Intent intent1 = new Intent();
			intent1.setClass(UnlockDevicesActivity.this, MainActivity.class);
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
	
	private void saveElectricResult(String newDate) {
		
		long recent = dbOpenHelper.getCount();
		int last0 = (int)(recent);
		int last = (int)(recent-1);
		if (recent > 0) {
			lastElectricData = dbOpenHelper.queryElectricDataById(last0);
			
			dbOpenHelper.updateElectricData(last0, newDate);
			Log.i("index", lastElectricData.begin_time+""+lastElectricData.id);
			Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_LONG).show();			
		}
		else {
			Toast.makeText(getApplicationContext(), "没有数据", Toast.LENGTH_LONG).show();
		}
	}
}
