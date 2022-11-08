import csv
import sys
import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os
from os import listdir

csvDelimiter = ';'

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

def norm(value, upper, lower):
    if value > upper:
        return 0
    if value < lower:
        return 1
    return (1 / (upper - lower)) * (upper - value)

def loadPerformanceBasedRewards(sampleSpaceFile, simExpStoreFile, modelLabel, rel_pref = 1, perf_pref = 1):
    quantityFilter = QuantityFilter(simExpStoreFile)

    time = []
    rewards = []
    with open(sampleSpaceFile) as csv_file:
        trajs = list(csv.reader(csv_file, delimiter=csvDelimiter))
        trajs = trajs[1:]
        for traj in trajs:
            counter = 0
            for each in traj:
                if each.startswith('Samples:'):
                    success = quantityFilter.findQuantity(each, '_NbnWMNDeEeqWne3bdagE9g')

                    rt = quantityFilter.findQuantity(each, 'EnvironmentPerception_Response Time')
                    perf_norm = norm(rt, 0.3, 0.1)

                    reward = (rel_pref * success) + (perf_pref * perf_norm)
                    rewards.append(reward)

                    time.append(counter)

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

    return time, averagedRewards, [modelLabel] * len(time)

def lineplotAccReward(results):
    plot = sns.lineplot(data=results, x="Time", y="Average reward", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_yticks(np.arange(0, 2.2, 0.2).tolist())
    plt.show()

def visualizePerformanceBasedStrategyResults(strategy_dir):
    d = {}

    sampleSpaceFile, simExpStoreFile = getSimExpResultFiles(strategy_dir, 'Default')
    time, rewards, modelLabels = loadPerformanceBasedRewards(sampleSpaceFile, simExpStoreFile, 'Default ' + r'$b_D$')
    setOrAppend(d, time, rewards, modelLabels)

    sampleSpaceFile, simExpStoreFile = getSimExpResultFiles(strategy_dir, 'Worst')
    time, rewards, modelLabels = loadPerformanceBasedRewards(sampleSpaceFile, simExpStoreFile, 'Worst ' + r'$b^-$')
    setOrAppend(d, time, rewards, modelLabels)

    sampleSpaceFile, simExpStoreFile = getSimExpResultFiles(strategy_dir, 'Perfect')
    time, rewards, modelLabels = loadPerformanceBasedRewards(sampleSpaceFile, simExpStoreFile, 'Perfect ' + r'$b^+$')
    setOrAppend(d, time, rewards, modelLabels)

    if bool(d) == False:
        return
    visualize(pd.DataFrame(data=d), custom_xlim=(0,100), custom_ylim=(0.9,2), custom_yticks=np.arange(0.9, 2.1, 0.1).tolist())

def getSimExpResultFiles(strategy_dir, label):
    simExpStoreFile = ''
    sampleSpaceFile = ''
    for file in listdir(strategy_dir):
        if label in file:
            if 'SampleSpace' in file:
                sampleSpaceFile = os.path.join(strategy_dir, file)
            if 'SimulatedExperience' in file:
                simExpStoreFile = os.path.join(strategy_dir, file)
    
    return sampleSpaceFile, simExpStoreFile


def visualizeStrategyResults(strategy_dir):
    d = {}
    for file in listdir(strategy_dir):
        if 'SampleSpace' in file:
            sampleSpaceFile = os.path.join(strategy_dir, file)  
            if 'Default' in file:
                time, rewards, modelLabels = loadGeneratedRewards(sampleSpaceFile, 'Default ' + r'$b_D$')
                setOrAppend(d, time, rewards, modelLabels)
            elif 'Worst' in file:
                time, rewards, modelLabels = loadGeneratedRewards(sampleSpaceFile, 'Worst ' + r'$b^-$')
                setOrAppend(d, time, rewards, modelLabels)
            elif 'Perfect' in file:
                time, rewards, modelLabels = loadGeneratedRewards(sampleSpaceFile, 'Perfect ' + r'$b^+$')
                setOrAppend(d, time, rewards, modelLabels)

    if bool(d) == False:
        return
    visualize(pd.DataFrame(data=d))

def visualize(results, custom_xlim=(0,100), custom_ylim=(0.0,1.0), custom_yticks=np.arange(0.0, 1.1, 0.1).tolist()):
    plot = sns.lineplot(data=results, x="Time", y="Average reward", hue="Model")
    plot.set(xlim=custom_xlim)
    plot.set(ylim=custom_ylim)
    plot.set_yticks(custom_yticks)
    plt.show()

    
def loadGeneratedRewards(sampleSpaceFile, modelLabel):
    rewards = []
    time = []
    with open(sampleSpaceFile) as csv_file:
        sampleSpace = list(csv.reader(csv_file, delimiter=csvDelimiter))
        sampleSpace = sampleSpace[1:]
        for eachRow in sampleSpace:
            counter = 0
            for eachEntry in eachRow:
                if eachEntry.startswith('Samples'):
                    continue
                else:
                    time.append(counter)

                    reward = float(eachEntry)
                    rewards.append(reward)

                    counter += 1
                    
    #count = 1
    count = 0
    accReward = 0
    averagedRewards = []
    for each in rewards:
        num_of_acc = time[count] + 1
        if num_of_acc == 1:
            accReward = 0

        accReward += each
        averagedRewards.append(accReward / num_of_acc)

        count += 1
        #if count - 1 == counter:
        #    count = 1
        #    accReward = 0
        #else:
        #    count += 1

    return time, averagedRewards, [modelLabel] * len(time)

def setOrAppend(d, time, rewards, modelLabels):
    if bool(d) == False:
        d['Time'] = time
        d['Average reward'] = rewards
        d['Model'] = modelLabels
    else:
        d['Time'].extend(time)
        d['Average reward'].extend(rewards)
        d['Model'].extend(modelLabels)

def visualizeWithAdjustedRewardFunction():
    d = {}

    sampleSpaceFile, simExpStoreFile = getSimExpResultFiles(strategy_dir_non, 'Default')
    time, rewards, modelLabels = loadPerformanceBasedRewards(sampleSpaceFile, simExpStoreFile, r'$\pi_{\delta_\emptyset}[b_D]$', rel_pref=1, perf_pref = 0)
    setOrAppend(d, time, rewards, modelLabels)

    sampleSpaceFile, simExpStoreFile = getSimExpResultFiles(strategy_dir_ran, 'Default')
    time, rewards, modelLabels = loadPerformanceBasedRewards(sampleSpaceFile, simExpStoreFile, r'$\pi_{\delta_{Ran}}[b_D]$', rel_pref=1, perf_pref = 0)
    setOrAppend(d, time, rewards, modelLabels)

    sampleSpaceFile, simExpStoreFile = getSimExpResultFiles(strategy_dir_rel, 'Default')
    time, rewards, modelLabels = loadPerformanceBasedRewards(sampleSpaceFile, simExpStoreFile, r'$\pi_{\delta_{Rel}}[b_D]$', rel_pref=1, perf_pref = 0)
    setOrAppend(d, time, rewards, modelLabels)

    print('Visualize all pi[b_D] regarding reliability.')
    visualize(pd.DataFrame(data=d), custom_xlim=(0,100), custom_ylim=(0.9,0.97), custom_yticks=np.arange(0.9, 0.98, 0.01).tolist())

    d = {}

    sampleSpaceFile, simExpStoreFile = getSimExpResultFiles(strategy_dir_non, 'Default')
    time, rewards, modelLabels = loadPerformanceBasedRewards(sampleSpaceFile, simExpStoreFile, r'$\pi_{\delta_\emptyset}[b_D]$')
    setOrAppend(d, time, rewards, modelLabels)

    sampleSpaceFile, simExpStoreFile = getSimExpResultFiles(strategy_dir_ran, 'Default')
    time, rewards, modelLabels = loadPerformanceBasedRewards(sampleSpaceFile, simExpStoreFile, r'$\pi_{\delta_{Ran}}[b_D]$')
    setOrAppend(d, time, rewards, modelLabels)

    sampleSpaceFile, simExpStoreFile = getSimExpResultFiles(strategy_dir_rel, 'Default')
    time, rewards, modelLabels = loadPerformanceBasedRewards(sampleSpaceFile, simExpStoreFile, r'$\pi_{\delta_{Rel}}[b_D]$')
    setOrAppend(d, time, rewards, modelLabels)
    
    print('Visualize all pi[b_D] regarding performability.')
    visualize(pd.DataFrame(data=d), custom_xlim=(0,100), custom_ylim=(1.4,2), custom_yticks=np.arange(1.4, 2.2, 0.2).tolist())


if __name__ == "__main__":
    dir = os.getcwd()
    if len(sys.argv) == 2:
        dir = str(sys.argv[1])

    for subdir in listdir(dir):
        if subdir.startswith('NonAdaptiveStrategy'):
            print('Visualize non-adaptive strategy results')
            strategy_dir = os.path.join(dir, subdir)
            visualizeStrategyResults(strategy_dir)
            #visualizePerformanceBasedStrategyResults(strategy_dir)
            global strategy_dir_non
            strategy_dir_non = strategy_dir
        elif subdir.startswith('RandomizedStrategy'):
            print('Visualize randomized strategy results')
            strategy_dir = os.path.join(dir, subdir)
            visualizeStrategyResults(strategy_dir)
            #visualizePerformanceBasedStrategyResults(strategy_dir)
            global strategy_dir_ran
            strategy_dir_ran = strategy_dir
        elif subdir.startswith('ReliabilityPrioritizedStrategy'):
            print('Visualize reliability prioritized strategy results')
            strategy_dir = os.path.join(dir, subdir)
            visualizeStrategyResults(strategy_dir)
            #visualizePerformanceBasedStrategyResults(strategy_dir)
            global strategy_dir_rel
            strategy_dir_rel = strategy_dir

    visualizeWithAdjustedRewardFunction()
