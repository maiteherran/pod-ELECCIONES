package ar.edu.itba.pod.client.parameters;

import ar.edu.itba.pod.client.exceptions.InvalidProgramParametersException;
import ar.edu.itba.pod.client.util.QueryType;
import ar.edu.itba.pod.util.ProvinceName;
import org.jeasy.props.PropertiesInjectorBuilder;
import org.jeasy.props.annotations.SystemProperty;

import java.util.Properties;

public class QueryClientParameters {
    private String serverAddress;
    private String id;
    private String outPath;

    private QueryType queryType;

    private ProvinceName provinceName;

    public QueryClientParameters() {
        super();
    }

    public String getId() {
        return id;
    }

    public String getOutPath() {
        return outPath;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public ProvinceName getProvinceName() {
        return provinceName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void validate() throws InvalidProgramParametersException {
        Properties properties = System.getProperties();
        boolean invalid = false;

        if(!properties.containsKey("serverAddress")) {
            System.out.println("Server address parameter missing.");
            invalid = true;
        }  else {
            serverAddress = properties.getProperty("serverAddress");
        }

        if (!properties.containsKey("outPath")) {
            System.out.println("Out path parameter missing.");
            invalid = true;
        }  else {
            outPath = properties.getProperty("outPath");
        }

        if ((properties.containsKey("id") && properties.containsKey("state"))) {
            System.out.println("You should enter either id or state parameter. Not both.");
            invalid = true;
        } else {
            if (properties.containsKey("id")) {
                id = properties.getProperty("id");
                this.queryType = QueryType.POLLING_STATION_QUERY;
            } else if (properties.containsKey("state")) {
                this.queryType = QueryType.PROVINCE_QUERY;
                try {
                    this.provinceName = ProvinceName.valueOf(properties.getProperty("state").toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Inexistent state");
                    invalid = true;
                }
            } else {
                this.queryType = QueryType.NATIONAL_QUERY;
            }
        }

        if (invalid) {
            printParametersHelp ();
            throw new InvalidProgramParametersException("Invalid program parameters");
        }
    }

    private void printParametersHelp () {
        System.out.println(
                "Here's an example of how you should execute the QueryClient from command line: \n" +
                " $> ./run-QueryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy [ -Dstate=stateName | -Did=pollingPlaceNumber ] -DoutPath=fileName\n" +
                "where, \n" +
                "- xx.xx.xx.xx:yyyy: is the IP address and port where the service is published\n" +
                "- stateName: name of province chosen to solve query #2\n" +
                "- pollingPlaceNumber: number of table chosen to solve query #3 \n" +
                "- fileName: path to file where the results of the query will be placed \n" +
                "If -Dstate and -Did are omitted, then query #1 will be executed\n"
        );
    }
}
