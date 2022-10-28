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
    estimateExpectedReward("NonAdaptive_chauffeur", "NonAdaptiveStrategy\Chauffeur_StaticSystemSimulationSampleSpace.csv")
    estimateExpectedReward("NonAdaptive_rambo", "NonAdaptiveStrategy\Rambo_StaticSystemSimulationSampleSpace.csv")
    estimateExpectedReward("NonAdaptive_perfect", "NonAdaptiveStrategy\Perfect_StaticSystemSimulationSampleSpace.csv")

    estimateExpectedReward("RandomizedFilter_worst", "RandomizedFilterStrategy\Worst_RandomizedFilterActivationStrategySampleSpace.csv")    
    estimateExpectedReward("RandomizedFilter_chauffeur", "RandomizedFilterStrategy\Chauffeur_RandomizedFilterActivationStrategySampleSpace.csv")
    estimateExpectedReward("RandomizedFilter_chauffeur_20000", "RandomizedFilterStrategy\Chauffeur_RandomizedFilterActivationStrategySampleSpace_20000.csv")
    estimateExpectedReward("RandomizedFilter_rambo", "RandomizedFilterStrategy\Rambo_RandomizedFilterActivationStrategySampleSpace.csv")
    estimateExpectedReward("RandomizedFilter_rambo_20000", "RandomizedFilterStrategy\Rambo_RandomizedFilterActivationStrategySampleSpace_20000.csv")
    estimateExpectedReward("RandomizedFilter_perfect", "RandomizedFilterStrategy\Perfect_RandomizedFilterActivationStrategySampleSpace.csv")

    estimateExpectedReward("ImgBlurMitigation_worst", "ImgBlurMitigationStrategy\Worst_ImageBlurMitigationStrategySampleSpace.csv")    
    estimateExpectedReward("ImgBlurMitigation_chauffeur", "ImgBlurMitigationStrategy\Chauffeur_ImageBlurMitigationStrategySampleSpace.csv")
    estimateExpectedReward("ImgBlurMitigation_rambo", "ImgBlurMitigationStrategy\Rambo_ImageBlurMitigationStrategySampleSpace.csv")
    estimateExpectedReward("ImgBlurMitigation_perfect", "ImgBlurMitigationStrategy\Perfect_ImageBlurMitigationStrategySampleSpace.csv")