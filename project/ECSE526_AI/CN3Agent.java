import java.lang.*;
import java.util.Random;

/* This class controls the workings of the
 * game and directs the working of the computer players.
 */
public class CN3Agent extends Thread {

    private static int ROW_SIZE = 6;
    private static int COL_SIZE = 7;

    private static int ROW_SIZEC3 = 4;
    private static int COL_SIZEC3 = 5;
    
    private static CN3MinimaxPrune minmax;

    //game board
    //private static int[][] gameBoard = new int[ROW_SIZEC3][COL_SIZEC3];
    private static int[][] gameBoard = {
    {1,0,0,0,-1},
    {-2,0,0,0,2},
    {3,0,0,0,-3},
    {-4,0,0,0,4}
    };
    private int moved=0;
    private Random randomNumber=new Random();
    private int whiteOrBlack=1;
    private int[] move=new int[2];
    
    //gameBoard square values
    private static int PLAYER1_MOVE = 1;  //black player
    private static int PLAYER2_MOVE = -1; //red player
    private static int OPEN_SQUARE = 0;

    //boolean variables to keep track of game gameBoard
    public static boolean player1win = false;
    public static boolean player2win = false;
    public static boolean player1move = true;
    private static int timeout = 1000;
    //private String player1Name = "";
    //private String player2Name = "";
    private String gameType = "";    //contains hc, cc, hh

    //public variables for next move from computer player(s)
    public int rowNextMove = -1;
    public int colNextMove = -1;

    private boolean flipTur;
    private boolean isNegative;
    
    public CN3Agent(boolean IsNegative) {

	//initialize game gameBoard
	player1win = false;
	player2win = false;
	player1move = true;
	isNegative=IsNegative;
    }

     public void human_move(int row, int col) {
	    if(!isLegalMoveC3(row,col))
	    	System.out.println("Search failed! Agent go in random.");
	    while(!isLegalMoveC3(row,col))
	    {
	    	rowNextMove=(-1-randomNumber.nextInt(4))*(isNegative?-1:1);
	    	colNextMove=1+randomNumber.nextInt(4);
	    }
	    
	    Move(row, col);
    	;
    }


    public void computer_moveC3() {
	int[] cmove = new int[2];
	rowNextMove = -1;
	colNextMove = -1;
	    minmax = new CN3MinimaxPrune(isNegative);
	    minmax.minFlag = true;
	    minmax.runC3();
	    rowNextMove = minmax.rowNextMove;
	    colNextMove = minmax.colNextMove;
	    //System.out.println(minmax.rowNextMove + " " + minmax.colNextMove);
	    minmax = null;	// release CN3Minimux object
	    //--------------- if no suggested move decision, move in random.
	    if(!isLegalMoveC3(rowNextMove,colNextMove))
	    	System.out.println("Search failed! Computer go in random.");
	    while(!isLegalMoveC3(rowNextMove,colNextMove))
	    {
	    	rowNextMove=(-1-randomNumber.nextInt(4))*(isNegative?-1:1);
	    	colNextMove=1+randomNumber.nextInt(4);
	    }
	    
	    Move(rowNextMove, colNextMove);

	} // end method computer_moveC3()
 
    public static  boolean isLegalMoveC3(int identity, int dir) {
    	
         int[] loc=Loc(identity);
         int i=loc[0];
         int j=loc[1];
            
        switch (dir)
        {
            case 1:
            {
                if(i<3 && gameBoard[i+1][j]==0)
                {
                	return true;
                }
                
            }
            case 2:
            {
                if(j<4 && gameBoard[i][j+1]==0)
                {
                	return true;
                }
                
            }
            case 3:
            {
                if(i>0 && gameBoard[i-1][j]==0)
                {
                	return true;
                }
                
            }
            case 4:
            {
                if(j>0 && gameBoard[i][j-1]==0)
                {
                	return true;
                }
                
            }
            default :return false;
        }
    }//end isLegalMove

    
    /**Call this to print the board to the command window using ASCII characters
     */
    public void PrintBoard() {
    	for (int i=0; i<gameBoard.length; i++) {
    		for (int j=0; j<gameBoard[0].length; j++) {
    			System.out.printf("%3d",gameBoard[i][j]);
    		}
    		System.out.println();
    	}
    }
    
    /**This method is used to get the current board gameBoard.
     *@return Returns the board in int[][] form.
     */
    public static int[][] getBoard() {
	int[][] newBoard = new int[gameBoard.length][gameBoard[0].length];
	for (int i=0; i < gameBoard.length; i++) {
	    for (int j=0; j < gameBoard[0].length; j++) {
		newBoard[i][j] = gameBoard[i][j];
	    }
	}
    	return newBoard;
    }

    /**This function returns the 4 winning spaces.
     *@return Returns the 4 winning moves in int[4][2] form.
     */

    public void Move(int identity, int dir) {
	//System.out.println("move call");
	if (isLegalMoveC3(identity,dir)) {
	    //if player == 1 (the black player/the first player)
		int[] loc=Loc(identity);
		int i=loc[0];
		int j=loc[1];
		 switch (dir)
	        {
	            case 1:
	            {
	                if(i<3 && gameBoard[i+1][j]==0)
	                {
	                    gameBoard[i+1][j]=gameBoard[i][j];gameBoard[i][j]=0;
	                }
	                
	            }
	            case 2:
	            {
	                if(j<4 && gameBoard[i][j+1]==0)
	                {
	                    gameBoard[i][j+1]=gameBoard[i][j];gameBoard[i][j]=0;
	                }
	                
	            }
	            case 3:
	            {
	                if(i>0 && gameBoard[i-1][j]==0)
	                {
	                    gameBoard[i-1][j]=gameBoard[i][j];gameBoard[i][j]=0;
	                }
	                
	            }
	            case 4:
	            {
	                if(j>0 && gameBoard[i][j-1]==0)
	                {
	                    gameBoard[i][j-1]=gameBoard[i][j];gameBoard[i][j]=0;
	                }
	                
	            }
	        }
		
	} else
		;
	}
    }
    
    public boolean checkWinner()
    {
        int[] gameOver={0,0};
        int winWhite=0;
        int winBlack=0;
        int[][] boardWhite=new int[4][5]; // white is 1
        int[][] boardBlack=new int[4][5]; // black is -1
        // locate whites and blacks
        for(int i=0;i<4;i++)
            for(int j=0;j<5;j++)
            {
                if(gameBoard[i][j]>0)
                    boardWhite[i][j]=1;
                if(gameBoard[i][j]<0)
                    boardBlack[i][j]=1;
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
        if (gameOver[0]+gameOver[1]>0)
        	return true;
        else return false;
    }


    private static int[] Loc( int nodeIdentity )
    {
        // choose node from -4:4
        // gameBoard is 4 by 5 matrix
        int[] loc = {-1 ,-1};
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<5;j++)
            {
                if(gameBoard[i][j]==nodeIdentity)
                {
                    loc[0] = i;
                    loc[1] = j;
                }
            }
        }
        return loc;
    } // end method Loc
 }//end Connect4Controller class

/* Nomenclatures are applied from: 
 * Group Members: Jason Fletchall, Mario Giombi
 * December 4, 2005
 */
