package ce301_lemnariu_dan;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            Global.ps = new PrintStream("Output.txt");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        boolean ShouldStop=false;// boolean that shows if we run out of possible moves
        Node n=new Node();//"iterator" that holds the node we started the evaluation at

        List explored = new ArrayList<Board>();

        Global.ps.println(n.board.toString());
        Global.ps.println('\n');
        Global.ps.flush();

        int i=1;

        long startTime;

        while(!ShouldStop){

            System.out.println(i+ ", size = "+ n.board.undiscovered.size()+ ", depth:" + n.AbsolutDepth);

            startTime = System.currentTimeMillis();

            int NextMove =n.FindNextMove(explored);

            if(NextMove==-1)
                ShouldStop=true;
            else {
                Global.ps.println(i + "\n");
                Global.ps.println(n.child.get(NextMove).board.toString(n.board));
                Global.ps.println('\n');
                Global.ps.flush();
                n = n.child.get(NextMove);
            }

            double timePlayed = (double) (System.currentTimeMillis() - startTime) / 1000;

            System.out.println("time spent : " + (timePlayed) + " seconds" + "\n");

            i++;
        }

        Global.ps.close();
    }
}