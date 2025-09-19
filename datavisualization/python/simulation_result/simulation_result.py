import argparse
import csv
import collections

import tabulate

class SimulationResult:

    def _read_file(self, csv_file):
        fieldnames = ["TaskId", "Values", "Reward", "Error", "ExecutorId"]
        reader = csv.DictReader(csv_file, fieldnames=fieldnames, delimiter=";")
        next(reader) # skip header
        return reader

    def _get_key(self, error):
        if error:
            return error
        return 'Success'

    def main(self):
        parser = argparse.ArgumentParser(prog="simulation_result", description="Analyses simulation results")
        parser.add_argument('infile', type=argparse.FileType('r'))
        args = parser.parse_args()

        print("Statistics:")
        content = self._read_file(args.infile)
        counter = collections.Counter()
        for row in content:
            key = self._get_key(row['Error'])
            counter[key] += 1
        table_entries = []
        for key, count in counter.items():
            entry = [key, count]
            table_entries.append(entry)
        table_entries.append(tabulate.SEPARATING_LINE)
        table_entries.append(["Total", counter.total()])
        print(tabulate.tabulate(table_entries, headers=['Result', 'Count']))

if __name__ == '__main__':
    sr = SimulationResult()
    sr.main()
