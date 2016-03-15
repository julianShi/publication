public class CN3MinimaxPrune{// extends Thread {

    private static int limit;
    private static int[] moveValue;
    public int test = 4;
    public int rowNextMove;
    public int colNextMove;
    public boolean minFlag;
    public int depthReached=0;
    public int nodesEvaluated=0;
    
    private int[][] gameBoard;
	public CN3MinimaxPrune (boolean MinFlag)
	{
		minFlag=MinFlag;
	}
	
	public void runC3() {
		//get the game board
		gameBoard = CN3Agent.getBoard();
		rowNextMove = -1;
		colNextMove = -1;
	    limit=11;
	    int alpha=Integer.MIN_VALUE;
	    int beta=Integer.MAX_VALUE;
	    moveValue = miniMax(gameBoard,0,limit,alpha,beta,minFlag);
	    //System.out.println("value is: "+moveValue[2]);
    	rowNextMove=moveValue[0];
    	colNextMove=moveValue[1];
	    System.out.println("Total nodes evaluated is "+nodesEvaluated);
	    nodesEvaluated=0;
	    System.out.println("The depth reached is "+depthReached);
	    depthReached=0;
    } // end method run()


    private int[] miniMax(int[][] gameBoard, int depth, int limit, int alpha, int beta, boolean myMinFlag) {
	int oldv, v;
	int[] nextMove=new int[] {0,0};
	int[][] gBLeaf=new int[4][5];
	nodesEvaluated++;
	if (isWin(gameBoard))
        {
			//System.out.println("white or black might win in depth "+depth); 
		 int[] temp = staticBoardEvaluator(gameBoard);
		temp[2]=temp[2]*(depth+2)/(depth+1);
		return temp; 
        }
		 
	
 	if (depth == limit) {
			//PrintBoard(gameBoard);
			//System.out.println();
            return staticBoardEvaluator(gameBoard);
 	}
	depth = depth + 1;
	depthReached=(depthReached<depth?depth:depthReached);
	if (myMinFlag) {
 	    //min player
 	    v = Integer.MAX_VALUE;
 	    for (int id=-1;id>=-4;id--)
 	    {
 	    	for (int dir=1;dir<=4;dir++)
 	    	{
 	 		    if (isLegalMoveC3(gameBoard, id, dir))
 	 		    {
 	 		    	oldv=v;
// 	 		    	System.out.println("depth "+depth+", id "+id+", dir "+dir+", oldv "+oldv);
 	 		    	gBLeaf=Move(gameBoard,id,dir);
 	 		    	
 	 	 			v = java.lang.Math.min(v, (int) miniMax(gBLeaf, depth, limit, alpha, beta, ! myMinFlag)[2]);
 	 	 			beta = v;
 	 	 			if (beta <= alpha) {
 	 	 			    return new int[] {nextMove[0],nextMove[1],alpha};
 	 	 			}
 	 	 			if ((depth == 1) && (v < oldv)) 
 	 	 			{
 	 	 				nextMove[0] = id;
 	 	 				nextMove[1] = dir;
 	 	 			}
 	 		    }
 	    	}
 	    }
 
 	    //return new int[] {nextMove[0],nextMove[1],beta};
		if(depth==1) System.out.println("Computer moves "+nextMove[0]+" towards "+nextMove[1]+" achieving utility "+v);
		// Comment: computer favor negative heuristic
 	    return new int[] {nextMove[0],nextMove[1],v};
 	}//end if
 	else {

 	    v = Integer.MIN_VALUE;
 	    for (int id=1;id<=4;id++)
 	    {
 	    	for (int dir=1;dir<=4;dir++)
 	    	{
 	 		    if (isLegalMoveC3(gameBoard, id, dir))
 	 		    {
 	 		    	oldv=v;
 	 		    	gBLeaf=Move(gameBoard,id,dir);
 	 		    	//System.out.println("depth "+depth+",mark "+(++mark));
 	 		    	//PrintBoard(gameBoard);
 	 				//System.out.println();
 	 	           
 	 		    	v = java.lang.Math.max(v, (int) miniMax(gBLeaf, depth, limit, alpha, beta, ! myMinFlag)[2]);
 	 		    	alpha = v;
 	 				if (beta <= alpha) {
 	 				    return new int[] {nextMove[0],nextMove[1],beta};
 	 				}
 	 	 			if ((depth == 1) && (v > oldv)) 
 	 	 			{
 	 	 				nextMove[0] = id;
 	 	 				nextMove[1] = dir;
 	 	 			}
 	 		    }
 	    	}
 	    }
 	   if(depth==1) System.out.println("Computer move "+nextMove[0]+" towards "+nextMove[1]+", and achieve heuristic "+v);
	    return new int[] {nextMove[0],nextMove[1],v};
	}//end else
    }
    
    private boolean isLegalMoveC3(int[][] gB, int identity, int dir) {
    	
         int[] loc=Loc(gB,identity);
         int i=loc[0];
         int j=loc[1];
         boolean legal=false;
            
        switch (dir)
        {
            case 1:
            {
                if(i<3 && gB[i+1][j]==0)
                	legal=true;
                break;
            }
            case 2:
            {
                if(j<4 && gB[i][j+1]==0)
                	legal=true;
                break;
            }
            case 3:
            {
                if(i>0 && gB[i-1][j]==0)
                	legal=true;
                break;
            }
            case 4:
            {
                if(j>0 && gB[i][j-1]==0)
                	legal=true;
                break;
            }
            default :legal= false;
        }
        return legal;
    }//end isLegalMoveC3

    private int[] Loc(int[][] gB, int nodeIdentity )
    {
        // choose node from -4:4
        // gameBoard is 4 by 5 matrix
        int[] loc = {-1 ,-1};
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<5;j++)
            {
                if(gB[i][j]==nodeIdentity)
                {
                    loc[0] = i;
                    loc[1] = j;
                }
            }
        }
        return loc;
    } // end method Loc    
 
	   private int[] utilityC3(int[][] state)
	    {
		   int score=0;
	        int[] gameOver={0,0};
	        int winWhite=0;
	        int winBlack=0;
	        int[][] boardWhite=new int[4][5]; // white is 1
	        int[][] boardBlack=new int[4][5]; // black is -1
	        // locate whites and blacks
	        for(int i=0;i<4;i++)
	            for(int j=0;j<5;j++)
	            {
	                if(state[i][j]>0)
	                    boardWhite[i][j]=1;
	                if(state[i][j]<0)
	                    boardBlack[i][j]=1;
	            }
	        // count connect3
	        for(int i=0;i<3;i++)
	            for(int j=0;j<5;j++)
	            {
	                if(boardWhite[i][j]+boardWhite[i+1][j]==2)
	                    score++;
	                if(boardBlack[i][j]+boardBlack[i+1][j]==2)
	                    score--;
	            }
	        for(int i=0;i<4;i++)
	            for(int j=0;j<4;j++)
	            {
	                if(boardWhite[i][j]+boardWhite[i][j+1]==2)
	                    score++;
	                if(boardBlack[i][j]+boardBlack[i][j+1]==2)
	                    score--;
	            }
	        for(int i=0;i<3;i++)
	            for(int j=0;j<4;j++)
	            {
	                if(boardWhite[i][j]+boardWhite[i+1][j+1]==2)
	                    score++;
	                if(boardWhite[i][j+1]+boardWhite[i+1][j]==2)
	                    score++;
	                if(boardBlack[i][j]+boardBlack[i+1][j+1]==2)
	                    score--;
	                if(boardBlack[i][j+1]+boardBlack[i+1][j]==2)
	                    score--;
	            }
	        for(int i=0;i<2;i++)
	            for(int j=0;j<5;j++)
	            {
	                if(boardWhite[i][j]+boardWhite[i+1][j]+boardWhite[i+2][j]==3)
	                    score+=10;
	                if(boardBlack[i][j]+boardBlack[i+1][j]+boardBlack[i+2][j]==3)
	                    score-=10;
	            }
	        for(int i=0;i<4;i++)
	            for(int j=0;j<3;j++)
	            {
	                if(boardWhite[i][j]+boardWhite[i][j+1]+boardWhite[i][j+2]==3)
	                    score+=10;
	                if(boardBlack[i][j]+boardBlack[i][j+1]+boardBlack[i][j+2]==3)
	                    score-=10;
	            }
	        for(int i=0;i<2;i++)
	            for(int j=0;j<3;j++)
	            {
	                if(boardWhite[i][j]+boardWhite[i+1][j+1]+boardWhite[i+2][j+2]==3)
	                    score+=10;
	                if(boardWhite[i][j+2]+boardWhite[i+1][j+1]+boardWhite[i+2][j]==3)
	                    score+=10;
	                if(boardBlack[i][j]+boardBlack[i+1][j+1]+boardBlack[i+2][j+2]==3)
	                    score-=10;
	                if(boardBlack[i][j+2]+boardBlack[i+1][j+1]+boardBlack[i+2][j]==3)
	                    score-=10;
	            }
			return new int[] {0,0,score};
	    }// end method utilityC3
	
    private int[] staticBoardEvaluator(int[][] BoardState) {
	int value;
	value = utilityC3(BoardState)[2];

	return new int[] {0,0,value};
    } // end method staticBoardEvaluator


