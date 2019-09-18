package ar.edu.itba.pod.client;

import ar.edu.itba.pod.ManagementService;
import ar.edu.itba.pod.QueryService;
import ar.edu.itba.pod.client.exceptions.InvalidProgramParametersException;
import ar.edu.itba.pod.client.parameters.QueryClientParameters;
import ar.edu.itba.pod.exceptions.InvalidStateException;
import ar.edu.itba.pod.exceptions.NoSuchPollingStationException;
import ar.edu.itba.pod.exceptions.NoSuchProvinceException;
import ar.edu.itba.pod.util.Party;
import ar.edu.itba.pod.util.ServiceName;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.TreeSet;


public class QueryClient extends Client {
    private static Logger logger = LoggerFactory.getLogger(QueryClient.class);
    private static QueryClientParameters parameters;
    private static QueryService queryService;
    private static TreeSet<MutablePair<Party, Double>> queryResults = new TreeSet<>();

    public static void main(String[] args) {
        parameters = new QueryClientParameters();

        try {
            parameters.validate();
        } catch (InvalidProgramParametersException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        }

        try {
            queryService = (QueryService) getServiceFromServer(parameters.getServerAddress(), ServiceName.QUERY_SERVICE);
        } catch (RemoteException e) {
            logger.error(e.getMessage());
            System.out.println("An error has occurred while establishing a connection to the server. ");
            System.exit(-1);
        } catch (NotBoundException e) {
            logger.error("The service required isn't in the name registry in the server");
            System.out.println("Server error");
            System.exit(-1);
        } catch (MalformedURLException e) {
            System.out.println("The server address entered is invalid.");
            System.exit(-1);
        }

        try {
            executeQueryOnServer();
        } catch (RemoteException e) {
            logger.error(e.getMessage(), e);
            System.out.println("An error has occurred while establishing a connection to the server. ");
            System.exit(-1);
        }
    }

    private static void executeQueryOnServer() throws RemoteException {
        try {
            switch (parameters.getQueryType()) {
                case NATIONAL_QUERY:
                    queryResults = queryService.getNationalResults();
                    System.out.println("At a national level: ");
                    printNwinnersInConsole (1);
                    break;
                case PROVINCE_QUERY:
                    queryResults = queryService.getProvinceResults(parameters.getProvinceName());
                    System.out.println("In state " + parameters.getProvinceName().toString() + ": ");
                    printNwinnersInConsole (5);
                    break;
                case POLLING_STATION_QUERY:
                    queryResults = queryService.getPollingStationResults(Integer.parseInt(parameters.getId()));
                    System.out.println("In polling station " + parameters.getId() + ": ");
                    printNwinnersInConsole (1);
                    break;
            }
            resultsToCsv ();
        } catch (InvalidStateException e) {
            System.out.println("Your query couldn't be processed due to an invalid state in the elections.");
        } catch (NoSuchProvinceException  e) {
            System.out.println("No votes have been emitted in the state entered yet.");
        } catch ( NoSuchPollingStationException e) {
            System.out.println("No votes have been emitted in the polling station entered yet.");
        }
    }

    /*Escribe los resultados de las elecciones en un archivo csv*/
    private static void resultsToCsv () {
        //"/home/maite/Documents/query.csv"
        try (CSVWriter writer = new CSVWriter(new FileWriter(parameters.getOutPath()), ';', CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            for (MutablePair<Party, Double> pair : queryResults){
                String[] outputline = new String[2];
                outputline[0] = String.format("%.2f", pair.getRight() * 100.0) + "%";
                outputline[1] = String.valueOf(pair.getLeft());
                writer.writeNext(outputline);
            }
        } catch (Exception e) {
            logger.error("Query results couldn't be written to file");
            System.out.println("Results couldn't be written to file.");
        }
    }

    /*Imprime los primeros n partidos ganadores en la consola*/
    private static void printNwinnersInConsole (int n) {
        if (queryResults.size() == 0 ){
            System.out.println("No votes have been registered yet. No results available.");
            return;
        }
        StringBuilder s = new StringBuilder();
        int position = 0;
            for (MutablePair<Party, Double> pair : queryResults){
            s.append(pair.left);
            if (position < n) {
                if (position == n-1 || position == queryResults.size() -1) {
                    break;
                }
                s.append(", ");
            }
            position++;
        }
        System.out.println(s + " won the election.");
    }


}
