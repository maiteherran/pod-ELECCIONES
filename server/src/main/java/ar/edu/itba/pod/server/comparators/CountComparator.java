package ar.edu.itba.pod.server.comparators;

import ar.edu.itba.pod.server.enums.Party;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Comparator;

public class CountComparator implements Comparator<MutablePair<Party, Double>> {
    @Override
    public int compare(MutablePair<Party, Double> o1, MutablePair<Party, Double> o2) {
        return Double.compare(o1.right, o2.right);
    }
}
