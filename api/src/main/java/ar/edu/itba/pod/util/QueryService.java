package ar.edu.itba.pod.util;

import org.apache.commons.lang3.tuple.MutablePair;

import java.rmi.Remote;
import java.util.TreeSet;

public interface QueryService extends Remote {
    /*Query 1: Porcentajes por partido pol[itico a nivel nacional*/
    TreeSet<MutablePair<Party, Double>> getNationalResults ();
    /*Query 2: Porcentajes por partido político a nivel provincial*/
    TreeSet<MutablePair<Party, Double>> getProvinceResults (ProvinceName province);
    /*Query 3: Porcentajes por partido político a nivel mesa de votación*/
    TreeSet<MutablePair<Party, Double>> getPollingStationResults (long id); //todo avisarle a jime que este no recibe por parametro la provincia pq el nro de mesa es independiente de la provinicia.
}
