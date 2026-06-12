# JupyterLab
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

## Run
To run execute:
'''
./venv/bin/jupyter-lab
'''

# Vizualize accumulated pareto front
pareto_front-cumulative.ipynb

# Vizualize accumulated pareto front hypervolumne
pareto_front_cumulative_rank.ipynb
