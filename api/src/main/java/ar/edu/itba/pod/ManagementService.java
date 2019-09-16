package ar.edu.itba.pod;

import ar.edu.itba.pod.exceptions.InvalidStateException;
import ar.edu.itba.pod.util.ElectionState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ManagementService extends Remote {
    ElectionState openElection () throws RemoteException, InvalidStateException;
    ElectionState getElectionState() throws RemoteException;
    ElectionState closeElection() throws RemoteException, InvalidStateException;
}
