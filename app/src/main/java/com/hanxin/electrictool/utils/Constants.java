package com.hanxin.electrictool.utils;

public class Constants {
	
	public static String server_ip="10.10.100.254";
	public static int server_port=8899;
	public static String supportPhone="000000000";
	
	public static final byte DEVICE_TYPE_OK=7;     	
	public static final byte DEVICE_TYPE_ERROR=8;     
	public static final byte DEVICE_TYPE_RESEND=9;  
	public static final byte SHOW=10;   
	public static final byte SELF_CHECK_OK=11;  
	public static final byte SELF_CHECK_FAIL=12;  
	public static final byte DISTANCE_CHECK_OK=13;  
	public static final byte DISTANCE_CHECK_FAR=15;  
	public static final byte DISTANCE_CHECK_NEAR=16; 
	public static final byte MEASURE_CHECK_OK=17;  
	public static final byte MEASURE_CHECK_PLUS=18; 
	public static final byte MEASURE_CHECK_DEC=19; 
	public static final byte MEASURE_CHECK_INDUCE=20;
	public static final byte CHECK_VOL_NORMAL=21; 
	public static final byte CHECK_VOL_LOW=22;  
	public static final byte SEND_CMD_OK=23; 
	public static final byte SEND_CMD_ERROR=24; 
	public static final byte DEVICE_ID_OK=25; 
	public static final byte DEVICE_ID_ERROR=26; 
	public static final byte DEVICE_ID_SEND=27;  
	public static final byte DEVICE_ID_RESEND=28;  
	public static final byte SELF_CHECK_FAIL2=29; 
	public static final byte MEASURE_CHECK_ADD=30;
	
	public static byte[] cmd_device_test=new byte[]{(byte)0x7E,(byte)0x00,(byte)0x00,(byte)0x00,(byte) 0xE7};//设备测试指令
	public static byte[] cmd_device_error=new byte[]{(byte)0x7E,(byte)0x40,(byte)0x42,(byte)0x02,(byte) 0xE7};//设备识别码匹配错误、重新发送
	public static byte[] cmd_device_ok=new byte[]{(byte)0x7E,(byte)0x40,(byte)0x41,(byte)0x01,(byte) 0xE7};//设备识别码匹配正确
	public static byte[] cmd_device_send=new byte[]{(byte)0x7E,(byte)0x40,(byte)0x43,(byte)0x03,(byte) 0xE7};//发送设备识别码指令
	public static byte[] cmd_self_check=new byte[]{(byte)0x7E,(byte)0x01,(byte)0x05,(byte)0x04,(byte) 0xE7};//发送设备自检指令
	public static byte[] cmd_distance_check=new byte[]{(byte)0x7E,(byte)0x30,(byte)0x34,(byte)0x04,(byte) 0xE7};//发送测距指令
	public static byte[] cmd_mearsure=new byte[]{(byte)0x7E,(byte)0x10,(byte)0x11,(byte)0x01,(byte) 0xE7};//发送设备测量指令
	public static byte[] cmd_shutup=new byte[]{(byte)0x7E,(byte)0x60,(byte)0x61,(byte)0x01,(byte) 0xE7};//发送设备关机指令
	
}
