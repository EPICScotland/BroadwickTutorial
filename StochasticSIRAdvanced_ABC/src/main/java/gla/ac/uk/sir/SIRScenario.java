/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gla.ac.uk.sir;

import broadwick.BroadwickConstants;
import broadwick.data.Lookup;
import broadwick.data.Movement;
import broadwick.rng.RNG;
import broadwick.stochastic.SimulationController;
import broadwick.stochastic.TransitionKernel;
import broadwick.stochastic.algorithms.TauLeapingFixedStep;
import com.google.common.base.Throwables;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * This class creates and runs a single scenario.
 */
@ToString
@Slf4j
class SIRScenario {

    private Lookup lookup;
    private final RNG generator = new RNG(RNG.Generator.Well19937c);
    private TauLeapingFixedStep simulator;
    @Getter
    private int scenarioID;

    /**
     * Create the scenario from a set of parameters.
     *
     */
    SIRScenario(final int maxTime, final String infectedAnimalID,
            final String dateFormat, final String startDate, 
            final String tauLeapIncrement, final double beta, final double rate2,
            final String initialState1, final String finalState1,
            final String initialState2, final String finalState2,
            Lookup lookup, final int finalEndDate, final String scenarioID) {

        try {
            //System.out.println("Scenario # " + scenarioID + " is doing this task");
            //System.out.println("*** SCENARIO ID ***  IS: " + scenarioID);

            final SimulationController controller = new SIRController(maxTime);
            final TransitionKernel transitionKernel = new TransitionKernel();
            final int startdate = BroadwickConstants.getDate(startDate, dateFormat);
            log.info("Start date in int format is: " + startdate);
            final StochasticSIRAmountManager amountManager = new StochasticSIRAmountManager(startdate, lookup, infectedAnimalID); 
            amountManager.setKernel(transitionKernel);
            amountManager.setScenarioID(Integer.parseInt(scenarioID));
            amountManager.setBeta(beta);
            amountManager.setRate2(rate2);
            amountManager.setInitialState1(initialState1);
            amountManager.setInitialState2(initialState2);
            amountManager.setFinalState1(finalState1);
            amountManager.setFinalState2(finalState2);
            amountManager.setTauLeapIncrement(tauLeapIncrement);
            //amountManager.setInfectedAnimalID(this.getInfectedAnimalID());
            final TauLeapingFixedStep simulatorImpl = new TauLeapingFixedStep(amountManager, transitionKernel, Integer.parseInt(tauLeapIncrement));
            this.simulator = simulatorImpl;
            simulatorImpl.setController(controller);
            simulatorImpl.setStartTime(startdate);
            simulatorImpl.init();
            this.lookup = lookup;
            simulatorImpl.getObservers().clear();
            final SIRObserverImpl observerImpl = new SIRObserverImpl(simulatorImpl, this.lookup);
            simulatorImpl.addObserver(observerImpl);
            try {
                if (lookup != null) {
                    // Register movements as theta events
                    for (Movement movement : lookup.getOnMovements(startdate, finalEndDate)) {//startdate + stepSize - 1)) {
                        log.trace("Adding movement as theta event {}", movement.toString());
                        // We know these are full movements.
                        //thetaEvents.put(movement.getDestinationDate(), movement);
                        Integer date = movement.getDepartureDate();
                        if (date == null || date == 0) {
                            date = movement.getDestinationDate();
                            final MovementSimulationState initialState = new MovementSimulationState(movement.getId(), movement.getDepartureId());
                            final MovementSimulationState finalState = new MovementSimulationState(movement.getId(), movement.getDestinationId());
                            final MovementSimulationEvent movementEvent = new MovementSimulationEvent(initialState, finalState);
                            simulatorImpl.registerNewTheta(observerImpl, date, movementEvent);
                        } else {
                            final MovementSimulationState initialState = new MovementSimulationState(movement.getId(), movement.getDepartureId());
                            final MovementSimulationState finalState = new MovementSimulationState(movement.getId(), movement.getDestinationId());
                            final MovementSimulationEvent movementEvent = new MovementSimulationEvent(initialState, finalState);
                            simulatorImpl.registerNewTheta(observerImpl, date, movementEvent);
                        }
                    }

                } else {
                    log.debug("********** Cannot find H2 DB file ********");
                }

            } catch (Exception e) {
                log.error("something went wrong initialising theta events. {}", Throwables.getStackTraceAsString(e));
            }

            log.info("Updating transition kernel");
            amountManager.updateTransitionKernelFromSusceptible(startdate);
            amountManager.updateTransitionKernelForOtherStates();

        } catch (Exception ex) {
            log.error("Could not create scenario. {}", Throwables.getStackTraceAsString(ex));
        }
    }

    /**
     * Run the scenario.
     */
    public final void run() {
        try {
            simulator.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
    }
}
