import csv
import sys
import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os
from os import listdir

csvDelimiter = ';'

defaultDeltaIoTStrategyResults = ''
defaultDeltaIoTStrategyConfigsIn = ''
defaultDeltaIoTStrategyConfigsOut = 'AvgDefaultDeltaIoTStrategyConfigs.csv'
nonAdaptiveDeltaIoTStrategyResults = ''
qualityBasedDeltaIoTStrategyResults = ''
qualityBasedDeltaIoTStrategyConfigsIn = ''
qualityBasedDeltaIoTStrategyConfigsOut = 'AvgQualityBasedDeltaIoTStrategyConfigs.csv'
upperQualityBasedDeltaIoTStrategyResults = ''
upperQualityBasedDeltaIoTStrategyConfigsIn = ''
upperQualityBasedDeltaIoTStrategyConfigsOut = 'AvgUpperQualityBasedDeltaIoTStrategyConfigs.csv'
lowerQualityBasedDeltaIoTStrategyResults = ''
lowerQualityBasedDeltaIoTStrategyConfigsIn = ''
lowerQualityBasedDeltaIoTStrategyConfigsOut = 'AvgLowerQualityBasedDeltaIoTStrategyConfigs.csv'
meanQualityBasedDeltaIoTStrategyResults = ''
meanQualityBasedDeltaIoTStrategyConfigsIn = ''
meanQualityBasedDeltaIoTStrategyConfigsOut = 'AvgMeanQualityBasedDeltaIoTStrategyConfigs.csv'

simExpStoreDeltaIoTStrategy = ''
sampleSpaceDeltaIoTStrategy = ''
simExpDeltaIoTStrategyConfigsIn = ''
simExpDeltaIoTStrategyConfigsOut = 'AvgSimExpDeltaIoTStrategyConfigs.csv'
simExpStoreNonAdaptiveStrategy = ''
sampleSpaceNonAdaptiveStrategy = ''
simExpStoreQualityBasedStrategy = ''
sampleSpaceQualityBasedStrategy = ''
simExpQualityBasedDeltaIoTStrategyConfigsIn = ''
simExpQualityBasedDeltaIoTStrategyConfigsOut = 'AvgSimExpQualityBasedDeltaIoTStrategyConfigs.csv'
simExpUpperStoreQualityBasedStrategy = ''
sampleSpaceUpperQualityBasedStrategy = ''
simExpUpperQualityBasedDeltaIoTStrategyConfigsIn = ''
simExpUpperQualityBasedDeltaIoTStrategyConfigsOut = 'AvgSimExpUpperQualityBasedDeltaIoTStrategyConfigs.csv'
simExpStoreLowerQualityBasedStrategy = ''
sampleSpaceLowerQualityBasedStrategy = ''
simExpLowerQualityBasedDeltaIoTStrategyConfigsIn = ''
simExpLowerQualityBasedDeltaIoTStrategyConfigsOut = 'AvgSimExpLowerQualityBasedDeltaIoTStrategyConfigs.csv'

nonAdaptiveStrategyLabel = 'Non-adaptive'
defaultStrategyLabel = 'Default'
qualityStrategyLabel = 'Quality-based'

class QuantityFilter:
    def __init__(self, simExpStore):
        self.cache = {}
        with open(simExpStore) as csv_file:
            simulatedExperienceStore = csv.DictReader(csv_file, delimiter=csvDelimiter) 
            for eachRow in simulatedExperienceStore:
                self.cache[eachRow['Id']] = eachRow['Quantified state current']

    def findQuantity(self, id, quantity):
        quantities = self.cache[id]
        for eachQuantity in quantities.split('|'):
            if quantity in eachQuantity:
                strValue = eachQuantity.split(',')[0]
                start = strValue.rfind(': ') + 1
                end = len(strValue)
                value = strValue[start:end]
                return float(value)

def visualizeDeltaIoTResults():
    nonAdaptiveResults = prepareDeltaIoTResultsOf(nonAdaptiveDeltaIoTStrategyResults, nonAdaptiveStrategyLabel)    
    lineplotPacketLoss(nonAdaptiveResults)
    lineplotDeltaIoTEnergyConsumption(nonAdaptiveResults)

    defaultResults = prepareDeltaIoTResultsOf(defaultDeltaIoTStrategyResults, defaultStrategyLabel)    
    lineplotPacketLoss(defaultResults)
    lineplotDeltaIoTEnergyConsumption(defaultResults)

    qualityResults = prepareDeltaIoTResultsOf(qualityBasedDeltaIoTStrategyResults, qualityStrategyLabel)    
    lineplotPacketLoss(qualityResults)
    lineplotDeltaIoTEnergyConsumption(qualityResults)

    results = nonAdaptiveResults.append(defaultResults, ignore_index = True).append(qualityResults, ignore_index = True)
    boxplotPacketLoss(results)
    boxplotEnergyConsumption(results)

