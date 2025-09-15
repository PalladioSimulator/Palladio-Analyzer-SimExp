import sys
import argparse
import csv
import collections

class SimulationResult:

    def _read_file(self, csv_file):
        fieldnames = ["TaskId", "Values", "Reward", "Error", "ExecutorId"]
        reader = csv.DictReader(csv_file, fieldnames=fieldnames, delimiter=";")
        next(reader) # skip header
        return reader

    def main(self):
        parser = argparse.ArgumentParser(prog="simulation_result", description="Analyses simulation results")
        parser.add_argument('infile', type=argparse.FileType('r'))
        args = parser.parse_args()

        print("Statistics:")
        content = self._read_file(args.infile)
        counter = collections.Counter()
        for row in content:
            counter[row['Error']] += 1
        for key, count in counter.items():
            if key:
                print("error: %s: %s" % (key, count))
        print("total entries: %s" % counter.total())

        return 0

if __name__ == '__main__':
    sr = SimulationResult()
    sys.exit(sr.main())

