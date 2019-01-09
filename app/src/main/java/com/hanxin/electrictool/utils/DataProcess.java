package com.hanxin.electrictool.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class DataProcess {
	
	public static byte RELAYOPT=1;
	public static byte RELAYCHK=2;
	public static byte RELAYSTATE=3;
	public static byte APPQUIT=4;	
	public static byte CLOSETCP=5;
	public static byte MAINMENU=6;
	
 
	
   protected Socket socket;//Socket 数据  
   //目标端口  
    public boolean State;
 	private byte[] sData=new byte[128];//接收缓存
 	ReadThread readThread=null;
 	HandleMsg hOptMsg=null;
 	Context mContext=null;
	 /**
	 * @param s
	 * @param ctrlHandle
	 */
 	public DataProcess(HandleMsg hmsg,Context context) 
 	{ 
		socket  = new Socket();
		hOptMsg=hmsg;
		mContext=context;
	}
	
	public boolean sendData(byte[] data) throws IOException {
		// TODO Auto-generated method stub
		OutputStream out=socket.getOutputStream();
		if(out==null) return false;
		out.write(data);
		return true;
	}
	
	public boolean sendData(String test) throws IOException {
		// TODO Auto-generated method stub
		OutputStream out=socket.getOutputStream();
		if(out==null) return false;
		out.write(test.getBytes());
		return true;
	}
	
	public boolean stopConn() {
		State=false;
		if(readThread==null) return false;
		readThread.abortRead();
		readThread=null;
		return false;
	}

 
	public boolean startConn( String  ip,int port) { 
		if(socket.isClosed()) socket=new Socket();
		SocketAddress remoteAddr=new InetSocketAddress(ip,port);
		try {
			socket.connect(remoteAddr, 2000);
		} catch (IOException e) {
			socket=new Socket();
			Log.v("tcpserver", e.getMessage());
			return false;
		}
		this.readThread=new ReadThread(hOptMsg, sData,socket,mContext);
		readThread.start();
		State=true;
		return true; 
	}	
	
	public boolean sendCmd(byte[] cmd,int len)
	{
		if(cmd==null) return false;
		
		if((readThread==null)||(readThread.state==false))
		{
				hOptMsg.sendEmptyMessage(Constants.DEVICE_TYPE_ERROR);
				return false;		
		}
		try {
			if (sendData(cmd)) {
				return true;
			}else {
				return false;	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hOptMsg.sendEmptyMessage(DataProcess.CLOSETCP);
			return false;
		} 
	}
	
	public static void showMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	public static void showMessage(Context context, String msg,int time) {
		Toast.makeText(context, msg, time).show();
	}
}
