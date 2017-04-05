package client;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import common.Channel;
import common.IClientService;
import common.Topic;

public class ClientService extends UnicastRemoteObject implements IClientService{

	private static final long serialVersionUID = 1L;
	private IClientService client; 
	
	protected ClientService(Registry registry, String name, IClientService client) throws RemoteException {
		this.client = client;
		registry.rebind(name, this);
	}

	@Override
	public void receiveChannels(List<Channel> channels) throws RemoteException {
		client.receiveChannels(channels);
	}

	@Override
	public void resetTheTable(List<Channel> channels) throws RemoteException {
		client.resetTheTable(channels);
	}

	@Override
	public void receiveTopics(List<Topic> topics, String channelName) throws RemoteException {
		client.receiveTopics(topics, channelName);
		
	}
	
}
