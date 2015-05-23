package com.likeshare.net.bio;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;

import com.likeshare.DeviceUI;
import com.likeshare.ProgressUI;
import com.likeshare.net.LikeShareService;
import com.likeshare.video.VideoPlayer;

public class Transfer
{
	public ClientAdapter ca = null;
	public static int transferLength = 0;
	public static int downLength = 0;
	public String fileName = null;
	public int fileSize = 0;
	public static boolean EnoughSpace = false;
	private String[] Msg;
	private File file = null; // 產生的檔案
	public boolean stop = false;
	protected static boolean turnoff = false;
	private DeviceUI deviceUI;
	private ProgressUI pui;
	private int cp = 0;
	private boolean cpb = false;
	/*-----------連接Socket方法-----------*/
	public void socketClose()
	{
		ca.socketClose();
	}
	public boolean setAddress(String ip,int port)throws Exception
	{
		ca = new ClientAdapter(ip,port); // 設定IP
		boolean b = ca.messageConnect(5000); // 設定訊息通道
		if(b)
		{
			ca.fileConnect();
			return true;
		} else
		{
			return false;
		}
	}

	public Thread Receiver = new Thread(new Runnable()
	{
		public void run()
		{
			while(true)
			{

				try
				{
					Msg = ca.waitMessage().split(":");
				} catch(Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				if(Msg[0].toString().equals("11")) // 11 為檔案傳送
				{
						new Thread(new Runnable()
						{
							public void run()
							{
								cpb = true;
								downFile(); // 下載檔案
							}
						}).start();
				}
			}
		}
	});
	
	public Thread VideoReceiver = new Thread(new Runnable()
	{
		public void run()
		{
			while(true)
			{

				try
				{
					Msg = ca.waitMessage().split(":");
				} catch(Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				if(Msg[0].toString().equals("11")) // 11 為檔案傳送
				{
						new Thread(new Runnable()
						{
							public void run()
							{
								cpb = false;
								if(cp != 0)
								{
									downFile(); // 下載檔案
									JFrame frame = new JFrame("Video Player");
									frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
									VideoPlayer sv = new VideoPlayer();
									sv.lo(LikeShareService.path+"/"+ Msg[1],cp);
									frame.add(sv,BorderLayout.CENTER);
									frame.setSize(800,600);
									frame.setLocationByPlatform(true);
									frame.setVisible(true);
								}else
								{
									new Thread(new Runnable()
									{
										public void run()
										{
											try {
												while(downLength < 0)
													Thread.sleep(2000);
											} catch (InterruptedException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											
											JFrame frame = new JFrame("Video Player");
											frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
											VideoPlayer sv = new VideoPlayer();
											sv.lo(LikeShareService.path+"/"+ Msg[1],cp);
											frame.add(sv,BorderLayout.CENTER);
											frame.setSize(800,600);
											frame.setLocationByPlatform(true);
											frame.setVisible(true);
										}
									}).start();
									downVideoFile();
								}
							}
						}).start();
				}
			}
		}
	});
	
	private void downVideoFile()
	{
		stop = false;
		int len;
		int max_len = 0;
		fileSize = Integer.parseInt(Msg[2]);
		byte[] buffer = new byte[8192];
		FileOutputStream fos;
		if(!(new File(LikeShareService.path).exists())) // 文件夾不存在
		{
			new File(LikeShareService.path).mkdir();// 文件夾不存在
		}
		file = new File(LikeShareService.path+"/"+ Msg[1]);
		fileName = Msg[1];
		pui.setFileName(fileName);
		pui.setMaxValue(fileSize);
		pui.startProgress.start();
		try
		{
			fos = new FileOutputStream(file);
			while((len = ca.readData(buffer)) != -1 && !stop)
			{
				max_len += len;
				fos.write(buffer,0,len);
				downLength = max_len;
				if(fileSize == max_len)
					break;
			}
			// downLength = 0;
			// sa.setFileOver(0);
			fos.flush();
			fos.close();
			pui.setStop(true);
			pui.dis();
			if(stop)
			{
				file.delete(); // 下載失敗 清除
			}
//			Server_Service.setMyDownloads(Msg[1]);
		} catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			file.delete(); // 下載失敗 清除
			stop = true;
			pui.setStop(true);
			pui.dis();
		}
		if(!stop && cpb)
			try
			{
				openDefault();
			} catch(IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
	}
	
	private void downFile()
	{
		stop = false;
		int len;
		int max_len = 0;
		fileSize = Integer.parseInt(Msg[2]);
		byte[] buffer = new byte[8192];
		FileOutputStream fos;
		if(!(new File(LikeShareService.path).exists())) // 文件夾不存在
		{
			new File(LikeShareService.path).mkdir();// 文件夾不存在
		}
		file = new File(LikeShareService.path+"/"+ Msg[1]);
		fileName = Msg[1];
		pui.setFileName(fileName);
		pui.setMaxValue(fileSize);
		pui.startProgress.start();
		try
		{
			fos = new FileOutputStream(file);
			while((len = ca.readData(buffer)) != -1 && !stop)
			{
				max_len += len;
				fos.write(buffer,0,len);
				downLength = max_len;
				if(fileSize == max_len)
					break;
			}
			// downLength = 0;
			// sa.setFileOver(0);
			fos.flush();
			fos.close();
			pui.setStop(true);
			pui.dis();
			if(stop)
			{
				file.delete(); // 下載失敗 清除
			}
//			Server_Service.setMyDownloads(Msg[1]);
		} catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			file.delete(); // 下載失敗 清除
			stop = true;
			pui.setStop(true);
			pui.dis();
		}
		if(!stop && cpb)
			try
			{
				openDefault();
			} catch(IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
	}

	/* ---------- 自定義方法 ------------------ */
	public void transfer(String path) throws Exception
	{
		stop = false;
		transferLength = 0;
		File f = new File(path);
		FileInputStream fis;
		fileName = f.getName();
		fis = new FileInputStream(path);
		fileSize = fis.available(); // 取得檔案大小
		System.out.println(77);
		ca.sendMessage("11:" + fileName + ":" + fileSize); // 傳送檔案資訊
		System.out.println(88);
		String s = ca.waitMessage(); // 等待訊息
		System.out.println("gg");
		pui.setFileName(fileName);
		pui.setMaxValue(fileSize);
		pui.startProgress.start();
		if(s.toString().equals("Y"))
		{
			EnoughSpace = true;
			int len;
			byte[] buffer = new byte[8192];
			while((len = fis.read(buffer)) != -1 && !stop)
			{
				ca.sendData(buffer,0,len);
				transferLength += len;
			}
			fis.close();
			pui.setStop(true);
			pui.dis();
			deviceUI.setDropSwitch(true);
			deviceUI.setImage(true);
			if(stop)
			{
				ca.socketClose();
			}
			// ca.sendMessage("fileOver");
		} else
		{
			// 空間不足事件
			EnoughSpace = false; // 空間不足
		}
//		FileManager.setSensorSwitch(true);// 開啟sensor

	}
	private void openDefault() throws IOException, InterruptedException
	{
		Desktop dt = Desktop.getDesktop();
		System.out.println(file.getPath());
		String fName = file.getName();
		System.out.println(fName);
		String end = fName.substring(fName.lastIndexOf(".") + 1,fName.length())
				.toLowerCase();
		if(end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav"))
		{
			dt.open(file);
		} else if(end.equals("3gp") || end.equals("mp4") || end.equals("rmvb")
				|| end.equals("avi"))
		{
			dt.open(file);
		} else if(end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp"))
		{
			dt.open(file);
		}
	}

	public DeviceUI getDeviceUI()
	{
		return deviceUI;
	}

	public void setDeviceUI(DeviceUI deviceUI)
	{
		this.deviceUI = deviceUI;
	}
	
	public void setProgressUI(ProgressUI pui)
	{
		this.pui = pui;
	}
	public int getCp()
	{
		return cp;
	}
	public void setCp(int cp)
	{
		this.cp = cp;
	}
}
