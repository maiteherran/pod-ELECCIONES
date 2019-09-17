package ar.edu.itba.pod.client;

import ar.edu.itba.pod.QueryService;
import ar.edu.itba.pod.client.parameters.QueryClientParameters;
import ar.edu.itba.pod.exceptions.InvalidStateException;
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
import java.util.TreeSet;


public class QueryClient extends Client {
    private static Logger logger = LoggerFactory.getLogger(QueryClient.class);
    private static QueryClientParameters parameters;
    private static QueryService queryService;
    private static TreeSet<MutablePair<Party, Double>> queryResults;

    private static void executeQueryOnServer() throws RemoteException, IllegalArgumentException {
        try {
            switch (parameters.getQueryType()) {
                case NATIONAL_QUERY:
                    queryResults = queryService.getNationalResults();
                    printNwinnersInConsole (1);
                    break;
                case PROVINCE_QUERY:
                    queryResults = queryService.getProvinceResults(parameters.getProvinceName());
                    printNwinnersInConsole (5);
                    break;
                case POLLING_STATION_QUERY:
                    queryResults = queryService.getPollingStationResults(Long.parseLong(parameters.getId()));
                    printNwinnersInConsole (1);
                    break;
            }
            resultsToCsv ();
        } catch (InvalidStateException e) {
            System.out.println("Your query couldn't be processed.");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        parameters = new QueryClientParameters();
        try {
            parameters.validate();
            queryService = (QueryService) getServiceFromServer(parameters.getServerAddress(), ServiceName.QUERY_SERVICE);
            executeQueryOnServer();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid params");
            System.exit(-1);
        } catch (RemoteException  | NotBoundException | MalformedURLException e) {
            logger.error("Connection error");
            System.out.println("An error occured");
            System.exit(-1);
        }
    }

    /*Escribe los resultados de las elecciones en un archivo csv*/
    private static void resultsToCsv () {
        //"/home/maite/Documents/query.csv"
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(parameters.getOutPath()), ';', CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            for (MutablePair<Party, Double> pair : queryResults){
                String[] outputline = new String[2];
                outputline[0] = Math.round(pair.getRight() * 100.0 * 10000.0)/10000.0 + "%";
                outputline[1] = String.valueOf(pair.getLeft());
                writer.writeNext(outputline);
            }
            writer.close();
        } catch (IOException e) {
            logger.error("Query results couldn't be written to file");
            System.out.println("Results couldn't be written to file.");
        }
    }

    /*Imprime los primeros n partidos ganadores en la consola*/
    private static void printNwinnersInConsole (int n) {
        StringBuilder s = new StringBuilder();
        int aux = 0;
        for (MutablePair<Party, Double> pair : queryResults){
            s.append(pair.left);
            if (aux < n) {
                if (aux == n-1) {
                    break;
                }
                s.append(", ");
            }
            aux++;
        }
        System.out.println(s + " won the election.");
    }
}
