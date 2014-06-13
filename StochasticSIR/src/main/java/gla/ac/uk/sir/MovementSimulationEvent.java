package gla.ac.uk.sir;

import broadwick.stochastic.SimulationEvent;
import broadwick.stochastic.SimulationState;
import java.io.Serializable;

/**
 *
 * @author Tom Doherty
 */
public class MovementSimulationEvent extends SimulationEvent implements Serializable {

    private MovementSimulationState initialState;
    private MovementSimulationState finalState;

    /**
     * Construct a new event from the initial state in one bin to a final state
     * in another bin.
     *
     * @param initialState the name and index of the bin of the initial state.
     * @param finalState the name and index of the bin of the final state.
     */
    public MovementSimulationEvent(final MovementSimulationState initialState, final MovementSimulationState finalState) {
        super(initialState, finalState);
        this.initialState = initialState;
        this.finalState = finalState;
    }

    /**
     *
     * @param initialState 
     */
    @Override
    public final void setInitialState(final SimulationState initialState) {
        try {
            if (initialState instanceof MovementSimulationState) {
                this.initialState = (MovementSimulationState) initialState;
            } else {
                throw new SIRException("Initial state is not an instance of MovementSimulationState");
            }
        } catch (SIRException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param finalState
     */
    @Override
    public final void setFinalState(final SimulationState finalState) {

        try {
            if (finalState instanceof MovementSimulationState) {
                this.finalState = (MovementSimulationState) finalState;
            } else {
                throw new SIRException("final state is not an instance of MovementSimulationState");
            }
        } catch (SIRException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public final MovementSimulationState getInitialState() {
        return (MovementSimulationState) this.initialState;
    }

    /**
     *
     * @return
     */
    @Override
    public final MovementSimulationState getFinalState() {
        return (MovementSimulationState) this.finalState;
    }
}
