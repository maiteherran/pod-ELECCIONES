package ar.edu.itba.pod;

import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.util.ElectionState;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface VoteService extends Remote {
   void emitVotes(List<Vote> votes) throws RemoteException, IllegalStateException;
}
