package ar.edu.itba.pod.client.util;

public enum ManagementClientActions {
    OPEN ( "Open elections"),
    STATE ( "Get state of elections"),
    CLOSE ("Close elections"),
    ;

    final String description;

    ManagementClientActions(String descr) {
        this.description = descr;
    }
}