def visualizeAllDeltaIoTResults():
    nonAdaptiveResults = prepareDeltaIoTResultsOf(nonAdaptiveDeltaIoTStrategyResults, nonAdaptiveStrategyLabel)  
    defaultResults = prepareDeltaIoTResultsOf(defaultDeltaIoTStrategyResults, defaultStrategyLabel)      
    qualityResults = prepareDeltaIoTResultsOf(qualityBasedDeltaIoTStrategyResults, qualityStrategyLabel)    
    
    results = nonAdaptiveResults.append(defaultResults, ignore_index = True).append(qualityResults, ignore_index = True)
    
    plot = sns.lineplot(data=results, x="Time", y="Packet loss", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 110, 10).tolist())
    plot.set_yticks(np.arange(0.05, 0.35, 0.05).tolist())
    plt.show()

    plot = sns.lineplot(data=results, x="Time", y="Energy consumption", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 110, 10).tolist())
    plot.set_yticks(np.arange(13, 24, 1).tolist())
    plt.show()

    boxplotPacketLoss(results)

    boxplotEnergyConsumption(results)

    lineplotAccReward(results)

def visualizeVariantsOfDeltaIoTQualityBasedStrategyResults():
    lower = prepareDeltaIoTResultsOf(lowerQualityBasedDeltaIoTStrategyResults, r'$\beta^- = 10$')  
    mean = prepareDeltaIoTResultsOf(meanQualityBasedDeltaIoTStrategyResults, r'$\beta = 18$')      
    upper = prepareDeltaIoTResultsOf(upperQualityBasedDeltaIoTStrategyResults, r'$\beta^+ = 26$')    
    
    results = lower.append(mean, ignore_index = True).append(upper, ignore_index = True)
    
    plot = sns.lineplot(data=results, x="Time", y="Packet loss", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 550, 50).tolist())
    plot.set_yticks(np.arange(0.05, 0.35, 0.05).tolist())
    plt.show()

    plot = sns.lineplot(data=results, x="Time", y="Energy consumption", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 550, 50).tolist())
    plot.set_yticks(np.arange(13, 26, 1).tolist())
    plt.show()

    plot = sns.lineplot(data=results, x="Time", y="Average reward", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 550, 50).tolist())
    plot.set_yticks(np.arange(0, 2.2, 0.2).tolist())
    plt.show()

def prepareDeltaIoTResultsOf(deltaiotFile, strategyLabel):
    with open(deltaiotFile) as csv_file:
        deltaIoTResults = csv.DictReader(csv_file, delimiter=csvDelimiter)
        time = []
        packetLoss = []
        energyConsumption = []
        rewards = []
        for eachRow in deltaIoTResults:
            pl = float(eachRow['Packet loss'])
            ec = float(eachRow['Energy consumption'])

            time.append(int(eachRow['Time']))
            packetLoss.append(pl)
            energyConsumption.append(ec)

            reward = norm(pl,0.2,0.025) + norm(ec,26,10)
            rewards.append(reward)
            
        count = 1
        accReward = 0
        averagedRewards = []
        total = time[-1]
        for each in rewards:
            accReward += each
            averagedRewards.append(accReward / count)
            if count - 1 == total:
                count = 1
                accReward = 0
            else:
                count += 1

    strategyLabels = [strategyLabel] * len(time)

    d = {'Time': time, 'Packet loss': packetLoss, 'Energy consumption': energyConsumption, 'Average reward': averagedRewards, 'Strategy': strategyLabels}
    return pd.DataFrame(data=d)

def norm(value, upper, lower):
    if value > upper:
        return 0
    if value < lower:
        return 1
    return (1 / (upper - lower)) * (upper - value)

def lineplotDeltaIoTEnergyConsumption(results):
    plot = sns.lineplot(data=results, x="Time", y="Energy consumption")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 110, 10).tolist())
    plot.set_yticks(np.arange(13, 22, 1).tolist())
    plt.show()

