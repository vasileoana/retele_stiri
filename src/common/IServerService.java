package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IServerService extends Remote {

	// metoda ce se apeleaza cand un utilzator deschide aplicatia
	void subscribe(IClientService client) throws RemoteException;

	// metoda ce se apeleaza cand un utilzator inchide aplicatia
	void unsubscribe(IClientService client) throws RemoteException;

	// cand un client adauga un nou canal
	void publishChannel(IClientService client, Channel channel) throws RemoteException;

	// stergere canal al unui client
	void deleteChannel(IClientService client, String channelName) throws RemoteException;

	void resetTable(IClientService client) throws RemoteException;
	
	void subscribeToChannel(IClientService client, String channelName) throws RemoteException;

	void unsubscribeToChannel(IClientService client, String channelName) throws RemoteException;
	
	void publishTopic(IClientService client, String channelName, Topic topic) throws RemoteException;
}
