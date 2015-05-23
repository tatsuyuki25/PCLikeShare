package com.likeshare;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.likeshare.net.LikeShareService;

public class LoginUI extends JDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5520084002445100661L;
	private static LikeShareService lss;
	private static LoginUI dialog;
	private final JPanel contentPanel = new JPanel();
	private static JTextField txtAccount;
	private JPasswordField pwdPass;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			String nimbus="com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
			UIManager.setLookAndFeel(nimbus);
		} catch(ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
		try
		{
			dialog = new LoginUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public LoginUI()
	{
		setBounds(100,100,274,181);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5,5,5,5));
		getContentPane().add(contentPanel,BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblAccount = new JLabel("account");
		lblAccount.setBounds(51, 23, 46, 15);
		contentPanel.add(lblAccount);
		
		JLabel lblPassword = new JLabel("password");
		lblPassword.setBounds(51, 60, 46, 15);
		contentPanel.add(lblPassword);
		
		txtAccount = new JTextField();
		txtAccount.setBounds(107, 17, 96, 27);
		contentPanel.add(txtAccount);
		txtAccount.setColumns(10);
		
		pwdPass = new JPasswordField();
		pwdPass.setBounds(107, 54, 96, 27);
		contentPanel.add(pwdPass);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane,BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						lss = new LikeShareService();
						try
						{
							lss.login(txtAccount.getText(),new String(pwdPass.getPassword()));
						} catch(Exception e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(EXIT_ON_CLOSE);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	public static void login(boolean status)
	{
		if(status)
		{
			EventQueue.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						lss.setAccount(txtAccount.getText());
						LikeShareMainUI frame = new LikeShareMainUI(lss);
						lss.setLsUI(frame);
						frame.setVisible(true);
					} catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			dialog.dispose();
		}else
		{
			JOptionPane.showMessageDialog(null, "帳號或密碼錯誤!", "登入失敗",JOptionPane.ERROR_MESSAGE);  
		}
	}
}
