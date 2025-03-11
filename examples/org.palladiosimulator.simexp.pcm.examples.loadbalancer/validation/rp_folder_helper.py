import os
import subprocess
import rp_extract
import csv
import statistics

def find_and_execute_rp_recursive(path):
    for dirpath, dirnames, filenames in os.walk(root_dir):
        if "Usage_Scenario_overloadUsageScenario_Response_Time_Tuple.csv" in filenames:
            # rp_path = Path("C:\\dev\\workspaces\\palladio\\Palladio-Workspace-Dev\\Palladio-Analyzer-SimExp\\examples\\org.palladiosimulator.simexp.pcm.examples.loadbalancer\\validation\\rp_extract.py")
            calc_mean(dirpath)
        else:
            print("didn't find a matching csv file")



def calc_mean(path):
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

    rt_min = min(response_times)
    rt_max = max(response_times)
    rt_avg = statistics.mean(response_times)
    rt_med = statistics.median(response_times)
    rt_stddev = statistics.pstdev(response_times)
    rt_var = statistics.pvariance(response_times)

    print("row count:   %s " % len(data))
    print("RT min:      %s " % rt_min)
    print("RT max:      %s " % rt_max)
    print("RT average:  %s " % rt_avg)
    print("RT median:   %s " % rt_med)
    print("RT std dev:  %s " % rt_stddev)
    print("RT variance: %s " % rt_var)


if __name__ == "__main__":
    root_dir = os.getcwd()
    print("searching in " + root_dir)
    find_and_execute_rp_recursive(root_dir)
    