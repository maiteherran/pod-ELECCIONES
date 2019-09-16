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
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Province {

    private List<PollingStation> pollingStations = Collections.synchronizedList(new ArrayList<>());
    private TreeSet<MutablePair<Party, Double>> resultsSTV = new TreeSet<>(new CountComparator());
    private VoteCounter fptpCounter = new VoteCounter();
    private ProvinceName name;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public Province (ProvinceName name) {
        this.name = name;
    }

    public void addVote(Vote vote) {
        Optional<PollingStation> maybePollingStation;

        readLock.lock();
        try {
            maybePollingStation = pollingStations.stream().filter(st -> st.getId() == vote.getPollingStation()).findFirst();
        } finally {
            readLock.unlock();
        }

        writeLock.lock();
        try {
            if (maybePollingStation.isPresent()) {
                maybePollingStation.get().addVote(vote);
            } else {
                PollingStation p = new PollingStation(vote.getPollingStation());
                p.addVote(vote);
                pollingStations.add(p);
            }
            fptpCounter.addVote(vote);
        } finally {
            writeLock.unlock();
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
        TreeSet<MutablePair<Party, Double>> resultsFPTP;

        readLock.lock();
        try {
            resultsFPTP = fptpCounter.getResultsFPTP();
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
