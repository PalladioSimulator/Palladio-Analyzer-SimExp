package org.palladiosimulator.simexp.pcm.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;

public class MultiQVToReconfiguration extends BaseQVToReconfiguration implements QVToReconfiguration {
    private static final Logger LOGGER = Logger.getLogger(MultiQVToReconfiguration.class);

    private final List<SingleQVToReconfiguration> reconfigurations;

    private MultiQVToReconfiguration(List<SingleQVToReconfiguration> reconfigurations) {
        this.reconfigurations = Collections.unmodifiableList(reconfigurations);
    }

    public static MultiQVToReconfiguration of(List<SingleQVToReconfiguration> reconfigurations) {
        return new MultiQVToReconfiguration(reconfigurations);
    }

    @Override
    public void execute(IExperimentProvider experimentProvider, IResourceTableManager resourceTableManager) {
        if (isEmptyReconfiguration()) {
            return;
        }
        LOGGER.info(String.format("'EXECUTE' applying %d single reconfigurations", reconfigurations.size()));
        for (SingleQVToReconfiguration reconf : this.reconfigurations) {
            reconf.execute(experimentProvider, resourceTableManager);
        }
    }

    @Override
    protected boolean isEmptyReconfiguration() {
        return reconfigurations.isEmpty();
    }

    @Override
    protected String getTransformationName() {
        List<String> names = new ArrayList<>();
        for (SingleQVToReconfiguration reconf : reconfigurations) {
            names.add(reconf.getTransformationName());
        }
        return StringUtils.join(names, ",");
    }
}
