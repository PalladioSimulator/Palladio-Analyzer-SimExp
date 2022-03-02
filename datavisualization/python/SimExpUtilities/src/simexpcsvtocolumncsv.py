import argparse
import sys
import csv

from rewardconverter import RewardConverter
from metricsconverter import MetricsConverter

def main():
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument("-i", "--input", type=str, help="input CSV file", required=True)
        parser.add_argument("-o", "--output", type=str, help="output CSV file", required=True)
        parser.add_argument("-c", "--convertertype", type=str, help="converter type used to convert values from CSV file", choices=['reward', 'metrics'], required=True)
        args = parser.parse_args()
        
        print(f"input: {args.input}, output: {args.output}")
        rows = readCSV(args.input)

        converter = None        
        if args.convertertype == 'reward':
            converter = RewardConverter()
        elif args.convertertype == 'metrics':
            converter = MetricsConverter()
        else:
            print("Unknown converter type\n")
            sys.exit(2)
        
        newFieldNames, convertedRows = converter.convertRows(rows)
        writeCSV(args.output, newFieldNames, convertedRows)
        print("converted\n")
    except argparse.ArgumentTypeError as err:
        print(err)
        sys.exit(2)

def readCSV(fileName):
    with open(fileName, newline='', encoding='utf-8') as csvfile:
        reader = csv.reader(csvfile, delimiter=";")
        rows = []
        for row in reader:
            rows.append(row)
        return rows

def writeCSV(fileName, fieldNames, dataRows):
    with open(fileName, 'w', newline='', encoding='utf-8') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldNames)
        writer.writeheader()
        for row in dataRows:
            writer.writerow(row)
    
if __name__ == "__main__":
    main()
        