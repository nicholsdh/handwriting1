package learning.classifiers;

import core.Duple;
import learning.core.Classifier;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.ToDoubleBiFunction;

// KnnTest.test() should pass once this is finished.
public class Knn<V, L> implements Classifier<V, L> {
    private ArrayList<Duple<V, L>> data = new ArrayList<>();
    private ToDoubleBiFunction<V, V> distance;
    private int k;

    public Knn(int k, ToDoubleBiFunction<V, V> distance) {
        this.k = k;
        this.distance = distance;
    }

    @Override
    public L classify(V value) {
        // TODO: Find the distance from value to each element of data. Use Histogram.getPluralityWinner()
        //  to find the most popular label.
        // Arraylist of labels and distances? distance.apply
        ArrayList<Duple<Double, L>> distances = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            distances.add(new Duple<>(distance.applyAsDouble(data.get(i).getFirst(), value), data.get(i).getSecond())); //help. how make duple.
        }
        // sort distances
        Collections.sort(distances, Comparator.comparing(Duple::getFirst));
        //return histogram plurality etc.
        Histogram<L> counts = new Histogram(); // type parameter? do I need to create new histogram? What do I put into it?
        for (int i = 0; i < k; i++) {
            counts.bump(distances.get(i).getSecond());
        }
        return counts.getPluralityWinner();
    }

    @Override
    public void train(ArrayList<Duple<V, L>> training) {
        // TODO: Add all elements of training to data.
        for (Duple<V, L> element : training) {
            data.add(element); // This feels too simple
        }
    }
}
