package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.comparators.CountComparator;
import ar.edu.itba.pod.server.enums.Party;
import ar.edu.itba.pod.server.enums.ProvinceName;
import ar.edu.itba.pod.server.exceptions.IllegalVoteException;
import ar.edu.itba.pod.server.exceptions.NoSuchPollingStationException;
import ar.edu.itba.pod.server.exceptions.NoSuchProvinceException;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;

public class GenericServiceImpl {

    private List<Province> provinces = new ArrayList<>();
    private CountComparator comparator = new CountComparator();


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
        return true;
    }

    public TreeSet<MutablePair<Party, Double>> getProvinceResults (ProvinceName name) throws NoSuchProvinceException {

        Province p = getProvince(name);
        return p.getResultsSTV();
    }

    public TreeSet<MutablePair<Party, Double>> getPollingStationResults (PollingStation id, ProvinceName name) throws NoSuchPollingStationException, NoSuchProvinceException {

        Province p = getProvince(name);
        for (PollingStation station: p.getPollingStations()) {
            return station.getResultsFPTP();
        }

        throw new NoSuchPollingStationException("Polling station number " + id + " does not exists.");
    }

    /**
     * Donde se elegirá a un partido político ganador para ocupar un cargo
     * ejecutivo nacional, mediante el sistema AV.
     */
    public TreeSet<MutablePair<Party, Double>> getNationalResults () {

        TreeSet<MutablePair<Party, Double>> results = new TreeSet<>(comparator);
        boolean finalResults = false;
        return getNationalResultsRec(results, 1, finalResults, null);
    }

    private TreeSet<MutablePair<Party, Double>> getNationalResultsRec (TreeSet<MutablePair<Party, Double>> results, int choice, boolean finalResults, Party loser) {

        if (finalResults || choice > 3) {
            return results;
        }

        for (Province p: provinces) {

            TreeSet<MutablePair<Party, Double>> resultsProv = p.getResultsAV(choice, loser);
            addResults(resultsProv, results);
        }

        if (!results.isEmpty() && results.last().getRight() >= 50) {
            return getNationalResultsRec(results, choice, true, loser);
        }

        return getNationalResultsRec(results, choice + 1, finalResults, choice >= 3 ? loser : results.pollFirst().getLeft());
    }

    public Province getProvince (ProvinceName name) throws NoSuchProvinceException {

        for (Province p: provinces) {

            if (p.getName().equals(name)) {

                return p;

            }
        }

        throw new NoSuchProvinceException("Province name " + name + " does not exists.");
    }

    public static void addResults (TreeSet<MutablePair<Party, Double>> from, TreeSet<MutablePair<Party, Double>> to) {

        /*boolean found;
        Iterator<MutablePair<Party, Double>> iteratorFrom = from.iterator();
        Iterator<MutablePair<Party, Double>> iteratorTo;
        while (iteratorFrom.hasNext()) {
            MutablePair<Party, Double> pair = iteratorFrom.next();
            found = false;

            iteratorTo = to.iterator();
            while ( !found && iteratorTo.hasNext() ) {
                MutablePair<Party, Double> finalPair = iteratorTo.next();
                if (finalPair.getLeft() == pair.getLeft()) {
                    found = true;
                    finalPair.setRight((finalPair.getRight() + pair.getRight()) / 2);
                }
            }

            if (!found) {
                to.add(new MutablePair<>(pair.getLeft(), pair.getRight()));
            }
        }*/

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

        TreeSet<MutablePair<Party, Double>> finalResults = new TreeSet<>(new CountComparator());

        for (Province p: provinces) {
            addResults(p.getResultsFPTP(), finalResults);
        }

        return finalResults;
    }

    public void printResults(TreeSet<MutablePair<Party, Double>> results) {

        System.out.println("--------RESULTS--------------");
        results.forEach(pair -> System.out.println("PARTY: " + pair.getLeft() + " VOTES: " + Math.round(pair.getRight() * 100.0 * 10000.0)/10000.0 + "%"));
    }
}
