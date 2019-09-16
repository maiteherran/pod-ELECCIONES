/*
    Se desea conocer el partido político ganador de cada mesa de votación, sólo
    para fines estadísticos de consulta, mediante el sistema FPTP.
 */
package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.comparators.CountComparator;
import ar.edu.itba.pod.server.enums.Party;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PollingStation {

    private final int id;
    private List<Vote> votes = new ArrayList<>();
    private TreeSet<MutablePair<Party, Double>> resultsFPTP = new TreeSet<>(new CountComparator());
    private int votesFPTPCount;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public PollingStation (int id) {
        this.id = id;
        this.votesFPTPCount = 0;
    }

    public int getId() {
        return id;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void addVote(Vote vote) {
        writeLock.lock();
        try {
            votes.add(vote);
        } finally {
            writeLock.unlock();
        }
    }

    public TreeSet<MutablePair<Party, Double>> getResultsFPTP() {
        readLock.lock();
        try {
            for (int i = votesFPTPCount; i < votes.size(); i++) {
                addVoteToParty(votes.get(i), votes.get(i).getFirstChoice());
            }

            votesFPTPCount = votes.size();
        } finally {
            readLock.unlock();
        }

        return resultsFPTP;
    }

    private void addVoteToParty (Vote vote, Party party) {
        //TODO: chequear thread-safe en este metodo
        readLock.lock();
        try {
            Optional<MutablePair<Party, Double>> maybeResult = resultsFPTP.stream().filter(result -> result.getLeft().equals(party)).findFirst();
            if (maybeResult.isPresent()) {
                maybeResult.get().setRight(maybeResult.get().getRight() + 1.0/(double)votes.size());
            } else {
                MutablePair<Party, Double> newVote = new MutablePair<>(vote.getFirstChoice(),  (1.0) / (double) votes.size());
                resultsFPTP.add(newVote);
            }
        } finally {
            readLock.unlock();
        }
    }

    public void countVotes(VoteCounter counter) {
        readLock.lock();
        try {
            votes.forEach(counter::addVote);
        } finally {
            readLock.unlock();
        }
    }
}
