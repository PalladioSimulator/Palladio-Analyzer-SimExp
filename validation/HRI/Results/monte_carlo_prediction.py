import csv
import sys
import os
from os import listdir
import statistics

csvDelimiter = ';'

class Sample:
    def __init__(self, state, reward):
        self.state = state
        self.reward = reward
        
    def __str__(self):
        return "%s: %s" % (self.state, self.reward)
        
        
class ValueFunction:
    def __init__(self):
        self.returns = []
        self._value = 0
        
    def addValue(self, v):
        self.returns.append(v)
        self._value = statistics.mean(self.returns)
    def __str__(self):
        return "%s: %s" % (self._value, self.returns)
        
class MonteCarlo:
    def estimate(self, episodes):
        #sample1 = [Sample("A", 3), Sample("A", 2), Sample("B", -4), Sample("A", 4), Sample("B", -3)]
        #sample2 = [Sample("B", -2), Sample("A", 3), Sample("B", -3)]
        #episodes = [sample1, sample2]
        
        valueFunctions = self.initMonteCarlo(episodes)
        self.monteCarlo(episodes, valueFunctions)
        return valueFunctions
        
    def initMonteCarlo(self, episodes):
        valueFunctions = {}
        for e in episodes:
            for sample in e:
                if not sample.state in valueFunctions:
                    valueFunctions[sample.state] = ValueFunction()
        return valueFunctions

    def monteCarlo(self, episodes, valueFunctions):
        for episode in episodes:
            g = 0
            T = len(episode)
            for t in range(T - 1, -1, -1):
                sample = episode[t]
                rewardplus1 = sample.reward
                g = g + rewardplus1
                statet = sample.state
                predecessorStates = self.getPredeccessorStates(episode, t)
                if not statet in predecessorStates:
                    vf = valueFunctions[statet]
                    vf.addValue(g)

    def getPredeccessorStates(self, episode, t):
        predecessorStates = []
        for i in episode[:t]:
            predecessorStates.append(i.state)
        return predecessorStates


def getSampleModel(sampleSpaceFile):
    with open(sampleSpaceFile) as csv_file:
        sampleSpace = csv.reader(csv_file, delimiter=csvDelimiter)
        
        episodes = []
        for eachRow in sampleSpace:
            if eachRow[0].startswith('Point in time'):
                continue

            episode = []
            state = ''
            for eachEntry in eachRow:
                if eachEntry.startswith('Samples'):
                    helper = eachEntry.split('_')
                    state = helper[0] + '_' + helper[1]
                else:
                    reward = float(eachEntry)
                    sample = Sample(state, reward)
                    episode.append(sample)
            
            episodes.append(episode)

        return episodes

def estimateInitialDistribution(episodes):
    initialProbs = {}
    for each in episodes:
        initial = each[0].state
        if initial in initialProbs:
            initialProbs[initial] = initialProbs[initial] + 1
        else:
            initialProbs[initial] = 1 

    for each in initialProbs:
        initialProbs[each] = initialProbs[each] / len(episodes)

    return initialProbs

def estimateExpectedReward(strategy, sampleSpaceFile):
    episodes = getSampleModel(sampleSpaceFile)
    values = MonteCarlo().estimate(episodes)

    initialProbs = estimateInitialDistribution(episodes)

    expected = 0
    for each in initialProbs:
        prob = initialProbs[each]
        value = values[each]._value
        expected += prob * value

    total = 0
    for episode in episodes:
        for sample in episode:
            total += sample.reward

    print('Expected reward of strategy ' + strategy + ': ' + str(expected))
    print('Total reward of strategy ' + strategy + ': ' + str(total))


if __name__ == "__main__":
    dir = os.getcwd()
    if len(sys.argv) == 2:
        dir = str(sys.argv[1])


    estimateExpectedReward("NonAdaptive_worst", "NonAdaptiveStrategy\Worst_StaticSystemSimulationSampleSpace.csv")    
    estimateExpectedReward("NonAdaptive_default", "NonAdaptiveStrategy\Default_StaticSystemSimulationSampleSpace.csv")
    estimateExpectedReward("NonAdaptive_perfect", "NonAdaptiveStrategy\Perfect_StaticSystemSimulationSampleSpace.csv")

    estimateExpectedReward("Randomized_worst", "RandomizedStrategy\Worst_RandomizedAdaptationStrategySampleSpace.csv")    
    estimateExpectedReward("Randomized_default", "RandomizedStrategy\Default_RandomizedAdaptationStrategySampleSpace.csv")
    estimateExpectedReward("Randomized_perfect", "RandomizedStrategy\Perfect_RandomizedAdaptationStrategySampleSpace.csv")
    
    estimateExpectedReward("ReliabilityPrioritized_worst", "ReliabilityPrioritizedStrategy\Worst_ReliabilityPrioritizedStrategySampleSpace.csv")    
    estimateExpectedReward("ReliabilityPrioritized_default", "ReliabilityPrioritizedStrategy\Default_ReliabilityPrioritizedStrategySampleSpace.csv")
    estimateExpectedReward("ReliabilityPrioritized_perfect", "ReliabilityPrioritizedStrategy\Perfect_ReliabilityPrioritizedStrategySampleSpace.csv")