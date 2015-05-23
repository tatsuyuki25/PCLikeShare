package com.likeshare.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import javax.swing.JDialog;

import com.likeshare.DeviceUI;
import com.likeshare.LikeShareMainUI;
import com.likeshare.ProgressUI;
import com.likeshare.net.bio.ClientAdapter;
import com.likeshare.net.bio.Transfer;
import com.likeshare.tree.DevicesManager;

public class LikeShareService {
	
	private static String selectTransferPath;
	/**
	 * NIO
	 */
	// 信道選擇器
	private static Selector selector;
	// 與服務器通信的信道
	public static SocketChannel socketChannel;
	// 要連接的服務器Ip地址
	private static String hostIp = "220.133.107.221";
	// 要連接的遠程服務器在監聽的端口
	private static int hostListenningPort = 1978;

	/**
	 * BIO
	 */
	private Transfer tf;
	private String account; // 登入的帳號
	private ClientAdapter ca;
	private String serverIP = "220.133.107.221";
	public DevicesManager dm;
	private DeviceUI deviceUI;
	private LikeShareMainUI lsUI;
	public static String path = "C:/downloads";
	private ProgressUI dialog;
	
	public void startBioFile(String msg)
	{
		bioFileService(msg);
	}
	/**
	 * 停止執行檔案下載
	 */
	public synchronized void stopBioFileService() {
		dialog.setStop(true);
		dialog.dis();
		tf.stop = true; // 停止下載迴圈
		tf.socketClose();
	}
	
	public void setUIEnabled(boolean b)
	{
		if(deviceUI != null && deviceUI.isDisplayable())
			deviceUI.setEnabled(b);
		if(lsUI != null && lsUI.isDisplayable())
			lsUI.setEnabled(b);
	}
	
	private void bioFileService(String msg)
	{
		final String[] tmp = msg.split(","); // 6,port,R or T
		for(int i = 0;i < tmp.length;i++)
			System.out.println(i+""+tmp[i]);
		tf = new Transfer();
		tf.setDeviceUI(deviceUI);
		try {
			boolean typeb = tf.setAddress(serverIP,Integer.parseInt(tmp[1]));
			if(typeb)
			{
				if(tmp[2].equalsIgnoreCase("T")) // 傳送端 selectTransferPath
				{
					System.out.println(55);
					dialog = new ProgressUI(deviceUI.getTitle(),LikeShareService.this,true);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
					tf.setProgressUI(dialog);
					System.out.println(66);
					tf.transfer(selectTransferPath);
					
				}else // 接收端
				{
					dialog = new ProgressUI(dm.getFriendName(tmp[3]),LikeShareService.this,false);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
					tf.setProgressUI(dialog);
					tf.ca.sendMessage("true");
					tf.Receiver.start();
				}
			
			}
			else
			{
				ca.sendCommand("false");
				//ca.waitCommand();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(deviceUI != null)
			{
				deviceUI.setDropSwitch(true);
				deviceUI.setImage(true);
			}
			e.printStackTrace();
		}
	}
	
	public void bioVideoService(String msg)
	{
		final String[] tmp = msg.split(","); // 6,port,R or T
		tf = new Transfer();
		tf.setDeviceUI(deviceUI);
		try {
			boolean typeb = tf.setAddress(serverIP,Integer.parseInt(tmp[1]));
			if(typeb)
			{
				if(tmp[2].equalsIgnoreCase("T")) // 傳送端 selectTransferPath
				{
					
				}else // 接收端
				{
					dialog = new ProgressUI(dm.getFriendName(tmp[3]),LikeShareService.this,false);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
					tf.setProgressUI(dialog);
					tf.setCp(Integer.parseInt(tmp[4]));
					tf.ca.sendMessage("true");
					tf.VideoReceiver.start();
				}
			
			}
			else
			{
				ca.sendCommand("false");
				//ca.waitCommand();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(deviceUI != null)
			{
				deviceUI.setDropSwitch(true);
				deviceUI.setImage(true);
			}
			e.printStackTrace();
		}
	}
	/**
	 * 登入用 (完成)
	 * 
	 * @param account
	 * @param pass
	 * @throws IOException
	 * @throws Exception
	 */
	public void login(String account, String pass) throws Exception {

		if (!checkConnectionStatus()) {
			// 打開監聽信道並設置為非阻塞模式
			socketChannel = SocketChannel.open(new InetSocketAddress(hostIp,
					hostListenningPort));
			socketChannel.configureBlocking(false);

			// 打開並註冊選擇器到信道
			selector = Selector.open();
			socketChannel.register(selector, SelectionKey.OP_READ);
			dm = new DevicesManager(account,this);
			// 啟動讀取線程
			new nioClientReadThread(selector,dm,this);
		}
		// 送出 MAC

		String m = getMACAddress(); // MAC
		sendMsg("1," + account + "," + pass + "," + m + ",pc");

		/*
		 * String b = ca.waitMessage(); if (b.equals("true")) {
		 * this.setAccount(account); // Log.i("return", ca.waitMessage());
		 * Log.i("login", waitCommand.isAlive() + ""); waitCommand.start();
		 * return true; } else return false;
		 */
	}

	public void signUp(String account, String pass, String name)
			throws IOException {
		if (!checkConnectionStatus()) {
			// 打開監聽信道並設置為非阻塞模式
			socketChannel = SocketChannel.open(new InetSocketAddress(hostIp,
					hostListenningPort));
			socketChannel.configureBlocking(false);

			// 打開並註冊選擇器到信道
			selector = Selector.open();
			socketChannel.register(selector, SelectionKey.OP_READ);

			// 啟動讀取線程
			new nioClientReadThread(selector,dm,this);
		}
		sendMsg("0," + account + "," + pass + "," + name);
	}

	protected static boolean checkConnectionStatus() // 確認是否連線
	{

		try {
			socketChannel.socket().getRemoteSocketAddress();
			return true;
		} catch (Exception e) {
			return false;
		}

	}


	/**
	 * 發送字串到伺服器 (完成) 0註冊 1登入 2請求我的所有設備 3請求我的所有好友清單 4請求好友的設備 5請求好友名單
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMsg(String message) throws IOException {
		ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes("UTF-16"));
		socketChannel.write(writeBuffer);
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccount() {
		return this.account;
	}

	public void connectImage(String mac,String path) throws IOException {
		// TODO Auto-generated method stub
		sendMsg("5," + mac);
		selectTransferPath = path;
	}

   private static String getMACAddress()throws Exception
   {
   	InetAddress ia = InetAddress.getLocalHost();
      byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
      StringBuffer sb = new StringBuffer();
      for(int i=0;i<mac.length;i++){
      	System.out.println(mac[i]);
          if(i!=0){
              sb.append(":");
          }
          String s = Integer.toHexString(mac[i] & 0xFF);
          sb.append(s.length()==1?0+s:s);
      }
      return sb.toString().toUpperCase();
   }

	public DeviceUI getDeviceUI()
	{
		return deviceUI;
	}

	public void setDeviceUI(DeviceUI deviceUI)
	{
		this.deviceUI = deviceUI;
	}
	public LikeShareMainUI getLsUI()
	{
		return lsUI;
	}
	public void setLsUI(LikeShareMainUI lsUI)
	{
		this.lsUI = lsUI;
	}
}
