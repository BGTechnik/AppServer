package de.bsesw.app.data;

import java.util.UUID;

public class Profile extends DataPacket
{
	
	private UUID uuid;
	private String name;
	private int xp = 0;
	private static int[] levelups = {};
	
	public Profile(UUID uuid,String name,int xp)
	{
		this.uuid=uuid;
		this.name=name;
		this.xp=xp;
	}
	
	public Profile(String string)
	{
		String [] parts = string.split(";");
		this.uuid=UUID.fromString(parts[0]);
		this.name=parts[1];
		this.xp=Integer.parseInt(parts[2]);
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getXP()
	{
		return xp;
	}
	
	public int getLevel()
	{
		int level = 0;
		for(int lvlup : levelups)
		{
			if(xp>=lvlup)
			{
				level++;
			}else{
				break;
			}
		}
		return level;
	}
	
	public String toString()
	{
		return uuid.toString()+";"+name+";"+xp;
	}
	
	public int getType()
	{
		return 1;
	}
	
}
