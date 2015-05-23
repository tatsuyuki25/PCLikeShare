package com.likeshare.tree;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.likeshare.DeviceUI;
import com.likeshare.net.LikeShareService;

/**
 * �Ω�޲z�]��tree
 * 
 * @author tatsuyuki
 * 
 */
public class DevicesManager
{
	private int FRIEND_DEVICES_UPDATE = 0;
	private String friendDevice;
	// key mac
	private HashMap<String, DefaultMutableTreeNode> myDevices = new HashMap<String, DefaultMutableTreeNode>();
	// key account
	private HashMap<String, DefaultMutableTreeNode> myFriends = new HashMap<String, DefaultMutableTreeNode>();
	// key mac
	private HashMap<String, DefaultMutableTreeNode> friendDevices = new HashMap<String, DefaultMutableTreeNode>();
	private DefaultMutableTreeNode top;
	private JTree deviceTree;
	private String myAccount;
	private LikeShareService lss;
	private DeviceUI frame = null;
	/**
	 * 
	 * @param myAccount
	 *           �b��
	 * @param lss
	 *           likeShare�A�Ⱦ�
	 */
	public DevicesManager(String myAccount,LikeShareService lss)
	{
		setTop(new DefaultMutableTreeNode("devices"));
		deviceTree = new JTree(getTop());
		this.myAccount = myAccount;
		this.lss = lss;
		deviceTree.addMouseListener(deviceTreeLinstener);
	}
	/**
	 * ��ťmouse��tree���ʧ@
	 */
	private MouseListener deviceTreeLinstener = new MouseAdapter()
	{
		public void mousePressed(MouseEvent e)
		{
			int selRow = deviceTree.getRowForLocation(e.getX(),e.getY());
			TreePath selPath = deviceTree.getPathForLocation(e.getX(),e.getY());
			if(selRow != -1)
			{
				if(e.getClickCount() == 2)
				{
					System.out.println(selRow + " " + selPath.getLastPathComponent());
					final DefaultMutableTreeNode deviceNote = (DefaultMutableTreeNode)selPath.getLastPathComponent();
					DeviceNote dn = null;
					if(deviceNote.getUserObject() instanceof DeviceNote)
					{
						dn = (DeviceNote)deviceNote.getUserObject();
					}
					if(deviceNote.isLeaf() && selRow != 2 && dn.isLogin())
					{
						EventQueue.invokeLater(new Runnable()
						{
							public void run()
							{
								try
								{
									if(frame != null)
										frame.dispose();
									frame = new DeviceUI((DeviceNote)deviceNote.getUserObject(),lss);
									lss.setDeviceUI(frame);
									frame.setVisible(true);
								} catch(Exception e)
								{
									e.printStackTrace();
								}
							}
						});
					}
				}
			}
		}
	};

	/**
	 * ���ͧڪ��]�ƪ�tree
	 * 
	 * @param devices
	 *           �q���A���^�Ǫ��]�Ʀr��
	 */
	public void createMyDevices(final String devices)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				DefaultMutableTreeNode category = new DefaultMutableTreeNode("my Devices");
				top.add(category);
				String[] tmp = devices.split(",");
				/*
				 * tmp[i] = type
				 * tmp[i+1] = ID
				 * tmp[i+2] = mac
				 * tmp[i+3] = ���A
				 */
				for(int i = 2;i < tmp.length;i += 4)
				{
					boolean b;
					if(tmp[i + 3].equals("1"))
						b = true;
					else
						b = false;
					DeviceNote dn = new DeviceNote(myAccount,tmp[i + 1],tmp[i],tmp[i + 2],b);
					DefaultMutableTreeNode note = new DefaultMutableTreeNode(dn);
					category.add(note);
					myDevices.put(tmp[i + 2],note);
				}
				refresh();
				getMyFriends();
			}
		}).start();
	}
	/**
	 * �t�d���ͦn�ͦW��Φn�ͳ]��
	 * @param friends �q���A���ǨӪ��n�ͦW��
	 */
	public void createMyFriends(final String friends)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				DefaultMutableTreeNode category = new DefaultMutableTreeNode("Friends");
				top.add(category);
				String[] tmp = friends.split(",");
				for(int i = 2;i < tmp.length;i += 3)
				{
					boolean b;
					if(tmp[i + 2].equals("1"))
						b = true;
					else
						b = false;
					DeviceNote friendC = new DeviceNote(tmp[i],tmp[i + 1],b);
					DefaultMutableTreeNode friendCategory = new DefaultMutableTreeNode(
							friendC);
					category.add(friendCategory);
					myFriends.put(tmp[i],friendCategory);
					try
					{
						lss.sendMsg("4," + tmp[i]);
					} catch(IOException e)
					{
						e.printStackTrace();
					}
					while(FRIEND_DEVICES_UPDATE == 0)
					{
						if(FRIEND_DEVICES_UPDATE == 1)
							break;
						System.out.print("");
					}
					FRIEND_DEVICES_UPDATE = 0;
					String[] tmp2 = friendDevice.split(",");
					for(int j = 2;j < tmp2.length;j += 4)
					{
						boolean b2;
						if(tmp2[j + 3].equals("1"))
							b2 = true;
						else
							b2 = false;
						DeviceNote dn = new DeviceNote(tmp[i],tmp2[j + 1],tmp2[j],tmp2[j + 2],b2);
						DefaultMutableTreeNode note = new DefaultMutableTreeNode(dn);
						friendCategory.add(note);
						friendDevices.put(tmp2[j + 2],note);
					}
				}
				refresh();
			}
		}).start();
	}
	
	public void deviceLogin(String mac)
	{
		DefaultMutableTreeNode tmp = myDevices.get(mac);
		if(tmp != null)
		{
			DeviceNote dn = (DeviceNote)tmp.getUserObject();
			dn.setLogin(true);
		}else
		{
			tmp = friendDevices.get(mac);
			if(tmp != null)
			{
				DeviceNote dn = (DeviceNote)tmp.getUserObject();
				dn.setLogin(true);
			}
		}
		refresh();
	}

	private void getMyFriends()
	{
		try
		{
			this.lss.sendMsg("3," + this.lss.getAccount());
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void refresh()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				deviceTree.updateUI();
			}
		});
	}

	public String getFriendName(String mac)
	{
		DefaultMutableTreeNode dmTmp = friendDevices.get(mac);
		DeviceNote dnTmp;
		if(dmTmp != null)
		{
			dnTmp = (DeviceNote)dmTmp.getUserObject();
			dmTmp = myFriends.get(dnTmp.getAccount());
			dnTmp = (DeviceNote)dmTmp.getUserObject();
			return dnTmp.getAccountName();
		}
		else
		{
			dnTmp = (DeviceNote)myDevices.get(mac).getUserObject();
			return dnTmp.getDeviceName();
		}
		
	}
	
	public DefaultMutableTreeNode getTop()
	{
		return top;
	}

	public void setTop(DefaultMutableTreeNode top)
	{
		this.top = top;
	}

	public JTree getDeviceTree()
	{
		return deviceTree;
	}

	public void setDeviceTree(JTree deviceTree)
	{
		this.deviceTree = deviceTree;
	}

	public void setFriendUp(String friendDevices)
	{
		this.friendDevice = friendDevices;
		FRIEND_DEVICES_UPDATE = 1;
	}
}
