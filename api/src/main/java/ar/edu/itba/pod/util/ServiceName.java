package ar.edu.itba.pod.util;

/*Este enum es para bindear los servicios*/
public enum ServiceName {
    MANAGEMENT_SERVICE ( "ManagementService"),
    VOTE_SERVICE ( "VoteService"),
    QUERY_SERVICE ("QueryService"),
    ;

    final String name;

    ServiceName(String name) {
        this.name = name;
    }

    public String getServiceName () {
        return name;
    }
}
