package gla.ac.uk.sir.agent;

/**
 * Base class for all agents in agent based modelling.
 */
public interface Agent {

    /**
     * Get the id of the agent.
     * @return the agents id.
     */
    //int getId();

    /**
     * Get the name of the state of the agent.
     * @return the name of the agents state.
     */
    String getState();

    /**
     * Set the name of the state of the agent.
     * @param state the name of the agents state.
     */
    void setState(final String state);
}
