package ar.edu.itba.pod.client.util;

public enum ManagementClientAction {
    OPEN ( "Open elections"),
    STATE ( "Get state of elections"),
    CLOSE ("Close elections"),
    ;

    final String description;

    ManagementClientAction(String descr) {
        this.description = descr;
    }
}
