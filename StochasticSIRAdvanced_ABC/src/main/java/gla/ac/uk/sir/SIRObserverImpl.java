/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gla.ac.uk.sir;

import broadwick.data.Lookup;
import broadwick.data.H2Database;
import broadwick.data.DatabaseImpl;
import broadwick.data.readers.PopulationsFileReader;
import broadwick.stochastic.Observer;
import broadwick.stochastic.SimulationEvent;
import broadwick.stochastic.StochasticSimulator;
import gla.ac.uk.sir.agent.SIRAgent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

/**
 *
 * @author Local Manager
 */
@Slf4j
public class SIRObserverImpl extends Observer {

    private final StochasticSimulator simulator;
    private DSLContext jooq;
    private Lookup lookup;

    public SIRObserverImpl(final StochasticSimulator sim, final Lookup lookup) {
        super(sim);
        simulator = sim;
        thetas = new HashSet<>();
        this.lookup = lookup;
    }

    @Override
    public final void started() {
        log.trace("Calling observer started method - doing nothing here");
    }

    @Override
    public final void step() {
        final double time = this.getProcess().getCurrentTime();
        //System.out.println("This is step: " + time);
        final StochasticSIRAmountManager amountManager = (StochasticSIRAmountManager) simulator.getAmountManager();
        final long sdate = Math.abs((long) time * 86400000);
        final DateTime ZERO_DATE = new DateTime(1900, 1, 1, 0, 0);
        final DateTime dt = ZERO_DATE.plus(sdate);
        final int day = dt.getDayOfMonth();
        final int month = dt.getMonthOfYear();
        final int year = dt.getYear();
        final String currentdate = day + "/" + month + "/" + year;
        amountManager.setDate(currentdate);
        
        // add part here to add header to output file - if first step =>Step,No.Susceptible,No.infectious,No.Recovered
        //print to file
        //amountManager.saveDetailsToFile("stepDetailsSIRForR.csv",(int) time);
        
        // If we are at final step - then save number of infectious at end of run
        if((int) time == 40908){
            System.out.println("Last step saving stat to file");
            amountManager.saveInfectiousDetailsToFile("numberOfInfectious.csv");
        }

        amountManager.updateTransitionKernelFromSusceptible((int) time);
        amountManager.updateTransitionKernelForOtherStates();
        // Now seems like a good time to remove all animals that have died on this time step
        // Want Animals that have died during tau step interval
        // Get id of all animals that have date of death at this time and remove them for model
        Collection animalIDs = this.getAnimalsThatHaveDied((int) time, Integer.parseInt(amountManager.getTauLeapIncrement()));        
        //System.out.println("Number of animals died during this step and being removed from model is: " + animalIDs.size());
        Collection<SIRAgent> agents = amountManager.getAgents();
        final Map agentMap1 = amountManager.getAgentMap1();
        for (final Iterator it = animalIDs.iterator(); it.hasNext();) {
            final String animal = (String) it.next();
            if (amountManager.getAgentMap1().containsKey(animal)) {
                // Now remove from map
                //log.info("Removing animal: "+animal);
                amountManager.getAgentMap1().remove(animal);
            }
        }
        // Clear agent list
        agents.clear();
        amountManager.setAgents(agents);
        // Now start to rebuild from agents in maps
        final Set keys1 = agentMap1.keySet();
        for (final Iterator i = keys1.iterator();
                i.hasNext();) {
            final String key = (String) i.next();
            final SIRAgent agent = (SIRAgent) agentMap1.get(key);
            if (agent != null) {
                agents.add(agent);
            }
        }
        amountManager.setAgents(agents);
        amountManager.setAgentMap1(agentMap1);
    }

    @Override
    public final void finished() {
    }

    @Override
    public final void theta(final double thetaTime,
            final Collection<Object> events) {
        final StochasticSIRAmountManager amountManager = (StochasticSIRAmountManager) simulator.getAmountManager();
        for (Object event : events) {
            if (event instanceof MovementSimulationEvent) {
                amountManager.performEvent((MovementSimulationEvent) event, 1);
            }
        }
    }

    @Override
    public final void observeEvent(final SimulationEvent event,
            final double tau,
            final int times) {
    }

    public final Collection<SIRAgent> getListAgentsInState(final String state) {
        final Collection<SIRAgent> agentsInState = new ArrayList<>();
        final StochasticSIRAmountManager amountManager = (StochasticSIRAmountManager) simulator.getAmountManager();
        final Collection<SIRAgent> agents = amountManager.getAgents();
        for (SIRAgent agent : agents) {
            if (agent.getState().equals(state)) {
                agentsInState.add(agent);
            }
        }
        return agentsInState;
    }
    @Getter
    Collection<Object> thetas;

    private Collection getAnimalsThatHaveDied(int time, int increment) {
        String dbName = "sir_model_db"; // change this to demo version
        DatabaseImpl dbImpl = new H2Database(dbName, false);
        //Connection connection = dbImpl.getConnection();
        final Settings settings = new Settings();
        settings.setExecuteLogging(Boolean.FALSE);
        try {
            jooq = DSL.using(dbImpl.getConnection(), dbImpl.getDialect(), settings);
        } catch (SQLException ex) {
            Logger.getLogger(SIRObserverImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        final Collection<String> animals = new HashSet<>();
        final String whereClause = String.format("%s >= %d and %s <= %d",
                PopulationsFileReader.getDATE_OF_DEATH(), time,
                PopulationsFileReader.getDATE_OF_DEATH(), time + increment);
        final Result<Record> records = jooq.select().from(PopulationsFileReader.getLIFE_HISTORIES_TABLE_NAME()).where(whereClause)
                .fetch();
        for (Record r : records) {
            String id = "";
            id = (String) r.getValue(PopulationsFileReader.getID());
            if (!id.equals("")) {
                animals.add(id);
            }
        }
        log.debug("Found {} animals.", animals.size());
        return animals;
    }
}