def visualizeAllSimExpResults():
    results = prepareSimExpResultsOf(sampleSpaceNonAdaptiveStrategy, simExpStoreNonAdaptiveStrategy, nonAdaptiveStrategyLabel)
    results1 = prepareSimExpResultsOf(sampleSpaceDeltaIoTStrategy, simExpStoreDeltaIoTStrategy, defaultStrategyLabel)
    results2 = prepareSimExpResultsOf(sampleSpaceQualityBasedStrategy, simExpStoreQualityBasedStrategy, qualityStrategyLabel)

    results = results.append(results1, ignore_index = True).append(results2, ignore_index = True)

    plot = sns.lineplot(data=results, x="Time", y="Packet loss", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 110, 10).tolist())
    plot.set_yticks(np.arange(0.05, 0.35, 0.05).tolist())
    plt.show()

    plot = sns.lineplot(data=results, x="Time", y="Energy consumption", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 110, 10).tolist())
    plt.show()

    lineplotAccReward(results)
    boxplotPacketLoss(results)
    boxplotEnergyConsumption(results)

def visualizeVariantsOfQualityBasedStrategySimExpResults():
    results = prepareSimExpResultsOf(sampleSpaceLowerQualityBasedStrategy, simExpStoreLowerQualityBasedStrategy, r'$\beta^- = 30.5$')
    results1 = prepareSimExpResultsOf(sampleSpaceQualityBasedStrategy, simExpStoreQualityBasedStrategy, r'$\beta = 32$')
    results2 = prepareSimExpResultsOf(sampleSpaceUpperQualityBasedStrategy, simExpStoreUpperQualityBasedStrategy, r'$\beta^+ = 34.5$')

    results = results.append(results1, ignore_index = True).append(results2, ignore_index = True)

    plot = sns.lineplot(data=results, x="Time", y="Packet loss", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_yticks(np.arange(0.05, 0.35, 0.05).tolist())
    plot.set_xticks(np.arange(0, 110, 10).tolist())
    plt.show()

    plot = sns.lineplot(data=results, x="Time", y="Energy consumption", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 110, 10).tolist())
    plt.show()

    lineplotAccReward(results)

def visualizeSimExpResults():
    nonAdaptiveResults = prepareSimExpResultsOf(sampleSpaceNonAdaptiveStrategy, simExpStoreNonAdaptiveStrategy, nonAdaptiveStrategyLabel)
    lineplotPacketLoss(nonAdaptiveResults)
    lineplotEnergyConsumption(nonAdaptiveResults)

    defaultResults = prepareSimExpResultsOf(sampleSpaceDeltaIoTStrategy, simExpStoreDeltaIoTStrategy, defaultStrategyLabel)
    lineplotPacketLoss(defaultResults)
    lineplotEnergyConsumption(defaultResults)

    qualityResults = prepareSimExpResultsOf(sampleSpaceQualityBasedStrategy, simExpStoreQualityBasedStrategy, qualityStrategyLabel)
    lineplotPacketLoss(qualityResults)
    lineplotEnergyConsumption(qualityResults)

    results = nonAdaptiveResults.append(defaultResults, ignore_index = True).append(qualityResults, ignore_index = True)
    lineplotAccReward(results)
    boxplotPacketLoss(results)
    boxplotEnergyConsumption(results)

def lineplotPacketLoss(results):
    #plot = sns.lineplot(data=results, x="Time", y="Packet loss", hue="Strategy")
    plot = sns.lineplot(data=results, x="Time", y="Packet loss")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 110, 10).tolist())
    plot.set_yticks(np.arange(0.025, 0.35, 0.025).tolist())
    plt.show()

def lineplotEnergyConsumption(results):
    #plot = sns.lineplot(data=results, x="Time", y="Energy consumption", hue="Strategy")
    plot = sns.lineplot(data=results, x="Time", y="Energy consumption")
    plot.set(xlim=(0,100))
    plot.set_xticks(np.arange(0, 110, 10).tolist())
    plot.set_yticks(np.arange(31, 35.5, 0.5).tolist())
    plt.show()

def boxplotPacketLoss(results):
    props = {
        "marker":"o",
        "markerfacecolor":"white", 
        "markeredgecolor":"black",
        "markersize":"10"
    }
    plot = sns.boxplot(data=results, x="Strategy", y="Packet loss", showmeans=True, meanprops=props)
    plot.set_yticks(np.arange(0, 0.4, 0.05).tolist())
    plt.show()

def boxplotEnergyConsumption(results):
    props = {
        "marker":"o",
        "markerfacecolor":"white", 
        "markeredgecolor":"black",
        "markersize":"10"
    }
    plot = sns.boxplot(data=results, x="Strategy", y="Energy consumption", showmeans=True, meanprops=props)
    plt.show()

