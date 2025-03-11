import csv
import statistics


def main():
    with open("Usage_Scenario_overloadUsageScenario_Response_Time_Tuple.csv", 'r') as file:
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


if __name__ == '__main__':
    main()
