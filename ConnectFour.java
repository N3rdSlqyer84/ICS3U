import java.util.*;
import java.io.*;

public class ConnectFour {
    static final int ROWS = 6, COLS = 7, INF = 1000000000;
    static int wins = 0, losses = 0, ties = 0;
    static int board[][] = new int[ROWS][COLS];
    static int highest[] = new int[COLS];
    static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        //variable declaration
        boolean moved = false, playAgain = false, inputValid = false;
        String line, playerName, fileName = "";
        int winner, move;
        
        do {
            //new or returning player
            System.out.print("Are you a new or returning player? Type new if you are new, or returning if you are a returning player: ");
            line = sc.nextLine();
            
            //for new players
            if (line.equals("new")) {
                System.out.print("What is your name?");
                playerName = sc.nextLine();
                fileName = playerName + ".txt"; 
                File profile = new File(fileName);
                try {
                    if (profile.createNewFile()) {
                        System.out.println("New profile created for player " + playerName);
                    }
                    else {
                        System.out.println("Profile already exists, overwriting profile!");
                    }
                    updateStats(0,0,0,fileName);
                    inputValid = true;
                }
                catch (IOException e) {
                    System.out.println("Problem creating file");
                }
            }
            
            //for returning players
            else if (line.equals("returning")) {
                System.out.print("What is your name?");
                playerName = sc.nextLine();
                fileName = playerName + ".txt"; 
                try {
                    Scanner fs = new Scanner(new File(fileName));
                    wins = fs.nextInt();
                    ties = fs.nextInt();
                    losses = fs.nextInt();
                    System.out.println("Loading profile for " + playerName);
                    System.out.println("Wins: " + wins);
                    System.out.println("Ties: " + ties);
                    System.out.println("Losses: " + losses);
                    inputValid = true;
                }
                catch (IOException e) {
                    System.out.println("Could not read file! Either you are not a returning player or you entered your name wrong! Please try again!");
                }
            }
            
            //bad input entered
            else {
                System.out.print("Please enter returning or new: ");
            }
        } while (!inputValid);
        
