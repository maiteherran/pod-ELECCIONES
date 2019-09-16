/*
    Se desea conocer el partido político ganador de cada mesa de votación, sólo
    para fines estadísticos de consulta, mediante el sistema FPTP.
 */
package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.comparators.CountComparator;
import ar.edu.itba.pod.util.Party;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;

public class PollingStation {

    private final int id;
    private List<Vote> votes = new ArrayList<>();
    private TreeSet<MutablePair<Party, Double>> resultsFPTP = new TreeSet<>(new CountComparator());
    private int votesFPTPCount;

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
        votes.add(vote);
    }

    public int getNumberOfVotes() {
        return votes.size();
    }

    public TreeSet<MutablePair<Party, Double>> getResultsFPTP() {

        for (int i = votesFPTPCount; i < getNumberOfVotes(); i++) {
            addVoteToParty(votes.get(i), votes.get(i).getFirstChoice());
        }

        votesFPTPCount = getNumberOfVotes();

        return resultsFPTP;
    }

    private void addVoteToParty (Vote vote, Party party) {

        Optional<MutablePair<Party, Double>> maybeResult = resultsFPTP.stream().filter(result -> result.getLeft().equals(party)).findFirst();
        if (maybeResult.isPresent()) {
            maybeResult.get().setRight(maybeResult.get().getRight() + 1.0/(double)votes.size());
        } else {
            MutablePair<Party, Double> newVote = new MutablePair<>(vote.getFirstChoice(),  (1.0) / (double) votes.size());
            resultsFPTP.add(newVote);
        }
    }

    public void countVotes(VoteCounter counter) {
        votes.forEach(counter::addVote);
    }
}
