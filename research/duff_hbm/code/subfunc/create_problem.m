function pr=create_problem(pr)
	pr.M=1;
	pr.D=pr.delta*pr.M;
	pr.K=1;
	pr.wnVec=sqrt(eig(pr.K,pr.M));
    pr.wn=pr.wnVec(1);
	pr.DOF=size(pr.K,1);
