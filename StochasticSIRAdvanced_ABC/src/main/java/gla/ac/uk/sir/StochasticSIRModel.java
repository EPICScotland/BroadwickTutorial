/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gla.ac.uk.sir;

import gla.ac.uk.sir.abc.Accumulator;
import broadwick.abc.AbcNamedQuantity;
import broadwick.abc.AbcPriorsSampler;
import broadwick.data.Lookup;
import broadwick.model.Model;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import broadwick.concurrent.Executor;
import broadwick.concurrent.LocalPoolExecutor;
import broadwick.rng.RNG;
import gla.ac.uk.sir.abc.ApproxBayesianComp;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple class that runs an Soho model.
 */
@Slf4j
public class StochasticSIRModel extends Model {

    @Getter
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
    private String infectedAnimalID;
    private static int scenarioID;
    @Getter
    @Setter
    //Number of times to run model per run
    private int numScenarios;
    @Getter
    @Setter
    //Number of times to run model
    private int numRuns;
    private static final AtomicInteger atomicInt = new AtomicInteger(0);
    private final RNG generator = new RNG(RNG.Generator.Well19937c);

    public final void init() {

        this.setMaxTime(this.getParameterValueAsInteger("maxTime"));
        this.setStartDate(this.getParameterValue("startDate"));
        this.setDateFormat(this.getParameterValue("dateFormat"));
        this.setTauLeapIncrement(this.getParameterValue("tauLeapIncrement"));
        this.setSpecies(this.getParameterValue("species"));
        this.setInitialState1(this.getParameterValue("initialState1"));
        this.setFinalState1(this.getParameterValue("finalState1"));
       // this.setBeta(this.getParameterValueAsDouble("beta"));
        this.setInitialState2(this.getParameterValue("initialState2"));
        this.setFinalState2(this.getParameterValue("finalState2"));
        this.setRate2(this.getParameterValueAsDouble("rate2"));
        this.setInfectedAnimalID(this.getParameterValue("infectedAnimalID"));
        this.setNumScenarios(this.getParameterValueAsInteger("numScenarios"));
        this.setNumRuns(this.getParameterValueAsInteger("numRuns"));
        log.info("Maximum time for simulation is: " + maxTime);
        log.info("The seed animal ID is: " + this.infectedAnimalID);
        log.info("Type of species is: " + species);
        log.info("Start date is: " + startDate);
        log.info("Start date format: " + dateFormat);
        log.info("Tau Leap Increment is: " + tauLeapIncrement);
        log.info("Number of scenarios per run is: " + this.getNumScenarios());
        log.info("Number of runs is: " + this.getNumRuns());
        log.info("Trans event 1 initial state is " + initialState1 + " and final state is " + finalState1 + " with beta " + beta);
        log.info("Trans event 2 initial state is " + initialState2 + " and final state is " + finalState2 + " with rate2 " + rate2);
    }

