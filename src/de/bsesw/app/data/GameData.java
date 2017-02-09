package de.bsesw.app.data;

import java.util.ArrayList;
import java.util.UUID;

import de.bsesw.appserver.GameType;

public class GameData extends DataPacket
{
	
	private UUID player1;
	private UUID player2;
	private Tuple<Integer,Integer>[] results;
	private ArrayList<GameType> games = new ArrayList<GameType>();
	
	public GameData(UUID player1,UUID player2,ArrayList<GameType> games,Tuple<Integer,Integer>[] results)
	{
		this.player1=player1;
		this.player2=player2;
		this.results=results;
		this.games=games;
	}
	
	public GameData(String string)
	{
		String [] parts = string.split(";");
		this.player1 = UUID.fromString(parts[0]);
		this.player2 = UUID.fromString(parts[1]);
		this.results = (Tuple<Integer,Integer>[])new Tuple[5];
		for(int i=0;i<5;i++)
		{
			int a = Integer.parseInt(parts[2+i].split(":")[0]);
			int b = Integer.parseInt(parts[2+i].split(":")[1]);
			this.results[i]=new Tuple<Integer,Integer>(a,b);
		}
		for(String s : parts[7].split(","))
		{
			GameType g = null;
			for(GameType t : GameType.values())
			{
				if(t.toString().equalsIgnoreCase(s))g=t;
			}
			games.add(g);
		}
	}
	
	public UUID getOther(UUID uuid)
	{
		if(player1.equals(uuid))return player2;
		if(player2.equals(uuid))return player1;
		return null;
	}
	
	public UUID getPlayer1()
	{
		return player1;
	}
	
	public UUID getPlayer2()
	{
		return player2;
	}
	
	public Tuple<Integer,Integer>[] getResults()
	{
		return results;
	}
	
	public boolean isFinished()
	{
		if(results[4].b!=-1)return true;
		return false;
	}
	
	public UUID getCurrentPlayer()
	{
		if(isFinished())return null;
		if(results[getRound()].a==-1)return player1;
		return player2;
	}
	
	public boolean isCurrentPlayer(UUID uuid)
	{
		if(uuid.equals(getCurrentPlayer()))return true;
		return false;
	}
	
	public GameType getGame(int round)
	{
		if(games.size()>round)return games.get(round);
		return null;
	}
	
	public int getRound()
	{
		int r = 0;
		for(int i=0;i<5;i++)
		{
			if(results[i].a!=-1 && results[i].b!=-1)
			{
				r++;
			}else{
				break;
			}
		}
		return r;
	}
	
	public void put(int round,int player,int value)
	{
		if(player==1)
		{
			results[round].a=value;
		}else{
			results[round].b=value;
		}
	}
	
	public String toString()
	{
		String string = player1.toString()+";"+player2.toString();
		for(Tuple<Integer,Integer> t : results)
		{
			string+=";"+t.a+":"+t.b;
		}
		String gl = "";
		for(GameType t : games)
		{
			if(gl.length()>0)gl+=",";
			gl+=t.toString();
		}
		string+=";"+gl;
		return string;
	}
	
	public int getType()
	{
		return 0;
	}
	
}
