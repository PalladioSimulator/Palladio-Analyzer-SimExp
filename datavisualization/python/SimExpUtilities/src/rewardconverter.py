'''
Created on 09.12.2021

@author: rapp
'''
import hashlib 

from converter import Converter

class RewardConverter(Converter):
    def convertRows(self, rows):
        converted = []
        headerLine = rows[0]
        rewardCount = len(headerLine) // 2
        effectiveRows = rows[1:]
        
        newFieldNames = []
        for i in range(rewardCount):
            newFieldNames.append("id%s" % i)
            newFieldNames.append("reward%s" % i)
        
        for row in effectiveRows:
            newRow = {}
            for i in range(rewardCount):
                sample = row[i*2]
                reward = row[i*2+1]
                sampleHash = self._calcHash(sample)
                newRow["id%s" % i] = sampleHash
                newRow["reward%s" % i] = reward 
            converted.append(newRow)
        return newFieldNames, converted

    def _calcHash(self, dataStr):
        return hashlib.md5(dataStr.encode("UTF-8")).hexdigest()