def lineplotAccReward(results):
    plot = sns.lineplot(data=results, x="Time", y="Average reward", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_yticks(np.arange(0, 2.2, 0.2).tolist())
    plt.show()

def prepareSimExpResultsOf(sampleSpaceFile, simExpStoreFile, strategyLabel):
    quantityFilter = QuantityFilter(simExpStoreFile)
    
    packetLoss = []
    energyConsumption = [] 
    rewards = []
    time = []
    with open(sampleSpaceFile) as csv_file:
        sampleSpace = csv.reader(csv_file, delimiter=csvDelimiter)
        for eachRow in sampleSpace:
            if eachRow[0].startswith('Point in time'):
                continue
            
            counter = 0
            for eachEntry in eachRow:
                if eachEntry.startswith('Environmental states'):
                    packetLossValue = quantityFilter.findQuantity(eachEntry, 'P=? [ F "Packetloss" ]')
                    packetLoss.append(packetLossValue)

                    energyValue = quantityFilter.findQuantity(eachEntry, 'Rmax=? [ F "EnergyConsumption" ]')
                    energyConsumption.append(energyValue)

                else:
                    time.append(counter)

                    reward = float(eachEntry)
                    rewards.append(reward)

                    counter += 1
                    
    count = 1
    accReward = 0
    averagedRewards = []
    for each in rewards:
        accReward += each
        averagedRewards.append(accReward / count)
        if count - 1 == counter:
            count = 1
            accReward = 0
        else:
            count += 1

    strategyLabels = [strategyLabel] * len(time)

    d = {'Time': time, 'Packet loss': packetLoss, 'Energy consumption': energyConsumption, 'Average reward': averagedRewards, 'Strategy': strategyLabels}
    return pd.DataFrame(data=d)

def calcAverageConfigTrajOfSimExp():
    calcAverageConfigTraj(simExpDeltaIoTStrategyConfigsIn, simExpDeltaIoTStrategyConfigsOut)
    calcAverageConfigTraj(simExpQualityBasedDeltaIoTStrategyConfigsIn, simExpQualityBasedDeltaIoTStrategyConfigsOut)

    calcAverageConfigTraj(simExpUpperQualityBasedDeltaIoTStrategyConfigsIn, simExpUpperQualityBasedDeltaIoTStrategyConfigsOut)
    calcAverageConfigTraj(simExpLowerQualityBasedDeltaIoTStrategyConfigsIn, simExpLowerQualityBasedDeltaIoTStrategyConfigsOut)

def calcAverageConfigTrajOfDeltaIoT():
    calcAverageConfigTraj(defaultDeltaIoTStrategyConfigsIn, defaultDeltaIoTStrategyConfigsOut)
    calcAverageConfigTraj(qualityBasedDeltaIoTStrategyConfigsIn, qualityBasedDeltaIoTStrategyConfigsOut)

    calcAverageConfigTraj(upperQualityBasedDeltaIoTStrategyConfigsIn, upperQualityBasedDeltaIoTStrategyConfigsOut)
    calcAverageConfigTraj(lowerQualityBasedDeltaIoTStrategyConfigsIn, lowerQualityBasedDeltaIoTStrategyConfigsOut)
    calcAverageConfigTraj(meanQualityBasedDeltaIoTStrategyConfigsIn, meanQualityBasedDeltaIoTStrategyConfigsOut)

def calcAverageConfigTraj(configFileIn, configFileOut):
    totalNumRuns = 10

    configMap = {}
    with open(configFileIn) as csv_file:
        csvRows = csv.DictReader(csv_file, delimiter=csvDelimiter) 
        for each in csvRows:
            key = each['Run'] + "_" + each['Link']
            power = float(each['Power'])
            dist = float(each['Distribution'])

            if key in configMap:
                oldValues = configMap[key]
                newPower = oldValues[0] + power
                newDist = oldValues[1] + dist
                configMap[key] = (newPower, newDist)
            else:
                configMap[key] = (power, dist)
        
        for each in configMap.keys():
            values = configMap[each]
            avgPower = values[0] / totalNumRuns
            avgDist = values[1] / totalNumRuns
            configMap[each] = (avgPower, avgDist)

    if os.path.exists(configFileOut) == False:
        open(configFileOut, "x")

    configsToWrite = [["Run","Link","Power","Distribution"]]
    for each in configMap.keys():
        splittedKey = each.split('_')
        values = configMap[each]

        run = splittedKey[0]
        link = splittedKey[1]
        avgPower = values[0]
        avgDist = values[1]

        configsToWrite.append([run, link, str(avgPower), str(avgDist)])

    with open(configFileOut, 'w', newline='') as f:
        writer = csv.writer(f, delimiter=csvDelimiter)
        writer.writerows(configsToWrite)

if __name__ == "__main__":
    dir = os.getcwd()
    if len(sys.argv) == 2:
        dir = str(sys.argv[1])

    for file in listdir(dir):
        if file.startswith('NonAdaptiveStrategySampleSpace'):
            sampleSpaceNonAdaptiveStrategy = file
        elif file.startswith('NonAdaptiveStrategySimulatedExperienceStore'):
            simExpStoreNonAdaptiveStrategy = file
        elif file.startswith('DeltaIoTStrategySampleSpace'):
            sampleSpaceDeltaIoTStrategy = file
        elif file.startswith('DeltaIoTStrategySimulatedExperienceStore'):
            simExpStoreDeltaIoTStrategy = file
        elif file.startswith('SimExpDeltaIoTStrategyConfigurations'):
            simExpDeltaIoTStrategyConfigsIn = file  
        elif file.startswith('QualityBasedStrategySampleSpace'):
            sampleSpaceQualityBasedStrategy = file
        elif file.startswith('QualityBasedStrategySimulatedExperienceStore'):
            simExpStoreQualityBasedStrategy = file
        elif file.startswith('SimExpQualityBasedDeltaIoTStrategyConfigurations'):
            simExpQualityBasedDeltaIoTStrategyConfigsIn = file  
        elif file.startswith('UpperQualityBasedStrategySampleSpace'):
            sampleSpaceUpperQualityBasedStrategy = file
        elif file.startswith('UpperQualityBasedStrategySimulatedExperienceStore'):
            simExpStoreUpperQualityBasedStrategy = file
        elif file.startswith('SimExpUpperQualityBasedDeltaIoTStrategyConfigurations'):
            simExpUpperQualityBasedDeltaIoTStrategyConfigsIn = file  
        elif file.startswith('LowerQualityBasedStrategySampleSpace'):
            sampleSpaceLowerQualityBasedStrategy = file
        elif file.startswith('LowerQualityBasedStrategySimulatedExperienceStore'):
            simExpStoreLowerQualityBasedStrategy = file
        elif file.startswith('SimExpLowerQualityBasedDeltaIoTStrategyConfigurations'):
            simExpLowerQualityBasedDeltaIoTStrategyConfigsIn = file  
        elif file.startswith('DefaultDeltaIoTStrategyResults'):
            defaultDeltaIoTStrategyResults = file
        elif file.startswith('DefaultDeltaIoTStrategyConfigurations'):
            defaultDeltaIoTStrategyConfigsIn = file 
        elif file.startswith('NonAdaptiveDeltaIoTStrategyResults'):
            nonAdaptiveDeltaIoTStrategyResults = file
        elif file.startswith('QualityBasedDeltaIoTStrategyResults'):
            qualityBasedDeltaIoTStrategyResults = file
        elif file.startswith('QualityBasedDeltaIoTStrategyConfigurations'):
            qualityBasedDeltaIoTStrategyConfigsIn = file
        elif file.startswith('UpperQualityBasedDeltaIoTStrategyResults'):
            upperQualityBasedDeltaIoTStrategyResults = file
        elif file.startswith('UpperQualityBasedDeltaIoTStrategyConfigurations'):
            upperQualityBasedDeltaIoTStrategyConfigsIn = file
        elif file.startswith('LowerQualityBasedDeltaIoTStrategyResults'):
            lowerQualityBasedDeltaIoTStrategyResults = file     
        elif file.startswith('LowerQualityBasedDeltaIoTStrategyConfigurations'):
            lowerQualityBasedDeltaIoTStrategyConfigsIn = file
        elif file.startswith('MeanQualityBasedDeltaIoTStrategyResults'):
            meanQualityBasedDeltaIoTStrategyResults = file     
        elif file.startswith('MeanQualityBasedDeltaIoTStrategyConfigurations'):
            meanQualityBasedDeltaIoTStrategyConfigsIn = file  
    
    visualizeAllSimExpResults()
    visualizeVariantsOfQualityBasedStrategySimExpResults()

    visualizeAllDeltaIoTResults()
    visualizeVariantsOfDeltaIoTQualityBasedStrategyResults()

    calcAverageConfigTrajOfDeltaIoT()
    calcAverageConfigTrajOfSimExp()
