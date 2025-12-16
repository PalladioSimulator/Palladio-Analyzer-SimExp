import argparse
import csv
import collections
import statistics
import json
from pathlib import Path

import tabulate


class SimulationResult:

    def _read_file(self, csv_file):
        fieldnames = ["TaskId", "Values", "Reward", "Error", "ExecutorId"]
        with csv_file.open("r", encoding="utf-8") as f:
            reader = csv.DictReader(f, fieldnames=fieldnames, delimiter=";")
            next(reader)    # skip header
            for row in reader:
                yield row

    def _get_key(self, error):
        if not error:
            return 'Success'
        if error.startswith("Max delivery count reached"):
            return "Max delivery count reached"
        if error.startswith("not found: /workspace/") and error.endswith("result.json"):
            return "not found: result.json"
        return error

    def _analyze_workflows(self, args):
        content = self._read_file(args.infile)
        counter = collections.Counter()
        for row in content:
            key = self._get_key(row['Error'])
            counter[key] += 1
        table_entries = []
        total = counter.total()
        for key, count in counter.most_common():
            rel = count / total
            entry = [key, count, rel]
            table_entries.append(entry)
        table_entries.append(tabulate.SEPARATING_LINE)
        table_entries.append(["Total", counter.total(), 1])
        table_str = tabulate.tabulate(table_entries, headers=['Result', 'Count', 'Rel'], floatfmt=".2%")
        print(table_str)


    def _read_result_task_file(self, result_file):
        with result_file.open("r", encoding="utf-8") as f:
            result = json.load(f)
            return result

    def _analyze_task_result(self, result_file):
        task_result = self._read_result_task_file(result_file)
        task_id = task_result["result"]["id"]
        reward = task_result["result"]["reward"]
        runs = task_result["result"]["quality_measurements"]["runs"]
        energy_consumption = []
        packet_loss = []
        for run in runs:
            qas = run["quality_attributes"]
            energy_consumption.extend(qas["EnergyConsumption.props"])
            packet_loss.extend(qas["PacketLoss.props"])

        result = (
            task_id,
            min(energy_consumption), max(energy_consumption), statistics.mean(energy_consumption),
            min(packet_loss), max(packet_loss), statistics.mean(packet_loss),
            reward
        )
        return result

    def _analyze_quality_attributes(self, args):
        all_stats = []
        for result_file in args.task_file:
            stats = self._analyze_task_result(result_file)
            all_stats.append(stats)

        table_entries = []
        for stats in all_stats:
            table_entries.append(stats)
        table_str = tabulate.tabulate(table_entries,
                                      headers=['ID', 'Energy Min', 'Energy Max', 'Energy Average',
                                      'Packet Loss Min', 'Packet Loss Max', 'Packet Loss Average',
                                      'Reward'],
                                      tablefmt="simple"
                                      )
        print(table_str)

    def main(self):
        parser = argparse.ArgumentParser(prog="simulation_result", description="Analyses simulation results")
        subparsers = parser.add_subparsers(required=True, help='available subcommands')

        parser_workflow = subparsers.add_parser('workflow', help='workflow analyzer')
        parser_workflow.add_argument('infile', type=Path)
        parser_workflow.set_defaults(func=self._analyze_workflows)

        parser_quality_attributes = subparsers.add_parser('qa', help='quality attributes analyzer')
        parser_quality_attributes.add_argument('task_file', type=Path, nargs='+')
        parser_quality_attributes.set_defaults(func=self._analyze_quality_attributes)

        args = parser.parse_args()

        args.func(args)


if __name__ == '__main__':
    sr = SimulationResult()
    sr.main()
