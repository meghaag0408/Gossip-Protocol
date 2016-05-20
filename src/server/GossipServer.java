package server;
import java.io.*;
import java.util.Random;
import java.util.HashMap;
import java.rmi.*;
import server.GossipProto.hearGossipRequest;
public class GossipServer
{  
	static int mypid;
	public static void main(String[] args)
		{  
		int noprocesses, pWaitTime, waitTime;
		noprocesses=Integer.parseInt(args[1]);
		int argslen, rpid;
		rpid=Integer.parseInt(args[0]);
		pWaitTime=10000;
		waitTime=10000;
		HashMap<Integer,Integer> processClocks = new HashMap<Integer,Integer>();	
		String inputFile="";
		String new_file="";
		argslen=args.length;
		boolean isActive = false;
		
		mypid=rpid;
		String current_line;	
    		for(int i=0;i<noprocesses;++i)
    			{
    			processClocks.put(i+1, 0);
    			}
    		try
				{  
				
	    		if(argslen>=3)
	    			{
	    				isActive = true;
	    				inputFile=System.getProperty("user.dir")+"/"+args[3];
	    				new_file = inputFile+"."+rpid;
	    			
	    				BufferedReader br = new BufferedReader(new FileReader(inputFile));
	    				File file = new File(new_file);
	    				PrintWriter pw = new PrintWriter(file);
	    				int i=0;
	    				while((current_line = br.readLine())!= null)
    					{
	    					if(i!=0)
	    						pw.write("\n");
	    					String ch = Integer.toString(i+1);
	    					pw.write(args[0]);	    				
	    					pw.write(":");
	    					pw.write(current_line.toString());
	    					pw.write(":");
	    					pw.write(ch);
	    					i++;
    					}
	    				pw.close();
	    				br.close();	
	    			}
	     
			GossipInterface stub=new GossipRemote();  
    		noprocesses=Integer.parseInt(args[1]);
			Naming.rebind("rmi://localhost:1099/"+"Gossip"+rpid, stub); 
			Thread.sleep(pWaitTime);
			GossipServer gs=new GossipServer();
			String line;
			gs.serializeMap(processClocks,mypid);
			if(isActive==true)
				{
					try (BufferedReader br = new BufferedReader(new FileReader(new_file))) 
						{
			    		String[] id_clock;
			   			while ((line = br.readLine()) != null) 
			   				{
			    			id_clock=line.split(":");
			    			int epid;
			    			epid = Integer.parseInt(id_clock[0]);
			    			String message = id_clock[1];
			    			int clockCounter;
			    			clockCounter = Integer.parseInt(id_clock[2]);
			    			gs.processGossip(mypid,epid,clockCounter,noprocesses,message, true);
			    			waitTime = 10000;
			    			Thread.sleep(waitTime);
			    			clockCounter=0;
			    			}
						}
				} 
				
			}
			catch(Exception e)
				{System.out.println(e);}  
		}  


	void processGossip(int rpid,int epid,int clockCounter,int noprocesses,String message, boolean isActive)
		{
		int peer1, peer2;
		Random rn = new Random();
		HashMap<Integer,Integer> hm=deserializeMap(mypid);
		peer1 = rn.nextInt(noprocesses)+1;
		peer2 = rn.nextInt(noprocesses)+1;
		while(peer1==mypid)
			peer1 = rn.nextInt(noprocesses)+1;
		while(peer2==mypid || peer2==peer1)
			peer2 = rn.nextInt(noprocesses)+1;
		
		int temp;
		if(isActive==true)
				{
				temp = hm.get(rpid)+1;
				hm.put(rpid,temp);
		  		serializeMap(hm,rpid);
				}
		else temp = hm.get(rpid);
		try {
				GossipInterface stub1=(GossipInterface)Naming.lookup("rmi://localhost/"+"Gossip"+peer1);
				GossipInterface stub2=(GossipInterface)Naming.lookup("rmi://localhost/"+"Gossip"+peer2);
				hearGossipRequest.Builder gp= hearGossipRequest.newBuilder();
				gp.setClock(clockCounter);
				byte[] buffer =new byte[10000];
			    	gp.setMessage(message);
			    	gp.setRpid(rpid);
				gp.setNoprocesses(noprocesses);
				gp.setPid(epid);
				gp.build();
				
				buffer= gp.build().toByteArray();			
				stub1.hearGossip(buffer);
				stub2.hearGossip(buffer);
				buffer=null;
			
			} 
		catch (Exception e) 
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}  

		}


	public void serializeMap(HashMap<Integer,Integer> hm,int rpid)
		{
		File inputFile=new File(System.getProperty("user.dir")+"/serialise/"+rpid+".txt");
		try 
			{
			ObjectOutputStream os= new ObjectOutputStream(new FileOutputStream(inputFile));
			os.writeObject(hm);
			os.flush();
			os.close();
			inputFile=null;
			} 
		catch (Exception e) 
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}

	@SuppressWarnings("unchecked")
	HashMap<Integer,Integer> deserializeMap(int epid)
		{
		HashMap<Integer,Integer> hm=null;
		int temp=0;
		File toRead= new File(System.getProperty("user.dir")+"/serialise/"+epid+".txt");
		try
		 	{
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(toRead));
			hm= (HashMap<Integer,Integer>)ois.readObject();
			ois.close();
			} 
		catch (Exception e) 
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		return hm;
		
		}
}  
