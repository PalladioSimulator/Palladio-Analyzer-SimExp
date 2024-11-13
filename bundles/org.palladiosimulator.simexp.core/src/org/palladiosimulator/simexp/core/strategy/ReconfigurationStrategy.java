package org.palladiosimulator.simexp.core.strategy;

import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.sampling.SampleDumper;

// This class is not yet integrated in the simulated experience process. This is rather a reminder for future work. 
// The idea is to introduce a dedicated concept for reconfiguration strategies following the MAPE-K principles.
public abstract class ReconfigurationStrategy<A, Aa extends Reconfiguration<A>> implements Policy<A, Aa> {

    protected static final Logger LOGGER = Logger.getLogger(ReconfigurationStrategy.class);

    private final SharedKnowledge knowledge;
    private final SampleDumper sampleDumper;

    public ReconfigurationStrategy(SampleDumper sampleDumper) {
        this.knowledge = new SharedKnowledge();
        this.sampleDumper = sampleDumper;
    }

    @Override
    public Aa select(State source, Set<Aa> options) {
        Aa reconfiguration = emptyReconfiguration();

        LOGGER.info("==== Start MAPE-K loop ====");
        LOGGER.info("'MONITOR' step start");
        if (sampleDumper != null) {
            sampleDumper.dump(source);
        }
        monitor(source, knowledge);
        LOGGER.debug(String.format("'MONITOR' knowledge snapshot: %s", knowledge.toString()));
        LOGGER.info("'MONITOR' step done");

        LOGGER.info("'ANALYZE' step start");
        boolean isAnalyzable = analyse(source, knowledge);
        LOGGER.info(String.format("'ANALYZE' found constraint violations: '%s'", isAnalyzable));
        LOGGER.info("'ANALYZE' step done");

        if (isAnalyzable) {
            LOGGER.info("'PLANNING' step start");
            reconfiguration = plan(source, options, knowledge);
            LOGGER.info(String.format("'PLANNING' selected action '%s'", reconfiguration));
            LOGGER.info("'PLANNING' step done");
        }
        LOGGER.info("'EXECUTE' step start");
        return reconfiguration;
    }

    protected abstract void monitor(State source, SharedKnowledge knowledge);

    protected abstract boolean analyse(State source, SharedKnowledge knowledge);

    protected abstract Aa plan(State source, Set<Aa> options, SharedKnowledge knowledge);

    protected abstract Aa emptyReconfiguration();

}
