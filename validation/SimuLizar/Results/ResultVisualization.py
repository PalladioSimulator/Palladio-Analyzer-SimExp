import csv
import sys
import string
import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

import os
from os import listdir

import os.path
from os.path import isdir

csvDelimiter = ';'
csvDelimiterSimuLizar = ","

def lineplotResponseTimes(results):
    plot = sns.lineplot(data=results, x="Time", y="Response time", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 110, 10).tolist())
    #plot.set_yticks(np.arange(0, 10, 1).tolist())
    plt.show()

def lineplotAccReward(results):
    plot = sns.lineplot(data=results, x="Time", y="Average reward", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_yticks(np.arange(-1, 1.2, 0.2).tolist())
    plt.show()

def prepareSimExpResultsOf(sampleSpaceFile, simExpStoreFile, strategyLabel):
    responseTimes = [] 
    rewards = []
    time = []
    with open(sampleSpaceFile) as csv_file:
        sampleSpace = csv.reader(csv_file, delimiter=csvDelimiter)
        for eachRow in sampleSpace:
            if eachRow[0].startswith('Point in time'):
                continue
            
            counter = 0
            for eachEntry in eachRow:
                if eachEntry.startswith('Samples'):
                    rtValue = findQuantity(eachEntry, 'spec: Usage Scenario: overloadUsageScenario_Response Time', simExpStoreFile)
                    responseTimes.append(rtValue)
                else:
                    time.append(counter)

                    reward = float(eachEntry)
                    rewards.append(reward)

                    counter += 1

    accRewardRatios = calculatAccRewardRatios(rewards)

    strategyLabels = [strategyLabel] * len(time)

    d = {'Time': time, 'Response time': responseTimes, 'Average reward': accRewardRatios, 'Strategy': strategyLabels}
    return pd.DataFrame(data=d)

def calculatAccRewardRatios(rewards):
    count = 1
    accReward = 0
    accRewardRatios = []

    for each in rewards:
        accReward += each
        accRewardRatios.append(accReward / count)
        count += 1
    
    return accRewardRatios

def findQuantity(id, quantity, simExpStore):
    with open(simExpStore) as csv_file:
        simulatedExperienceStore = csv.DictReader(csv_file, delimiter=csvDelimiter) 
        for eachRow in simulatedExperienceStore:
            if eachRow['Id'] == id:
                quantities = eachRow['Quantified state current']
                return filterQuantity(quantity, quantities)

def filterQuantity(quantity, quantities):
    if quantity in quantities:
        strValue = quantities.split(',')[0]
        start = strValue.rfind(': ') + 1
        end = len(strValue)
        value = strValue[start:end]
        return float(value)
    raise Exception('The target quantity could not be found.')

def visualizeAllSimuLizarResults(resultDir):
    for path in listdir(resultDir):
        if os.path.isfile(path):
            continue

        usageEvolDir = os.path.join(resultDir, path)
        for uePath in listdir(usageEvolDir):
            if uePath.startswith('SimuLizar_Results') and uePath.endswith('nonadaptive'):
                global simulizarNonAdaptiveStrategyFolder
                simulizarNonAdaptiveStrategyFolder = os.path.join(usageEvolDir, uePath)
            elif uePath.startswith('SimuLizar_Results') and uePath.endswith('onestep'):
                global simulizarOneStepStrategyFolder
                simulizarOneStepStrategyFolder = os.path.join(usageEvolDir, uePath)
            elif uePath.startswith('SimuLizar_Results') and uePath.endswith('twostep'):
                global simulizarTwoStepStrategyFolder
                simulizarTwoStepStrategyFolder = os.path.join(usageEvolDir, uePath)
            elif uePath.startswith('UsageEvolution'):
                global usageEvolutionSamples
                usageEvolutionSamples = os.path.join(usageEvolDir, uePath)

        print('SimuLizar: Start visualizing files from directory: ' + path)
        visualizeSimuLizarResults()
        visualizeUsageEvolutionSamples()

def visualizeSimuLizarResults():
    resultsNon = captureSimuLizarResults(simulizarNonAdaptiveStrategyFolder, "Non-adaptive")
    resultsOneStep = captureSimuLizarResults(simulizarOneStepStrategyFolder, "0.1-step")
    resultsTwoStep = captureSimuLizarResults(simulizarTwoStepStrategyFolder, "0.2-step")

    results = resultsNon.append(resultsOneStep, ignore_index = True).append(resultsTwoStep, ignore_index = True)
    plot = sns.lineplot(data=results, x="Time", y="Response time", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set(ylim=(0,11))
    #plot.set_yticks(np.arange(0, 35, 5).tolist())
    plt.show()

    lineplotAccReward(results)
    #boxplotResponseTimes(resultsOneStep.append(resultsTwoStep, ignore_index = True))

def visualizeUsageEvolutionSamples():
    times = []
    arrivalRates = []
    interArrivalTimes = []
    with open(usageEvolutionSamples) as csv_file:
        rows = csv.reader(csv_file, delimiter=csvDelimiter)
        for each in rows:
            if each[0].startswith('Time'):
                continue
            
            time = int(float(each[0]))
            times.append(time)

            interArrivalTime = float(each[1])
            interArrivalTimes.append(interArrivalTime)

            # Recall: interArrivalTime = 1 / arrivalRate
            arrivalRate = 1 / interArrivalTime
            arrivalRates.append(arrivalRate)

    d = {'Time': times, 'Inter-arrival time': interArrivalTimes, 'Arrival rate': arrivalRates, 'Hue': [0] * len(times)}
    pd.DataFrame(data=d)

    plot = sns.lineplot(data=d, x="Time", y="Arrival rate", hue="Hue", palette = "Greys", legend = False)
    plot.set(xlim=(0,100))
    plt.show()

    plot = sns.lineplot(data=d, x="Time", y="Inter-arrival time", hue="Hue", palette = "Greys", legend = False)
    plot.set(xlim=(0,100))
    plt.show()

def captureSimuLizarResults(resultFileFolder, strategyLabel):
    resultFiles = filterSimuLizarResultFiles(resultFileFolder)
    return readInSimuLizarResults(resultFiles, strategyLabel)


def filterSimuLizarResultFiles(resultFileFolder):
    resultFiles = []
    for root,dirs,files in os.walk(resultFileFolder):
        for file in files:
            if file.startswith("Usage_Scenario_overloadUsageScenario_Response_Time_Tuple"):
                resultFiles.append(os.path.join(root, file))
    return resultFiles

def readInSimuLizarResults(resultFiles, strategyLabel):
    responseTimes = [] 
    times = []
    rewards = []

    for file in resultFiles:
        with open(file) as csv_file:
            rows = csv.reader(csv_file, delimiter=csvDelimiterSimuLizar)
            for each in rows:
                if each[0].startswith('Point in Time'):
                    continue
                
                time = int(float(each[0]))
                times.append(time)

                rt = float(each[1])
                responseTimes.append(rt)

                reward = calculateReward(rt)
                rewards.append(reward)

    accRewardRatios = calculatAccRewardRatios(rewards)

    strategyLabels = [strategyLabel] * len(times)

    d = {'Time': times, 'Response time': responseTimes, 'Average reward': accRewardRatios, 'Strategy': strategyLabels}
    return pd.DataFrame(data=d)

def calculateReward(rt):
    if rt <= 2.0:
        return 1
    return -1

def readSimuLizarResults(resultFile, strategyLabel):
    responseTimes = [] 
    times = []
    aggregates = {}
    for i in range(0,100):
        aggregates[i] = []

    with open(resultFile) as csv_file:
        rows = csv.reader(csv_file, delimiter=csvDelimiterSimuLizar)
        for each in rows:
            if each[0].startswith('Point in Time'):
                continue
            
            time = int(float(each[0]))
            aggregates[time].append(float(each[1]))

    for key in aggregates:
        times.append(key)

        meanRT = sum(aggregates[key]) / len(aggregates[key])
        responseTimes.append(meanRT)

    strategyLabels = [strategyLabel] * len(times)

    d = {'Time': times, 'Response time': responseTimes,'Strategy': strategyLabels}
    return pd.DataFrame(data=d)

def boxplotResponseTimes(results):
    props = {
        "marker":"o",
        "markerfacecolor":"white", 
        "markeredgecolor":"black",
        "markersize":"10"
    }
    plot = sns.boxplot(data=results, x='Strategy', y='Response time', showmeans=True, meanprops=props)
    #plot.set_yticks(np.arange(0, 0.4, 0.05).tolist())
    plt.show()

def visualizeAllSimExpResults(resultDir):
    for root,dirs,files in os.walk(resultDir):
        for dir in dirs:
            fileDir = os.path.join(root, dir)
            for file in listdir(fileDir):
                if file.startswith('1StepAdaptationStrategySampleSpace'):
                    global sampleSpaceOneStepStrategy
                    sampleSpaceOneStepStrategy = os.path.join(fileDir, file)
                elif file.startswith('1StepAdaptationStrategySimulatedExperienceStore'):
                    global simExpStoreOneStepStrategy
                    simExpStoreOneStepStrategy = os.path.join(fileDir, file)
                elif file.startswith('NonAdaptativeSimulatedExperienceStore'):
                    global simExpStoreNonAdaptiveStrategy
                    simExpStoreNonAdaptiveStrategy = os.path.join(fileDir, file)
                elif file.startswith('NonAdaptativeSampleSpace'):
                    global sampleSpaceNonAdaptiveStrategy
                    sampleSpaceNonAdaptiveStrategy = os.path.join(fileDir, file)
                elif file.startswith('2StepAdaptationStrategySampleSpace'):
                    global sampleSpaceTwoStepStrategy
                    sampleSpaceTwoStepStrategy = os.path.join(fileDir, file)
                elif file.startswith('2StepAdaptationStrategySimulatedExperienceStore'):
                    global simExpStoreTwoStepStrategy
                    simExpStoreTwoStepStrategy = os.path.join(fileDir, file)
                elif file.startswith('EnvDyn'):
                    global envDynSamples
                    envDynSamples = os.path.join(fileDir, file)

            print('SimExp: Start visualizing files from directory: ' + dir)
            visualizeSimExpResults()
            visualizeEnvDynTrajectory()

def visualizeSimExpResults():
    nonAdaptiveStrategyResults = prepareSimExpResultsOf(sampleSpaceNonAdaptiveStrategy, simExpStoreNonAdaptiveStrategy, "Non-adaptive")
    #lineplotResponseTimes(nonAdaptiveStrategyResults)

    oneStepStrategyResults = prepareSimExpResultsOf(sampleSpaceOneStepStrategy, simExpStoreOneStepStrategy, "0.1-step")
    #lineplotResponseTimes(oneStepStrategyResults)

    twoStepStrategyResults = prepareSimExpResultsOf(sampleSpaceTwoStepStrategy, simExpStoreTwoStepStrategy, "0.2-step")
    #lineplotResponseTimes(twoStepStrategyResults)

    results = nonAdaptiveStrategyResults.append(oneStepStrategyResults, ignore_index = True).append(twoStepStrategyResults, ignore_index = True)
    lineplotResponseTimes(results)
    lineplotAccReward(results)
    
    #boxplotResponseTimes(oneStepStrategyResults.append(twoStepStrategyResults, ignore_index = True))

def visualizeEnvDynTrajectory():
    times = []
    interArrivalTimes = []
    with open(envDynSamples) as csv_file:
        rows = csv.reader(csv_file, delimiter=csvDelimiter)
        for each in rows:
            if each[0].startswith('Time'):
                continue
            
            time = int(float(each[0]))
            times.append(time)

            interArrivalTime = float(each[1])
            interArrivalTimes.append(interArrivalTime)
    
    d = {'Time': times, 'Inter-arrival time': interArrivalTimes, 'Hue': [0] * len(times)}
    pd.DataFrame(data=d)

    plot = sns.scatterplot(data=d, x="Time", y="Inter-arrival time", hue="Hue", palette = "Greys", legend = False)
    plot.set(xlim=(0,100))
    plt.show()

if __name__ == "__main__":
    base = os.getcwd()
    if len(sys.argv) == 2:
        base = str(sys.argv[1])

    for dir in listdir(base):
        if os.path.isfile(dir):
            continue

        resultDir = os.path.join(base, dir)
        if dir.startswith("SimExp"):
            visualizeAllSimExpResults(resultDir)
        if dir.startswith("SimuLizar"):
            visualizeAllSimuLizarResults(resultDir)
