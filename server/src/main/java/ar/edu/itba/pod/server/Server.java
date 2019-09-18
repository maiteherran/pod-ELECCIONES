package ar.edu.itba.pod.server;

import ar.edu.itba.pod.util.Party;
import ar.edu.itba.pod.util.ProvinceName;
import ar.edu.itba.pod.server.exceptions.IllegalVoteException;
import ar.edu.itba.pod.util.ServiceName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);
    private static int port = 1099;

    public static void main(String[] args) {
        logger.info("tpe Server Starting ...");
        System.out.println("Server has started.");

        if (System.getProperties().containsKey("port")) {
            port = Integer.parseInt(System.getProperty("port"));
        }

        final GenericServiceImpl electionsService = new GenericServiceImpl();
        final Remote remote;
        final Registry registry;
        try {
            remote = UnicastRemoteObject.exportObject(electionsService, 0);
            registry = LocateRegistry.createRegistry(port);
            registry.rebind(ServiceName.MANAGEMENT_SERVICE.getServiceName(), remote);
            registry.rebind(ServiceName.QUERY_SERVICE.getServiceName(), remote);
            registry.rebind(ServiceName.VOTE_SERVICE.getServiceName(), remote);
        } catch (RemoteException e) {
            logger.error("Remote error");
            System.exit(-1);
        }
    }
}
