# Basics

raw_data:
-- generations
-- pareto fronts

# Fixed Pareto fronts
to create fixed pareto fronts:
simulation_result pareto create <resource folder raw data> 

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

