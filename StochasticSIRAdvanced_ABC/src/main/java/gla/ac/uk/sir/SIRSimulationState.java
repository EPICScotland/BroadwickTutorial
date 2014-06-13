/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gla.ac.uk.sir;

import broadwick.stochastic.SimulationState;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Local Manager
 */
public class SIRSimulationState implements SimulationState {

    @Getter
    @Setter
    private String agentId;
    @Getter
    @Setter
    private String state;
   

    SIRSimulationState(final String agentId, final String state) {
        this.agentId=agentId;
        this.state=state;
    }
    

    @Override
    public final String getStateName() {
        final StringBuilder sb = new StringBuilder(10);
        sb.append(agentId).append(":").append(state);
        return sb.toString();
    }
}
