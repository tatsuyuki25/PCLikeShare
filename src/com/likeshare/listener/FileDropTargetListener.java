package com.likeshare.listener;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.JLabel;

import com.likeshare.DeviceUI;

public class FileDropTargetListener implements DropTargetListener
{
	private boolean draggingFile;
	private boolean acceptableType;
	private DeviceUI deviceUI;
	public FileDropTargetListener(JLabel transImage,DeviceUI dui)
	{
		new DropTarget(transImage,DnDConstants.ACTION_COPY_OR_MOVE,this,true,null);
		this.deviceUI = dui;
	}

	// Implementation of the DropTargetListener interface
	public void dragEnter(DropTargetDragEvent dtde)
	{

		// Get the type of object being transferred and determine
		// whether it is appropriate.
		checkTransferType(dtde);

		// Accept or reject the drag.
		acceptOrRejectDrag(dtde);
		if(deviceUI.isDropSwitch())
			deviceUI.transHold(true);
	}

	public void dragExit(DropTargetEvent dte)
	{
		if(deviceUI.isDropSwitch())
			deviceUI.transHold(false);
	}

	public void dragOver(DropTargetDragEvent dtde)
	{

		// Accept or reject the drag
		acceptOrRejectDrag(dtde);
	}

	public void dropActionChanged(DropTargetDragEvent dtde)
	{

		// Accept or reject the drag
		acceptOrRejectDrag(dtde);
	}

	public void drop(DropTargetDropEvent dtde)
	{

		// Check the drop action
		if((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0)
		{
			// Accept the drop and get the transfer data
			dtde.acceptDrop(dtde.getDropAction());
			Transferable transferable = dtde.getTransferable();

			try
			{
				boolean result = false;

				if(draggingFile)
				{
					result = dropFile(transferable);
				} else
				{
					result = dropContent(transferable,dtde);
				}

				dtde.dropComplete(result);
			} catch(Exception e)
			{
				dtde.dropComplete(false);
			}
		} else
		{
			dtde.rejectDrop();
		}
	}

	// Internal methods start here

	protected boolean acceptOrRejectDrag(DropTargetDragEvent dtde)
	{
		int dropAction = dtde.getDropAction();
		int sourceActions = dtde.getSourceActions();
		boolean acceptedDrag = false;

		// Reject if the object being transferred
		// or the operations available are not acceptable
		if(!acceptableType
				|| (sourceActions & DnDConstants.ACTION_COPY_OR_MOVE) == 0)
		{
			dtde.rejectDrag();
		} else if(!draggingFile)
		{
			// Can't drag text to a read-only JEditorPane
			dtde.rejectDrag();
		} else if((dropAction & DnDConstants.ACTION_COPY_OR_MOVE) == 0)
		{
			// Not offering copy or move - suggest a copy
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
			acceptedDrag = true;
		} else
		{
			// Offering an acceptable operation: accept
			dtde.acceptDrag(dropAction);
			acceptedDrag = true;
		}

		return acceptedDrag;
	}

	@SuppressWarnings("deprecation")
	protected void checkTransferType(DropTargetDragEvent dtde)
	{
		// Accept a list of files, or data content that
		// amounts to plain text or a Unicode text string
		acceptableType = false;
		draggingFile = false;

		if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
		{
			acceptableType = true;
			draggingFile = true;
		} else if(dtde.isDataFlavorSupported(DataFlavor.plainTextFlavor)
				|| dtde.isDataFlavorSupported(DataFlavor.stringFlavor))
		{
			acceptableType = true;
		}
	}

	// This method handles a drop for a list of files
	protected boolean dropFile(Transferable transferable) throws IOException,
			UnsupportedFlavorException, MalformedURLException
	{
		@SuppressWarnings("rawtypes")
		List fileList = (List) transferable
				.getTransferData(DataFlavor.javaFileListFlavor);
		final File transferFile = (File) fileList.get(0);
		/*
		 * final URL transferURL = transferFile.toURL();
		 * DnDUtils.debugPrintln("File URL is " + transferURL);
		 * 
		 * pane.setPage(transferURL);
		 */
		if(!transferFile.isDirectory())
		{
			if(deviceUI.isDropSwitch())
				deviceUI.transfer(transferFile.getPath());
		}
		return true;
	}

	// This method handles a drop with data content

	protected boolean dropContent(Transferable transferable,
			DropTargetDropEvent dtde)
	{

		try
		{
			// Check for a match with the current content type
			DataFlavor[] flavors = dtde.getCurrentDataFlavors();

			DataFlavor selectedFlavor = null;

			// Look for either plain text or a String.
			for(int i = 0;i < flavors.length;i++)
			{
				DataFlavor flavor = flavors[i];

				if(flavor.equals(flavor.getReaderForText(transferable))
						|| flavor.equals(DataFlavor.stringFlavor))
				{
					selectedFlavor = flavor;
					break;
				}
			}

			if(selectedFlavor == null)
			{
				// No compatible flavor - should never happen
				return false;

			}

			// Get the transferable and then obtain the data
			Object data = transferable.getTransferData(selectedFlavor);

			String insertData = null;
			if(data instanceof InputStream)
			{
				// Plain text flavor
				String charSet = selectedFlavor.getParameter("charset");
				InputStream is = (InputStream) data;
				byte[] bytes = new byte[is.available()];
				is.read(bytes);
				try
				{
					insertData = new String(bytes,charSet);
				} catch(UnsupportedEncodingException e)
				{
					// Use the platform default encoding
					insertData = new String(bytes);
				}
			} else if(data instanceof String)
			{
				// String flavor
				insertData = (String) data;
			}

			if(insertData != null)
			{
				return true;
			}
			return false;
		} catch(Exception e)
		{
			return false;
		}
	}

}
