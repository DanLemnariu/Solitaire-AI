package ce301_lemnariu_dan;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LoopMain {
/////////////////////////////////////////////test depth
    public static void main(String[] args) {

        int nrGamesPlayed = 0;
        int nrGamesWon = 0;

        try {
            Global.ps = new PrintStream("Output.txt");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < 1 * 60 * 60 * 1000) {

            System.out.println("Playing Game: " + (nrGamesPlayed + 1));

            long gameStartTime = System.currentTimeMillis();

            boolean ShouldStop = false;// boolean that shows if we run out of possible moves
            Node n = new Node();//"iterator" that holds the node we started the evaluation at

            List explored = new ArrayList<Board>();

            int nrMoves=0;

            while (!ShouldStop) {

                int NextMove = n.FindNextMove(explored);

                if (NextMove == -1)
                    ShouldStop = true;
                else
                    n = n.child.get(NextMove);
                nrMoves++;
            }

            double timePlayed = (double) (System.currentTimeMillis() - gameStartTime) / 1000;

            Global.ps.println("Game " + (nrGamesPlayed + 1) + " done, moves: " + nrMoves + ", time: " + timePlayed + " seconds");
            if(n.board.Win())
                Global.ps.println("Game won!!!!!");
            Global.ps.println("\n");
            Global.ps.flush();

            nrGamesPlayed++;

            if (n.board.Win() == true)
                nrGamesWon++;
        }

        System.out.println("Done!!!" + "\n");

        Global.ps.println("\n" + "Games played: " + nrGamesPlayed + "\n");
        Global.ps.println("Games won: " + nrGamesWon + "\n");

        double winrate =(double)(nrGamesWon * 100) / nrGamesPlayed;

        Global.ps.println("Win rate: " + winrate);

        Global.ps.close();
    }
}
