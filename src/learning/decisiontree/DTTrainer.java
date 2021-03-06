package learning.decisiontree;

import core.Duple;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DTTrainer<V,L, F, FV extends Comparable<FV>> {
	private ArrayList<Duple<V,L>> baseData;
	private boolean restrictFeatures;
	private Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures;
	private BiFunction<V,F,FV> getFeatureValue;
	private Function<FV,FV> successor;
	
	public DTTrainer(ArrayList<Duple<V, L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					 boolean restrictFeatures, BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		baseData = data;
		this.restrictFeatures = restrictFeatures;
		this.allFeatures = allFeatures;
		this.getFeatureValue = getFeatureValue;
		this.successor = successor;
	}
	
	public DTTrainer(ArrayList<Duple<V, L>> data, Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures,
					 BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		this(data, allFeatures, false, getFeatureValue, successor);
	}

	// TODO: Call allFeatures.apply() to get the feature list. Then shuffle the list, retaining
	//  only targetNumber features. Should pass DTTest.testReduced().
	public static <V,L, F, FV  extends Comparable<FV>> ArrayList<Duple<F,FV>>
	reducedFeatures(ArrayList<Duple<V,L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					int targetNumber) {
		ArrayList<Duple<F, FV>> featlist = allFeatures.apply(data);
		Collections.shuffle(featlist);
		ArrayList<Duple<F, FV>> newfeatlist = new ArrayList<>();
		for (int i = 0; i < targetNumber; i++) {
			newfeatlist.add(featlist.get(i));
		}
		return newfeatlist;
    }
	
	public DecisionTree<V,L,F,FV> train() {
		return train(baseData);
	}

	public static <V,L> int numLabels(ArrayList<Duple<V,L>> data) {
		return data.stream().map(Duple::getSecond).collect(Collectors.toUnmodifiableSet()).size();
	}
	
	private DecisionTree<V,L,F,FV> train(ArrayList<Duple<V,L>> data) {
		// TODO: Implement the decision tree learning algorithm
		if (numLabels(data) == 1) {
			// TODO: Return a leaf node consisting of the only label in data
			// System.out.println("Only label in data: ");
			return new DTLeaf<>(data.get(0).getSecond());
		} else {
			ArrayList<Duple<F, FV>> workinglist = new ArrayList<Duple<F, FV>>();
			// TODO: Return an interior node.
			//  If restrictFeatures is false, call allFeatures.apply() to get a complete list
			//  of features and values, all of which you should cosider when splitting.
			//  If restrictFeatures is true, call reducedFeatures() to get sqrt(# features)
			//  of possible features/values as candidates for the split. In either case,
			//  for each feature/value combination, use the splitOn() function to break the
			//  data into two parts. Then use gain() on each split to figure out which
			//  feature/value combination has the highest gain. Use that combination, as
			//  well as recursively created left and right nodes, to create the new
			//  interior node.
			//  Note: It is possible for the split to fail; that is, you can have a split
			//  in which one branch has zero elements. In this case, return a leaf node
			//  containing the most popular label in the branch that has elements.
			if (!restrictFeatures) {
				workinglist = allFeatures.apply(data);
			} else {
				int target = (int) Math.sqrt(allFeatures.apply(data).size());
				workinglist = reducedFeatures(data, allFeatures, target);
			}
			// duple variable here
			Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> bestdup = null;
			// best gain -- what's best value? negative double max
			Duple<F, FV> bestitem = null;
			double bestgain = -Double.MAX_VALUE;
			for (Duple<F, FV> item : workinglist) { /// maxgain, maxsplits variables, etc.
				Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> testdup = splitOn(data, item.getFirst(), item.getSecond(), getFeatureValue);

				double testgain = gain(data, testdup.getFirst(), testdup.getSecond());
				if (testgain > bestgain) {
					bestdup = testdup;
					bestgain = testgain;
					bestitem = item;
				}
			} // Create and return interior node?? REMEMBER TO NOT RETURN NULL
			if (bestdup.getFirst().size() == 0) {
				System.out.println("Size of first was 0");
				return new DTLeaf(mostPopularLabelFrom(bestdup.getSecond()));
			} else if (bestdup.getSecond().size() == 0) {
				System.out.println("Size of second was 0");
				return new DTLeaf(mostPopularLabelFrom(bestdup.getFirst()));
			}
			DecisionTree<V,L,F,FV> left = train(bestdup.getFirst());
			DecisionTree<V,L,F,FV> right = train(bestdup.getSecond());
			DTInterior<V, L, F, FV> interior = new DTInterior<>(bestitem.getFirst(), bestitem.getSecond(), left, right, getFeatureValue, successor);
			// System.out.println("Returning an interior...");
			return interior;
		}		
	}

	public static <V,L> L mostPopularLabelFrom(ArrayList<Duple<V, L>> data) {
		Histogram<L> h = new Histogram<>();
		for (Duple<V,L> datum: data) {
			h.bump(datum.getSecond());
		}
		return h.getPluralityWinner();
	}

	// TODO: Generates a new data set by sampling randomly with replacement. It should return
	//    an `ArrayList` that is the same length as `data`, where each element is selected randomly
	//    from `data`. Should pass `DTTest.testResample()`.
	public static <V,L> ArrayList<Duple<V,L>> resample(ArrayList<Duple<V,L>> data) {
		ArrayList<Duple<V,L>> newdata = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			Random rnd = new Random();
			int index = rnd.nextInt(data.size());
			newdata.add(data.get(index));
		}
		return newdata;
	}

	public static <V,L> double getGini(ArrayList<Duple<V,L>> data) {
		// TODO: Calculate the Gini coefficient:
		//  For each label, calculate its portion of the whole (p_i).
		//  Use of a Histogram<L> for this purpose is recommended.
		//  Gini coefficient is 1 - sum(for all labels i, p_i^2)
		//  Should pass DTTest.testGini().
		Histogram<L> labelhist = new Histogram<>();// How do I use histogram?
		ArrayList<L> labellist = new ArrayList<>();
		for (Duple<V, L> labelpair : data) {
			labelhist.bump(labelpair.getSecond());
			if (!labellist.contains(labelpair.getSecond())) {
				labellist.add(labelpair.getSecond());
			}
		}
		double sum = 0;
		for (L label : labellist) {
			sum += Math.pow(labelhist.getPortionFor(label), 2);
		}
			// Iterate through, data.getsecond?
		return 1.0 - sum;
	}

	public static <V,L> double gain(ArrayList<Duple<V,L>> parent, ArrayList<Duple<V,L>> child1,
									ArrayList<Duple<V,L>> child2) {
		// TODO: Calculate the gain of the split. Add the gini values for the children.
		//  Subtract that sum from the gini value for the parent. Should pass DTTest.testGain().
		double sum = getGini(child1) + getGini(child2);
		return getGini(parent) - sum;
	}

	public static <V,L, F, FV  extends Comparable<FV>> Duple<ArrayList<Duple<V,L>>,ArrayList<Duple<V,L>>> splitOn
			(ArrayList<Duple<V,L>> data, F feature, FV featureValue, BiFunction<V,F,FV> getFeatureValue) {
		// TODO:
		//  Returns a duple of two new lists of training data.
		//  The first returned list should be everything from this set for which
		//  feature has a value less than or equal to featureValue. The second
		//  returned list should be everything else from this list.
		//  Should pass DTTest.testSplit().
		ArrayList<Duple<V, L>> duplebuilder1 = new ArrayList<>();
		ArrayList<Duple<V, L>> duplebuilder2 = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			if (getFeatureValue.apply(data.get(i).getFirst(), feature).compareTo(featureValue) <= 0) { // Something strange when I flip < to >...
				duplebuilder1.add(data.get(i));
			}
			else {
				duplebuilder2.add(data.get(i));
			}
		}
		// Remember to not return NULL!!!!
		return new Duple<>(duplebuilder1, duplebuilder2);
	}
}
