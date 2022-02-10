'''
Created on 09.12.2021

@author: rapp
'''

import hashlib 

from converter import Converter

class MetricsConverter(Converter):
    '''
    classdocs
    '''

    def convertRows(self, rows):
        converted = []
        newFieldNames = ["id", "metricspec", "responsetime", "successrate"]
        
        effectiveRows = rows[1:]
        
        for row in effectiveRows:
            newRow = {}
            sample = row[0]
            sampleId = self.extractIdFromSample(sample)
            newRow["id"] = sampleId
            metric = row[2]
            metricSpec = self.extractMetricSpecFromMetric(metric)
            newRow["metricspec"] = metricSpec
            responsetime = self.extractResponsetimeFromMetric(metric)
            newRow["responsetime"] = responsetime
            successrate = self.extractSuccessRateFromMetric(metric)
            newRow["successrate"] = successrate
            converted.append(newRow)
        
        return newFieldNames, converted
    
    def extractIdFromSample(self, sample):
        sampleHash = self._calcHash(sample)
        return sampleHash
        
    def extractMetricSpecFromMetric(self, metric):
        tokens = self.splitMetric(metric)
        for token in tokens:
            entries = self.splitToken(token)
            entriesDict = self.convertEntriesToDict(entries)
            specStr = self.findEntryWithSpec(entriesDict, "Response Time")
            if specStr:
                result = self.rchop(specStr, "Response Time")
                result = result.rstrip("_")
                return result
        return None
       
    def rchop(self, s, suffix):
        if suffix and s.endswith(suffix):
            return s[:-len(suffix)]
        return s

    def extractResponsetimeFromMetric(self, metric):
        tokens = self.splitMetric(metric)
        for token in tokens:
            entries = self.splitToken(token)
            entriesDict = self.convertEntriesToDict(entries)
            specStr = self.findEntryWithSpec(entriesDict, "Response Time")
            if specStr:
                responseTime = entriesDict["value"]
                return responseTime
        return None

    def extractSuccessRateFromMetric(self, metric):
        tokens = self.splitMetric(metric)
        for token in tokens:
            entries = self.splitToken(token)
            entriesDict = self.convertEntriesToDict(entries)
            specStr = self.findEntryWithSpec(entriesDict, "Execution Result Type")
            if specStr:
                responseTime = entriesDict["value"]
                return responseTime
        return None

                    
    def findEntryWithSpec(self, entriesDict, searchEnd):
        if not "spec" in entriesDict:
            return None
        
        specStr = entriesDict["spec"]
        if not specStr.endswith(searchEnd):
            return None
        return specStr
    
    def splitMetric(self, metric):
        metric = metric.strip()
        metric = metric.lstrip("[")
        metric = metric.rstrip("]")
        tokens = metric.split("|")
        return tokens
    
    def splitToken(self, token):
        token = token.strip()
        token = token.lstrip("{")
        token = token.rstrip("}")
        entries = token.split(",")
        entries = [e.strip() for e in entries]
        return entries
        
    def convertEntriesToDict(self, entries):
        convertedDict = {}
        for entry in entries:
            dictEntry = self.convertEntryToDict(entry)
            convertedDict.update(dictEntry)
        return convertedDict
            
    def convertEntryToDict(self, entry):
        tokens = entry.split(":", 1)
        tokens = [e.strip() for e in tokens]
        return {tokens[0]: tokens[1]}

    def _calcHash(self, dataStr):
        return hashlib.md5(dataStr.encode("UTF-8")).hexdigest()
