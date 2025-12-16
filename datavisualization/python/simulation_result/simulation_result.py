import argparse
import csv
import collections
import statistics
import json

import tabulate


class SimulationResult:

    def _read_file(self, csv_file):
        fieldnames = ["TaskId", "Values", "Reward", "Error", "ExecutorId"]
        reader = csv.DictReader(csv_file, fieldnames=fieldnames, delimiter=";")
        next(reader) # skip header
        return reader

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
        #with result_file.open("r", encoding="utf-8") as f:
        #    result = json.load(f)
        #    return result
        return json.load(result_file)

    def _analyze_quality_attributes(self, args):
        task_result = self._read_result_task_file(args.task_file)
        task_id = task_result["result"]["id"]
        reward = task_result["result"]["reward"]
        runs = task_result["result"]["quality_measurements"]["runs"]
        energy_consumption = []
        packet_loss = []
        for run in runs:
            qas = run["quality_attributes"]
            energy_consumption.extend(qas["EnergyConsumption.props"])
            packet_loss.extend(qas["PacketLoss.props"])

        table_entries = []
        table_entries.append([
            task_id,
            min(energy_consumption), max(energy_consumption), statistics.mean(energy_consumption),
            min(packet_loss), max(packet_loss), statistics.mean(packet_loss),
            reward
        ])
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
        parser_workflow.add_argument('infile', type=argparse.FileType('r'))
        parser_workflow.set_defaults(func=self._analyze_workflows)

        parser_quality_attributes = subparsers.add_parser('qa', help='quality attributes analyzer')
        parser_quality_attributes.add_argument('task_file', type=argparse.FileType('r'))
        parser_quality_attributes.set_defaults(func=self._analyze_quality_attributes)

        args = parser.parse_args()

        args.func(args)


if __name__ == '__main__':
    sr = SimulationResult()
    sr.main()
