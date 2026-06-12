# Create DeltaIoT pareto front visualization
These instructions are for linux.
For windows, these instructions need to be adapted slightly.

## Install
Required: Python >= 3.12

Create virtual environment:
'''
python3 -m venv venv
'''

Install required libraries:
'''
venv/bin/pip install -r requirements.txt
'''

# Basics
Required raw simulation result data of the strategy as input.

# Pareto front correction
The pareto fronts contained in the raw simulation results contains minor calculation errors
resulting from a wrong boundary check.

Therefore we first need to re-calculate the pareto fronts from the raw simulation data.

1. raw data reource folder
2. corrected resource folder

## Pareto front re-calculation
To create fixed pareto fronts:
'''
./simulation_result.sh pareto create <path to raw resource folder>/resource/<strategy id>

e.g. for strategy1d
./simulation_result.sh pareto create /raw_data/resource/DeltaIoT_modelled_ea_strategy1d
'''
Creates a new pareto front file for each generation:
resources/<strategy id>/generations/pareto_front_<generation>.json
Also a final pareto front file:
resources/<strategy id>/pareto_front.json


# Accumulated pareto front calculation
Requires fixed pareto fronts.

'''
./simulation_result.sh pareto create --cumulative <path to raw resource folder>/resource/<strategy id>

e.g. for strategy1d
./simulation_result.sh pareto create --cumulative /raw_data/resource/DeltaIoT_modelled_ea_strategy1d
'''
Creates a cummulated pareto front file for each generation:
resources/<strategy id>/generations/cumulative_pareto_front_<generation>.json

Create CSV file for visualization:
'''
./simulation_result.sh pareto extract --cumulative <resource folder fixed front data>

e.g. for strategy1d
./simulation_result.sh pareto extract --cumulative resources/DeltaIoT_modelled_ea_strategy1c
'''
Creates cummulated fronts file:
resources/<strategy id>_cumulative_pareto_fronts.csv


# Accumulated pareto hypervolumne calculation


raw_data:
-- generations
-- pareto fronts

# Fixed Pareto fronts

to create approximated pareto front:
simulation_result pareto create --approximated <resource folder raw data> 

to create cumulated pareto fronts:
simulation_result pareto create --cumulative <resource folder raw data> 


# Pareto front CSV extraction
simulation_result pareto extract <resource folder fixed front data>
simulation_result pareto extract --approximated <resource folder fixed front data>
simulation_result pareto extract --cumulative <resource folder fixed front data>


# Pareto front CSV rank 
simulation_result pareto rank <resource folder fixed front data>
# Needs fixed cumulated pareto fronts
simulation_result pareto rank --cumulative <resource folder fixed front data>

