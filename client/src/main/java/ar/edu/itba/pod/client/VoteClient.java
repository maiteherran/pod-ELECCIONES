package ar.edu.itba.pod.client;
import ar.edu.itba.pod.VoteService;
import ar.edu.itba.pod.client.exceptions.InvalidCSVvotingFileException;
import ar.edu.itba.pod.client.exceptions.InvalidProgramParametersException;
import ar.edu.itba.pod.client.parameters.VoteClientParameters;
import ar.edu.itba.pod.exceptions.InvalidStateException;
import ar.edu.itba.pod.models.Vote;
import ar.edu.itba.pod.util.Party;
import ar.edu.itba.pod.util.ProvinceName;
import ar.edu.itba.pod.util.ServiceName;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;


public class VoteClient extends Client {
    private static Logger logger = LoggerFactory.getLogger(VoteClient.class);
    private static VoteService voteService;
    private static  VoteClientParameters parameters;
    private static List<Vote> votes = new LinkedList<>();
    private static final int POLLING_STATION = 0;
    private static final int PROVINCE = 1 ;
    private static final int PARTIES = 2;

    public static void main(String[] args) {
        parameters = new VoteClientParameters();

        try {
            parameters.validate();
        } catch (InvalidProgramParametersException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        }

        try {
            voteService = (VoteService) getServiceFromServer(parameters.getServerAddress(), ServiceName.VOTE_SERVICE);
        } catch (RemoteException e) {
            logger.error(e.getMessage());
            System.out.println("An error has occurred while establishing a connection to the server. ");
            System.exit(-1);
        } catch (NotBoundException e) {
            logger.error("The service required isn't in the name registry in the server");
            System.out.println("Server error.");
            System.exit(-1);
        } catch (MalformedURLException e) {
            System.out.println("The server address entered is invalid.");
            System.exit(-1);
        }

        try {
            parseVotes();
        } catch (InvalidCSVvotingFileException | IllegalArgumentException e) {
            logger.error("Invalid csv file");
            System.out.println("The voting file's format is invalid. Please provide a valid one.");
            System.exit(-1);
        } catch (FileNotFoundException e) {
            logger.error("File not found");
            System.out.println("The voting file wasn't found. Please provide a valid one.");
            System.exit(-1);
        } catch (IOException e) {
            logger.error("IO error");
            System.exit(-1);
        }

        try {
            voteService.emitVotes(votes);
            if (votes.size() == 1) {
                System.out.println(votes.size() + " vote registered");
            } else {
                System.out.println(votes.size() + " votes registered");
            }
        } catch (InvalidStateException e) {
            logger.error("Invalid state");
            System.out.println("Elections aren't currently being held. No votes can be emitted.");
            System.exit(-1);
        } catch (RemoteException e) {
            logger.error("Connection error");
            System.out.println("An error has occurred while establishing a connection to the server. ");
            System.exit(-1);
        }
    }

    private static void parseVotes () throws InvalidCSVvotingFileException, IOException, IllegalArgumentException {
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(parameters.getVotesPath()))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {
            String[] voteData;
            while ((voteData = csvReader.readNext()) != null) {
                if (voteData.length != 3) {
                    throw new InvalidCSVvotingFileException ("Invalid csv file");
                }
                String[] parties = voteData[PARTIES].split(",");
                List<Party> p = new LinkedList<>();
                for (String province : parties) {
                    p.add(Party.valueOf(province));
                }
                votes.add(new Vote(p , Integer.parseInt(voteData[POLLING_STATION]) , ProvinceName.valueOf(voteData[PROVINCE]) ));
            }
        }
    }
}
