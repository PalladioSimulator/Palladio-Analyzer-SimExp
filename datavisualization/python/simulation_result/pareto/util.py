from pathlib import Path


def validate_resource_folder(folder: Path, with_final: bool = True):
    if not folder.is_dir():
        raise ValueError("not a directory: %s", folder)
    if with_final and not (folder / "pareto_front.json").is_file():
        raise ValueError("missing final pareto_front.json file in: %s" % folder)
    if not (folder / "generations").is_dir():
        raise ValueError("missing generations folder in: %s" % folder)
    if not (folder / "kubernetes").is_dir():
        raise ValueError("missing kubernetes folder in: %s" % folder)
