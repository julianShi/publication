function arclength_continuation(pr,so)

% -- temporal setups
LevelN=100;
LevelM=1+2*pr.LevelM0;
options=optimoptions('fsolve','Display','off','MaxIter',30);

% -- record
Level=1000;
ds=0.1;
so.xVec=zeros(LevelM+1,Level);
so.energyVec=zeros(1,Level);

% -- initial values
x=[zeros(LevelM,1);.5];
dx=[zeros(LevelM,1);1];
dx=dx/norm(dx);	% normalize

for ik=1:Level
	% -- inheritance
	x0=x;
	dx0=dx;
	% -- define new functions containing coefficients x0, dx0
	fun2=@(x) [ (x-x0)'*dx0-ds ];
	fun3=@(x) [pr.fun1(x);fun2(x)];
	% -- solve 
	[x,~,~,~,J]=fsolve(fun3,x0,options);
	dx=J\[zeros(LevelM,1);1];
	dx=dx/norm(dx);	
	% -- record
	so.xVec(:,ik)=x;
    u=x(1:end-1);
    w=x(end);
    energy=sum(u.^2)/2+w^2*( ([1:pr.LevelM0,1:pr.LevelM0]).^2 *u(2:end).^2)/2;
    so.energyVec(ik)=energy;
	% -- breaking conditions
    if(x(end)<=.2|| x(end)>2 && energy<1 )break;end;
end
so.xVec(:,ik:end)=[];
so.energyVec(:,ik:end)=[];
