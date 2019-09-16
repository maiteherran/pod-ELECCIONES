package ar.edu.itba.pod.util;

public enum ElectionState {
    PENDING ( "Election hasn't started. "),
    STARTED ( "Election is being held. "),
    ENDED ("Election has ended. "),
    ;

    final String description;

    ElectionState(String descr) {
        this.description = descr;
    }
}
