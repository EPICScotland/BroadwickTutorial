/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gla.ac.uk.sir.agent;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * A representation of each animal (cow/badger/pig/horse) in the simulation
 */
public class SIRAgent implements Agent,Serializable {


    @Getter
    @Setter
    private String infectionStatus;
    @Getter
    private final String id;
    @Getter
    @Setter
    protected String currentLocation;
    @Getter
    @Setter
    protected String dateFormat = "yyyy-MM-dd";

    /**
     * Create a new animal agent object.
     *
     * @param id of the animal to be created.
     * @param state the infection state of the animal.
     * @param location the current location of the animal .
     */
    public SIRAgent(final String id, final String state, final String location) { 
        this.id = id;
        this.infectionStatus = state;
        this.currentLocation=location;
    }

        @Override
    public final String getState() {
        return infectionStatus;
    }

    @Override
    public final void setState(final String state) {
        this.infectionStatus = state;
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder(250);
        sb.append(id); 
        return sb.toString();
    }
}
