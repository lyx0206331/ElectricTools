package com.hanxin.electrictool.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hanxin.electrictool.R;
import com.hanxin.electrictool.R.layout;
import com.hanxin.electrictool.activity.UnlockDevicesActivity.TimeThread;
import com.hanxin.electrictool.db.DBOpenHelper;
import com.hanxin.electrictool.entity.ElectricData;
import com.hanxin.electrictool.utils.Constants;
import com.hanxin.electrictool.utils.DataProcess;
import com.hanxin.electrictool.utils.HandleMsg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TestElectricActivity extends Activity implements OnClickListener {

    private TextView dateTx, timeTx;
    private Button selfCheckBt, checkDistanceBt, measureBt, endTestBt, exitBt, shutUpBt;
    private Timer timer;
    private ImageView backImg;
    private DataProcess dataProcess;
    private ProgressDialog connect_dialog;
    private TextView selfCheckResultTx, distanceCheckResultTx, electricCheckResultTx;
    private ImageView progressBar1, progressBar2, progressBar3;
    private ImageView image1, image2, image3;
    private String testResult = null;
    private ImageView progressBar0;
    private Animation hyperspaceJumpAnimation;
    public byte[] cmd_deviceId_send = new byte[]{(byte) 0x7E, (byte) 0x40, (byte) 20, (byte) 016, (byte) 00, (byte) 0x01, (byte) 0xE7};//发送设备识别码指令
    private boolean device_id_check = false;
    private MediaPlayer mMediaPlayer;
    private static Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_electric);
        initView();
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        dataProcess = new DataProcess(hMsg, TestElectricActivity.this);
        connect_dialog = ProgressDialog
                .show(this, "提示", "正在连接设备中---", false);
        connect_dialog.setCancelable(true);
        new Thread() {
            @Override
            public void run() {
                if (dataProcess.startConn(Constants.server_ip, Constants.server_port)) {
                    Message msg = new Message();
                    msg.what = Constants.DEVICE_TYPE_OK;
                    hMsg.sendMessage(msg);

                } else {
                    Message msg = new Message();
                    msg.what = Constants.DEVICE_TYPE_ERROR;
                    hMsg.sendMessage(msg);
                }
            }
        }.start();
        ((TextView) findViewById(R.id.tx2)).setText(MainActivity.electric_type);

        if (!MainActivity.device_id.equals("未知") && MainActivity.device_id.length() == 8) {

            cmd_deviceId_send[2] = (byte) ((Integer.valueOf(MainActivity.device_id.substring(0, 2))).intValue());
            cmd_deviceId_send[3] = (byte) ((Integer.valueOf(MainActivity.device_id.substring(2, 4))).intValue());
            cmd_deviceId_send[4] = (byte) ((Integer.valueOf(MainActivity.device_id.substring(4, 6))).intValue());
            cmd_deviceId_send[5] = (byte) ((Integer.valueOf(MainActivity.device_id.substring(6, 8))).intValue());
        } else {
            showMessage(getApplicationContext(),
                    "设备Id有误，请重新设置");
        }
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

        selfCheckBt = (Button) findViewById(R.id.checkElectricBt);
        selfCheckBt.setOnClickListener(this);
        checkDistanceBt = (Button) findViewById(R.id.checkDistanceBt);
        checkDistanceBt.setOnClickListener(this);
        measureBt = (Button) findViewById(R.id.measureBt);
        measureBt.setOnClickListener(this);
        endTestBt = (Button) findViewById(R.id.endTestBt);
        endTestBt.setOnClickListener(this);
        shutUpBt = (Button) findViewById(R.id.shutUpBt);
        shutUpBt.setOnClickListener(this);

        selfCheckBt.setEnabled(false);
        checkDistanceBt.setEnabled(false);
        measureBt.setEnabled(false);
        endTestBt.setEnabled(true);

        selfCheckResultTx = (TextView) findViewById(R.id.selfCheck_result);
        distanceCheckResultTx = (TextView) findViewById(R.id.distance_result);
        electricCheckResultTx = (TextView) findViewById(R.id.measure_result);

        image1 = (ImageView) findViewById(R.id.electric_status2);
        image2 = (ImageView) findViewById(R.id.distance_status2);
        image3 = (ImageView) findViewById(R.id.measure_status2);


        progressBar0 = (ImageView) findViewById(R.id.electric_status);
        progressBar2 = (ImageView) findViewById(R.id.distance_status);
        progressBar3 = (ImageView) findViewById(R.id.measure_status);
