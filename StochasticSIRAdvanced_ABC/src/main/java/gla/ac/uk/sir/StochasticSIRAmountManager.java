package gla.ac.uk.sir;

import broadwick.data.Animal;
import broadwick.data.Lookup;
import broadwick.stochastic.SimulationEvent;
import broadwick.stochastic.SimulationState;
import broadwick.stochastic.TransitionKernel;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import gla.ac.uk.sir.agent.SIRAgent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * implementation of the agent amount manager.
 */
@Slf4j
public class StochasticSIRAmountManager extends AgentAmountManager {

    @Getter
    @Setter
    private Collection<SIRAgent> agents = new ArrayList<>(5);
    @Getter
    @Setter
    private Map agentMap1 = new HashMap();
    private Collection<SIRAgent> savedAgents = new ArrayList<>(5);
    @Setter
    @Getter
    private TransitionKernel kernel = null;
    @Setter
    private double beta;
    @Setter
    private double rate2;
    @Getter
    @Setter
    //Initial state of first transmission event
    private String initialState1;
    @Getter
    @Setter
    //Initial state of second transmission event
    private String initialState2;
    @Getter
    @Setter
    //Final state of first transmission event
    private String finalState1;
    @Getter
    @Setter
    //Final state of second transmission event
    private String finalState2;
    @Getter
    @Setter
    //Increment for Tau Leap
    private String tauLeapIncrement;
    @Getter
    @Setter
    //Current date
    private String date;
    @Getter
    @Setter
    private String infectedAnimalID;
    @Getter
    @Setter
    private int scenarioID;
    /**
     * Create the default agent amount manager. This is the manager for agents
     * where each agent is handled individually.
     *
     * @param startdate
     */
    public StochasticSIRAmountManager(int startdate, Lookup lookup, String infectedAnimalID) {
        super();
        //UNCOMMENT 4b
        createInitialAnimalCollection(lookup, startdate, infectedAnimalID);
        log.info("Map of size {} has been created", agentMap1.size());
    }

