/*
    Donde, en cada provincia, se elegirán cinco partidos políticos
    ganadores para ocupar los 5 representantes que cada provincia tiene en la
    legislatura nacional, mediante el sistema STV.
 */
package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.enums.Party;
import ar.edu.itba.pod.server.enums.ProvinceName;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.MutablePair;
import ar.edu.itba.pod.server.comparators.CountComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Province {

    private List<PollingStation> pollingStations = new ArrayList<>();
    private TreeSet<MutablePair<Party, Double>> resultsAV = new TreeSet<>(new CountComparator());
    private TreeSet<MutablePair<Party, Double>> resultsSTV = new TreeSet<>(new CountComparator());
    private TreeSet<MutablePair<Party, Double>> resultsFPTP = new TreeSet<>(new CountComparator());
    private ProvinceName name;

    public Province (ProvinceName name) {
        this.name=name;
    }

    public void addVote(Vote vote) {

        boolean pollingFound = false;

        for (PollingStation p: pollingStations) {

            if (p.getId() == vote.getPollingStation()) {
                p.addVote(vote);
                pollingFound = true;
                break;
            }
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

    public TreeSet<MutablePair<Party, Double>> getResultsAV (int choice, Party loser) {

        for (PollingStation station: pollingStations) {

            GenericServiceImpl.addResults(station.getResultsAV(choice, loser), resultsAV);
        }
        return resultsAV;
    }

    public TreeSet<MutablePair<Party, Double>> getResultsSTV () {

        return resultsSTV;
    }

    public TreeSet<MutablePair<Party, Double>> getResultsFPTP () {

        for (PollingStation station: pollingStations) {
            GenericServiceImpl.addResults(station.getResultsFPTP(), resultsFPTP);
        }
        return resultsFPTP;
    }
}
