package ar.edu.itba.pod.server;

import ar.edu.itba.pod.ManagementService;
import ar.edu.itba.pod.QueryService;
import ar.edu.itba.pod.VoteService;
import ar.edu.itba.pod.exceptions.InvalidStateException;
import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.util.ElectionState;
import ar.edu.itba.pod.util.Party;
import ar.edu.itba.pod.util.ProvinceName;
import ar.edu.itba.pod.exceptions.NoSuchPollingStationException;
import ar.edu.itba.pod.exceptions.NoSuchProvinceException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.omg.CORBA.DynAnyPackage.Invalid;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GenericServiceImpl implements ManagementService, VoteService, QueryService {
    private TreeSet<MutablePair<Party, Double>> resultsAV = null;
    private List<Province> provinces = new ArrayList<>();
    private VoteCounter fptpCounter = new VoteCounter();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private ElectionState electionState;
    private final String electionStateLock = "ElectionStateLock";

    public GenericServiceImpl () {
        provinces.add(new Province(ProvinceName.JUNGLE));
        provinces.add(new Province(ProvinceName.SAVANNAH));
        provinces.add(new Province(ProvinceName.TUNDRA));
        electionState = ElectionState.PENDING;
    }

    @Override
    public void openElection() throws RemoteException, InvalidStateException { //TODO FRANCO SE PUEDE BORRAR EL THROWS DE LA REMOTE EXCEPTION?
        synchronized (electionStateLock) {
            if (electionState.equals(ElectionState.PENDING)) {
                electionState = ElectionState.STARTED;
            } else {
                throw new InvalidStateException(electionState.getDescription());
            }
        }
    }

    @Override
    public ElectionState getElectionState() throws RemoteException {
        return electionState;
    }

    @Override
    public void closeElection() throws RemoteException, InvalidStateException {
       synchronized (electionStateLock) {
            if (electionState.equals(ElectionState.STARTED)) {
                electionState = ElectionState.ENDED;
            } else {
                throw new InvalidStateException(electionState.getDescription());
            }
        }
    }

    @Override
    public void emitVotes(List<Vote> votes) throws RemoteException, InvalidStateException {
        synchronized (electionStateLock) {
            if (!electionState.equals(ElectionState.STARTED)) {
                throw new InvalidStateException(electionState.getDescription());
            }
        }
        for (Vote vote : votes) {
            addVote(vote);
        }
    }

    @Override
    /**
     * Donde se elegirá a un partido político ganador para ocupar un cargo
     * ejecutivo nacional, mediante el sistema AV.
     */
    public TreeSet<MutablePair<Party, Double>> getNationalResults() throws InvalidStateException {
        synchronized (electionStateLock) {
            if (electionState.equals(ElectionState.STARTED)) {
                return getResultsFPTP();
            }

            if (electionState.equals(ElectionState.ENDED)) {
                return getResultsAV();
            }
        }

        throw new InvalidStateException(electionState.getDescription());
    }

    public TreeSet<MutablePair<Party, Double>> getResultsAV() throws InvalidStateException {
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

    public TreeSet<MutablePair<Party, Double>> getResultsFPTP() {
        TreeSet<MutablePair<Party, Double>> finalResults;

        readLock.lock();
        try {
            finalResults = fptpCounter.getResultsFPTP();
        } finally {
            readLock.unlock();
        }

        return finalResults;
    }


    @Override
    public TreeSet<MutablePair<Party, Double>> getProvinceResults (ProvinceName name)  throws NoSuchProvinceException, InvalidStateException {
        Province p = getProvince(name);
        synchronized (electionStateLock) {
            if (electionState.equals(ElectionState.STARTED)) {
                return p.getResultsFPTP();
            }

            if (electionState.equals(ElectionState.ENDED)) {
                return p.getResultsSTV();
            }
        }
        throw new InvalidStateException(electionState.getDescription());
    }

    @Override
    public TreeSet<MutablePair<Party, Double>> getPollingStationResults (int id)
            throws NoSuchPollingStationException, InvalidStateException {

        synchronized (electionStateLock) {
            if (electionState.equals(ElectionState.PENDING))
                throw new InvalidStateException(electionState.getDescription());

            for (Province p : provinces) {
                TreeSet<MutablePair<Party, Double>> ret = p.getPollingStationResultsFPTP(id);
                if (ret != null)
                    return ret;
            }
        }

        throw new NoSuchPollingStationException("Polling station number " + id + " does not exists.");
    }


    public void addVote(Vote vote) {
        Optional<Province> maybeProvince = provinces.stream().filter(p -> p.getName().equals(vote.getProvince())).findFirst();
        /* podemos asumir que la provincia se encuentra dentro de las 3 ya agregadas --> no va a ser null el Optional */
        Province p = maybeProvince.get();
        p.addVote(vote);

        writeLock.lock();
        try {
            fptpCounter.addVote(vote);
        } finally {
            writeLock.unlock();
        }
    }

    Province getProvince (ProvinceName name) throws NoSuchProvinceException {
        for (Province p: provinces) {
            if (p.getName().equals(name)) {
                return p;
            }
        }

        throw new NoSuchProvinceException("Province name " + name + " does not exists.");
    }

    void printResults(TreeSet<MutablePair<Party, Double>> results) {
        System.out.println("--------RESULTS--------------");
        results.forEach(pair -> System.out.println("PARTY: " + pair.getLeft() + " VOTES: " + Math.round(pair.getRight() * 100.0 * 10000.0)/10000.0 + "%"));
    }
}
