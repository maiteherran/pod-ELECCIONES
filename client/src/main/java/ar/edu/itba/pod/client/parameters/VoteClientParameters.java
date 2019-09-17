package ar.edu.itba.pod.client.parameters;

import org.jeasy.props.PropertiesInjectorBuilder;
import org.jeasy.props.annotations.SystemProperty;

import java.util.Properties;

public class VoteClientParameters extends ClientParameters {
    @SystemProperty(value = "votesPath")
    private String votesPath;

    public VoteClientParameters() {
      super();
    }

    public String getVotesPath() {
        return votesPath;
    }

    public void setVotesPath(String votesPath) {
        this.votesPath = votesPath;
    }

    public void validate() throws IllegalArgumentException {
        Properties properties = System.getProperties();

        if(!properties.containsKey("serverAddress") || !properties.containsKey("votesPath")) {
            System.out.println("Invalid program arguments.\n" +
                    "Here's an example of how you should execute the VoteClient from command line: \n" +
                    "$> java -DserverAddress=xx.xx.xx.xx:yyyy -DvotesPath=fileName ar.edu.itba.pod.client.VoteClient\n" +
                    "where, \n" +
                    "- xx.xx.xx.xx:yyyy: is the IP address and port where the service is published\n" +
                    "- fileName: path to citizinen voting file"
            );
            throw new IllegalArgumentException();
        }
    }


}
