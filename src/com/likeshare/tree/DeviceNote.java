package com.likeshare.tree;
/**
 * JTree節點客製化
 * @author tatsuyuki
 * 
 */
public class DeviceNote
{
	private String account;
	private String accountName;
	private String deviceName = null;
	private String deviceType;
	private String deviceMac;
	private boolean login;

	/**
	 * @param {@link #account} 帳號
	 * @param {@link #deviceName} ID
	 * @param {@link #deviceType} 手機 or PC
	 * @param {@link #deviceMac} mac
	 * @param {@link #login} 是否上線
	 */
	public DeviceNote(String account,String deviceName,String deviceType,String deviceMac,boolean login)
	{
		this.account = account;
		this.deviceName = deviceName;
		this.deviceType = deviceType;
		this.deviceMac = deviceMac;
		this.login = login;
	}
	/**
	 * @param {@link #account} 帳號
	 * @param {@link #accountName} ID
	 * @param {@link #login} 是否上線
	 */
	public DeviceNote(String account,String accountName,boolean login)
	{
		this.account = account;
		this.accountName = accountName;
		this.login = login;
	}
	public String getAccount()
	{
		return account;
	}
	public void setAccount(String account)
	{
		this.account = account;
	}
	public String getDeviceName()
	{
		return deviceName;
	}
	public void setDeviceName(String deviceName)
	{
		this.deviceName = deviceName;
	}
	public String getDeviceType()
	{
		return deviceType;
	}
	public void setDeviceType(String deviceType)
	{
		this.deviceType = deviceType;
	}
	public boolean isLogin()
	{
		return login;
	}
	public void setLogin(boolean login)
	{
		this.login = login;
	}
	public String toString()
	{
		if(deviceName != null)
			return deviceName;
		else
			return accountName;
	}
	public String getDeviceMac()
	{
		return deviceMac;
	}
	public void setDeviceMac(String deviceMac)
	{
		this.deviceMac = deviceMac;
	}
	public String getAccountName()
	{
		return accountName;
	}
	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}
	
}
