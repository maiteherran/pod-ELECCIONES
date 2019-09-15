package ar.edu.itba.pod;

import ar.edu.itba.pod.models.Vote;

import java.rmi.RemoteException;
import java.util.List;

public interface VoteService {
   void emitVotes(List<Vote> votes) throws RemoteException, IllegalStateException;
}
