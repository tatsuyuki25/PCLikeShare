package com.likeshare;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeSelectionModel;

import com.likeshare.net.LikeShareService;
import com.likeshare.tree.DeviceTreeCellRenderer;

import javax.swing.JTree;
import javax.swing.JScrollPane;

public class LikeShareMainUI extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8843276072207251594L;
	private LikeShareService lss;
	private JPanel contentPane;
	private JTree devices;
	/**
	 * Create the frame.
	 */
	public LikeShareMainUI(LikeShareService lss)
	{
		this.lss = lss;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100,100,348,729);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 312, 670);
		contentPane.add(scrollPane);

		devices = this.lss.dm.getDeviceTree();
		devices.getSelectionModel().setSelectionMode  (TreeSelectionModel.SINGLE_TREE_SELECTION);  

		DeviceTreeCellRenderer dtcr = new DeviceTreeCellRenderer();
		devices.setCellRenderer(dtcr);
		scrollPane.setViewportView(devices);
		getMyDevice();
	}
	private void getMyDevice()
	{
		try
		{
			this.lss.sendMsg("2,"+this.lss.getAccount());
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
