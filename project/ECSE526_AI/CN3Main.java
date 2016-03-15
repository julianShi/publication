/* README: In terminal, change the current direction to the file direction.
Type in: java CN3Main
Then, follow the steps shown on the terminal. 
*/
// used for debugging 
// used for estimate average time
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CN3Main {
		// TODO Auto-generated method stub
	    private String[] arguments;
	    private String gameType;
	    private static int[] humanMove=new int[2];
	    private static CN3Agent AIAgent;        //the connect 4 game AIAgent
    	private static long timeStart;
    	private static long timeFinal;
    	private static int timeTotal;
	    final private static boolean isNegative=true;
	    // Comment: Positive go first
	    static Scanner input=new Scanner(System.in);
	    public static void main (String[] args)
	    {
	        AIAgent = new CN3Agent(isNegative);
            System.out.println("The initial game board is");
	        AIAgent.PrintBoard();
            System.out.println("You can move a piece with positive ID=1, 2, 3, or 4.");
	        
	        //------------- white (human) first
	        //humanMove[0]=1;humanMove[1]=2;
	        for(int step=0;step<100;step++)
	        {
                System.out.println("please type ID and direction");
	        	// 1 = south, 2=east, 3=north, 4=west
	            humanMove[0]=input.nextInt(); humanMove[1]=input.nextInt();
	            while (!AIAgent.isLegalMoveC3(humanMove[0],humanMove[1]))
	            {
                    System.out.println("please type ID and direction");
    	            humanMove[0]=input.nextInt(); humanMove[1]=input.nextInt();
    	            
	            }
	            AIAgent.human_move(humanMove[0],humanMove[1]);
	            if(AIAgent.checkWinner())
	            {
	            	System.out.println("Positive (human) win");break;
	            }
			    timeStart = System.currentTimeMillis();
	            AIAgent.computer_moveC3();
			    timeFinal = System.currentTimeMillis();
	    	    System.out.println("Excution time: "+(timeFinal-timeStart));
	            if(AIAgent.checkWinner())
	            {
	            	System.out.println("Negative (computer) win");break;
	            }
	            AIAgent.PrintBoard();
	            //AIAgent.checkWinner();
	            timeTotal+=(timeFinal-timeStart);
	        }
            System.out.println("Reach maximum step, game over.");
            
            //------------ black computer first

	}

}


