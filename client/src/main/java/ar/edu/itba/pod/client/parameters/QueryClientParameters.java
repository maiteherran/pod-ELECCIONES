package ar.edu.itba.pod.client.parameters;

import ar.edu.itba.pod.client.util.QueryType;
import ar.edu.itba.pod.util.ProvinceName;
import org.jeasy.props.PropertiesInjectorBuilder;
import org.jeasy.props.annotations.SystemProperty;

import java.util.Properties;

public class QueryClientParameters extends ClientParameters {
    @SystemProperty(value = "state")
    private String state;

    @SystemProperty(value = "id")
    private String id;

    @SystemProperty(value = "outPath")
    private String outPath;

    @SystemProperty(value = "party")
    private String party;

    private QueryType queryType;

    private ProvinceName provinceName;

    public QueryClientParameters() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public ProvinceName getProvinceName() {
        return provinceName;
    }

    public void validate() throws IllegalArgumentException {
        Properties properties = System.getProperties();

        if (!properties.containsKey("serverAddress") || !properties.containsKey("outPath")
        || (properties.containsKey("id") && properties.containsKey("state"))) {

            System.out.println("Invalid program arguments.\n" +
                    "Here's an example of how you should execute the QueryClient from command line: \n" +
                    "$> java -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName  [ -Dstate=stateName | -Did=pollingPlaceNumber] -DoutPath= fileName ar.edu.itba.pod.client.QueryClient\n" +
                    "where, \n" +
                    "- xx.xx.xx.xx:yyyy: is the IP address and port where the service is published\n" +
                    "- stateName: name of province chosen to solve query #2\n" +
                    "- pollingPlaceNumber: number of table chosen to solve quer #3 \n" +
                    " - fileName: path to file where the results of the query will be placed \n" +
                    "If -Dstate and -Did are omitted, then query #1 will be executed\n"
            );
            throw new IllegalArgumentException();
        }
        if (properties.containsKey("id")) {
            this.queryType = QueryType.POLLING_STATION_QUERY;
        } else if (properties.containsKey("state")) {
            this.queryType = QueryType.PROVINCE_QUERY;
            this.provinceName = ProvinceName.valueOf(state.toUpperCase());
        } else {
            this.queryType = QueryType.NATIONAL_QUERY;
        }

    }
}
