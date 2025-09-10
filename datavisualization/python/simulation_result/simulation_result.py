import sys
import argparse

class SimulationResult:
  def main(self):
    parser = argparse.ArgumentParser(prog="simulation_result", description="Analyses simulation results")
    parser.add_argument('infile', type=argparse.FileType('r'))
    args = parser.parse_args()

    print("validate input...")

if __name__ == '__main__':
    sr = SimulationResult()
    sys.exit(sr.main())

