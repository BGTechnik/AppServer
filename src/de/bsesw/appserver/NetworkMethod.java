package de.bsesw.appserver;

public enum NetworkMethod {
	
	AUTHENTIFICATE(0),
	GETPROFILE(1),
	GETGAMEDATA(2),
	GETCHAT(3),
	NEWGAME(4),
	GAMELIST(5),
	FRIENDLIST(6),
	UPDATEGAMEDATA(7);
	
	private int method;
	
	NetworkMethod(int method)
	{
		this.method=method;
	}
	
	public int getID()
	{
		return method;
	}
	
}
