package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.enums.Party;
import ar.edu.itba.pod.server.enums.ProvinceName;
import ar.edu.itba.pod.server.exceptions.InvalidChoiceException;

import java.util.List;

public class Vote {

    private List<Party> vote;
    private int pollingStation;
    private ProvinceName province;

    public Vote (List<Party> vote, int pollingStation, ProvinceName province) {

        this.pollingStation = pollingStation;
        this.province = province;

        /*  queremos que la lista tenga 3 lugares,
            donde el indice representa la prioridad del partido político,
            aunque el voto sea en blanco o se hayan votado a menos partidos
            (en ese caso el partido politico va a aparecer como un voto en blanco)
         **/
        switch (vote.size()) {

            case 1:
                this.vote = vote;
                vote.add(Party.BLANK);
                vote.add(Party.BLANK);
                break;
            case 2:
                this.vote = vote;
                vote.add(Party.BLANK);
                break;
            case 3:
                this.vote = vote;
                break;

        }

    }

    public int getPollingStation() {
        return pollingStation;
    }

    public List<Party> getVote() {
        return vote;
    }

    public Party getFirstChoice() {
        return vote.get(0);
    }

    public Party getSecondChoice() {
        return vote.get(1);
    }

    public Party getThirdChoice() {
        return vote.get(2);
    }

    public Party getChoice (int choice) throws InvalidChoiceException {

        if (choice <= 0 || choice > 3) {
            throw new InvalidChoiceException("Invalid choice: choice should be a number between 1 and 3.");
        }
        return vote.get(choice-1);
    }

    public ProvinceName getProvince() {
        return province;
    }
}