private boolean isWin(int[][] gB)
{
    boolean win=false;
    int[] gameOver=new int[2];
    int winWhite=0;
    int winBlack=0;
    int[][] boardWhite=new int[4][5]; // white is 1
    int[][] boardBlack=new int[4][5]; // black is -1
    // locate whites and blacks
    for(int i=0;i<4;i++)
    {
    	for(int j=0;j<5;j++)
        {
            if(gB[i][j]>0)
                boardWhite[i][j]=1;
//            System.out.printf("%2d ",boardWhite[i][j]);
            if(gB[i][j]<0)
                boardBlack[i][j]=1;
        }
//    	System.out.println(" ");
    }
    // count connect3
    for(int i=0;i<2;i++)
        for(int j=0;j<5;j++)
        {
            if(boardWhite[i][j]+boardWhite[i+1][j]+boardWhite[i+2][j]==3)
                winWhite=1;
            if(boardBlack[i][j]+boardBlack[i+1][j]+boardBlack[i+2][j]==3)
                winBlack=1;
        }
    for(int i=0;i<4;i++)
        for(int j=0;j<3;j++)
        {
            if(boardWhite[i][j]+boardWhite[i][j+1]+boardWhite[i][j+2]==3)
                winWhite=1;
            if(boardBlack[i][j]+boardBlack[i][j+1]+boardBlack[i][j+2]==3)
                winBlack=1;
        }
    for(int i=0;i<2;i++)
        for(int j=0;j<3;j++)
        {
            if(boardWhite[i][j]+boardWhite[i+1][j+1]+boardWhite[i+2][j+2]==3)
                winWhite=1;
            if(boardWhite[i][j+2]+boardWhite[i+1][j+1]+boardWhite[i+2][j]==3)
                winWhite=1;
            if(boardBlack[i][j]+boardBlack[i+1][j+1]+boardBlack[i+2][j+2]==3)
                winBlack=1;
            if(boardBlack[i][j+2]+boardBlack[i+1][j+1]+boardBlack[i+2][j]==3)
                winBlack=1;
        }
    gameOver[0]=winWhite;
    gameOver[1]=winBlack;
    if(gameOver[0]+gameOver[1]>0)
    	win=true;
    return win;
} // end method isWin()


