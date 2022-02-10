@setlocal
@echo off

SET SCRIPT_PATH=%~dp0
SET SCRIPT_PATH=%SCRIPT_PATH:~0,-1%

rem echo SCRIPT_PATH=%SCRIPT_PATH%

set DATA_IN_BASE_PATH=C:\projects\SimExp\Workspace\ExperimentResults\DataVisualization\20211203
set DATA_OUT_PATH=c:\tmp\SIMEXP

python "%SCRIPT_PATH%\src\simexpcsvtocolumncsv.py" -i "%DATA_IN_BASE_PATH%\LoadBalancing_50_100_emptyReconfiguration_av0.9_unav0.1_20211203\PerformabilityStrategySampleSpace.csv" -o "%DATA_OUT_PATH%\reward_emptyReconfiguration.csv" -c reward

python "%SCRIPT_PATH%\src\simexpcsvtocolumncsv.py" -i "%DATA_IN_BASE_PATH%\LoadBalancing_50_100_emptyReconfiguration_av0.9_unav0.1_20211203\SimulatedExperienceStore.csv" -o "%DATA_OUT_PATH%\metrics_emptyReconfiguration.csv" -c metrics


python "%SCRIPT_PATH%\src\simexpcsvtocolumncsv.py" -i "%DATA_IN_BASE_PATH%\LoadBalancing_50_100_scaling_av0.9_unav0.1_20211203\PerformabilityStrategySampleSpace.csv" -o "%DATA_OUT_PATH%\reward_scaling.csv" -c reward

python "%SCRIPT_PATH%\src\simexpcsvtocolumncsv.py" -i "%DATA_IN_BASE_PATH%\LoadBalancing_50_100_scaling_av0.9_unav0.1_20211203\SimulatedExperienceStore.csv" -o "%DATA_OUT_PATH%\metrics_scaling.csv" -c metrics


python "%SCRIPT_PATH%\src\simexpcsvtocolumncsv.py" -i "%DATA_IN_BASE_PATH%\LoadBalancing_50_100_scaling_with_node_recovery_av0.9_unav0.1_20211203\PerformabilityStrategySampleSpace.csv" -o "%DATA_OUT_PATH%\reward_scalingWithNodeRecovery.csv" -c reward

python "%SCRIPT_PATH%\src\simexpcsvtocolumncsv.py" -i "%DATA_IN_BASE_PATH%\LoadBalancing_50_100_scaling_with_node_recovery_av0.9_unav0.1_20211203\SimulatedExperienceStore.csv" -o "%DATA_OUT_PATH%\metrics_scalingWithNodeRecovery.csv" -c metrics

