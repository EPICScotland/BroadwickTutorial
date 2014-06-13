/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gla.ac.uk.sir;

import broadwick.stochastic.SimulationState;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Local Manager
 */
public class MovementSimulationState implements SimulationState,Serializable{

    @Getter
    @Setter
    private String agentId;
    @Getter
    @Setter
    private String agentLocation;

    MovementSimulationState(final String agentId, final String location) { 
        this.agentId = agentId;
        this.agentLocation = location;
    }

    @Override
    public final String getStateName() {
        final StringBuilder sb = new StringBuilder(10);
        sb.append(agentId).append(":").append(agentLocation).append(":"); 
        return sb.toString();
    }
}
