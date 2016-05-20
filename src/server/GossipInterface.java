package server;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GossipInterface extends Remote 
{
	public void hearGossip(byte[] buffer) throws RemoteException; 

}