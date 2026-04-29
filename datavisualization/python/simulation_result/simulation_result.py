import argparse
import csv
import collections
import statistics
import json
from pathlib import Path

import tabulate

from prism_property import PrismKind, identify_prism
from pareto import ParetoFrontExtractor
from pareto import ParetoFrontRanking, ParetoFittest, ParetoFrontComparator
from pareto import ParetoFrontCreator, ApproxParetoFrontCreator, CumulativeParetoFrontCreator
from pareto import ParetoIO, ApproximatedParetoIO, CumulatedParetoIO


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

    def _read_json_file(self, json_file):
        with json_file.open("r", encoding="utf-8") as f:
            result = json.load(f)
            return result

    def _analyze_task_result(self, result_file):
        task_result = self._read_json_file(result_file)
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
            min(energy_consumption), max(energy_consumption), statistics.mean(energy_consumption), statistics.stdev(energy_consumption),
            min(packet_loss), max(packet_loss), statistics.mean(packet_loss), statistics.stdev(packet_loss),
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

        headers = ['ID',
                   'Energy Min', 'Energy Max', 'Energy Average', 'Energy SD',
                   'Packet Loss Min', 'Packet Loss Max', 'Packet Loss Average', 'Packet Loss SD',
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
                                     'Energy SD': entry[4],
                                     'Packet Loss Min': entry[5],
                                     'Packet Loss Max': entry[6],
                                     'Packet Loss Average': entry[7],
                                     'Packet Loss SD': entry[8],
                                     'Reward': entry[9],
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
        task_result = self._read_json_file(task_file)
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

    def _pareto_extract(self, args):
        if args.approximated:
            pareto_io = ApproximatedParetoIO()
        elif args.cumulative:
            pareto_io = CumulatedParetoIO()
        else:
            pareto_io = ParetoIO()
        extractor = ParetoFrontExtractor(pareto_io)
        extractor.extract(args.resource, args.result)

    def _pareto_rank(self, args):
        ranking = ParetoFrontRanking()
        ranking.rank_pareto_fronts(args.resource, args.result, args.cumulative)

    def _pareto_fittest(self, args):
        fittest = ParetoFittest()
        fittest.pareto_fittest_check(args.resource)

    def _pareto_create(self, args):
        if args.approximated:
            creator = ApproxParetoFrontCreator()
        elif args.cumulative:
            creator = CumulativeParetoFrontCreator()
        else:
            creator = ParetoFrontCreator()
        creator.create_fronts(args.resource, args.target)

    def _pareto_compare(self, args):
        comparator = ParetoFrontComparator()
        comparator.compare_fronts(args.resource, args.target)

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

        parser_pareto = subparsers.add_parser('pareto', help='pareto commands')
        pareto_subparsers = parser_pareto.add_subparsers(required=True, help='available pareto subcommands')

        parser_pareto_extractor = pareto_subparsers.add_parser('extract', help='pareto front extractor')
        parser_pareto_extractor.add_argument('resource', type=Path, help="simulation resource folder")
        parser_pareto_extractor.add_argument('-r', '--result', type=Path, required=True, help="result CSV file")
        parser_pareto_extractor_group = parser_pareto_extractor.add_mutually_exclusive_group()
        parser_pareto_extractor_group.add_argument("--approximated", action="store_true", help="add approximated pareto front")
        parser_pareto_extractor_group.add_argument("--cumulative", action="store_true", help="use cummulative approximated pareto fronts")
        parser_pareto_extractor.set_defaults(func=self._pareto_extract)

        parser_pareto_rank = pareto_subparsers.add_parser('rank', help='pareto front ranking')
        parser_pareto_rank.add_argument('resource', type=Path, help="simulation resource folder")
        parser_pareto_rank.add_argument('-r', '--result', type=Path, help="result folder for rank CSV file")
        parser_pareto_rank.add_argument("--cumulative", action="store_true", help="use cummulative approximated pareto fronts")
        parser_pareto_rank.set_defaults(func=self._pareto_rank)

        parser_pareto_fittest = pareto_subparsers.add_parser('fittest', help='checks if the fittest is in the pareto front')
        parser_pareto_fittest.add_argument('resource', type=Path, help="simulation resource folder")
        parser_pareto_fittest.set_defaults(func=self._pareto_fittest)

        parser_pareto_create = pareto_subparsers.add_parser('create', help='create pareto fronts from existing resources')
        parser_pareto_create.add_argument('--target', type=Path, default=Path("resources"), help="target path for pareto fronts")
        parser_pareto_create.add_argument('resource', type=Path, help="simulation resource folder")
        parser_pareto_create_group = parser_pareto_create.add_mutually_exclusive_group()
        parser_pareto_create_group.add_argument("--approximated", action="store_true", help="create approximated pareto front over all generations")
        parser_pareto_create_group.add_argument("--cumulative", action="store_true", help="create cummulative approximated pareto fronts")
        parser_pareto_create.set_defaults(func=self._pareto_create)

        parser_pareto_compare = pareto_subparsers.add_parser('compare', help='compare created pareto fronts with existing resources')
        parser_pareto_compare.add_argument('--target', type=Path, default=Path("resources"), help="path for created pareto fronts")
        parser_pareto_compare.add_argument('resource', type=Path, help="simulation resource folder")
        parser_pareto_compare.set_defaults(func=self._pareto_compare)

        args = parser.parse_args()

        args.func(args)


if __name__ == '__main__':
    sr = SimulationResult()
    sr.main()
