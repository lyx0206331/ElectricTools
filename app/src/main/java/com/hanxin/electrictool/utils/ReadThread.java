package com.hanxin.electrictool.utils;

import java.io.IOException;
import java.net.Socket;


import android.content.Context;
import android.os.Message;
import android.util.Log; 
import android.widget.Toast;

public class ReadThread extends Thread { 
	boolean state;
	HandleMsg hOptMsg;
	byte[] sData;
	Socket socket;
	Context mContext;
	public ReadThread(HandleMsg hmsg,byte[] sData,Socket socket,Context context)
	{
		hOptMsg=hmsg; 
		this.sData=sData;
		this.socket=socket;
		this.mContext =context;
	}
	/*  
	 * @see java.lang.Thread#run()
	 * 线程接收主循环
	 */

	public void run() 
	 {
		int rlRead;  
		state=true;
		try 
		{
			while(state)
			{
				rlRead=socket.getInputStream().read(sData);//对方断开返回-1
				if(rlRead>0)
				{
					 unpackageElectricCmd(sData,rlRead);
				}
				else 
				{
			    	state=false;
			    	hOptMsg.sendEmptyMessage(DataProcess.CLOSETCP);
 				}
			}
	    }
	    catch (Exception e) {
	    	Log.v("tcpserver",e.getMessage());
	    	state=false;
	    	hOptMsg.sendEmptyMessage(DataProcess.CLOSETCP);
	    }
		finally{
			hOptMsg.sendEmptyMessage(DataProcess.CLOSETCP);
		}
	 }
	
	
	
	public void abortRead()
	{
		if(socket==null) return;
		try 
		{
		   socket.shutdownInput();
		   socket.shutdownOutput();
		   socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		state = false;	
	}
	
	public void unpackageElectricCmd(byte[] cmd,int len)
	{
		int size=len;
		Message msg0=new Message();
		msg0.what=Constants.SHOW;
		msg0.arg1=cmd[1];
		msg0.arg2=cmd[2];
		hOptMsg.sendMessage(msg0);
		if(size<5) return;
		
		int i;
		
		boolean cmdStart=false;
		int cmdInx = 0;
			if(cmdStart==false)
			{
				if(cmd[0]==(byte)0x7E&&cmd[4]==(byte)0xE7) 
				{
					cmdStart=true;
					cmdInx  =0;
				}
			}
			if(cmdStart==true)
			{
				cmdInx++;
				if (cmdStart) {

					if(hOptMsg==null) return;
					
					Message msg=new Message();
					if (cmd[1]==(byte)0x40&&cmd[2]==(byte)0x41&&cmd[3]==(byte)0x01) {
						msg.what=Constants.DEVICE_ID_OK;
						msg.arg1=cmd[1];
						msg.arg2=cmd[2];
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x40&&cmd[2]==(byte)0x42&&cmd[3]==(byte)0x02) {
						msg.what=Constants.DEVICE_ID_RESEND;
						msg.arg1=cmd[1];
						msg.arg2=cmd[2];
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x40&&cmd[2]==(byte)0x43&&cmd[3]==(byte)0x03) {
						msg.what=Constants.DEVICE_ID_SEND;
						msg.arg1=cmd[1];
						msg.arg2=cmd[2];
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x40&&cmd[2]==(byte)0x44&&cmd[3]==(byte)0x04) {
						msg.what=Constants.DEVICE_ID_ERROR;
						msg.arg1=cmd[1];
						msg.arg2=cmd[2];
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x30&&cmd[2]==(byte)0x31&&cmd[3]==(byte)0x01) {
						msg.what=Constants.DISTANCE_CHECK_NEAR;
						hOptMsg.sendMessage(msg);
					}
					else if (cmd[1]==(byte)0x30&&cmd[2]==(byte)0x32&&cmd[3]==(byte)0x02) {
						msg.what=Constants.DISTANCE_CHECK_OK;
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x30&&cmd[2]==(byte)0x33&&cmd[3]==(byte)0x03) {
						msg.what=Constants.DISTANCE_CHECK_FAR;
						hOptMsg.sendMessage(msg);
					} 
					else if (cmd[1]==(byte)0x01&&cmd[2]==(byte)0x03&&cmd[3]==(byte)0x02) {
						msg.what=Constants.CHECK_VOL_LOW;
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x01&&cmd[2]==(byte)0x02&&cmd[3]==(byte)0x03) {//自检成功
						msg.what=Constants.SELF_CHECK_OK;
						hOptMsg.sendMessage(msg);
					}
					else if (cmd[1]==(byte)0x01&&cmd[2]==(byte)0x04&&cmd[3]==(byte)0x05) {//自检失败
						msg.what=Constants.SELF_CHECK_FAIL;
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x01&&cmd[2]==(byte)0x06&&cmd[3]==(byte)0x07) {
						msg.what=Constants.SELF_CHECK_FAIL2;
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x10&&cmd[2]==(byte)0x12&&cmd[3]==(byte)0x02) {
						msg.what=Constants.MEASURE_CHECK_DEC;
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x10&&cmd[2]==(byte)0x13&&cmd[3]==(byte)0x03) {
						msg.what=Constants.MEASURE_CHECK_PLUS;
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x10&&cmd[2]==(byte)0x14&&cmd[3]==(byte)0x04) {
						msg.what=Constants.MEASURE_CHECK_OK;
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x10&&cmd[2]==(byte)0x15&&cmd[3]==(byte)0x05) {
						msg.what=Constants.MEASURE_CHECK_INDUCE;
						hOptMsg.sendMessage(msg);
					}else if (cmd[1]==(byte)0x10&&cmd[2]==(byte)0x16&&cmd[3]==(byte)0x06) {
						msg.what=Constants.MEASURE_CHECK_ADD;
						hOptMsg.sendMessage(msg);
					}						

				}
				
			}			

	}
	
	public void showMessage(Context context,String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	 }
	
	
}










