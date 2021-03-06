/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gla.ac.uk.sir.abc;

import broadwick.abc.AbcNamedQuantity;
import java.util.Map;

/**
 * Create the distance measure required for the ABC rejection algorithm.
 */
public interface AbcDistance {

    /**
     * Calculated the distance between the observed data and the generated data.
     * Depending on the project, the implementing class can create summary
     * statistics from the data.
     *
     * @param generated the data generated from the mdoel.
     * @param observed the observed data.
     * @return a measure of the calculated distance between the observed and
     * generated data, for example, if we have a simple case where the data is
     * single value the distance returned may be the absolute value of the
     * difference in data.
     */
    double calculate(AbcNamedQuantity generated, AbcNamedQuantity observed);
}

/**
 * Create a default controller that run 1000 samples from the prior.
 */
class AbcAbsDistance implements AbcDistance {

    @Override
    public final double calculate(final AbcNamedQuantity generated, final AbcNamedQuantity observed) {


        final Map<String, Double> gen = generated.getParameters();
        final Map<String, Double> obs = observed.getParameters();

        if ((gen.size() == 1 && obs.size() == 1)
                && gen.keySet().iterator().next().equals(obs.keySet().iterator().next())) {

            final double genValue = gen.values().iterator().next();
            final double obsValue = obs.values().iterator().next();
            return Math.abs(genValue - obsValue);

        } else {
            throw new IllegalArgumentException("AbcAbsDistance can only be used for single valued data, which must contain the same data.");
        }
    }
};