    @Override
    public final void performEvent(final SimulationEvent event,
            final int times) {
        //UNCOMMENT 5a
        if (event instanceof MovementSimulationEvent) {
            final MovementSimulationEvent me = (MovementSimulationEvent) event;
            // Note iState location is blank - so never use it.
            final MovementSimulationState fState = (MovementSimulationState) me.getFinalState();
            final SIRAgent agent = (SIRAgent) agentMap1.get(fState.getAgentId());
            //Change location of animal to new location   
            //System.out.println("Moving animal to: " + fState.getAgentLocation());
            if (agent != null) { // Already in map so normal movement - update its location
                agent.setCurrentLocation(fState.getAgentLocation());
            } else { // This is a birth movement as not in map already - create new object for agent
                final SIRAgent newAgent = new SIRAgent(fState.getAgentId(), "SUSCEPTIBLE", fState.getAgentLocation());
                this.agents.add(newAgent);
                this.agentMap1.put(fState.getAgentId(), newAgent);
            }

        } else if (event instanceof SimulationEvent) {
            final SIRSimulationState finalState = (SIRSimulationState) event.getFinalState();
            try {
                if (this.agentMap1.containsKey(finalState.getAgentId())) {
                    final SIRAgent agent = (SIRAgent) this.agentMap1.get(finalState.getAgentId());
                    //Change state of animal to new state
                    agent.setInfectionStatus(finalState.getState());
                }
            } catch (Exception ex) {
                Logger.getLogger(StochasticSIRAmountManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public final TransitionKernel updateTransitionKernelFromSusceptible(int currentDate) {
        this.kernel.clear();
        // do all S -> I events
        Collection<SIRAgent> susceptibleAnimals = this.getListAgentsInState("SUSCEPTIBLE");
        log.info("Number suceptibles = {}", susceptibleAnimals.size());
        Collection<SIRAgent> infectiousAnimals = this.getListAgentsInState("INFECTIOUS");
        log.info("Number infectious = {}", infectiousAnimals.size());
        Collection<String> infectedLocations = new ArrayList<>();
        for (SIRAgent infected : infectiousAnimals) {
            //log.info("Infected animal ID is = {}",infected);
            if (!infectedLocations.contains(infected.getCurrentLocation())) {
                infectedLocations.add(infected.getCurrentLocation());
            }
        }
        log.info("Infected locations = {}", infectedLocations);
        for (final String location : infectedLocations) {
            final Collection<SIRAgent> susceptibleAnimalsAtInfectedLocation = Collections2.filter(susceptibleAnimals,
                    new Predicate<SIRAgent>() {
                        @Override
                        public final boolean apply(final SIRAgent thisSusceptibleAnimal) {
                            return thisSusceptibleAnimal.getCurrentLocation().contains(location);
                        }
                    });
            final Collection<SIRAgent> infectedAnimalsAtInfectedLocation = Collections2.filter(infectiousAnimals,
                    new Predicate<SIRAgent>() {
                        @Override
                        public final boolean apply(final SIRAgent thisInfectiousAnimal) {
                            return thisInfectiousAnimal.getCurrentLocation().contains(location);
                        }
                    });

            log.info("Susceptible animals in location {} = {}", location, susceptibleAnimalsAtInfectedLocation);
            log.info("Infected animals in location {} = {}", location, infectedAnimalsAtInfectedLocation);

            for (final SIRAgent animal : susceptibleAnimalsAtInfectedLocation) {
                final SimulationState initialState = new SIRSimulationState(animal.getId(), "SUSCEPTIBLE");
                final SimulationState finalState = new SIRSimulationState(animal.getId(), this.getFinalState1());
                // Need to put in beta times the number of infected animals at each location.
                int numberOfInfectedAtLocation = infectedAnimalsAtInfectedLocation.size();
                kernel.addToKernel(new SimulationEvent(initialState, finalState), beta * numberOfInfectedAtLocation);
            }
        }
        return this.kernel;
    }

    public final TransitionKernel updateTransitionKernelForOtherStates() {

        // do all I -> R events
        for (SIRAgent animal : this.getListAgentsInState(this.getInitialState2())) {
            final SimulationState initialState = new SIRSimulationState(animal.getId(), this.getInitialState2());
            final SimulationState finalState = new SIRSimulationState(animal.getId(), this.getFinalState2());
            this.kernel.addToKernel(new SimulationEvent(initialState, finalState), rate2);
        }
        return this.kernel;
    }

    /**
     * Create a new collection of initialAnimalColl that were alive at the start
     * of our simulation, setting the current location for each and the
     * infection status for each one of the seeds.
     * <p/>
     * @return a collection of agents that were alive at the beginning of the
     * simulation.
     */
    // UNCOMMENT 4c
    private Collection<SIRAgent> createInitialAnimalCollection(Lookup lookup, int startDate, String infectedAnimalID) {
        log.debug("Lookup is {}", lookup);
        String full_state_name = "";
        log.info("Current infected animal ID is {}", infectedAnimalID);
        final Collection<SIRAgent> initialAnimalColl = new ArrayList<>();
        for (Animal animal : lookup.getAnimals(startDate)) {
            log.info("Initialising animal {}", animal);
            full_state_name = "SUSCEPTIBLE";
            final String currentHerdId = lookup.getAnimalLocationIdAtDate(animal.getId(), startDate);
            log.trace("Current herd of animal {} is {}", animal.getId(), currentHerdId);

            if (infectedAnimalID.equals(animal.getId())) {
                full_state_name = "INFECTIOUS";
                log.debug(String.format("Initialising %s as %s at location %s", animal.getId(), "Infectious", currentHerdId));
            }
            final SIRAgent agent = new SIRAgent(animal.getId(), full_state_name, currentHerdId);
            this.agentMap1.put(animal.getId(), agent);
            this.agents.add(agent);
            initialAnimalColl.add(agent);
        }
        return initialAnimalColl;
    }

    /**
     *
     * @param location
     * @param state
     * @return
     */
    public final Collection<SIRAgent> getAgentAtLocationInState(final String location, final String state) {
        final Collection<SIRAgent> agentsAtLocation = new ArrayList<>();
        for (SIRAgent agent : agents) {
            if (agent.getCurrentLocation().equals(location) && agent.getState().equals(state)) {
                agentsAtLocation.add(agent);
            }
        }
        return agentsAtLocation;
    }

    @Override
    public final Collection<SIRAgent> getAgentsInState(final String state) {

        return Collections2.filter(agents, new Predicate<SIRAgent>() {
            @Override
            public boolean apply(final SIRAgent agent) {
                return agent.getState().equals(state);
            }
        });
    }

    public final Collection<SIRAgent> getListAgentsInState(final String state) {
        final Collection<SIRAgent> agentsInState = new ArrayList<>();
        for (SIRAgent agent : agents) {
            if (agent != null && agent.getState().equals(state)) {
                agentsInState.add(agent);
            }
        }
        return agentsInState;
    }

    public final Collection<SIRAgent> getAllAnimalsAtLocation(final String location) {
        final Collection<SIRAgent> agentsAtLocation = new ArrayList<>();
        for (SIRAgent agent : agents) {
            if (agent.getCurrentLocation().equals(location)) {
                agentsAtLocation.add(agent);
            }
        }
        return agentsAtLocation;
    }

    /**
     * Get a list of animals at a specified location in a given state
     *
     * @param location The location of interest
     * @param state The disease state of interest
     * @return A collection of animals at the location given in a specified
     * state
     */
    public final Collection<Integer> getAnimalsAtLocationInState(final String location, final String state) {
        final Collection<Integer> agentsAtLocation = new ArrayList<>();
        for (SIRAgent agent : agents) {
            if (agent.getCurrentLocation().equals(location) && agent.getState().equals(state)) {
                agentsAtLocation.add(Integer.parseInt(agent.getId()));
            }
        }
        return agentsAtLocation;
    }

    /**
     * Get a list of infected animals at given location
     *
     * @param location The location of interest
     * @param infectiousAnimals The current list of all infectious animals
     * @return A list of all infected animals at given location
     */
    public final Collection<Integer> getInfectedAtLocation(final String location, final Collection<SIRAgent> infectiousAnimals) {
        final Collection<Integer> agentsAtLocation = new ArrayList<>();
        for (SIRAgent agent : infectiousAnimals) {
            // If infected animal has two locations then add second to infected locations also.
            if (agent.getCurrentLocation().equals(location)) {
                agentsAtLocation.add(Integer.parseInt(agent.getId()));
            }
        }
        return agentsAtLocation;
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        for (SIRAgent agent : agents) {
            sb.append(agent.getId());
            sb.append('\t');
            sb.append(agent.getState());
            sb.append('\t');
        }
        sb.append('\n');
        return sb.toString();
    }

    @Override
    public final String toVerboseString() {
        final StringBuilder sb = new StringBuilder(10);

        for (SIRAgent agent : agents) {
            sb.append(agent.getId());
            sb.append('[').append(agent.getState()).append(']');
            sb.append(',');
        }
        final int index = sb.lastIndexOf(",");
        sb.deleteCharAt(index);

        return sb.toString();
    }

    @Override
    public final void resetAmount() {
        agents.clear();
        //agents.addAll(initialAgents);
    }

    @Override
    public final void save() {
        savedAgents.clear();
        savedAgents.addAll(agents);
    }

    @Override
    public final void rollback() {
        agents.clear();
        agents.addAll(savedAgents);
    }

    /**
     * Add an agent to the amount manager. The initial and saved collections are
     * not updated.
     *
     * @param agent the agent to be added.
     */
    public final void addAgent(final SIRAgent agent) {
        this.agents.add(agent);
    }

    /**
     * Add an agent to the amount manager. The initial and saved collections are
     * not updated.
     *
     * @param agent the agent to be added.
     */
    public final void removeAgent(final SIRAgent agent) {
        this.agents.remove(agent);
    }

    public void saveDetailsToFile(String nameOfLocalFile, int step) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(nameOfLocalFile, true);
            final StringBuilder outputString = new StringBuilder();
            Collection<SIRAgent> susceptibleAnimals = this.getListAgentsInState("SUSCEPTIBLE");
            Collection<SIRAgent> infectiousAnimals = this.getListAgentsInState("INFECTIOUS");
            Collection<SIRAgent> recoveredAnimals = this.getListAgentsInState("RECOVERED");
            outputString.append(step).append(",").append(susceptibleAnimals.size()).append(",")
                    .append(infectiousAnimals.size()).append(",").append(recoveredAnimals.size());
            outputString.append("\n");
            fw.write(outputString.toString());
            fw.flush();
        } catch (IOException ex) {
            Logger.getLogger(StochasticSIRAmountManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();

            } catch (IOException ex) {
                Logger.getLogger(StochasticSIRAmountManager.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void saveInfectiousDetailsToFile(String nameOfLocalFile) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(nameOfLocalFile,true);
            final StringBuilder outputString = new StringBuilder();
            Collection<SIRAgent> infectiousAnimals = this.getListAgentsInState("INFECTIOUS");
            outputString.append(infectiousAnimals.size());
            outputString.append("\n");
            fw.write(outputString.toString());
            fw.flush();
        } catch (IOException ex) {
            Logger.getLogger(StochasticSIRAmountManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(StochasticSIRAmountManager.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
