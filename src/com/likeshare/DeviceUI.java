package com.likeshare;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JLabel;

import com.likeshare.listener.FileDropTargetListener;
import com.likeshare.net.LikeShareService;
import com.likeshare.tree.DeviceNote;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class DeviceUI extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2140661980305355378L;
	private JPanel contentPane;
	private JTextField txtPcpath;
	private JLabel lblTransimage;
	private boolean dropSwitch = true;
	private LikeShareService lss;
	private DeviceNote dn;
	/**
	 * Create the frame.
	 */
	public DeviceUI(DeviceNote dn,LikeShareService lss)
	{
		this.lss = lss;
		this.dn = dn;
		setResizable(false);
		setTitle(dn.getDeviceName());
		
		File tmp = new File("C:/downloads");
		if(!tmp.exists())
			tmp.mkdir();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100,100,290,202);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtPcpath = new JTextField();
		txtPcpath.setEditable(false);
		txtPcpath.setText("C:/downloads");
		txtPcpath.setBounds(10,10,176,21);
		contentPane.add(txtPcpath);
		txtPcpath.setColumns(10);

		JButton selectPath = new JButton("\u8DEF\u5F91");
		selectPath.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String path = getRoute();
				txtPcpath.setText(path);
			}
		});
		selectPath.setBounds(205,9,70,23);
		contentPane.add(selectPath);
		

		lblTransimage = new JLabel();
		lblTransimage.setBounds(90,49,107,93);
		// lblTransimage.setIcon(new
		lblTransimage.setIcon(new ImageIcon(DeviceUI.class.getResource("/com/likeshare/image/trans_ok.png")));
		contentPane.add(lblTransimage);
		new FileDropTargetListener(lblTransimage,this);
	}
	
	public String getRoute()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("C:/downloads"));
		chooser.setDialogTitle("選擇儲存路徑");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setApproveButtonText("確定");
		chooser.setAcceptAllFileFilterUsed(false);
		//
		if(chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION)
		{
			LikeShareService.path = chooser.getSelectedFile().getPath();
			return chooser.getSelectedFile().getPath();
		} else
		{
			File tmp = new File("C:/downloads");
			LikeShareService.path = "C:/downloads";
			return tmp.getPath();
		}
	}

	public void setImage(boolean b)
	{
		if(b)
		{
			lblTransimage.setIcon(new ImageIcon(DeviceUI.class
					.getResource("/com/likeshare/image/trans_ok.png")));
		} else
		{
			lblTransimage.setIcon(new ImageIcon(DeviceUI.class
					.getResource("/com/likeshare/image/trans_now.png")));
		}
	}

	public void transHold(boolean b)
	{
		if(b)
		{
			lblTransimage.setIcon(new ImageIcon(DeviceUI.class
					.getResource("/com/likeshare/image/trans_hold.png")));
		} else
		{
			lblTransimage.setIcon(new ImageIcon(DeviceUI.class
					.getResource("/com/likeshare/image/trans_ok.png")));
		}
	}

	public boolean isDropSwitch()
	{
		return dropSwitch;
	}

	public void setDropSwitch(boolean dropSwitch)
	{
		this.dropSwitch = dropSwitch;
	}
	
	public void transfer(String path)
	{
		setDropSwitch(false);
		setImage(false);
		String mac = dn.getDeviceMac();
		try
		{
			lss.connectImage(mac,path);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
