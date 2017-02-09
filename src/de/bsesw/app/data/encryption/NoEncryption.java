package de.bsesw.app.data.encryption;

public class NoEncryption extends Encryption
{

	public String encrypt(String source)
	{
		return source;
	}

	public String decrypt(String source)
	{
		return source;
	}
	
}
