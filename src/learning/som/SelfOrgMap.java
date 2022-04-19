package learning.som;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;

public class SelfOrgMap<V> {
    private V[][] map;
    private double[][] trainingCounts;
    private ToDoubleBiFunction<V, V> distance;
    private WeightedAverager<V> averager;

    public SelfOrgMap(int side, Supplier<V> makeDefault, ToDoubleBiFunction<V, V> distance, WeightedAverager<V> averager) {
        this.distance = distance;
        this.averager = averager;
        map = (V[][])new Object[side][side];
        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                map[i][j] = makeDefault.get();
            }
        }
        trainingCounts = new double[side][side];
    }

    // TODO: Return a SOMPoint corresponding to the map square which has the
    //  smallest distance compared to example.
    public SOMPoint bestFor(V example) {
		// Your code here.
        // nested loop to explore "map", find distance between map and example
        // return som point (pair of ints) that says "this is best"
        double current_best = distance.applyAsDouble(map[0][0], example);
        SOMPoint to_return = new SOMPoint(0,0);
        for (int i = 0; i < getMapHeight(); i++) {
            for (int j = 0; j < getMapWidth(); j++) {
                double test = distance.applyAsDouble(map[i][j], example);
                if (test < current_best) {
                    current_best = test;
                    to_return = new SOMPoint(i, j); // Am I recording the right thing here???
                }
            }
        }
        return to_return;
    }

    // TODO: Train this SOM with example.
    //  1. Find the best matching node.
    //  2. For every node in the map:
    //     a. Find the distance weight to the best matching node. Call computeDistanceWeight().
    //     b. Add the distance weight to its training count.
    //     c. Find the effective learning rate. Call effectiveLearningRate().
    //     d. Update the node with the average of itself and example, with example weighted by
    //        the effective learning rate.
    public void train(V example) {
        // Your code here
        // calls best_for, does steps
        SOMPoint best = bestFor(example);

        for (int i = 0; i < getMapWidth(); i++) {
            for (int j = 0; j < getMapHeight(); j++) {
                SOMPoint test = new SOMPoint(i, j); // Probably not right?
                double weight = computeDistanceWeight(best, test);
                trainingCounts[i][j] += weight;
                double elr = effectiveLearningRate(weight, trainingCounts[i][j]);
                map[i][j] = averager.weightedAverage(example, map[i][j], elr);
            }
        }
    }

    // TODO: Find the distance between the locations of sp1 and sp2 in the
    //  self-organizing map. Next, scale the distance based on the map length,
    //  so that it is a value between zero and one. Then, since big distances
    //  should have small weights, subtract it from 1. Finally, make sure it
    //  is not any smaller than zero.
    public double computeDistanceWeight(SOMPoint sp1, SOMPoint sp2) {
		// Your code here
        // euclidian distance
        double length = sp1.distanceTo(sp2);
        length = length / getMapWidth();
        length = 1 - length;
        if (length > 0) {
            return length;
        }
        return 0.0;
    }

    // TODO: First, find the update rate. This is the reciprocal of the training
    //  count. But make sure it is no more than one, even if the training count is
    //  tiny. Then, multiply it by the distance weight.
    public static double effectiveLearningRate(double distWeight, double trainingCounts) {
		// Your code here
        double update_rate = 1/trainingCounts;
        if (update_rate < 1) { //Check to make sure this part is right...
            return update_rate * distWeight;
        }
        return distWeight;
    }

    public V getNode(int x, int y) {
        return map[x][y];
    }

    public int getMapWidth() {
        return map.length;
    }

    public int getMapHeight() {
        return map[0].length;
    }

    public boolean inMap(SOMPoint point) {
        return point.x() >= 0 && point.x() < getMapWidth() && point.y() >= 0 && point.y() < getMapHeight();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SelfOrgMap that) {
            if (this.getMapHeight() == that.getMapHeight() && this.getMapWidth() == that.getMapWidth()) {
                for (int x = 0; x < getMapWidth(); x++) {
                    for (int y = 0; y < getMapHeight(); y++) {
                        if (!map[x][y].equals(that.map[x][y])) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int x = 0; x < getMapWidth(); x++) {
            for (int y = 0; y < getMapHeight(); y++) {
                result.append(String.format("(%d, %d):\n", x, y));
                result.append(getNode(x, y));
            }
        }
        return result.toString();
    }
}
