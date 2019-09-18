package ar.edu.itba.pod.server;

import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.comparators.CountComparator;
import ar.edu.itba.pod.util.Party;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public class VoteCounter {
    private Party party;
    private double votes; /* Is double to make STV easier */
    private ArrayList<VoteCounter> nextVotes;

    public VoteCounter() {
        this(null);
    }

    private VoteCounter(Party party) {
        this.party = party;
        this.votes = 0;
        this.nextVotes = new ArrayList<>();
    }

    public void addVote(Vote vote) {
        addVoteRec(vote, 0);
    }

    private void addVoteRec(Vote vote, int level) {
        votes += 1.0;

        if (level == 3) {
            return;
        }

        Optional<VoteCounter> maybeVoteCounter = nextVotes.stream()
                .filter(votes -> votes.party.equals(vote.getChoice(level + 1))).findFirst();
        if (maybeVoteCounter.isPresent()) {
            maybeVoteCounter.get().addVoteRec(vote, level + 1);
        } else {
            VoteCounter nextVotesItem = new VoteCounter(vote.getChoice(level + 1));
            nextVotesItem.addVoteRec(vote, level + 1);
            nextVotes.add(nextVotesItem);
        }
    }

    public TreeSet<MutablePair<Party, Double>> getResultsFPTP() {
        TreeSet<MutablePair<Party, Double>> resultsAV = new TreeSet<>(new CountComparator());
        nextVotes.forEach(nextVotesItem ->
                resultsAV.add(new MutablePair<>(nextVotesItem.party, nextVotesItem.votes / votes)));
        return resultsAV;
    }

    public TreeSet<MutablePair<Party, Double>> getResultsAV() {
        VoteCounter winner = nextVotes.get(0);
        VoteCounter loser = nextVotes.get(0);

        for (VoteCounter nextVotesItem : nextVotes) {
            if (nextVotesItem.votes > winner.votes) {
                winner = nextVotesItem;
            }
            if (nextVotesItem.votes < loser.votes  || (nextVotesItem.votes == loser.votes &&
                    nextVotesItem.party.getName().compareTo(loser.party.getName()) >= 0)) {
                loser = nextVotesItem;
            }
        }

        if (winner.votes > votes / 2 || nextVotes.size() == 1) {
            return getResultsFPTP();
        } else {
            nextVotes.remove(loser);
            mergeVotes(loser.nextVotes);
            return getResultsAV();
        }
    }

    public TreeSet<MutablePair<Party, Double>> getResultsSTV() {
        TreeSet<MutablePair<Party, Double>> resultsSTV = new TreeSet<>(new CountComparator());
        VoteCounter winner;
        VoteCounter loser;
        double quota = votes / 5;
        int winners = 0;

        while (winners < 5) {
            winner = nextVotes.get(0);
            loser = nextVotes.get(0);

            for (VoteCounter nextVotesItem : nextVotes) {
                if (nextVotesItem.votes > winner.votes || (nextVotesItem.votes == winner.votes &&
                        nextVotesItem.party.getName().compareTo(winner.party.getName()) <= 0)) {
                    winner = nextVotesItem;
                }
                if (nextVotesItem.votes < loser.votes  || (nextVotesItem.votes == loser.votes &&
                        nextVotesItem.party.getName().compareTo(loser.party.getName()) >= 0)) {
                    loser = nextVotesItem;
                }
            }

            if (winner.votes > quota) {
                nextVotes.remove(winner);
                double weight = (winner.votes - quota) / winner.votes;
                weightVotes(winner.nextVotes, weight);
                mergeVotes(winner.nextVotes);
                resultsSTV.add(new MutablePair<>(winner.party, 0.2));
                winners++;
            } else {
                if (nextVotes.size() + winners > 5) {
                    nextVotes.remove(loser);
                    mergeVotes(loser.nextVotes);
                } else {
                    /* The remaining parties are accepted */
                    nextVotes.forEach(nextVotesItem ->
                            resultsSTV.add(new MutablePair<>(nextVotesItem.party, nextVotesItem.votes / votes)));
                    winners = 5;
                }
            }
        }

        return resultsSTV;
    }

    private void weightVotes(List<VoteCounter> votesToWeight, double weight) {
        for (VoteCounter itemToWeight: votesToWeight) {
            itemToWeight.votes *= weight;
            weightVotes(itemToWeight.nextVotes, weight);
        }
    }

    private void mergeVotes(List<VoteCounter> votesToMerge) {
        mergeVotesRec(votesToMerge, 1);
    }

    private void mergeVotesRec(List<VoteCounter> votesToMerge, int level) {
        for (VoteCounter itemToMerge : votesToMerge) {
            Optional<VoteCounter> maybeVoteCounter = nextVotes.stream()
                    .filter(votes -> votes.party.equals(itemToMerge.party)).findFirst();

            if (maybeVoteCounter.isPresent()) {
                maybeVoteCounter.get().votes += itemToMerge.votes;
                maybeVoteCounter.get().mergeVotesRec(itemToMerge.nextVotes, level + 1);
            } else {
                if (level != 1) {
                    nextVotes.add(itemToMerge);
                } else {
                    /* The party has already won or lost */
                    mergeVotesRec(itemToMerge.nextVotes, level);
                }
            }
        }
    }
}
