package ar.edu.itba.pod;

import ar.edu.itba.pod.exceptions.InvalidStateException;
import ar.edu.itba.pod.util.ElectionState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ManagementService extends Remote {
    void openElection () throws RemoteException, InvalidStateException;
    ElectionState getElectionState() throws RemoteException;
    void closeElection() throws RemoteException, InvalidStateException;
}