//	    // 加载动画  
        hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                TestElectricActivity.this, R.anim.loading_animation);
        toast = new Toast(getApplicationContext());
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

            case R.id.checkElectricBt:
                selfCheckResultTx.setText("");
                distanceCheckResultTx.setText("");
                electricCheckResultTx.setText("");

                progressBar0.startAnimation(hyperspaceJumpAnimation);
                if (!startSelfCheck()) {
                    progressBar0.clearAnimation();
                }

                break;
            case R.id.checkDistanceBt:
                progressBar2.startAnimation(hyperspaceJumpAnimation);
                if (!startDistanceCheck()) {
                    progressBar2.clearAnimation();
                }


                break;
            case R.id.measureBt:
                selfCheckBt.setEnabled(false);
                checkDistanceBt.setEnabled(false);
                measureBt.setEnabled(false);
                endTestBt.setEnabled(true);
                progressBar3.startAnimation(hyperspaceJumpAnimation);
                if (!startElectricCheck()) {
                    progressBar3.clearAnimation();
                }
                break;
            case R.id.endTestBt:
                saveElectricResult();
                reset();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.exit:
                Intent intent1 = new Intent();
                intent1.setClass(TestElectricActivity.this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 注意本行的FLAG设置
                startActivity(intent1);
                break;
            case R.id.shutUpBt:
                shutUpDevice();
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
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        if (connect_dialog != null && connect_dialog.isShowing())
            connect_dialog.dismiss();
        dataProcess.stopConn();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
        toast.cancel();
        toast = null;
    }

    private boolean startSelfCheck() {

        if (dataProcess.sendCmd(Constants.cmd_self_check, 5)) {
            return true;
        } else {
            showMessage(getApplicationContext(),
                    "发送命令失败");
        }
        return false;
    }

    private boolean sendCommonCmd(byte[] cmd) {

        if (dataProcess.sendCmd(cmd, 5)) {
            return true;
        } else {
            showMessage(getApplicationContext(),
                    "发送命令失败");
        }
        return false;
    }

    private boolean startDistanceCheck() {
        if (dataProcess.sendCmd(Constants.cmd_distance_check, 5)) {
            return true;
        } else {
            showMessage(getApplicationContext(),
                    "发送命令失败");
        }
        return false;
    }

    private boolean startElectricCheck() {
        if (dataProcess.sendCmd(Constants.cmd_mearsure, 5)) {
			/*showMessage(getApplicationContext(),
					"Send cmd_mearsure success.");*/
            return true;
        } else {
            showMessage(getApplicationContext(),
                    "发送命令失败");
        }
        return false;
    }

    HandleMsg hMsg = new HandleMsg() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            if (dataProcess == null) {
                showMessage(getApplicationContext(), "连接设备错误");
                return;
            }
            if (msg.what == dataProcess.CLOSETCP) { //断开连接
                showMessageShort(getApplicationContext(), "设备连接断开");
                dataProcess.stopConn();
            } else if (msg.what == Constants.DEVICE_TYPE_OK) {
                //connect_dialog.dismiss();
                connect_dialog.setMessage("连接设备成功");
                //showMessage(getApplicationContext(), "连接设备成功");
                dataProcess.sendCmd(Constants.cmd_device_test, 5);  //连接成功的指令
            } else if (msg.what == Constants.DEVICE_TYPE_ERROR) {
                if (connect_dialog != null && connect_dialog.isShowing()) {
                    connect_dialog.dismiss();
                }
                showMessage(getApplicationContext(), "连接设备超时");
                dataProcess.stopConn();
                selfCheckBt.setEnabled(false);
                selfCheckBt.setBackgroundResource(R.drawable.unuse_bg);
            } else if (msg.what == Constants.SEND_CMD_OK) {
                showMessage(getApplicationContext(), "发送命令成功");
            } else if (msg.what == Constants.SEND_CMD_ERROR) {
                showMessage(getApplicationContext(), "发送命令失败");
            } else if (msg.what == Constants.DEVICE_ID_OK) {
                connect_dialog.dismiss();
                showMessage(getApplicationContext(), "设备识别码匹配正确");
                selfCheckBt.setEnabled(true);
                selfCheckBt.setBackgroundResource(R.drawable.finish_bg);
                device_id_check = true;
            } else if (msg.what == Constants.DEVICE_ID_ERROR) {
                connect_dialog.setMessage("设备识别码匹配错误！");
                //connect_dialog.dismiss();
                showMessage(getApplicationContext(), "设备识别码匹配错误");
                device_id_check = false;
                selfCheckBt.setEnabled(false);
                selfCheckBt.setBackgroundResource(R.drawable.unuse_bg);
            } else if (msg.what == Constants.DEVICE_ID_RESEND) {
                dataProcess.sendCmd(cmd_deviceId_send, 7);
            } else if (msg.what == Constants.DEVICE_ID_SEND) {
                connect_dialog.setMessage("发送设备识别码，等待结果返回");
                if (dataProcess.sendCmd(cmd_deviceId_send, 7)) {
                    showMessage(getApplicationContext(), "发送设备识别码成功");
                } else {
                    showMessage(getApplicationContext(), "发送设备识别码失败");
                }
            } else if (msg.what == Constants.SHOW) {
                showMessage(getApplicationContext(), "Received CMD is " + msg.arg1
                        + msg.arg2 + ".");
            } else if (msg.what == Constants.SELF_CHECK_OK) {
                showMessage(getApplicationContext(), "设备自检成功");
                showVoice(9);
                selfCheckResultTx.setText("自检通过！");
                checkDistanceBt.setEnabled(true);
                selfCheckBt.setEnabled(false);
                checkDistanceBt.setBackgroundResource(R.drawable.finish_bg);
                progressBar0.setVisibility(View.GONE);
                progressBar0.clearAnimation();
                image1.setVisibility(View.VISIBLE);

            } else if (msg.what == Constants.SELF_CHECK_FAIL) {
                selfCheckResultTx.setText("自检失败,传感器电路故障！");
                showVoice(7);
                //reset();
                progressBar0.clearAnimation();
                progressBar0.setVisibility(View.VISIBLE);
                image1.setVisibility(View.GONE);
            } else if (msg.what == Constants.SELF_CHECK_FAIL2) {
                selfCheckResultTx.setText("自检失败，电源欠压、电路故障");
                showVoice(6);
                //reset();
                progressBar0.clearAnimation();
                progressBar0.setVisibility(View.VISIBLE);
                image1.setVisibility(View.GONE);
            } else if (msg.what == Constants.CHECK_VOL_LOW) {
                selfCheckResultTx.setText("自检失败,电源电压低");
                showMessage(getApplicationContext(), "电源电压低，请充电");
                showVoice(8);
                progressBar0.clearAnimation();
                progressBar0.setVisibility(View.VISIBLE);
                image1.setVisibility(View.GONE);
            } else if (msg.what == Constants.DISTANCE_CHECK_OK) {
                showMessage(getApplicationContext(), "距离合适");
                distanceCheckResultTx.setText("距离合适");
                checkDistanceBt.setEnabled(false);
                measureBt.setBackgroundResource(R.drawable.finish_bg);
                measureBt.setEnabled(true);
                showVoice(12);
                progressBar2.setVisibility(View.GONE);
                progressBar2.clearAnimation();
                image2.setVisibility(View.VISIBLE);
                // 打圈 打钩
            } else if (msg.what == Constants.DISTANCE_CHECK_FAR) {
                distanceCheckResultTx.setText("超过范围,请调整位置");
                progressBar2.clearAnimation();
                showMessage(getApplicationContext(), "超过范围,请调整位置");
                showVoice(10);
                progressBar2.setVisibility(View.VISIBLE);
                image2.setVisibility(View.GONE);
            } else if (msg.what == Constants.DISTANCE_CHECK_NEAR) {
                distanceCheckResultTx.setText("距离过近,请调整位置");
                progressBar2.clearAnimation();
                showMessage(getApplicationContext(), "距离过近,请调整位置");
                showVoice(11);
                progressBar2.setVisibility(View.VISIBLE);
                image2.setVisibility(View.GONE);
            } else if (msg.what == Constants.MEASURE_CHECK_DEC) {
                electricCheckResultTx.setText("负极带电运行");
                progressBar3.clearAnimation();
                showVoice(2);
                progressBar3.setVisibility(View.VISIBLE);
                image3.setVisibility(View.GONE);
            } else if (msg.what == Constants.MEASURE_CHECK_PLUS) {
                electricCheckResultTx.setText("正极带电运行");
                progressBar3.clearAnimation();
                showVoice(5);
                progressBar3.setVisibility(View.VISIBLE);
                image3.setVisibility(View.GONE);
            } else if (msg.what == Constants.MEASURE_CHECK_OK) {
                electricCheckResultTx.setText("设备不带电");
                progressBar3.clearAnimation();
                showVoice(4);
                progressBar3.setVisibility(View.VISIBLE);
                image3.setVisibility(View.GONE);
            } else if (msg.what == Constants.MEASURE_CHECK_INDUCE) {
                electricCheckResultTx.setText("线路带电运行");
                progressBar3.clearAnimation();
                showVoice(3);
                progressBar3.setVisibility(View.VISIBLE);
                image3.setVisibility(View.GONE);
            } else if (msg.what == Constants.MEASURE_CHECK_ADD) {
                electricCheckResultTx.setText("线路带感应电压");
                progressBar3.clearAnimation();
                showVoice(1);
                progressBar3.setVisibility(View.VISIBLE);
                image3.setVisibility(View.GONE);
            }


        }
    };


    private void saveElectricResult() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String dataStr = formatter.format(curDate);
        DBOpenHelper dbOpenHelper = new DBOpenHelper(getApplicationContext());
        testResult = electricCheckResultTx.getText().toString();
        if (TextUtils.isEmpty(testResult)) {
            testResult = distanceCheckResultTx.getText().toString();
            if (TextUtils.isEmpty(testResult)) {
                testResult = selfCheckResultTx.getText().toString();
            }
        }
        ElectricData electricData = new ElectricData(dataStr, testResult, MainActivity.userNick);
        if (dbOpenHelper.getCount() == 30) {
            dbOpenHelper.deleteFirstElectricData();
        }
        dbOpenHelper.save(electricData);
    }

    public void showMessage(Context context, String msg) {
        if (toast != null) {
            toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void showMessageShort(Context context, String msg) {
        if (toast != null) {
            toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void reset() {

        dataProcess.stopConn();
        selfCheckBt.setEnabled(true);
        checkDistanceBt.setEnabled(true);
        measureBt.setEnabled(true);
        if (device_id_check) {
            selfCheckBt.setBackgroundResource(R.drawable.finish_bg);
        } else {
            selfCheckBt.setBackgroundResource(R.drawable.unuse_bg);
        }
        checkDistanceBt.setBackgroundResource(R.drawable.unuse_bg);
        measureBt.setBackgroundResource(R.drawable.unuse_bg);

        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);

        progressBar0.clearAnimation();
        progressBar2.clearAnimation();
        progressBar3.clearAnimation();

    }

    private boolean shutUpDevice() {

        if (dataProcess.sendCmd(Constants.cmd_shutup, 5)) {
            if (timer != null) {
                timer.cancel();
            }
        } else {
            showMessage(getApplicationContext(),
                    "发送命令失败");
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        return false;
    }

    public void showVoice(int type) {
        // 创建media player
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        boolean isLoop = false;
        try {
            mMediaPlayer.reset();
            switch (type) {
                case 1:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.add));
                    isLoop = true;
                    break;
                case 2:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.dec));
                    isLoop = true;
                    break;
                case 3:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.induce));
                    isLoop = true;
                    break;
                case 4:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.ok));
                    isLoop = true;
                    break;
                case 5:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.plus));
                    isLoop = true;
                    break;
                case 6:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.check_fail_battery));
                    isLoop = true;
                    break;
                case 7:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.check_fail_circuit));
                    isLoop = true;
                    break;
                case 8:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.check_fail_power));
                    isLoop = true;
                    break;
                case 9:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.check_success));
                    isLoop = false;
                    break;
                case 10:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.distance_far));
                    isLoop = true;
                    break;
                case 11:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.distance_near));
                    isLoop = true;
                    break;
                case 12:
                    mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.distance_right));
                    isLoop = false;
                    break;
                default:
                    break;
            }
            mMediaPlayer.setLooping(isLoop);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
