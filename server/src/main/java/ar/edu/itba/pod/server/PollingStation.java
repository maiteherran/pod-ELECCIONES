/*
    Se desea conocer el partido político ganador de cada mesa de votación, sólo
    para fines estadísticos de consulta, mediante el sistema FPTP.
 */
package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.comparators.CountComparator;
import ar.edu.itba.pod.server.enums.Party;
import ar.edu.itba.pod.server.exceptions.NoWinnerException;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.MutablePair;
import sun.tools.tree.DoubleExpression;

import java.util.*;

public class PollingStation {

    private final int id;
    private List<Vote> votes = new ArrayList<>();
    private Comparator<Pair<Party, Double>> countComparator = Comparator.comparing(Pair<Party, Double>::getValue);
    private TreeSet<MutablePair<Party, Double>> resultsFPTP = new TreeSet<>(new CountComparator());
    private TreeSet<MutablePair<Party, Double>> resultsAV = new TreeSet<>(new CountComparator());

    public PollingStation (int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void addVote(Vote vote) {
        votes.add(vote);
    }

    public int getNumberOfVotes() {
        return votes.size();
    }

    public TreeSet<MutablePair<Party, Double>> getResultsFPTP() {

        Iterator<MutablePair<Party, Double>> iterator;
        boolean found;

        for (Vote vote: votes) {

            found = false;
            iterator = resultsFPTP.iterator();
            while (!found && iterator.hasNext()) {
                MutablePair<Party, Double> pair = iterator.next();
                if (pair.getLeft() == vote.getFirstChoice()) {
                    found = true;
                    pair.setRight(pair.getRight() + 1/votes.size());
                }
            }
            if (!found) {
                resultsFPTP.add(new MutablePair<>(vote.getFirstChoice(), (double) (1 / votes.size())));
            }
        }

        return resultsFPTP;
    }

    public TreeSet<MutablePair<Party, Double>> getResultsAV (int choice, Party loser) {

        return resultsAV;
    }

}
