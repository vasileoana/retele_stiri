package server;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Channel;
import common.IClientService;
import common.IServerService;
import common.Topic;

public class ServerService extends UnicastRemoteObject implements IServerService {

	private static final long serialVersionUID = 1L;
	private List<IClientService> clients = new ArrayList<>();
	private List<Channel> allChannels = new ArrayList<>();
	private Map<IClientService, List<Channel>> usersChannels = new HashMap<>();
	private Map<Channel, List<IClientService>> subscribers = new HashMap<>();
	private String[] cuvinteInterzie = new String[] { "urat1", "urat2", "interzis1", "interzis2" };

	protected ServerService(Registry registry, String name) throws RemoteException {
		registry.rebind(name, this);
		allChannels.add(new Channel("canal1", "desc1"));
		allChannels.add(new Channel("canal2", "desc1"));
		allChannels.add(new Channel("canal3", "desc1"));
		allChannels.add(new Channel("canal4", "desc1"));
	}

	@Override
	public void subscribe(IClientService client) throws RemoteException {
		if (!clients.contains(client)) {
			synchronized (clients) {
				clients.add(client);
				client.receiveChannels(allChannels);
			}
		}
	}

	@Override
	public void unsubscribe(IClientService client) throws RemoteException {
		if (clients.contains(client)) {
			synchronized (clients) {
				clients.remove(client);
			}
		}
	}

	@Override
	public void publishChannel(IClientService client, Channel channel) throws RemoteException {
		allChannels.add(channel);
		if (usersChannels.keySet().contains(client)) {
			usersChannels.get(client).add(channel);
		} else {
			List<Channel> chClients = new ArrayList<>();
			chClients.add(channel);
			usersChannels.put(client, chClients);
		}
		List<Channel> newChan = new ArrayList<>();
		newChan.add(channel);
		synchronized (clients) {
			for (IClientService cli : clients) {
				cli.receiveChannels(newChan);
			}
		}
	}

	@Override
	public void deleteChannel(IClientService client, String channelName) throws RemoteException {
		if (usersChannels.keySet().contains(client)) {
			Channel gasit = new Channel();
			for (Channel c : usersChannels.get(client)) {
				if (c.getName().equals(channelName)) {
					c.getTopics().clear();
					allChannels.remove(c);
					gasit = c;
					break;
				}
			}
			usersChannels.get(client).remove(gasit);
			for (IClientService cli : clients) {
				for (Channel key : subscribers.keySet()) {
					if (subscribers.get(key).contains(cli)) {
						cli.receiveTopics(key.getTopics(), key.getName());
					}
				}
			}
		}
	}

	@Override
	public void resetTable(IClientService client) throws RemoteException {
		synchronized (clients) {
			for (IClientService cli : clients) {
				cli.resetTheTable(allChannels);
			}
		}
	}

	@Override
	public void subscribeToChannel(IClientService client, String channelName) throws RemoteException {
		Channel c = new Channel();
		for (Channel key : allChannels) {
			if (channelName.equals(key.getName())) {
				c = key;
				break;
			}
		}
		if (c.getName() != null) {
			if (subscribers.keySet().contains(c)) {
				if (!subscribers.get(c).contains(client)) {
					subscribers.get(c).add(client);
				}
			} else {
				List<IClientService> clients = new ArrayList<>();
				clients.add(client);
				subscribers.put(c, clients);
			}
			client.receiveTopics(c.getTopics(), c.getName());
		}
	}

	@Override
	public void unsubscribeToChannel(IClientService client, String channelName) throws RemoteException {
		Channel c = new Channel();
		for (Channel key : allChannels) {
			if (channelName.equals(key.getName())) {
				c = key;
				break;
			}
		}
		if (c.getName() != null) {
			if (subscribers.keySet().contains(c)) {
				if (subscribers.get(c).contains(client)) {
					subscribers.get(c).remove(client);
				}
			}
		}
	}

	@Override
	public void publishTopic(IClientService client, String channelName, Topic topic) throws RemoteException {
		boolean interzis = false;
		for (String cuvantInterzis : cuvinteInterzie) {
			if (topic.getText().contains(cuvantInterzis)) {
				interzis = true;
			}
		}
		if (!interzis) {
			if (usersChannels.keySet().contains(client)) {
				for (Channel c : usersChannels.get(client)) {
					if (c.getName().equals(channelName)) {
						c.getTopics().add(topic);
						break;
					}
				}
			}
			List<Topic> topics = new ArrayList<>();
			topics.add(topic);
			client.receiveTopics(topics, channelName);
			synchronized (clients) {
				for (IClientService cli : clients) {
					for (Channel key : subscribers.keySet()) {
						if (subscribers.get(key).contains(cli)) {
							cli.receiveTopics(key.getTopics(), key.getName());
						}
					}
				}
			}
		}

	}

}
