package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.comparators.CountComparator;
import ar.edu.itba.pod.util.Party;
import ar.edu.itba.pod.util.ProvinceName;
import ar.edu.itba.pod.server.exceptions.IllegalVoteException;
import ar.edu.itba.pod.server.exceptions.NoSuchPollingStationException;
import ar.edu.itba.pod.server.exceptions.NoSuchProvinceException;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GenericServiceImpl {
    private TreeSet<MutablePair<Party, Double>> resultsAV = null;
    private List<Province> provinces = new ArrayList<>();
    private VoteCounter fptpCounter = new VoteCounter();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public GenericServiceImpl () {
        provinces.add(new Province(ProvinceName.JUNGLE));
        provinces.add(new Province(ProvinceName.SAVANNAH));
        provinces.add(new Province(ProvinceName.TUNDRA));
    }

    public boolean addVote(int pollingStationId, ProvinceName province, List<Party> vote) throws IllegalVoteException {
        if (vote.size() <= 0 || vote.size() > 3) {
            throw new IllegalVoteException("No such choices are possible: minimum choices are 0 and maximum are 3");
        }

        Vote newVote = new Vote(vote, pollingStationId, province);
        if (newVote.getVote() == null) {
            return false;
        }

        Optional<Province> maybeProvince = provinces.stream().filter(p -> p.getName().equals(province)).findFirst();
        /* podemos asumir que la provincia se encuentra dentro de las 3 ya agregadas --> no va a ser null el Optional */
        Province p = maybeProvince.get();
        p.addVote(newVote);

        writeLock.lock();
        try {
            fptpCounter.addVote(newVote);
        } finally {
            writeLock.unlock();
        }

        return true;
    }

    public Province getProvince (ProvinceName name) throws NoSuchProvinceException {
        for (Province p: provinces) {
            if (p.getName().equals(name)) {
                return p;
            }
        }

        throw new NoSuchProvinceException("Province name " + name + " does not exists.");
    }

    public TreeSet<MutablePair<Party, Double>> getProvinceResults (ProvinceName name) throws NoSuchProvinceException {
        Province p = getProvince(name);
        return p.getResultsSTV();
    }

    public TreeSet<MutablePair<Party, Double>> getPollingStationResults (int id)
            throws NoSuchPollingStationException{

        for (Province p : provinces) {
            TreeSet<MutablePair<Party, Double>> ret = p.getPollingStationResultsFPTP(id);
            if (ret != null)
                return ret;
        }

        throw new NoSuchPollingStationException("Polling station number " + id + " does not exists.");
    }

    /**
     * Donde se elegirá a un partido político ganador para ocupar un cargo
     * ejecutivo nacional, mediante el sistema AV.
     */
    public TreeSet<MutablePair<Party, Double>> getNationalResults() {
        writeLock.lock();
        try {
            if (resultsAV == null) {
                resultsAV = fptpCounter.getResultsAV();
            }
        } finally {
            writeLock.unlock();
        }
        return resultsAV;
    }

    public static void addResults (TreeSet<MutablePair<Party, Double>> from, TreeSet<MutablePair<Party, Double>> to) {
        //TODO: chequear thread-safe en este metodo
        from.forEach( fromPair -> {
            Optional<MutablePair<Party, Double>> maybeToPair = to.stream().filter(toPair -> toPair.getLeft().equals(fromPair.getLeft())).findFirst();
            if (maybeToPair.isPresent()) {
                maybeToPair.get().setRight((maybeToPair.get().getRight() + fromPair.getRight()) / 2);
            } else {
                to.add(new MutablePair<>(fromPair.getLeft(), fromPair.getRight()));
            }
        });
    }

    public TreeSet<MutablePair<Party, Double>> queryResults() {
        TreeSet<MutablePair<Party, Double>> finalResults;

        readLock.lock();
        try {
            finalResults = fptpCounter.getResultsFPTP();
        } finally {
            readLock.unlock();
        }

        return finalResults;
    }

    public void printResults(TreeSet<MutablePair<Party, Double>> results) {
        System.out.println("--------RESULTS--------------");
        results.forEach(pair -> System.out.println("PARTY: " + pair.getLeft() + " VOTES: " + Math.round(pair.getRight() * 100.0 * 10000.0)/10000.0 + "%"));
    }
}
