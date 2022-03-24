 package de.fzi.srp.simulatedexperience.prism.wrapper.service;

import java.io.File;
import java.io.FileNotFoundException;

import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;

import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import parser.ast.Property;
import prism.Prism;
import prism.PrismDevNullLog;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLog;
import prism.Result;

public class PrismInvocationService implements PrismService {
    
    private Prism prism = null;
	private File logFile = null;

	@Override
	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}
	
	@Override
	public PrismResult modelCheck(PrismContext context) {
		initPrism();
		
		PropertiesFile propertyFile = null;
		try {
			
			System.out.println("Start prism invocation: " + context.propertyFileContent);
			long start = System.currentTimeMillis();
			propertyFile = setUpPrism(context);

			PrismResult prismResult = new PrismResult();
			for (int i = 0; i < propertyFile.getNumProperties(); i++) {
				Property propertyToCheck = propertyFile.getPropertyObject(i);
				Result result = prism.modelCheck(propertyFile, propertyToCheck);

				prismResult.addResult(propertyToCheck.toString(), quantify(result));
			}
			
			prism.closeDown();
			
			long end = System.currentTimeMillis();

			System.out.println("Stop prism invocation, took:" + ((end - start) / 1000));
			return prismResult;
		} catch (FileNotFoundException | PrismException e) {
			throw new RuntimeException("Something went wrong during prism model checking.", e);
		}
	}

	private void initPrism() {
		PrismLog log = logFile == null ? new PrismDevNullLog() : new PrismFileLog(logFile.toString());
		prism = new Prism(log);
		try {
			prism.initialise();
		} catch (PrismException e) {
			// TODO Exception handling
			throw new RuntimeException("There went something wrong while initialising prism.", e);
		}
	}
	
	private PropertiesFile setUpPrism(PrismContext context) throws FileNotFoundException, PrismException {
		ModulesFile moduleFile = prism.parseModelString(context.moduleFileContent);
		prism.loadPRISMModel(moduleFile);
		PropertiesFile propertyFile = prism.parsePropertiesString(moduleFile, context.propertyFileContent);
		//prism.buildModelExplicit(moduleFile);
		return propertyFile;
	}

	private Double quantify(Result result) {
		return Double.class.cast(result.getResult());
	}

}
