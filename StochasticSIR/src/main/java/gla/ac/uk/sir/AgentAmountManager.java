package gla.ac.uk.sir;

import broadwick.stochastic.AmountManager;
import gla.ac.uk.sir.agent.SIRAgent;
import java.util.Collection;

/**
 * Amount manager that deals with collections of agents.
 */
public abstract class AgentAmountManager implements AmountManager {

    /**
     * Get a collection of agents in a given state.
     * @param state the state of animals we wish to find.
     * @return  a collection of agents.
     */
    public abstract Collection<SIRAgent> getAgentsInState(final String state);
}
