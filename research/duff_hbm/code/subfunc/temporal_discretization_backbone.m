function pr=temporal_discretization(pr)

M=pr.M;
% D=pr.D;
K=pr.K;
% f=pr.f;

LevelM0=pr.LevelM0;
LevelM=1+LevelM0;
LevelN=10000;

phaseVec=2*pi*(1/LevelN:1/LevelN:1)';
Phi=[ones(LevelN,1)/sqrt(2),cos(phaseVec*(1:LevelM0))];
LM1=2\kron( -1,diag((1:LevelM0).^2) );
LM=zeros(LevelM);
LM(2:end,2:end)=LM1;
LK=2\eye(LevelM);
Lf=zeros(LevelM,1);
Lf(2)=2\1;

A=@(w) w^2*kron(M,LM)+kron(K,LK);
% b=kron(f,Lf);
% c=1;
pr.fun1=@(x) A(x(end))*x(1:end-1)+pr.epsilon*Phi'*(Phi*x(1:end-1)).^3/LevelN;
