package ar.edu.itba.pod.client;

import ar.edu.itba.pod.ManagementService;
import ar.edu.itba.pod.client.util.ManagementClientActions;
import ar.edu.itba.pod.client.parameters.ManagementClientParameters;
import ar.edu.itba.pod.exceptions.InvalidStateException;
import ar.edu.itba.pod.util.ElectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class ManagementClient {
    private static Logger logger = LoggerFactory.getLogger(ManagementClient.class);
    private static ManagementService managementService;
    private static ManagementClientParameters parameters;

    public static void main(String[] args)  throws Exception {
        parameters = new ManagementClientParameters();
        try {
            parameters.validate();
        } catch (Exception e) {
            logger.error("invalid params");
        }
        managementService = (ManagementService) Naming.lookup("//" + parameters.getServerAddress() + "/ManagementService"); //TODO desde el server poner el mismo nombre en el binding
        executeActionOnServer();
    }

    private static void executeActionOnServer() {
        ManagementClientActions action = ManagementClientActions.valueOf(parameters.getAction());
        ElectionState electionState = ElectionState.PENDING;
        try {
            switch (action) {
                case OPEN:
                    electionState = managementService.openElection();
                    break;
                case STATE:
                    electionState = managementService.getElectionState();
                    break;
                case CLOSE:
                    electionState = managementService.closeElection();
                    break;
            }
        } catch (RemoteException e) {
            logger.error("error");
        } catch (InvalidStateException e) {
            System.out.println(electionState.toString() + "\nThe action requested couldn't be processed.");
        }

    }
}
