import argparse
from pathlib import Path
import json


def chunks(lst, n):
    """Yield successive n-sized chunks from lst."""
    for i in range(0, len(lst), n):
        yield lst[i:i + n]


def read_sample(sample_file):
    sample_data = read_json_file(sample_file)
    return {
        "value": sample_data["result"],
        "property_name": sample_data["kind"],
    }


def read_json_file(json_file):
    with json_file.open("r", encoding="utf-8") as f:
        result = json.load(f)
        return result


def main():
    parser = argparse.ArgumentParser(prog="property_extractor", description="Extracts QA properties from PRISM results")
    default = ' (default: %(default)s)'
    parser.add_argument('--input-dir', type=Path, help="PRISM folder containing results")
    parser.add_argument('--sample-count', default=96, help="sample count per run" + default)
    parser.add_argument('-r', '--result', type=Path, required=True, help="json result file")
    args = parser.parse_args()

    files = [f for f in args.input_dir.iterdir() if f.is_file()]
    result_files = [file for file in files if file.suffix == ".json"]
    print("found result files: %d" % len(result_files))
    sorted_results = sorted(result_files, key=lambda x: x.stem)

    run_results = [c for c in chunks(sorted_results, args.sample_count * 2)]
    print("found runs:         %d" % len(run_results))

    runs = []
    for i, samples in enumerate(run_results):
        #print("run: %d" % (i + 1))
        qas = {}
        for sample_pair in chunks(samples, 2):
            for sample in sample_pair:
                qa = read_sample(sample)
                property_name = qa["property_name"]
                values = qas.get(property_name, [])
                values.append(qa["value"])
                qas[property_name] = values
        runs.append(qas)

    with args.result.open("w") as f:
        json.dump(runs, f, indent=2)


if __name__ == "__main__":
    main()
