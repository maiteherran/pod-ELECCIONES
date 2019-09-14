/*
    Se desea conocer el partido político ganador de cada mesa de votación, sólo
    para fines estadísticos de consulta, mediante el sistema FPTP.
 */
package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.comparators.CountComparator;
import ar.edu.itba.pod.server.enums.Party;
import ar.edu.itba.pod.server.exceptions.InvalidChoiceException;
import ar.edu.itba.pod.server.exceptions.NoWinnerException;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.MutablePair;
import sun.tools.tree.DoubleExpression;

import java.util.*;
import java.util.stream.Stream;

public class PollingStation {

    private final int id;
    private List<Vote> votes = new ArrayList<>();
    private TreeSet<MutablePair<Party, Double>> resultsFPTP = new TreeSet<>(new CountComparator());
    private int votesFPTPCount;
    private TreeSet<MutablePair<Party, Double>> resultsAV = new TreeSet<>(new CountComparator());

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

        for (int i=votesFPTPCount; i<votes.size(); i++) {
            addVoteToParty(votes.get(i), votes.get(i).getFirstChoice());
        }

        votesFPTPCount = votes.size();

        return resultsFPTP;
    }

    public TreeSet<MutablePair<Party, Double>> getResultsAV (int choice, Party loser) {

        /*Optional<Province> maybeProvince = provinces.stream().filter(p -> p.getName().equals(province)).findFirst();*/
        /*results.forEach(pair -> System.out.println("PARTY: " + pair.getLeft() + " VOTES: " + Math.round(pair.getRight() * 100.0 * 10000.0)/10000.0 + "%"));*/
        /*inscribedUsers.removeIf(e -> e.getState() == InscriptionState.optout);*/

        ArrayList<MutablePair<Party, Double>> changedVotes = new ArrayList<>();

        if (choice == 1) {
            resultsAV.addAll(getResultsFPTP());
            return resultsAV;
        }

        votes.stream().filter(v -> v.getChoice(choice-1).equals(loser)).forEach(
                vote -> {
                    Optional<MutablePair<Party, Double>> maybeChangedVote = changedVotes.stream().filter
                            (c -> c.getLeft().equals(vote.getChoice(choice))).findFirst();
                    if (!maybeChangedVote.isPresent()) {
                        changedVotes.add(new MutablePair<>(vote.getChoice(choice), (double) 1/votes.size()));
                    } else {
                        maybeChangedVote.get().setRight(maybeChangedVote.get().getRight() + 1/votes.size());
                    }
                }
        );

        /*try {

            for (Vote vote: votes) {

                found = false;
                if (vote.getChoice(choice-1) == loser) {

                    for (MutablePair<Party, Double> changedVote: changedVotes) {

                        if (changedVote.getLeft() == vote.getChoice(choice)) {
                            changedVote.setRight( changedVote.getRight() + 1/votes.size());
                            found = true;
                        }

                    }
                    if (!found) {
                        changedVotes.add(new MutablePair<>(vote.getChoice(choice), (double) 1/votes.size()));
                    }
                }
            }
        } catch (InvalidChoiceException e) {

            *//*
             * nunca deberíamos llegar a este punto
             *//*
            e.printStackTrace();
        }*/

        resultsAV.removeIf(pair -> pair.getLeft().equals(loser));

        resultsAV.forEach(
                result -> {
                    Optional<MutablePair<Party, Double>> toAdd = changedVotes.stream().filter(ch -> ch.getLeft().equals(result.getLeft())).findFirst();
                    toAdd.ifPresent(partyDoubleMutablePair -> result.setRight(result.getRight() + partyDoubleMutablePair.getRight()));
                }
        );

        /*while (iteratorAV.hasNext()) {

            MutablePair<Party, Double> pairResult = iteratorAV.next();
            if (pairResult.getLeft() == loser) {
                *//* lo marcamos como "eliminado" *//*
                pairResult.setRight(0.0);
                pairResult.setLeft(Party.BLANK);
            } else {
                for (MutablePair<Party, Double> pairToAdd: changedVotes) {
                    if (pairToAdd.getLeft() == pairResult.getLeft()) {
                        pairResult.setRight(pairResult.getRight() + pairToAdd.getRight());
                        break;   *//* todo: esta bien esto? *//*
                    }
                }
            }

        }*/

        return resultsAV;
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
}
