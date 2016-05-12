% HBM for duffing
% Yulin May 5, 2016
clear all
addpath subfunc

%% == define system
% ddu + delta * du + u + epsilon * u^3 - f* cos(wt)
pr.delta=.1;
pr.epsilon=0.1;
pr.f=1;

pr=create_problem(pr);

%% == temporal discretization
pr.LevelM0=1;	% number of harmonics
pr=temporal_discretization(pr);

%% == continuation solver
so=solutionContinuation;	% solution class
arclength_continuation(pr,so);	

%% == visualize
plot(so.xVec(end,:),so.energyVec,'.')
