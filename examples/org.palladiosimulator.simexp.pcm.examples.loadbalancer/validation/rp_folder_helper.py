import os
import subprocess
import rp_extract
import csv
import statistics
import re

def find_and_execute_rp(path):
    with open('data.txt', 'w') as dataFile:
        for dirpath, dirnames, filenames in os.walk(root_dir):
            if "Usage_Scenario_overloadUsageScenario_Response_Time_Tuple.csv" in filenames:
               calc_mean(dirpath, dataFile)
            else:
                print("didn't find a matching csv file")



def calc_mean(path, dataFile):
    print("found a csv file: " + path)
    csv_path = os.path.join(path, "Usage_Scenario_overloadUsageScenario_Response_Time_Tuple.csv")
    print("\n")
    print("joined: " + csv_path)
    with open(csv_path, 'r') as file:
        reader = csv.DictReader(file)
        data = [row for row in reader]


    #print("Header: ", ", ".join(reader.fieldnames))
    #for row in data:
    #    print(", ".join([row[n] for n in reader.fieldnames]))

    rt = "Response Time[s]"
    response_times = [float(row[rt]) for row in data]

    generation = re.search("gen_?(\d+)", path).group(1)
    rt_min = min(response_times)
    rt_max = max(response_times)
    rt_avg = statistics.mean(response_times)
    rt_med = statistics.median(response_times)
    rt_stddev = statistics.pstdev(response_times)
    rt_var = statistics.pvariance(response_times)

    dataFile.write("Gen %s \n" % generation)
    dataFile.write("row count:   %s \n" % len(data))
    dataFile.write("RT min:      %s \n" % rt_min)
    dataFile.write("RT max:      %s \n" % rt_max)
    dataFile.write("RT average:  %s \n" % rt_avg)
    dataFile.write("RT median:   %s \n" % rt_med)
    dataFile.write("RT std dev:  %s \n" % rt_stddev)
    dataFile.write("RT variance: %s \n" % rt_var)
    dataFile.write("\n\n")


if __name__ == "__main__":
    root_dir = os.getcwd()
    print("searching in " + root_dir)
    find_and_execute_rp(root_dir)
    