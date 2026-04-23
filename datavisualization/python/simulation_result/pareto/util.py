from pathlib import Path


def validate_resource_folder(folder: Path):
    if not folder.is_dir():
        raise ValueError("not a directory: %s", folder)
    if not (folder / "pareto_front.json").is_file():
        raise ValueError("missing final pareto_front.json file in: %s" % folder)
    if not (folder / "generations").is_dir():
        raise ValueError("missing generations folder in: %s" % folder)
