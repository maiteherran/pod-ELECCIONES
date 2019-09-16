/*
    Donde, en cada provincia, se elegirán cinco partidos políticos
    ganadores para ocupar los 5 representantes que cada provincia tiene en la
    legislatura nacional, mediante el sistema STV.
 */
package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.enums.Party;
import ar.edu.itba.pod.server.enums.ProvinceName;
import ar.edu.itba.pod.server.exceptions.NoSuchPollingStationException;
import org.apache.commons.lang3.tuple.MutablePair;
import ar.edu.itba.pod.server.comparators.CountComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Province {

    private List<PollingStation> pollingStations = Collections.synchronizedList(new ArrayList<>());
    final private TreeSet<MutablePair<Party, Double>> resultsFPTP = new TreeSet<>(new CountComparator());
    final private TreeSet<MutablePair<Party, Double>> resultsSTV = new TreeSet<>(new CountComparator());
    private ProvinceName name;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();

    public Province (ProvinceName name) {
        this.name = name;
    }

    public void addVote(Vote vote) {

        boolean pollingFound = false;

        readLock.lock();
        try {
            for (PollingStation p: pollingStations) {
                if (p.getId() == vote.getPollingStation()) {
                    p.addVote(vote);
                    pollingFound = true;
                    break;
                }
            }
        } finally {
            readLock.unlock();
        }

        if (!pollingFound) {
            PollingStation p = new PollingStation(vote.getPollingStation());
            p.addVote(vote);
            pollingStations.add(p);
        }
    }

    public List<PollingStation> getPollingStations () {
        return pollingStations;
    }

    public ProvinceName getName() {
        return name;
    }

    public void countVotes (VoteCounter counter) {
        readLock.lock();
        try {
            pollingStations.forEach(station -> station.countVotes(counter));
        } finally {
            readLock.unlock();
        }
    }

    public TreeSet<MutablePair<Party, Double>> getResultsFPTP () {
        //TODO: chequear thread-safe en este metodo
        readLock.lock();
        try {
            for (PollingStation station: pollingStations) {
                GenericServiceImpl.addResults(station.getResultsFPTP(), resultsFPTP);
            }
        } finally {
            readLock.unlock();
        }
        return resultsFPTP;
    }

    public TreeSet<MutablePair<Party, Double>> getResultsSTV () {
        VoteCounter counter = new VoteCounter();
        countVotes(counter);
        return counter.getResultsSTV();
    }

    public TreeSet<MutablePair<Party, Double>> getPollingStationResultsFPTP (int id)
            throws NoSuchPollingStationException {

        readLock.lock();
        try {
            for (PollingStation station: pollingStations) {
                if (station.getId() == id) {
                    return station.getResultsFPTP();
                }
            }
        } finally {
            readLock.unlock();
        }
        throw new NoSuchPollingStationException("Polling station number " + id + " does not exists.");
    }
}