    @Override
    public final void run() {

        try {
            // Use these lines for video 8 - but then comment out for video 9
            // It uses beta given in XML file - rather than a sampled beta
            File file = new File("numberOfInfectious.csv");
             // Want to delete it so that we have fresh one for next run.
             if (file.exists()) {
             file.delete();
             }
             this.runScenarios(this.getBeta());
             Accumulator totalInfectious = this.AccumulateStatistics();
             System.out.println("The summary stat value from simulation for total infectious is: " + totalInfectious.getMean());

            //UNCOMMENT - Video 9
            // We want to sample for a certain number of runs - specified by XML configuration value numRuns
          /*  for (int runs = 0; runs <= numRuns; runs++) {
                final AbcPriorsSampler new_priors = new AbcPriorsSampler() {
                    @Override
                    public AbcNamedQuantity sample() {
                        final LinkedHashMap<String, Double> sample = new LinkedHashMap();
                        sample.put("Beta", generator.getDouble(.0003, .0008));
                        return new AbcNamedQuantity(sample);
                    }
                };
                AbcNamedQuantity new_sample = new_priors.sample();
             //Need to use a newly sampled beta for every run 
                //(a run is finished once all scenarios are finished)
                double beta = new_sample.getParameters().get("Beta");
                log.info("ABOUT TO START RUN FOR SAMPLED BETA " + Double.toString(beta));

                // Run the simulations for this parameter set - using beta sampled from priors
                this.runScenarios(beta);
                log.info("RUN NOW FINISHED ACCUMULATING STATS");
                // Once all scenarios finished - read output file and average results using Accumulator                
                Accumulator totalInfectious = this.AccumulateStatistics();
                File file = new File("numberOfInfectious.csv");
                // Want to delete it so that we have fresh one for next run.
                if (file.exists()) {
                    file.delete();
                }

                final Map<String, Double> observed_tr1 = new LinkedHashMap();
                observed_tr1.put("TotalInfectious", 275.788);
                final AbcNamedQuantity observedData_tr1 = new AbcNamedQuantity(observed_tr1);

                final Map<String, Double> priorSample = new LinkedHashMap();
                priorSample.put("Beta", beta);
                final AbcNamedQuantity priors = new AbcNamedQuantity(priorSample);

                final Map<String, Double> generatedData_tr = new LinkedHashMap();
                //Accumulator totalReactors = fu.getTotalReactors();
                System.out.println("The summary stat value from simulation for total infectious is: " + totalInfectious.getMean());
                generatedData_tr.put("TotalInfectious", totalInfectious.getMean());
                System.out.println("Means taken from " + totalInfectious.getSize() + " values");

                final AbcNamedQuantity generated_tr = new AbcNamedQuantity(generatedData_tr);
                final ApproxBayesianComp abc_tinf = new ApproxBayesianComp(observedData_tr1, generated_tr, priors, 10);

                // Check to see if generated data within distance of observed data - if so save for posterior
                abc_tinf.calculate();

                // Total Infectious posteriors
                final Map<String, LinkedList<Double>> posteriors_tr = abc_tinf.getPosteriors();
                final LinkedList<Double> posteriorValues_tr_beta = posteriors_tr.get("Beta");
                String posteriorFile_tr = "posterior.out";
                try {
                    FileWriter fileWritter = new FileWriter(posteriorFile_tr, true);
                    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                    //String betaStr = Double.toString(beta);
                    System.out.println("The beta value for total infectious posteriorValues is: " + posteriorValues_tr_beta);
                    // Sigma is constant for now
                    String sigma = ".003";

                    if (posteriorValues_tr_beta != null) {
                        bufferWritter.write("" + beta + ", " + sigma + " , " + totalInfectious.getMean());
                        bufferWritter.newLine();
                        bufferWritter.close();
                    } else {
                        System.out.println("There are no posterior values saved for this run");
                    }
                } catch (Exception e) {
                    System.out.println("Problem writing to posterior file");
                    e.printStackTrace();
                }
                totalInfectious.clear();
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runScenarios(double sampledBeta) {
        init(); // Get values from xml file
        lookup = this.getLookup();
        scenarioID = 0;
        final int poolSize = Runtime.getRuntime().availableProcessors();
        final StopWatch sw = new StopWatch();
        sw.start();
        // for abc used sampled beta passed into method - rather than beta passed in from xml
        // This beta changes per run (group of scenarios)
        this.beta = sampledBeta;
        final LocalPoolExecutor executor = new LocalPoolExecutor(poolSize, numScenarios, new Callable() {
            @Override

            public Void call() {
                Object myObject = new Object();
                synchronized (myObject) {
                    scenarioID = atomicInt.incrementAndGet();
                    Thread.currentThread().setName("" + scenarioID);

                    final SIRScenario scenario = new SIRScenario(maxTime, infectedAnimalID, dateFormat, startDate,
                            tauLeapIncrement, beta, rate2, initialState1, finalState1, initialState2, finalState2,
                            lookup, maxTime, Thread.currentThread().getName());

                    System.out.println("Scenario [" + Thread.currentThread().getName() + "] starting");
                    scenario.run();
                    System.out.println("Scenario [" + Thread.currentThread().getName() + "] finished");
                }
                return null;
            }
        });
        try {
            executor.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (executor.getStatus() != Executor.Status.COMPLETED) {
            log.error("Failed to run scenarios.");
        } else {
            sw.stop();
            if (executor.isOk()) {
                log.debug("Finished {} simulations in {}.", numScenarios, sw);
                System.out.println("Finished " + numScenarios + " simulations in " + sw);
            }
        }
    }

    @Override
    public void finalise() {
    }

    private Accumulator AccumulateStatistics() {
        Accumulator inf = new Accumulator();
        try {
            // Now read the numberOfInfectious.csv file
            List<String> lines = Files.readAllLines(Paths.get("numberOfInfectious.csv"), Charset.forName("UTF-8"));
            for (String line : lines) {
                inf.add(Double.parseDouble(line));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(StochasticSIRModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return inf;
    }
}
