from pathlib import Path
import json

input_dir = Path(r"prism")

def chunks(lst, n):
    """Yield successive n-sized chunks from lst."""
    for i in range(0, len(lst), n):
        yield lst[i:i + n]

def extract_property_name(line):
    # Rmax=? [ F "EnergyConsumption" ]
    # P=? [ F "Packetloss" ]
    name = line.split('"')[1]
    return name

def read_property_name(prism_folder, result_number):
    property_file = prism_folder / ("prism_%s.properties" % result_number)
    with open(property_file) as f:
        line = next(f)
        property_name = extract_property_name(line)
        return property_name

def read_sample(sample_file):
    with open(sample_file) as f:
        next(f)
        value_str = next(f)
        value = float(value_str)

    result_number = sample_file.stem.split("_")[1]
    property_name = read_property_name(sample_file.parent, result_number)
    return {
        "value": value,
        "property_name": property_name,
    }

def main():
    types = ('*.log') #('*.model', '*.properties', '*.result')  # the tuple of file types
    files = [f for f in input_dir.iterdir() if f.is_file()]
    result_files = [file for file in files if file.suffix == ".result"]
    sorted_results = sorted(result_files, key=lambda x: x.stem)
    sample_count = 96
    runs = []
    for i, samples in enumerate(chunks(sorted_results, sample_count * 2)):
        print("run: %d" % (i + 1))
        qas = {}
        for sample_pair in chunks(samples, 2):
            for sample in sample_pair:
                qa = read_sample(sample)
                property_name = qa["property_name"]
                values = qas.get(property_name, [])
                values.append(qa["value"])
                qas[property_name] = values
        runs.append(qas)

    with open("result.json", "w") as f:
        print(json.dump(runs, f, indent=2))

if __name__ == "__main__":
    main()