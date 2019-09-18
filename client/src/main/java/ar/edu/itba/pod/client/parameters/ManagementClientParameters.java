package ar.edu.itba.pod.client.parameters;
import ar.edu.itba.pod.client.exceptions.InvalidProgramParametersException;
import ar.edu.itba.pod.client.util.ManagementClientAction;
import org.jeasy.props.annotations.SystemProperty;

import java.util.Properties;

public class ManagementClientParameters  {
    private String serverAddress;
    private ManagementClientAction managementAction;

    public ManagementClientAction getManagementAction() {
        return managementAction;
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

        if(!properties.containsKey("action")) {
            System.out.println("Action parameter missing.");
            invalid = true;
        } else {
            try {
                managementAction = ManagementClientAction.valueOf(properties.getProperty("action").toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("The action entered is invalid.");
                invalid = true;
            }
        }

        if (invalid) {
            printParametersHelp ();
            throw new InvalidProgramParametersException("Invalid program parameters.");
        }
    }

    public final String getServerAddress() {
        return serverAddress;
    }


    private void printParametersHelp () {
        System.out.println(
                "Here's an example of how you should execute the ManagementClient from command line: \n" +
                " $> ./run-ManagementClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName \n" +
                "where, \n" +
                "- xx.xx.xx.xx:yyyy: is the IP address and port where the service is published\n" +
                "- actionName: name of the action to perform:\n" +
                "       o open: \n" +
                "       o state: \n" +
                "       o close: \n"
        );
    }
}
