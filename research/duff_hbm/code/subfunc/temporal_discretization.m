function pr=temporal_discretization(pr)

M=pr.M;
D=pr.D;
K=pr.K;
f=pr.f;

LevelM0=pr.LevelM0;
LevelM=1+2*LevelM0;
LevelN=1000;	% temporal discretization over one period

phaseVec=2*pi*(1/LevelN:1/LevelN:1)';
Phi=[ones(LevelN,1)/sqrt(2),cos(phaseVec*((1:LevelM0))),sin(phaseVec*((1:LevelM0)))];
LD1=2\kron( [0,1;-1,0],diag((1:LevelM0)) );
LD=zeros(LevelM);
LD(2:end,2:end)=LD1;
LM1=2\kron( [-1,0;0,-1],diag((1:LevelM0).^2) );
LM=zeros(LevelM);
LM(2:end,2:end)=LM1;
LK=2\eye(LevelM);
Lf=zeros(LevelM,1);
Lf(2)=2\1;

A=@(w) w^2*kron(M,LM)+w*kron(D,LD)+kron(K,LK);
b=kron(f,Lf);
c=1;
pr.fun1=@(x) A(x(end))*x(1:end-1)+ (Phi'*(Phi*x(1:end-1)).^3) /LevelN*pr.epsilon-b;
	
