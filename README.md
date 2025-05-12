# SAPDSM

**Structured Agent Population Dynamics Simulation Model** (SAPDSM) is a Java + Processing software for simulating the dynamics of two competing populations with internal structure. The model is based on the study described in the paper [**"A model of competition between two populations, taking into account their structurality"**](https://www.impb.ru/icmbb/docs/2022/29.pdf) and [further research work](https://drive.google.com/file/d/1lNg_scyBcRdXQl3_ICJexiuEVglmMIvy/view?usp=sharing).

Developed by:  
- Institution of Russian Academy of Sciences, Dorodnicyn Computing Centre of RAS 
- Bauman Moscow State Technical University

## Overview

This agent-based simulation investigates the classical ecological problem of two species competing for a shared resource. A key innovation of the model is the introduction of internal structure within each population, allowing individuals to form cooperative flocks or networks.

Each individual has a life cycle, consumes resources, and may die when its mass-energy drops to zero or below. Flocks are represented as networks where individuals share available resources. The number of links an individual can maintain is limited by a property called **valence**.

### Core Features

- Agent-based simulation of two species competing for a common resource.
- Individuals can form flocks (networks) to redistribute resources internally.
- Support for different **valence** configurations (number of links per agent).
- Dynamic life-cycle modeling with energy and survival thresholds.
- Real-time and post-simulation visualization using Processing.

### Key Findings

Simulation experiments reveal:
- Under high-resource conditions, the population with **higher valence** dominates due to efficient internal redistribution.
- Under low-resource conditions, the population with **lower valence** prevails as simpler structures are more energy-efficient.

### Keywords

Population, competition, agent-based model, flock structure, valence, resource redistribution.

![image](https://github.com/user-attachments/assets/88aa6ca2-b04d-4a34-b810-2ee62e04d24d)
