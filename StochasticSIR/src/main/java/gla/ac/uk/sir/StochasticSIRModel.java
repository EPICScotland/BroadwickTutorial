/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gla.ac.uk.sir;

import broadwick.BroadwickConstants;
import broadwick.data.Lookup;
import broadwick.data.Movement;
import broadwick.data.Test;
import broadwick.model.Model;
import broadwick.stochastic.SimulationController;
import broadwick.stochastic.TransitionKernel;
import broadwick.stochastic.algorithms.TauLeapingFixedStep;
import com.google.common.base.Throwables;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple class that runs an Soho model.
 */
@Slf4j
public class StochasticSIRModel extends Model {

    // UNCOMMENT NUMBER 2a
  /*  private TauLeapingFixedStep simulatorImpl;
    //private SIRObserverImpl observerImpl;
    private StochasticSIRAmountManager amountManager = null;
    private SimulationController controller = null;
    @Getter
    private TransitionKernel transitionKernel = new TransitionKernel();*/
    
    // UNCOMMENT NUMBER 1a
 /*   @Getter
    @Setter
    //Maximum time the simulation should run
    private int maxTime;
    @Getter
    @Setter
    //Animal type
    private String species;
    @Getter
    @Setter
    //Transmission term
    private double beta;
    @Getter
    @Setter
    //Infectious to recovered
    private double rate2;
    @Getter
    @Setter
    //Date to start simulation
    private String startDate;
    @Getter
    @Setter
    private String dateFormat;
    @Getter
    @Setter
    //Increment for Tau Leap
    private String tauLeapIncrement;
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
    private Lookup lookup;
    @Getter
    @Setter
    private String infectedAnimalID;*/

    public final void init() {

       // UNCOMMENT NUMBER 1b
  /*      this.setMaxTime(this.getParameterValueAsInteger("maxTime"));
        this.setStartDate(this.getParameterValue("startDate"));
        this.setDateFormat(this.getParameterValue("dateFormat"));
        this.setTauLeapIncrement(this.getParameterValue("tauLeapIncrement"));
        this.setSpecies(this.getParameterValue("species"));
        this.setInitialState1(this.getParameterValue("initialState1"));
        this.setFinalState1(this.getParameterValue("finalState1"));
        this.setBeta(this.getParameterValueAsDouble("beta"));
        this.setInitialState2(this.getParameterValue("initialState2"));
        this.setFinalState2(this.getParameterValue("finalState2"));
        this.setRate2(this.getParameterValueAsDouble("rate2"));
        this.setInfectedAnimalID(this.getParameterValue("infectedAnimalID"));
        log.info("Maximum time for simulation is: " + maxTime);
        log.info("The seed animal ID is: " + this.infectedAnimalID);
        log.info("Type of species is: " + species);
        log.info("Start date is: " + startDate);
        log.info("Start date format: " + dateFormat);
        log.info("Tau Leap Increment is: " + tauLeapIncrement);
        log.info("Trans event 1 initial state is " + initialState1 + " and final state is " + finalState1 + " with beta " + beta);
        log.info("Trans event 2 initial state is " + initialState2 + " and final state is " + finalState2 + " with rate2 " + rate2);*/
    }

    @Override
    public final void run() {
        try {
            init();
            // UNCOMMENT NUMBER 2b
           /* controller = new SIRController(maxTime);
            final int startdate = BroadwickConstants.getDate(startDate, dateFormat);
            log.info("Start date in int format is: " + startdate);
            this.lookup = this.getLookup();
            amountManager = new StochasticSIRAmountManager(startdate, this.lookup, infectedAnimalID);
            amountManager.setKernel(transitionKernel);
            // To be used for transition kernel updates within amount manager
            amountManager.setBeta(beta);
            // To be used for transition kernel updates within amount manager        
            amountManager.setRate2(rate2);
            // To be used for transition kernel updates within amount manager        
            amountManager.setInitialState1(this.getInitialState1());
            amountManager.setInitialState2(this.getInitialState2());
            amountManager.setFinalState1(this.getFinalState1());
            amountManager.setFinalState2(this.getFinalState2());
            amountManager.setTauLeapIncrement(this.tauLeapIncrement);
            simulatorImpl = new TauLeapingFixedStep(amountManager, transitionKernel, Integer.parseInt(this.tauLeapIncrement));
            simulatorImpl.setController(controller);
            simulatorImpl.setStartTime(startdate);
            simulatorImpl.init();
            final SIRObserverImpl observerImpl = new SIRObserverImpl(simulatorImpl, this.lookup);
            simulatorImpl.addObserver(observerImpl);*/
            
            // UNCOMMENT NUMBER 4a
            
            try {
              /*  if (lookup != null) {
                    // Register movements as theta events
                    for (Movement movement : lookup.getOnMovements(startdate, maxTime)) {
                        log.trace("Adding movement as theta event {}", movement.toString());
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
                }*/
            } catch (Exception e) {
                log.error("something went wrong initialising theta events. {}", Throwables.getStackTraceAsString(e));
            }
            
            
            //UNCOMMENT NUMBER 2c
            
          /*  log.info("Updating transition kernel");
            amountManager.updateTransitionKernelFromSusceptible(startdate);
            amountManager.updateTransitionKernelForOtherStates();
            log.info("Starting model...........");
            simulatorImpl.run();*/

        } catch (Exception ex) {
            Logger.getLogger(StochasticSIRModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void finalise() {
    }
}
