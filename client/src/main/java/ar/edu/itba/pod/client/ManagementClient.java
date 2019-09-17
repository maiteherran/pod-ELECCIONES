package ar.edu.itba.pod.client;

import ar.edu.itba.pod.ManagementService;
import ar.edu.itba.pod.client.parameters.ManagementClientParameters;
import ar.edu.itba.pod.exceptions.InvalidStateException;
import ar.edu.itba.pod.util.ElectionState;
import ar.edu.itba.pod.util.ServiceName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ManagementClient extends Client {
    private static Logger logger = LoggerFactory.getLogger(ManagementClient.class);
    private static ManagementService managementService;
    private static ManagementClientParameters parameters;

    public static void main (String[] args) {
        parameters = new ManagementClientParameters();
        try {
            parameters.validate();
            managementService = (ManagementService) getServiceFromServer(parameters.getServerAddress(), ServiceName.MANAGEMENT_SERVICE);
            executeActionOnServer();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid params");
            System.exit(-1);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            logger.error("Connection error");
            System.out.println("A connection error occured");
            System.exit(-1);
        }
    }

    private static void executeActionOnServer () throws RemoteException {
        try {
            switch (parameters.getManagementAction()) {
                case OPEN:
                    managementService.openElection();
                    System.out.println("Election started.");
                    break;
                case STATE:
                    ElectionState state = managementService.getElectionState();
                    System.out.println(state.getDescription());
                    break;
                case CLOSE:
                    managementService.closeElection();
                    System.out.println("Election has been closed.");
                    break;
            }
        } catch (InvalidStateException e) {
            System.out.println(managementService.getElectionState().getDescription()+ "\nThe action requested couldn't be processed.");
        }
    }
}
