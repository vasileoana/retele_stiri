package server;

import java.rmi.registry.Registry;

import common.RegistryManager;
import common.Settings;

public class ServerConsola {

	public static void main(String[] args) {
		try {
			int port = Settings.getServerPort();
			Registry registry = RegistryManager.getRegistryManager(port);
			new ServerService(registry, Settings.getServerService());
			
			System.out.println("Server started!");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
