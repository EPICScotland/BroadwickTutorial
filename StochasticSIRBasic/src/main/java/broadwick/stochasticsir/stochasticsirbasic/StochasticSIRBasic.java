package broadwick.stochasticsir.stochasticsirbasic;

import broadwick.model.Model;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StochasticSIRBasic extends Model {

    @Getter
    @Setter
    private String test;

    @Override
    public final void init() {
        log.info("Initialising project");
        this.setTest(this.getParameterValue("test"));
        log.info("Read in variable from XML file: {}", this.getTest());
        // We can therefore read in parameter values in this method - such as transmission rates

    }

    @Override
    public final void run() {
        log.info("Running project");
    }

    @Override
    public final void finalise() {
        log.info("Closing project");
    }

}
