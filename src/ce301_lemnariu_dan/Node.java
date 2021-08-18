package ce301_lemnariu_dan;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class Node{
    Board board;
    Vector<Node> child= new Vector<>();
    private final int NO_MORE_VALID_MOVES_AVAILABLE =-1;
    static public int AbsolutDepth=2;

    Node(Board b){
        this.board = b;
    }

    Node() {
        board=new Board();
    }

    //find the best child of the root node and returns its address

    public int FindNextMove(List explored){

        if(AbsolutDepth>10)
            AbsolutDepth=10;

        if(explored.size()==0)
            AbsolutDepth=2;

        long startTime = System.currentTimeMillis();

        this.Expand();

        if(child.size()==0)
            return NO_MORE_VALID_MOVES_AVAILABLE;

        if((board.discard.size()==0) && (board.stock.size()>0) && (board.count<2)) {
            child.get(0).board.revealCard();
            if(AbsolutDepth>2)
                AbsolutDepth--;
            return 0;
        }

        int HeuristicValue=-1;

        explored.add(board);

        //it find the position of the children with the highest Heuristic value

        int pos=-1;

        for (int i = 0; i < child.size(); i++) {
            int val = 0;

            if(child.get(i).board.isNonDeterministic()) {
                List<Node> PossibleOutcomes = child.get(i).predictCard();
                for (Node N : PossibleOutcomes) {
                    val += N.Explore(AbsolutDepth, explored);
                }
                val = val / PossibleOutcomes.size();
            }
            else
                val= child.get(i).Explore(AbsolutDepth, explored);

            if(HeuristicValue==val){
                if((HeuristicValue!=-1) && (child.get(i).board.Heuristic()>child.get(pos).board.Heuristic())){
                    HeuristicValue=val;
                    pos=i;
                }
            }
            else {
                if (HeuristicValue < val) {
                    HeuristicValue = val;
                    pos = i;
                }
            }
        }

        long moveTime = System.currentTimeMillis() -  startTime;

        if(moveTime>1 * 1000)
            if(AbsolutDepth>2)
            AbsolutDepth--;
        if(moveTime<board.undiscovered.size() * 0.002 * 1000)
            AbsolutDepth++;

        if(HeuristicValue==-1)
            return NO_MORE_VALID_MOVES_AVAILABLE;

        if(HeuristicValue<=board.Heuristic()) {
            if (board.count < 2) {
                return 0;
            } else if (board.count==2)
                return NO_MORE_VALID_MOVES_AVAILABLE;
        }

        if (child.get(pos).board.isNonDeterministic())
            child.get(pos).board.revealCard();
        return pos;
    }

    //exploration function

    public int Explore(int depth, List explored) {

        int HeuristicValue = -1;

        for (int i = 0; i < explored.size(); i++)
            if (explored.get(i).equals(board))
                return -1;

        //to avoid repetition of moves the current board is saved at a -1 one heuristic value so that later calls of
        // the explore() function will ignore identical board states
        explored.add(board);


        //if the recursive function has reached depth it then stops exploring, call the heuristic() function on its children and returns the value

        if (depth == 1) {
            HeuristicValue = board.Heuristic();
        }

        //if it hasn't reached depth 1 then it call explore on its children and then return the value

        else {
            this.Expand();

            if (!board.Win()) {

                for (int i = 0; i < child.size(); i++) {
                    int val = 0;

                    if (child.get(i).board.isNonDeterministic()) {
                        List<Node> PossibleOutcomes = child.get(i).predictCard();
                        for (Node N : PossibleOutcomes)
                            val += N.Explore(depth - 1, explored);
                        val = val / PossibleOutcomes.size();
                    } else
                        val = child.get(i).Explore(depth - 1, explored);

                    if (HeuristicValue < val)
                        HeuristicValue = val;
                }
            } else
                HeuristicValue = board.Heuristic();
        }


        explored.remove(board);
        child.clear();

        return HeuristicValue;
    }

    //Expand method used to create a nodes children

    public void Expand(){

        //drawing another card from the stock

        List<Board> temp3 = board.MoveStackToDiscard();
        if(temp3.size()!=0)
            for (int i = 0; i < temp3.size(); i++)
                child.add(new Node(temp3.get(i)));

        //checking if a card can be moved on one of foundations from the discard pile

        List<Board> temp1 = board.MoveDiscardToFoundation();
        if(temp1.size()!=0)
            for (int i = 0; i < temp1.size(); i++)
                child.add(new Node(temp1.get(i)));

        //checking if i can put a card here from the discard pile;(only if colours differ and the numbers are consecutive)

        List<Board> temp2 = board.MoveDiscardToPiles();
        if(temp2.size()!=0)
            for (int i = 0; i < temp2.size(); i++)
                child.add(new Node(temp2.get(i)));

        // checking if a card can be moved on one of foundations from the piles

        List<Board> temp = board.MovePilesToFoundation();
        if(temp.size()!=0)
            for (int i = 0; i < temp.size(); i++)
                child.add(new Node(temp.get(i)));

        //moving cards between the piles

        List<Board> temp4 = board.MovePilesToPiles();
        if(temp4.size()!=0)
            for (int i = 0; i < temp4.size(); i++)
                child.add(new Node(temp4.get(i)));
    }

    public List<Node> predictCard(){
        List<Board> temp = board.predictCard();
        List<Node> outcomes = new ArrayList<>();

        for (Board b : temp)
            outcomes.add(new Node(b));

        return outcomes;
    }
}