package ar.edu.itba.pod.client;

import ar.edu.itba.pod.util.ServiceName;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

abstract class Client {
    static Remote getServiceFromServer(final String serverAddress, final ServiceName service) throws RemoteException, NotBoundException, MalformedURLException {
        return Naming.lookup("//" + serverAddress + "/" + service.getServiceName());
    }
}
