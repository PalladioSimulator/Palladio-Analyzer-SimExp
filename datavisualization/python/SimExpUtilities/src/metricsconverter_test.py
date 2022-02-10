'''
Created on 09.12.2021

@author: rapp
'''
import unittest
import hashlib

from metricsconverter import MetricsConverter


class TestMetricsConverter(unittest.TestCase):
    def setUp(self):
        self.converter = MetricsConverter()

    def testConvertRowName(self):
        rawRows = []
        rawRows.append(["Id", "Environmental state before", "Quantified state current"])
        sample = "Samples: _61415588"
        rawRows.append([sample, "before", "[{value: 0.0, spec: anySpec}|{value: 0.1, spec: Usage Scenario: overloadUsageScenario_Response Time}|{value: 1.0, spec: Usage Scenario: overloadUsageScenario_Execution Result Type}]"])
        
        newFieldNames, convertedRows = self.converter.convertRows(rawRows)
        
        self.assertEqual(4, len(newFieldNames))
        self.assertEqual("id", newFieldNames[0])
        self.assertEqual("metricspec", newFieldNames[1])
        self.assertEqual("responsetime", newFieldNames[2])
        self.assertEqual("successrate", newFieldNames[3])
        self.assertEqual(1, len(convertedRows))
        expectedId = self._calcHash(sample)
        self.assertEqual({'id': expectedId, 'metricspec':'Usage Scenario: overloadUsageScenario', 'responsetime':'0.1', 'successrate' : '1.0'}, convertedRows[0])

    def testExtractIdFromSample(self):
        sample = "Samples: [(Variable: GRV_StaticInstance_VaryingWorkload, Value: 0.375),(Variable: GRV_StaticInstance_ServerNode1, Value: available),(Variable: GRV_StaticInstance_ServerNode2, Value: available)])_61415588_EmptyReconf_Samples: [(Variable: GRV_StaticInstance_VaryingWorkload, Value: 0.325),(Variable: GRV_StaticInstance_ServerNode1, Value: available),(Variable: GRV_StaticInstance_ServerNode2, Value: available)])_61415588"
        
        actualId = self.converter.extractIdFromSample(sample)

        expectedId = self._calcHash(sample)        
        self.assertEqual(expectedId, actualId)

    def testExtractIdFromSampleNegativ(self):
        sample = "Samples: [(Variable: GRV_StaticInstance_ServerNode2, Value: available)])_-61415588"
        
        actualId = self.converter.extractIdFromSample(sample)
        
        expectedId = self._calcHash(sample)
        self.assertEqual(expectedId, actualId)

    def testExtractMetricSpecFromMetric(self):
        metric = "[{value: 1.0, spec: Usage Scenario: overloadUsageScenario_Execution Result Type}|{value: Infinity, spec: Server2_State of Active Resource}|{value: Infinity, spec: Server1_State of Active Resource}|{value: 0.30000000000000226, spec: Usage Scenario: overloadUsageScenario_Response Time}]"
        
        actualSpec = self.converter.extractMetricSpecFromMetric(metric)
        
        self.assertEqual("Usage Scenario: overloadUsageScenario", actualSpec)
        
    def testSplitMetric(self):
        metric = "[{value: 1.0, spec: Usage Scenario: overloadUsageScenario_Execution Result Type}|{value: Infinity, spec: Server2_State of Active Resource}]"
         
        actualTokens = self.converter.splitMetric(metric)
        
        expectedTokens = ["{value: 1.0, spec: Usage Scenario: overloadUsageScenario_Execution Result Type}", "{value: Infinity, spec: Server2_State of Active Resource}"]
        self.assertEqual(expectedTokens, actualTokens)
        
    def testSplitToken(self):
        token = "{value: 1.0, spec: Usage Scenario: overloadUsageScenario_Execution Result Type}"
        
        actualEntries = self.converter.splitToken(token)
        
        expectedEntries = ["value: 1.0", "spec: Usage Scenario: overloadUsageScenario_Execution Result Type"]
        self.assertEqual(expectedEntries, actualEntries)
        
    def testConvertEntriesToDict(self):
        entries = ["value: 1.0", "spec: Usage"]
        
        actualDict = self.converter.convertEntriesToDict(entries)
        
        self.assertEqual({"value": "1.0", "spec": "Usage"}, actualDict)
        
    def testConvertEntryToDictNum(self):
        entry = "value: 1.0"
        
        actualEntryDict = self.converter.convertEntryToDict(entry)
        
        self.assertEqual({"value": "1.0"}, actualEntryDict)

    def testConvertEntryToDictString(self):
        entry = "spec: Usage Scenario: overloadUsageScenario_Execution Result Type"
        
        actualEntryDict = self.converter.convertEntryToDict(entry)
        
        self.assertEqual({"spec": "Usage Scenario: overloadUsageScenario_Execution Result Type"}, actualEntryDict)
        
    def testFindEntryWithSpecWithValue(self):
        entriesDict = {"value": "1.0", "spec": "Usage Scenario: overloadUsageScenario_Response Time"}
        
        actualValue = self.converter.findEntryWithSpec(entriesDict, "Response Time")
        
        self.assertEqual("Usage Scenario: overloadUsageScenario_Response Time", actualValue)

    def _calcHash(self, strData):
        return hashlib.md5(strData.encode("UTF-8")).hexdigest()

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()