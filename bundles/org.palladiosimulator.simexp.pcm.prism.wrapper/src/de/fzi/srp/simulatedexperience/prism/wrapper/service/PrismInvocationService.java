package de.fzi.srp.simulatedexperience.prism.wrapper.service;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;

import de.fzi.srp.simulatedexperience.prism.wrapper.service.impl.PrismLoader;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import parser.ast.Property;
import prism.Prism;
import prism.PrismException;
import prism.PrismFileLog;
import prism.Result;

public class PrismInvocationService implements PrismService {

    private static final Logger LOGGER = Logger.getLogger(PrismInvocationService.class.getName());

    private Prism prism;

    @Override
    public void initialise(Path prismFolder, String strategyId) {
        PrismLoader pl = PrismLoader.INSTANCE;
        pl.load();
        Path prismLogPath = prismFolder.resolve("prism.log");
        prism = new Prism(new PrismFileLog(prismLogPath.toString()));
        try {
            prism.initialise();
        } catch (PrismException e) {
            // TODO Exception handling
            throw new RuntimeException("There went something wrong while initialising prism.", e);
        }
    }

    @Override
    public PrismResult modelCheck(PrismContext context) {
        try {
            String contentKind = context.getKind();
            LOGGER.info("Start prism invocation: " + contentKind);
            long start = System.currentTimeMillis();
            PropertiesFile propertyFile = setUpPrism(context);

            PrismResult prismResult = new PrismResult();
            for (int i = 0; i < propertyFile.getNumProperties(); i++) {
                Property propertyToCheck = propertyFile.getPropertyObject(i);
                Result result = prism.modelCheck(propertyFile, propertyToCheck);

                prismResult.addResult(propertyToCheck.toString(), quantify(result));
            }
            long end = System.currentTimeMillis();

            LOGGER
                .info("Stop prism invocation: " + contentKind + ", Elapsed time in seconds: " + ((end - start) / 1000));
            return prismResult;
        } catch (FileNotFoundException | PrismException e) {
            throw new RuntimeException("Something went wrong during prism model checking.", e);
        }
    }

    private PropertiesFile setUpPrism(PrismContext context) throws FileNotFoundException, PrismException {
        ModulesFile moduleFile = prism.parseModelString(context.getModuleFileContent());
        prism.loadPRISMModel(moduleFile);
        PropertiesFile propertyFile = prism.parsePropertiesString(moduleFile, context.getPropertyFileContent());
        // prism.buildModelExplicit(moduleFile);
        return propertyFile;
    }

    private Double quantify(Result result) {
        return Double.class.cast(result.getResult());
    }

}
