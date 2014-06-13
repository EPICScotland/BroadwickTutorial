/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gla.ac.uk.sir.abc;

import broadwick.abc.AbcNamedQuantity;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * This class runs an approximate Bayesian computation, sampling from the
 * posterior.
 */
public class ApproxBayesianComp {

    @Setter
    private AbcDistance distance = new AbcAbsDistance();
    @Getter
    private Map<String, LinkedList<Double>> posteriors;
    private AbcNamedQuantity observedData;
    private AbcNamedQuantity priors;
    private AbcNamedQuantity generatedData;
    private double epsilon;

    /**
     * Create an ABC instance. The created instance will be ready to run a
     * calculation using the default controller and distance measure, these will
     * need to be set if custom ones are required.
     *
     * @param observedData the observed data from which the distance will be
     * calculated.
     * @param generatedData the generated data from which the distance will be
     * calculated.
     * @param prior the sample points from which the generated Data was created
     * @param sensitivity the distance cutoff, distance greater than this value
     * will be ignored.
     */
    public ApproxBayesianComp(final AbcNamedQuantity observedData, final AbcNamedQuantity generatedData, final AbcNamedQuantity parameters, final double sensitivity) {
        this.posteriors = new LinkedHashMap();
        this.observedData = observedData;
        this.generatedData = generatedData;
        this.priors = parameters;
        this.epsilon = sensitivity;
    }

    /**
     * Run the ABC algorithm.
     */
    public final void calculate() {
        if (distance.calculate(generatedData, observedData) < epsilon) {
            save(priors);
        }
    }

    /**
     * Save the sample taken from the prior as it meets the criteria set for
     * being a posterior sample.
     *
     * @param prior the sampled values from the prior.
     */
    private void save(final AbcNamedQuantity prior) {
        System.out.println("Saving sample taken from prior as a posterior");
        if (posteriors.isEmpty()) {
            for (Map.Entry<String, Double> entry : prior.getParameters().entrySet()) {
                final LinkedList<Double> vals = new LinkedList();
                vals.add(entry.getValue());
                posteriors.put(entry.getKey(), vals);
                System.out.println("Parameter being saved is: " + entry.getKey());
                System.out.println("Value being saved is: " + entry.getValue());

            }
        } else {
            for (Map.Entry<String, Double> entry : prior.getParameters().entrySet()) {
                posteriors.get(entry.getKey()).add(entry.getValue());
                System.out.println("Parameter being saved is: " + entry.getKey());
                System.out.println("Value being saved is: " + entry.getValue());
            }
        }
    }
}
