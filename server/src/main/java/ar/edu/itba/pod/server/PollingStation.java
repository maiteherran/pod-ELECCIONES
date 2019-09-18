/*
    Se desea conocer el partido político ganador de cada mesa de votación, sólo
    para fines estadísticos de consulta, mediante el sistema FPTP.
 */
package ar.edu.itba.pod.server;

import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.util.Party;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PollingStation {

    private final int id;
    private List<Vote> votes = new ArrayList<>();
    private VoteCounter counter = new VoteCounter();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public PollingStation (int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getNumberOfVotes() {
        return votes.size();
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void addVote(Vote vote) {
        writeLock.lock();
        try {
            votes.add(vote);
            counter.addVote(vote);
        } finally {
            writeLock.unlock();
        }
    }

    public TreeSet<MutablePair<Party, Double>> getResultsFPTP() {
        TreeSet<MutablePair<Party, Double>> resultsFPTP;

        readLock.lock();
        try {
            resultsFPTP = counter.getResultsFPTP();
        } finally {
            readLock.unlock();
        }

        return resultsFPTP;
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
