package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IClientService extends Remote {
	
	void receiveChannels(List<Channel> channels) throws RemoteException;
	
	void receiveTopics(List<Topic> topics, String channelName) throws RemoteException;
	
	void resetTheTable(List<Channel> channels) throws RemoteException;
}
