package com.likeshare.net.bio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Receiver
{
	protected ServerAdapter sa = null;
	private String[] Msg;
	private File file = null; // 產生的檔案
	protected static int downLength;
	protected static int Port = 8801;
	protected static boolean turnoff = false;
	public static int transferLength = 0;
	public String fileName = null;
	public int fileSize = 0;
	public static boolean EnoughSpace = false;

	/**
	 * 設定接收端口
	 * @return 成功或失敗
	 * @throws Exception
	 */
	public boolean setSocket() throws Exception
	{
		sa = new ServerAdapter(Port);
		boolean b = sa.messageListen(10000);
		if(b)
		{
			sa.fileListen();
			sa.socketClose();
			return true;
		} else
		{
			sa.socketClose();
			return false;
		}
	}
	/**
	 * 執行序等待命令
	 * 11檔案傳送
	 */
	Thread Receiver = new Thread(new Runnable()
	{
		public void run()
		{
				try
				{
					Msg = sa.waitMessage().split(":");
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
								downFile(); // 下載檔案
							}
						}).start();
				}
		}
	});

	private void downFile()
	{
		int len;
		int max_len = 0;
		int max = Integer.parseInt(Msg[2]);
		byte[] buffer = new byte[8192];
		FileOutputStream fos;
		try
		{
			fos = new FileOutputStream(file);
			while((len = sa.readData(buffer)) != -1)
			{
				max_len += len;
				fos.write(buffer,0,len);
				downLength = max_len;
				if(max == max_len)
					break;
			}
			// downLength = 0;
			// sa.setFileOver(0);
			fos.flush();
			fos.close();
			//Server_Service.setMyDownloads(Msg[1]);
		} catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			file.delete(); // 下載失敗 清除
		}
	}

	/**
	 * 開始傳送
	 * @param path
	 * @throws Exception
	 */
	protected void transfer(String path) throws Exception
	{
		transferLength = 0;
		File f = new File(path);
		FileInputStream fis;
		
		fis = new FileInputStream(path);
		sa.sendMessage("11:" + f.getName() + ":" + fis.available()); // 傳送檔案資訊
													// 檔案大小
		String s = sa.waitMessage(); // 等待訊息
		System.out.println("gg");
		if(s.toString().equals("Y"))
		{
			EnoughSpace = true;
			int len;
			byte[] buffer = new byte[8192];
			while((len = fis.read(buffer)) != -1)
			{
				sa.sendData(buffer,0,len);
				transferLength += len;
			}
			fis.close();
			// ca.sendMessage("fileOver");
		} else
		{
			// 空間不足事件
			EnoughSpace = false; // 空間不足
		}
		//FileManager.setSensorSwitch(true);// 開啟sensor

	}


	public String getAddress()
	{
		return sa.getFileSocket().getLocalAddress()+","+sa.getFileSocket().getPort(); 
	}

}
