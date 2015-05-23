package com.likeshare.video;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.win32.JWMediaPlayer;
import chrriis.dj.nativeswing.swtimpl.components.win32.WMPControls;

public class VideoPlayer extends JPanel
{
	private JWMediaPlayer player;
	private static boolean b = false;
	public VideoPlayer()
	{
		this.setLayout(new BorderLayout());
		NativeInterface.open();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				load();
			}
		});
		this.setVisible(true);
	}

	public void load()
	{
		player = new JWMediaPlayer(null);
		player.setControlBarVisible(true);
		System.out.println("tt");
		//player.load(youtubeURL);
		this.add(player,BorderLayout.CENTER);
	}

	public void lo(final String path,final int p)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				player.load(path);
				WMPControls wc = player.getWMPControls();
				wc.setAbsolutePosition(p);
			}

		}).start();
	}
	public static void main(String[] args)
	{

	}
}