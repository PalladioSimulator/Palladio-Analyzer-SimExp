import argparse
import csv
import collections
import statistics
import json
from pathlib import Path

import tabulate

from prism_property import PrismKind, identify_prism


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

        headers = ['ID', 'Energy Min', 'Energy Max', 'Energy Average',
                   'Packet Loss Min', 'Packet Loss Max', 'Packet Loss Average',
                   'Reward']

        if args.result:
            with args.result.open("w", encoding="utf-8") as f:
                writer = csv.DictWriter(f, fieldnames=headers)
                writer.writeheader()
                for entry in table_entries:
                    writer.writerow({'ID': entry[0],
                                     'Energy Min': entry[1],
                                     'Energy Max': entry[2],
                                     'Energy Average': entry[3],
                                     'Packet Loss Min': entry[4],
                                     'Packet Loss Max': entry[5],
                                     'Packet Loss Average': entry[6],
                                     'Reward': entry[7],
                                     })

        table_entries.append(tabulate.SEPARATING_LINE)
        table_entries.append(["total",
                              min([stats[1] for stats in all_stats]), max([stats[2] for stats in all_stats]), statistics.mean([stats[3] for stats in all_stats]),
                              min([stats[4] for stats in all_stats]), max([stats[5] for stats in all_stats]),
                              statistics.mean([stats[6] for stats in all_stats]),
                              None
                              ])

        table_str = tabulate.tabulate(table_entries,
                                      headers=headers,
                                      tablefmt="simple"
                                      )
        print(table_str)

    def _extract_quality_attributes(self, args):
        headers = ['ID', 'Values', 'Run', 'Sample', 'Energy', 'Packet Loss']
        with args.result.open("w", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=headers)
            writer.writeheader()
            for result_file in args.task_file:
                self._process_task_file(writer, result_file)

    def _process_task_file(self, writer, task_file):
        print("processing: %s" % task_file.name)
        task_result = self._read_result_task_file(task_file)
        task_id = task_result["result"]["id"]
        optimizables = ["%s=%s" % (key, value) for key, value in task_result["optimizables"].items()]
        values = ",".join(optimizables)
        runs = task_result["result"]["quality_measurements"]["runs"]
        for r, run in enumerate(runs):
            qas = run["quality_attributes"]
            energy_list = qas["EnergyConsumption.props"]
            pl_list = qas["PacketLoss.props"]
            for s, energy in enumerate(energy_list):
                packet_loss = pl_list[s]
                writer.writerow({'ID': task_id,
                                 'Values': values,
                                 'Run': r,
                                 'Sample': s,
                                 'Energy': energy,
                                 'Packet Loss': packet_loss,
                                 })

    def _read_prism_property(self, property_file):
        with property_file.open("r", encoding="utf-8") as f:
            content = f.readline()
            return identify_prism(content)

    def _read_prism_result(self, result_file):
        with result_file.open("r", encoding="utf-8") as f:
            next(f)
            value = float(next(f))
            return value

    def _analyze_prism(self, args):
        energy_consumption = []
        packet_loss = []
        for property_file in args.prism_property_file:
            kind = self._read_prism_property(property_file)
            result_file = property_file.with_suffix(".result")
            value = self._read_prism_result(result_file)
            if kind == PrismKind.ENERGY_CONSUMPTION:
                energy_consumption.append(value)
            else:
                packet_loss.append(value)

        table_entries = []
        table_entries.append([
            len(energy_consumption),
            min(energy_consumption), max(energy_consumption), statistics.mean(energy_consumption),
            min(packet_loss), max(packet_loss), statistics.mean(packet_loss),
        ])

        table_str = tabulate.tabulate(table_entries,
                                      headers=['Count', 'Energy Min', 'Energy Max', 'Energy Average',
                                      'Packet Loss Min', 'Packet Loss Max', 'Packet Loss Average'],
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
        parser_quality_attributes.add_argument('-r', '--result', type=Path)
        parser_quality_attributes.set_defaults(func=self._analyze_quality_attributes)

        parser_quality_attributes_raw = subparsers.add_parser('qa_raw', help='raw quality attributes extractor')
        parser_quality_attributes_raw.add_argument('task_file', type=Path, nargs='+')
        parser_quality_attributes_raw.add_argument('-r', '--result', type=Path, required=True)
        parser_quality_attributes_raw.set_defaults(func=self._extract_quality_attributes)

        parser_prism = subparsers.add_parser('prism', help='prism result analyzer')
        parser_prism.add_argument('prism_property_file', type=Path, nargs='+')
        parser_prism.set_defaults(func=self._analyze_prism)

        args = parser.parse_args()

        args.func(args)


if __name__ == '__main__':
    sr = SimulationResult()
    sr.main()
