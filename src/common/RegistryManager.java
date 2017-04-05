package common;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class RegistryManager {

	private RegistryManager() {
		
	}
	
	public static Registry getRegistryManager(int port) throws RemoteException {
		try {
			return LocateRegistry.createRegistry(port);
		} catch(RemoteException ex) {
			return LocateRegistry.getRegistry(port);
		}
	}
}
