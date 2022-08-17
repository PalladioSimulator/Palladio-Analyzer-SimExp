import csv
import sys
import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os
from os import listdir

csvDelimiter = ';'

def lineplotAccReward(results):
    plot = sns.lineplot(data=results, x="Time", y="Average reward", hue="Strategy")
    plot.set(xlim=(0,100))
    plot.set_yticks(np.arange(0, 2.2, 0.2).tolist())
    plt.show()

def visualizeStrategyResults(strategy_dir):
    d = {}
    for file in listdir(strategy_dir):
        if 'SampleSpace' in file:
            sampleSpaceFile = os.path.join(strategy_dir, file)  
            if 'Rambo' in file:
                time, rewards, modelLabels = loadGeneratedRewards(sampleSpaceFile, 'Rambo ' + r'$b_R$')
                setOrAppend(d, time, rewards, modelLabels)
            elif 'Chauffeur' in file:
                time, rewards, modelLabels = loadGeneratedRewards(sampleSpaceFile, 'Chauffeur ' + r'$b_C$')
                setOrAppend(d, time, rewards, modelLabels)
            elif 'Worst' in file:
                time, rewards, modelLabels = loadGeneratedRewards(sampleSpaceFile, 'Worst ' + r'$b^-$')
                setOrAppend(d, time, rewards, modelLabels)
            elif 'Perfect' in file:
                time, rewards, modelLabels = loadGeneratedRewards(sampleSpaceFile, 'Perfect ' + r'$b^+$')
                setOrAppend(d, time, rewards, modelLabels)

    visualize(pd.DataFrame(data=d))

def visualize(results):
    plot = sns.lineplot(data=results, x="Time", y="Average reward", hue="Model")
    plot.set(xlim=(0,100))
    plot.set(ylim=(0.9,1.0))
    plot.set_yticks(np.arange(0.9, 1.02, 0.02).tolist())
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

def setOrAppend(d, time, rewards, modelLabels):
    if bool(d) == False:
        d['Time'] = time
        d['Average reward'] = rewards
        d['Model'] = modelLabels
    else:
        d['Time'].extend(time)
        d['Average reward'].extend(rewards)
        d['Model'].extend(modelLabels)

if __name__ == "__main__":
    dir = os.getcwd()
    if len(sys.argv) == 2:
        dir = str(sys.argv[1])

    for subdir in listdir(dir):
        if subdir.startswith('NonAdaptiveStrategy'):
            print('Visualize non-adaptive strategy results')
            strategy_dir = os.path.join(dir, subdir)
            visualizeStrategyResults(strategy_dir)
        elif subdir.startswith('RandomizedFilterStrategy'):
            print('Visualize randomized filter strategy results')
            strategy_dir = os.path.join(dir, subdir)
            visualizeStrategyResults(strategy_dir)
        elif subdir.startswith('ImgBlurMitigationStrategy'):
            print('Visualize img blur mitigation strategy results')
            strategy_dir = os.path.join(dir, subdir)
            visualizeStrategyResults(strategy_dir)
