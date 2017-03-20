package de.bsesw.appserver;

import java.util.Random;

import de.bsesw.app.data.encryption.Encryption;
import de.bsesw.app.data.encryption.NoEncryption;

public class AppServer
{
	
	public static MySQL mysql;
	public static Encryption encryption;
	
	public static void main(String [] args)
	{
		mysql = new MySQL("localhost","3306","app","root","");
		encryption = new NoEncryption();
		NetworkManager.init(4455);
		NetworkManager.addHandler(new Handler());
		NetworkManager.start();
	}
	
	public static int randInt(int min,int max)
	{
		Random r = new Random();
		return r.nextInt((max+1)-min)+min;
	}
	
}
