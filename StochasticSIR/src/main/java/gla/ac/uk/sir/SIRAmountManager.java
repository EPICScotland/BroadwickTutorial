/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gla.ac.uk.sir;

import broadwick.stochastic.AmountManager;
import broadwick.stochastic.SimulationEvent;

/**
 *
 * @author Local Manager
 */
public class SIRAmountManager implements AmountManager{

    @Override
    public final void performEvent(final SimulationEvent reaction, final int times) {
        System.out.println("IN SOHOAMOUNTMANAGER PERFORMEVENT - DOING NOTHING!");
    }

    @Override
    public final String toVerboseString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public final void resetAmount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public final void save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public final void rollback() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