        //main loop for multiple games
        do {
            //resetting board and variables
            System.out.println("Connect 4");
            System.out.println("============================================================");
            setupBoard();
            winner = -1;
            
            //main game loop
            while (winner < 0) {
                printBoard();
                playerTurn();
                printBoard();
                winner = gameOver();
                if (winner >= 0) {
                    break;
                }
                computerTurn();
                winner = gameOver();
            }
            
            //ending display 
            printBoard();
            displayResult(winner);
            updateStats(wins, losses, ties, fileName);
            System.out.print("Would you like to play again? Type yes if so, and otherwise no: ");
            line = sc.nextLine();
            while (!line.toLowerCase().equals("yes") && !line.toLowerCase().equals("no")) {
                System.out.print("Please enter yes or no! ");
                line = sc.nextLine();
            }
            playAgain = line.toLowerCase().equals("yes");
        } while (playAgain); 
    }
    
    /*==================================================
    void setupBoard()
    ====================================================
    returns nothing
    ====================================================
    no parameters
    =====================================================
    procedure to reset board 
    =====================================================*/
    public static void setupBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                highest[j] = ROWS-1;
                board[i][j] = 0; 
            }
        }
    }
    
    /*==================================================
    void displayResult(int winner)
    ====================================================
    returns nothing
    ====================================================
    int winner --> winner of current game
    =====================================================
    prints appropriate message based on who the winner is
    =====================================================*/
    public static void displayResult(int winner) {
        if (winner == 1) {
            System.out.println("Congrats you won! ");
            System.out.println("Your wins: " + wins + " --> " + ++wins);
        }
        else if (winner == 2) {
            System.out.println("The computer won!");
            System.out.println("Your losses: " + losses + " --> " + ++losses);
        }
        else {
            System.out.println("Tie!");
            System.out.println("Your ties: " + ties + " --> " + ++ties);
        }
    }
    /*==================================================
    void playerTurn()
    ====================================================
    returns nothing
    ====================================================
    no parameters 
    =====================================================
    simulates through player turn
    =====================================================*/
    public static void playerTurn() {
        int move = 0;
        boolean inputValid = false;
        
        //input getting and verification
        do {
            try {
                System.out.println("Enter a column from 1 to 7: ");
                move = sc.nextInt();
                sc.nextLine();
                if (move > COLS || move <= 0) {
                    System.out.print("Out of range! ");
                } 
                else if (highest[move-1] < 0) {
                    System.out.print("This column is full! ");
                }
                else {
                    inputValid = true;
                }
            }
            catch (InputMismatchException e) {
                System.out.print("Bad data! ");   
                sc.nextLine();             
            }
        } while(!inputValid); 
        
        board[highest[move-1]--][move-1] = 1;
    }
    /*==================================================
    void computerTurn()
    ====================================================
    returns nothing
    ====================================================
    no parameters 
    =====================================================
    simulates through computer turn
    =====================================================*/
    public static void computerTurn() {
        boolean moved = false;
        int val, bestVal = 0, bestMove = (COLS/2); 
        System.out.println("Computer Move: ");
        
        //blocking game-ending moves
        for (int i = 0; i < COLS; i++) {
            if (highest[i] < 0) {
                continue;
            }
            board[highest[i]][i] = 1; // temporarily alter board to see if this is a winning move for the player
            if (gameOver() >= 0) {
                board[highest[i]--][i] = 2;
                moved = true;
                break;
            }
            board[highest[i]][i] = 0;
        }
        
        //choosing move that gives maximum instantaneous value otherwise
        if (!moved) {
            bestVal = -INF;
            bestMove = (COLS/2);
            for (int i = 0; i < COLS; i++) {
                val = posVal(i);
                if (val > bestVal) {
                    bestVal = val;
                    bestMove = i;
                }
            }
            board[highest[bestMove]--][bestMove] = 2;
        }
    }
    /*==================================================
    void updateStats()
    ====================================================
    returns nothing
    ====================================================
    no parameters 
    =====================================================
    updates stats in the txt file of the player
    =====================================================*/
    public static void updateStats(int wins, int losses, int ties, String fileName) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));
            out.write(Integer.toString(wins));
            out.newLine();
            out.write(Integer.toString(ties));
            out.newLine();
            out.write(Integer.toString(losses));
            out.close();        
            System.out.println("Wins: " + wins);
            System.out.println("Ties: " + ties);
            System.out.println("Losses: " + losses);
        }
        catch (IOException e) {
            System.out.println("Problem creating file");
            //wont happen, but need for compilation
        }
    }
    /*==================================================
    int gameOver()
    ====================================================
    returns int --> who wins
    0 = Tie
    1 = Player wins
    2 = Computer wins
    -1 = Nobody has won yet
    ====================================================
    no parameters 
    =====================================================
    prints current state of board array
    =====================================================*/
    public static int gameOver(){
        int won = -1;
        boolean filled = true;
        
        //check if whole board is filled
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j <ROWS;j++) {
                if (board[i][j] == 0) {
                    filled = false;
                }
            }
        }
        
        //check for 4 connected in horizontal direction
        for (int i = 0; i<ROWS;i++) {
            for (int j = 0;j+3<COLS;j++) {
                if (board[i][j] == board[i][j+1] && board[i][j+1] == board[i][j+2] && board[i][j+2] == board[i][j+3] && board[i][j] != 0) {
                    won = board[i][j];
                }
            }
        }
        
        //check for 4 connected in vertical direction
        for (int i = 0; i<COLS;i++) {
            for (int j = 0;j+3<ROWS;j++) {
                if (board[j][i] == board[j+1][i] && board[j+1][i] == board[j+2][i] && board[j+2][i] == board[j+3][i] && board[j][i] != 0) {
                    won = board[j][i];
                }
            }
        }
        
        //check for diagonals 
        for (int i = 0; i + 3 < ROWS; i++) {
            for (int j = 0; j + 3 < COLS; j++) {
                if (board[i][j] == board[i+1][j+1] && board[i+1][j+1] == board[i+2][j+2] && board[i+2][j+2] == board[i+3][j+3] && board[i][j] != 0) {
                    won = board[i][j];
                }
            }
        }
        
        //check for diagonals(other direction)
        for (int i = 3; i < ROWS; i++) {
            for (int j = 0; j + 3 < COLS; j++) {
                if (board[i][j] == board[i-1][j+1] && board[i-1][j+1] == board[i-2][j+2] && board[i-2][j+2] == board[i-3][j+3] && board[i][j] != 0) {
                    won = board[i][j];
                }
            }
        }
        if (won == -1 && filled) {
            won = 0;
        }
        return won;
    }
    
    /*==================================================
    int posVal(int col)
    ====================================================
    returns int --> value of a move col
    ====================================================
    int col --> column of the move being considered
    =====================================================
    returns value of some move to position val based on 
    the method below
    =====================================================*/
    public static int posVal(int col) {
        //this block determines the value of a move to col, and the value is calculated by the number of threats created where threats are connected pieces adjacent to the move being considered
        int val = 0;
        if (highest[col] < 0) {
            val = -INF;
            //cant place here, column filled
        }
        else {
            board[highest[col]][col] = 2; //temporarily alter board to see if winning move
            if (gameOver() == 2 || gameOver() == 0) {
                val = INF;
                //winning move, place here
            }
            else {
                if (highest[col] != 0) {
                    board[highest[col]-1][col] = 1; // temporarily alter board to see if player gets winning move
                    if (gameOver() == 1) {
                        val = -INF;
                        //if placed here, player gets winning move
                    }
                    board[highest[col]-1][col] = 0 ;
                }
                board[highest[col]][col] = 0;
                
                //threats below candidate move
                for (int i = highest[col]-1; i>=0;i--) {
                    if (board[i][col] == 2) {
                        val++;
                    }
                    else {
                        break;
                    }
                }
                
                //threats to the left of candidate move
                for (int i = col-1; i>=0;i--) {
                    if (board[highest[col]][i] == 2) {
                        val++;
                    }
                    else {
                        break;
                    }
                }   
                
                //threats to the right of candidate move
                for (int i = col+1; i<COLS;i++) {
                    if (board[highest[col]][i] == 2) {
                        val++;
                    }
                    else {
                        break;
                    }
                }            
                
                //threats in the diagonal above and to the right
                for (int i = 1; col + i < COLS && highest[col] + i < ROWS; i++ ) {
                    if (board[highest[col]+i][col+i] == 2) {
                        val++;
                    }
                    else {
                        break;
                    }
                }
                
                //threats in the diagonal above and to the left
                for (int i = 1; col - i >= 0 && highest[col] + i < ROWS; i++ ) {
                    if (board[highest[col]+i][col-i] == 2) {
                        val++;
                    }
                    else {
                        break;
                    }
                }
                
                //threats in the diagonal below and to the right
                for (int i = 1; col + i < COLS && highest[col] - i >= 0; i++ ) {
                    if (board[highest[col]-i][col+i] == 2) {
                        val++;
                    }
                    else {
                        break;
                    }
                }
                
                //threats in the diagonal below and to the left
                for (int i = 1; col - i >= 0 && highest[col] - i >= 0; i++ ) {
                    if (board[highest[col]-i][col-i] == 2) {
                        val++;
                    }
                    else {
                        break;
                    }
                }
            }
        }
        return val;
    }
    
    /*==================================================
    void printBoard()
    ====================================================
    returns nothing --> simply prints array
    ====================================================
    no parameters
    =====================================================
    prints current state of board array
    =====================================================*/
    public static void printBoard() { 
        for (int i = 0; i < ROWS;i++){
            for (int j =0 ; j<COLS;j++) {
                if (board[i][j] == 0) {
                        System.out.print(". ");
                    }
                else if(board[i][j] == 1) {
                    System.out.print("X ");
                }
                else {
                    System.out.print("O ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
