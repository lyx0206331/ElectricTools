package com.hanxin.electrictool.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import com.hanxin.electrictool.R;

import com.hanxin.electrictool.R.layout;
import com.hanxin.electrictool.activity.AddSuperAccountActivity.TimeThread;
import com.hanxin.electrictool.entity.UserData;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AdminSettingsActivity extends Activity implements OnClickListener {

	private TextView dateTx, timeTx;
	private Button setupAccountBt, setupDetailsBt, exitBt;
	private ImageView backImg;
	private Timer timer;
	private Spinner spinner;
	private EditText inputPhoneEt, deviceIdEt;
	private Button savePhoneBt, savedeviceIdBt, delPhone1Bt, delPhone2Bt;
	private TextView phone1Tx, phone2Tx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_settings);
		initView();
		if (getActionBar() != null) {
			getActionBar().hide();
		}
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

		spinner = (Spinner) findViewById(R.id.select_type);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {


			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				if (position == 0) {
					return;
				}
				String[] types = getResources().getStringArray(R.array.types);
				MainActivity.electric_type = types[position];

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		List<String> list = new ArrayList<String>();
		String[] level = this.getResources().getStringArray(R.array.types);
		for (String str1 : level) {
			list.add(str1);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		inputPhoneEt = (EditText) findViewById(R.id.inputPhone);
		deviceIdEt = (EditText) findViewById(R.id.inputDeviceIds);

		savePhoneBt = (Button) findViewById(R.id.savePhone);
		savedeviceIdBt = (Button) findViewById(R.id.saveDeviceId);
		delPhone1Bt = (Button) findViewById(R.id.delPhone1);
		delPhone2Bt = (Button) findViewById(R.id.delPhone2);

		delPhone1Bt.setOnClickListener(this);
		delPhone2Bt.setOnClickListener(this);
		savePhoneBt.setOnClickListener(this);
		savedeviceIdBt.setOnClickListener(this);

		phone1Tx = (TextView) findViewById(R.id.phone1);
		phone2Tx = (TextView) findViewById(R.id.phone2);
	}

	private void initData() {
		deviceIdEt.setText(MainActivity.device_id);
		phone1Tx.setText(MainActivity.phone[0]);
		phone2Tx.setText(MainActivity.phone[1]);
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
		case R.id.savePhone:
			savePhone();
			break;

		case R.id.saveDeviceId:
			saveDeviceId();
			break;
		case R.id.delPhone1:
			delPhone1();
			break;

		case R.id.delPhone2:
			delPhone2();
			break;

		case R.id.back:
			finish();
			break;
		case R.id.exit:
			SharedPreferences state = this.getSharedPreferences("system",
					MODE_PRIVATE);
			Editor et = state.edit();
			et.putString("device_type", MainActivity.electric_type);
			et.commit();
			Intent intent1 = new Intent();
			intent1.setClass(AdminSettingsActivity.this, MainActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 注意本行的FLAG设置
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
		SharedPreferences state = this.getSharedPreferences("system",
				MODE_PRIVATE);
		Editor et = state.edit();
		et.putString("device_type", MainActivity.electric_type);
		et.commit();
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
		SharedPreferences state = this.getSharedPreferences("system",
				MODE_PRIVATE);
		if (TextUtils.isEmpty(MainActivity.phone[0])) {
			phone1Tx.setText(phone);
			Editor et = state.edit();
			et.putString("phone1", phone);
			et.commit();
			MainActivity.phone[0] = phone;
			Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT)
					.show();
		} else if (TextUtils.isEmpty(MainActivity.phone[1])) {
			phone2Tx.setText(phone);
			Editor et = state.edit();
			et.putString("phone2", phone);
			et.commit();
			MainActivity.phone[1] = phone;
			Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(getApplicationContext(), "未保存", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private void saveDeviceId() {
		// 待加入 密码校验功能
		String deviceId = deviceIdEt.getText().toString();
		if (TextUtils.isEmpty(deviceId)) {
			Toast.makeText(getApplicationContext(), "请输入设备ID",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (deviceId.length()!=8) {
			Toast.makeText(getApplicationContext(), "请输入8位设备ID",
					Toast.LENGTH_SHORT).show();
			return;
		}
		SharedPreferences state = this.getSharedPreferences("system",
				MODE_PRIVATE);
		Editor et = state.edit();
		et.putString("device_id", deviceId);
		et.commit();
		Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT)
				.show();
		MainActivity.device_id = deviceId;

	}

	void delPhone1() {
		SharedPreferences state = this.getSharedPreferences("system",
				MODE_PRIVATE);
		if (!TextUtils.isEmpty(MainActivity.phone[0])) {
			phone1Tx.setText("");
			MainActivity.phone[0] = "";
			Editor et = state.edit();
			et.putString("phone1", "");
			et.commit();
			Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT)
					.show();
		}
	}

	void delPhone2() {
		SharedPreferences state = this.getSharedPreferences("system",
				MODE_PRIVATE);
		if (!TextUtils.isEmpty(MainActivity.phone[1])) {
			phone2Tx.setText("");
			MainActivity.phone[1] = "";
			Editor et = state.edit();
			et.putString("phone2", "");
			et.commit();
			Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT)
					.show();
		}
	}


}