private int[][] Move( int[][] gBPrimary, int identity, int dir) {
//System.out.println("move call");
	int[][] gB=new int[4][5];
	for (int i=0;i<gB.length;i++)
		for (int j=0;j<gB[0].length;j++)
			gB[i][j]=gBPrimary[i][j];
		
	int[] loc=Loc(gB,identity);
	int i=loc[0];
	int j=loc[1];
	 switch (dir)
        {
            case 1:
            {
                if(i<3 && gB[i+1][j]==0)
                {
                	gB[i+1][j]=gB[i][j];gB[i][j]=0;break;
                }
                
            }
            case 2:
            {
                if(j<4 && gB[i][j+1]==0)
                {
                	gB[i][j+1]=gB[i][j];gB[i][j]=0;break;
                }
                
            }
            case 3:
            {
                if(i>0 && gB[i-1][j]==0)
                {
                	gB[i-1][j]=gB[i][j];gB[i][j]=0;break;
                }
                
            }
            case 4:
            {
                if(j>0 && gB[i][j-1]==0)
                {
                	gB[i][j-1]=gB[i][j];gB[i][j]=0;break;
                }
                
            }
            default: System.out.println("invalide move."); break;
        }

return gB;
}

// the method PrintBoard is useful here
private void PrintBoard(int[][] state) {
	for (int i=0; i<state.length; i++) {
		for (int j=0; j<state[0].length; j++) {
			System.out.printf("%2d",state[i][j]);
		}
		System.out.println();
	}
} // end method PrintBoard

}// end class

/* The recursive minimax() method are suggested by 
 * Group Members: Mario Giombi, Brian Schuette
 * December 4, 2005
 */
