package com.likeshare;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

import com.likeshare.net.LikeShareService;
import com.likeshare.net.bio.Transfer;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class ProgressUI extends JDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -887119023834616120L;
	private final JPanel contentPanel = new JPanel();
	private LikeShareService lss;
	private JProgressBar progressBar;
	private JLabel lblFilename;
	private JLabel lblProgress;
	private boolean T;
	private boolean stop = false;
	/**
	 * Create the dialog.
	 */
	public ProgressUI(String title,LikeShareService lss,boolean T)
	{
		this.lss = lss;
		this.T = T;
		setTitle(title);
		setBounds(100,100,331,156);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5,5,5,5));
		getContentPane().add(contentPanel,BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 52, 252, 22);
		contentPanel.add(progressBar);
		
		lblProgress = new JLabel("0%");
		lblProgress.setBounds(272, 59, 34, 15);
		contentPanel.add(lblProgress);
		
		lblFilename = new JLabel("\u4E0B\u8F09");
		lblFilename.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		lblFilename.setBounds(10, 10, 295, 32);
		contentPanel.add(lblFilename);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane,BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ProgressUI.this.lss.stopBioFileService();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		lss.setUIEnabled(false);
	}
	
	public Thread startProgress = new Thread(new Runnable()
	{
		public void run()
		{
			int len = 0;
			while(!stop)
			{
				if(T)
					len = Transfer.transferLength;
				else
					len = Transfer.downLength;
				int p = (int) (((double) len / (double) progressBar.getMaximum()) * 100);
				lblProgress.setText(p+"%");
				progressBar.setValue(len);
			}
			lss.setUIEnabled(true);
		}
	});
	
	public void dis()
	{
		dispose();
	}
	
	public void setFileName(String name)
	{
		if(!T)
			lblFilename.setText("下載："+name);
		else
			lblFilename.setText("傳送："+name);
	}
	
	public void setMaxValue(int max)
	{
		progressBar.setMaximum(max);
	}
	
	public void setStop(boolean stop)
	{
		this.stop = stop;
	}
   /**
    * Handles window events depending on the state of the
    * {@code defaultCloseOperation} property.
    *
    * @see #setDefaultCloseOperation
    */
	@Override
   protected void processWindowEvent(WindowEvent e) {
       super.processWindowEvent(e);

       if (e.getID() == WindowEvent.WINDOW_CLOSING) {
           switch(getDefaultCloseOperation()) {
             case HIDE_ON_CLOSE:
                setVisible(false);
                break;
             case DISPOSE_ON_CLOSE:
            	 lss.stopBioFileService();
                break;
             case DO_NOTHING_ON_CLOSE:
                default:
                break;
           }
       }
   }
}
