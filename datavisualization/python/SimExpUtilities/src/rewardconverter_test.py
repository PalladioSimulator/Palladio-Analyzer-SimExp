'''
Created on 09.12.2021

@author: rapp
'''
import unittest
import hashlib 

from rewardconverter import RewardConverter

class TestRewardConverter(unittest.TestCase):
    def testConvertRowsSingle(self):
        rewardConverter = RewardConverter()
        rawRows = []
        rawRows.append(["Point in time:  0", "Reward"])
        sample = "Sample_1"
        rawRows.append([sample, "0.0"])
        
        newFieldNames, convertedRows = rewardConverter.convertRows(rawRows)
        
        self.assertEqual(2, len(newFieldNames))
        self.assertEqual("id0", newFieldNames[0])
        self.assertEqual("reward0", newFieldNames[1])
        self.assertEqual(1, len(convertedRows))
        sampleHash = self._calcHash(sample)
        self.assertEqual({'id0': sampleHash, 'reward0':'0.0'}, convertedRows[0])

    def testConvertRowsDouble(self):
        rewardConverter = RewardConverter()
        rawRows = []
        rawRows.append(["Point in time:  0", "Reward", "Point in time:  1", "Reward"])
        sample1 = "Sample_1"
        sample2 = "Sample_2"
        rawRows.append([sample1, "0.0", sample2, "0.1"])
        
        newFieldNames, convertedRows = rewardConverter.convertRows(rawRows)
        
        self.assertEqual(4, len(newFieldNames))
        self.assertEqual("id0", newFieldNames[0])
        self.assertEqual("reward0", newFieldNames[1])
        self.assertEqual("id1", newFieldNames[2])
        self.assertEqual("reward1", newFieldNames[3])
        self.assertEqual(1, len(convertedRows))
        sampleHash1 = self._calcHash(sample1)
        sampleHash2 = self._calcHash(sample2)
        self.assertEqual({'id0': sampleHash1, 'reward0':'0.0', 'id1': sampleHash2, 'reward1':'0.1'}, convertedRows[0])

    def testConvertRowsTwoSamplesSingleReward(self):
        rewardConverter = RewardConverter()
        rawRows = []
        rawRows.append(["Point in time:  0", "Reward"])
        sample1 = "Sample_1"
        sample2 = "Sample_2"
        rawRows.append([sample1, "0.0"])
        rawRows.append([sample2, "1.0"])
        
        newFieldNames, convertedRows = rewardConverter.convertRows(rawRows)
        
        self.assertEqual(2, len(newFieldNames))
        self.assertEqual("id0", newFieldNames[0])
        self.assertEqual("reward0", newFieldNames[1])
        self.assertEqual(2, len(convertedRows))
        sampleHash1 = self._calcHash(sample1)
        self.assertEqual({'id0': sampleHash1, 'reward0':'0.0'}, convertedRows[0])
        sampleHash2 = self._calcHash(sample2)
        self.assertEqual({'id0': sampleHash2, 'reward0':'1.0'}, convertedRows[1])

    
    def testConvertRowsTwoSamplesWithTwoRewards(self):
        rewardConverter = RewardConverter()
        rawRows = []
        rawRows.append(["Point in time:  0", "Reward", "Point in time:  1", "Reward"])
        sample1 = "Sample_1"
        sample2 = "Sample_2"
        sample3 = "Sample_3"
        sample4 = "Sample_4"
        rawRows.append([sample1, "0.0", sample2, "0.1"])
        rawRows.append([sample3, "1.0", sample4, "1.1"])
        
        newFieldNames, convertedRows = rewardConverter.convertRows(rawRows)
        
        self.assertEqual(4, len(newFieldNames))
        self.assertEqual("id0", newFieldNames[0])
        self.assertEqual("reward0", newFieldNames[1])
        self.assertEqual("id1", newFieldNames[2])
        self.assertEqual("reward1", newFieldNames[3])
        self.assertEqual(2, len(convertedRows))
        sampleHash1 = self._calcHash(sample1)
        sampleHash2 = self._calcHash(sample2)
        self.assertEqual({'id0': sampleHash1, 'reward0':'0.0', 'id1': sampleHash2, 'reward1':'0.1'}, convertedRows[0])
        sampleHash3 = self._calcHash(sample3)
        sampleHash4 = self._calcHash(sample4)
        self.assertEqual({'id0': sampleHash3, 'reward0':'1.0', 'id1': sampleHash4, 'reward1':'1.1'}, convertedRows[1])

    def _calcHash(self, strData):
        return hashlib.md5(strData.encode("UTF-8")).hexdigest()
    
if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testConvertRows']
    unittest.main()