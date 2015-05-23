package com.likeshare.tree;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
/**
 * 繼承DefaultTreeCellRenderer，客製化JTree風格
 * @author tatsuyuki
 *
 */
public class DeviceTreeCellRenderer extends DefaultTreeCellRenderer
{

	private static final long serialVersionUID = -1205245689906142106L;
	
	/**
	 * {@inheritDoc}
	 */
	  public Component getTreeCellRendererComponent(JTree tree, Object value,
			    boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
			  {
			    final Component c = super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);
			    if(leaf)
			    {
			   	 DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)value;
			   	 if(dmtn.getUserObject() instanceof DeviceNote)
			   	 {
			   		 DeviceNote dn = (DeviceNote)dmtn.getUserObject();
			   		 if(dn.isLogin())
			   			 setText("<html><font size=4>" + String.valueOf(value) + "</font></html>");
			   		 else
			   			 setText("<html><font color=#666666><i>" + String.valueOf(value) + "</i></font></html>");
			   		 if(dn.getDeviceType().equals("android"))
			   			 setIcon(new ImageIcon(DeviceTreeCellRenderer.class.getResource("/com/likeshare/image/smart_phone.png")));
			   		 else
			   			 setIcon(new ImageIcon(DeviceTreeCellRenderer.class.getResource("/com/likeshare/image/notebook.png")));
			   	 }
			    }
			    else if(String.valueOf(value).equals("Friends"))
			    {
			   	 setIcon(new ImageIcon(DeviceTreeCellRenderer.class.getResource("/com/likeshare/image/friend.png")));
			    }
			    return c;
			  }

}
