package server;

import java.io.*;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.rmi.server.UnicastRemoteObject;
import com.google.protobuf.InvalidProtocolBufferException;
import server.GossipProto.hearGossipRequest;
@SuppressWarnings("serial")
public class GossipRemote extends UnicastRemoteObject implements  GossipInterface
{
	
	GossipRemote ()throws RemoteException
	{  
		super();  
	}  
	public void hearGossip(byte[] buffer)
		{
		hearGossipRequest np=null;
		try {
			np = hearGossipRequest.parseFrom(buffer);
		} catch (InvalidProtocolBufferException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int clock = np.getClock();
		String message = np.getMessage();
		int epid = np.getPid();
		int noprocesses = np.getNoprocesses();
		
		HashMap<Integer,Integer> hm=deserializeMap(GossipServer.mypid);
		if(clock>hm.get(epid))
			{		
			hm.put(epid, clock);
			System.out.println("Accept "+epid+":"+message);
			GossipServer gs= new GossipServer();
			int temp = GossipServer.mypid;
			gs.serializeMap(hm, temp );
			int time = 5000;
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String msg = message;
			gs.processGossip(GossipServer.mypid, epid, clock,noprocesses ,msg, false);
			}
		else{
			System.out.println("Reject "+epid+":"+message);
			}
		}
	
		@SuppressWarnings("unchecked")
		HashMap<Integer,Integer> deserializeMap(int epid)
			{
			HashMap<Integer,Integer> hm=null;
			File toRead= new File(System.getProperty("user.dir")+"/serialise/"+epid+".txt");
			try 
				{
				ObjectInputStream ois=new ObjectInputStream(new FileInputStream(toRead));
				hm= (HashMap<Integer,Integer>)ois.readObject();
				ois.close();   
				} 
			catch (Exception e) 
				{
				e.printStackTrace();
				}
			return hm;
		
			}	
}