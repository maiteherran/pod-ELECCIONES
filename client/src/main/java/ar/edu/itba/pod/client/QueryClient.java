package ar.edu.itba.pod.client;

import ar.edu.itba.pod.QueryService;
import ar.edu.itba.pod.client.parameters.QueryClientParameters;
import ar.edu.itba.pod.exceptions.InvalidStateException;
import ar.edu.itba.pod.util.Party;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.TreeSet;

public class QueryClient {
    private static Logger logger = LoggerFactory.getLogger(ManagementClient.class);
    private static QueryClientParameters parameters;
    private static QueryService queryService;
    private static TreeSet<MutablePair<Party, Double>> queryResults;

    public static void main(String[] args) throws Exception  {
        parameters = new QueryClientParameters();
        try {
            parameters.validate();
        } catch (Exception e) {
            logger.error("invalid params");
        }
        queryService = (QueryService) Naming.lookup("//" + parameters.getServerAddress() + "/QueryService");
        executeQueryOnServer();
    }

    private static void executeQueryOnServer() {
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
        } catch (RemoteException e) {
            logger.error("error");
        } catch (InvalidStateException e) {
            System.out.println("Your query couldn't be processed.");
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
