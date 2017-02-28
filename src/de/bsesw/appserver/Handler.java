package de.bsesw.appserver;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import de.bsesw.app.data.GameData;
import de.bsesw.app.data.Profile;
import de.bsesw.app.data.Tuple;

public class Handler implements RequestHandler {
	
	public String handle(String sessionid, int method, String data) {
		if(method==NetworkMethod.AUTHENTIFICATE.getID())
		{
			if(data.split(";").length==2)
			{
				String username = data.split(";")[0];
				String password = data.split(";")[1];
				try {
					ResultSet rs = AppServer.mysql.query("SELECT * FROM users WHERE name='"+username+"'");
					if(rs.next())
					{
						if(rs.getString("password").equals(password))
						{
							String session = UUID.randomUUID().toString();
							NetworkManager.sessions.put(session, UUID.fromString(rs.getString("uuid")));
							return session;
						}
					}
				}catch(Exception ex){ex.printStackTrace();}
			}
			return null;
		}
		if(!NetworkManager.sessions.containsKey(sessionid))return null;
		if(method==NetworkMethod.GETPROFILE.getID())
		{
			UUID target = null;
			if(!data.equals("null"))
			{
				target=UUID.fromString(data);
			}else{
				if(NetworkManager.sessions.containsKey(sessionid))target = NetworkManager.sessions.get(sessionid);
			}
			if(target!=null)
			{
				try {
					ResultSet rs = AppServer.mysql.query("SELECT * FROM users WHERE uuid='"+target.toString()+"'");
					if(rs.next())
					{
						Profile p = new Profile(UUID.fromString(rs.getString("uuid")),rs.getString("name"),rs.getInt("xp"));
						return p.toString();
					}
				}catch(Exception ex){ex.printStackTrace();}
			}
		}
		if(method==NetworkMethod.GAMELIST.getID())
		{
			if(NetworkManager.sessions.containsKey(sessionid))
			{
				UUID p = NetworkManager.sessions.get(sessionid);
				String gamelist = "";
				try {
					ResultSet rs = AppServer.mysql.query("SELECT id FROM gamedata WHERE p1='"+p.toString()+"' OR p2='"+p.toString()+"';");
					while(rs.next())
					{
						if(gamelist.length()>0)gamelist+=",";
						gamelist+=rs.getInt("id");
					}
				}catch(Exception ex){ex.printStackTrace();}
				return gamelist;
			}
		}
		if(method==NetworkMethod.NEWGAME.getID())
		{
			if(NetworkManager.sessions.containsKey(sessionid))
			{
				UUID p = NetworkManager.sessions.get(sessionid);
				UUID a = UUID.fromString(data);
				try {
					ResultSet rs = AppServer.mysql.query("SELECT score5 FROM gamedata WHERE p1='"+p.toString()+"' AND p2='"+a.toString()+"' AND NOT score5='-1;-1';");
					while(rs.next())
					{
						if(rs.getString("score5").split(":")[1].equals("-1"))return "running";
					}
				}catch(Exception ex){ex.printStackTrace();}
				try {
					ResultSet rs = AppServer.mysql.query("SELECT score5 FROM gamedata WHERE p1='"+a.toString()+"' AND p2='"+p.toString()+"' AND NOT score5='-1;-1';");
					while(rs.next())
					{
						if(rs.getString("score5").split(":")[1].equals("-1"))return "running";
					}
				}catch(Exception ex){ex.printStackTrace();}
				AppServer.mysql.update("INSERT INTO gamedata (p1,p2,games) VALUES ('"+p.toString()+"','"+a.toString()+"','"+randomGameList()+"');");
				return "null";
			}
		}
		if(method==NetworkMethod.GETGAMEDATA.getID())
		{
			if(NetworkManager.sessions.containsKey(sessionid))
			{
				try {
					ResultSet rs = AppServer.mysql.query("SELECT * FROM gamedata WHERE id='"+data+"';");
					if(rs.next())
					{
						Tuple<Integer,Integer> [] results = (Tuple<Integer, Integer>[]) Array.newInstance(Tuple.class, 5);
						for(int i=0;i<5;i++)
						{
							results[i]=new Tuple<Integer,Integer>(Integer.parseInt(rs.getString("score"+(i+1)).split(":")[0]),Integer.parseInt(rs.getString("score"+(i+1)).split(":")[1]));
						}
						ArrayList<GameType> games = new ArrayList<GameType>();
						for(String s : rs.getString("games").split(","))
						{
							GameType g = null;
							for(GameType t : GameType.values())
							{
								if(t.toString().equalsIgnoreCase(s))g=t;
							}
							games.add(g);
						}
						return new GameData(UUID.fromString(rs.getString("p1")),UUID.fromString(rs.getString("p2")),games,results).toString();
					}
				}catch(Exception ex){ex.printStackTrace();}
				return "null";
			}
		}
		if(method==NetworkMethod.FRIENDLIST.getID())
		{
			if(NetworkManager.sessions.containsKey(sessionid))
			{
				UUID p = NetworkManager.sessions.get(sessionid);
				String gamelist = "";
				try {
					ResultSet rs = AppServer.mysql.query("SELECT uuid FROM users WHERE NOT uuid='"+p.toString()+"';");
					while(rs.next())
					{
						if(gamelist.length()>0)gamelist+=",";
						gamelist+=rs.getString("uuid");
					}
				}catch(Exception ex){ex.printStackTrace();}
				return gamelist;
			}
		}
		if(method==NetworkMethod.UPDATEGAMEDATA.getID())
		{
			if(NetworkManager.sessions.containsKey(sessionid))
			{
				UUID p = NetworkManager.sessions.get(sessionid);
				int id = Integer.parseInt(data.split(";")[0]);
				String ds = "";
				String [] spl = data.split(";");
				for(int i=1;i<spl.length;i++)
				{
					if(ds.length()>0)ds+=";";
					ds+=spl[i];
				}
				GameData d = new GameData(ds);
				int c = 1;
				for(Tuple<Integer,Integer> r : d.getResults())
				{
					AppServer.mysql.update("UPDATE gamedata SET score"+c+"='"+r.a+":"+r.b+"' WHERE id='"+id+"';");
					c++;
				}
				return "success";
			}
		}
		return null;
	}
	
	public String randomGameList()
	{
		String l = "";
		for(int i=0;i<5;i++)
		{
			if(l.length()>0)l+=",";
			l+=GameType.values()[AppServer.randInt(1, GameType.values().length)-1].toString();
		}
		return l;
	}

}
