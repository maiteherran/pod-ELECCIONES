package ar.edu.itba.pod.client.parameters;

import ar.edu.itba.pod.client.exceptions.InvalidProgramParametersException;
import org.jeasy.props.PropertiesInjectorBuilder;
import org.jeasy.props.annotations.SystemProperty;

import java.util.Properties;

public class VoteClientParameters  {
    private String serverAddress;
    private String votesPath;

    public VoteClientParameters() {
      super();
    }

    public String getVotesPath() {
        return votesPath;
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
        } else {
            serverAddress = properties.getProperty("serverAddress");
        }

        if (!properties.containsKey("votesPath")) {
            System.out.println("Votes path parameter missing.");
            invalid = true;
        } else {
            votesPath = properties.getProperty("votesPath");
        }
        if (invalid) {
            printParametersHelp ();
            throw new InvalidProgramParametersException("Invalid program parameters");
        }
    }

    private void printParametersHelp () {
        System.out.println(
                "Here's an example of how you should execute the VoteClient from command line: \n" +
                "$> ./run-VoteClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -DvotesPath=fileName\n" +
                "where, \n" +
                "- xx.xx.xx.xx:yyyy: is the IP address and port where the service is published\n" +
                "- fileName: path to citizinen voting file"
        );
    }


}
