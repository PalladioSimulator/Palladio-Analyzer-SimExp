import os
import subprocess
import rp_extract
import csv
import statistics
import re
from collections import defaultdict

def find_and_execute_rp(path):
    data_dict = defaultdict(list)
    with open('data.txt', 'w') as dataFile:
        for dirpath, dirnames, filenames in os.walk(root_dir):
            if "Usage_Scenario_overloadUsageScenario_Response_Time_Tuple.csv" in filenames:
               calc_mean(dirpath, data_dict)
            else:
                print("didn't find a matching csv file")
        print_results(dataFile, data_dict)




def calc_mean(path, data_dict):
    print("found a csv file: " + path)
    csv_path = os.path.join(path, "Usage_Scenario_overloadUsageScenario_Response_Time_Tuple.csv")
    print("\n")
    print("joined: " + csv_path)
    rt = "Response Time[s]"
    with open(csv_path, 'r') as file:
        reader = csv.DictReader(file)
        data = [row for row in reader]
        generation = re.search("gen_?(\d+)", path).group(1)
        response_times = [float(row[rt]) for row in data]
        data_dict[int(generation)].append((data, response_times))


    #print("Header: ", ", ".join(reader.fieldnames))
    #for row in data:
    #    print(", ".join([row[n] for n in reader.fieldnames]))



def print_results(dataFile, data_dict):
    sorted_data_dict = dict(sorted(data_dict.items()))
    for generation, tupels in sorted_data_dict.items():
        dataFile.write("Gen %s \n" % generation)
        for current_tuple in tupels:
            data = current_tuple[0]
            response_times = current_tuple[1]

            rt_min = min(response_times)
            rt_max = max(response_times)
            rt_avg = statistics.mean(response_times)
            rt_med = statistics.median(response_times)
            rt_stddev = statistics.pstdev(response_times)
            rt_var = statistics.pvariance(response_times)

            dataFile.write("row count:   %s \n" % len(data))
            dataFile.write("RT min:      %s \n" % rt_min)
            dataFile.write("RT max:      %s \n" % rt_max)
            dataFile.write("RT average:  %s \n" % rt_avg)
            dataFile.write("RT median:   %s \n" % rt_med)
            dataFile.write("RT std dev:  %s \n" % rt_stddev)
            dataFile.write("RT variance: %s \n" % rt_var)
            dataFile.write("-------------------------\n")
        dataFile.write("\n")


if __name__ == "__main__":
    root_dir = os.getcwd()
    print("searching in " + root_dir)
    find_and_execute_rp(root_dir)
    