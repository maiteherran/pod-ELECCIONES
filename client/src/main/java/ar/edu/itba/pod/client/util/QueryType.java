package ar.edu.itba.pod.client.util;

public enum QueryType {
    NATIONAL_QUERY ( "Query #1"),
    PROVINCE_QUERY ( "Query #2"),
    POLLING_STATION_QUERY ("Query #3"),
    ;

    final String description;

    QueryType(String descr) {
        this.description = descr;
    }
}
