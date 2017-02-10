package de.bsesw.appserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NetworkManager {
	
	private static List<RequestHandler> handerList = new ArrayList<RequestHandler>();
	public static HashMap<String,UUID> sessions = new HashMap<String,UUID>();
	private static Thread thread;
	private static int port = -1;
	
	public static void init(int port)
	{
		NetworkManager.port=port;
		thread = new Thread(){
			public void run()
			{
				try
				{
					ServerSocket s = new ServerSocket(port);
					while(true)
					{
						final Socket fs = s.accept();
						new Thread()
						{
							public void run()
							{
								try
								{
									OutputStream out = fs.getOutputStream();
									PrintWriter writer = new PrintWriter(out);
									InputStream in = fs.getInputStream();
									BufferedReader reader = new BufferedReader(new InputStreamReader(in));
									String s = null;
									while((s = reader.readLine()) != null)
									{
										String [] parts = AppServer.encryption.decrypt(s).split("//");
										int method = Integer.parseInt(parts[1]);
										String str = null;
										for(RequestHandler handler : handerList)
										{
											str=handler.handle(parts[0], method, parts[2]);
											if(str!=null)break;
										}
										String packet = "0//null";
										if(str!=null)
										{
											packet="1//"+str;
										}
										writer.write(AppServer.encryption.encrypt(packet)+"\n");
										writer.flush();
									}
									writer.close();
									reader.close();
								}
								catch(IOException e)
								{
									e.printStackTrace();
								}
							}
						}.start();
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		};
	}
	
	public static void start()
	{
		thread.start();
	}
	
	public static void stop()
	{
		thread.stop();
	}
	
	public static int getListenPort()
	{
		return port;
	}
	
	public static void addHandler(RequestHandler handler)
	{
		handerList.add(handler);
	}
	
}
