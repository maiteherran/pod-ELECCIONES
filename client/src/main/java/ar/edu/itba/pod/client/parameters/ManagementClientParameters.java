package ar.edu.itba.pod.client.parameters;
import ar.edu.itba.pod.client.exceptions.InvalidProgramParametersException;
import ar.edu.itba.pod.client.util.ManagementClientAction;
import org.jeasy.props.annotations.SystemProperty;

import java.util.Properties;

public class ManagementClientParameters extends ClientParameters {
    @SystemProperty(value = "action")
    private String action;
    private ManagementClientAction managementAction;

    public ManagementClientParameters() {
        super();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ManagementClientAction getManagementAction() {
        return managementAction;
    }

    public void validate() throws InvalidProgramParametersException {
        Properties properties = System.getProperties();
        boolean invalid = false;
        if(!properties.containsKey("serverAddress")) {
            System.out.println("Server address parameter missing.");
            invalid = true;
        }

        if(!properties.containsKey("action")) {
            System.out.println("Action parameter missing.");
            invalid = true;
        } else {
            try {
                managementAction = ManagementClientAction.valueOf(action.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("The action entered is invalid.");
                invalid = true;
            }
        }

        if (invalid) {
            printParametersHelp ();
            throw new InvalidProgramParametersException("Invalid program parameters.");
        }

        managementAction = ManagementClientAction.valueOf(action.toUpperCase());
    }

    private void printParametersHelp () {
        System.out.println(
                "Here's an example of how you should execute the ManagementClient from command line: \n" +
                "$> java -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName ar.edu.itba.pod.client.ManagementClient\n" +
                "where, \n" +
                "- xx.xx.xx.xx:yyyy: is the IP address and port where the service is published\n" +
                "- actionName: name of the action to perform:\n" +
                "       o open: \n" +
                "       o state: \n" +
                "       o close: \n"
        );
    }
}
