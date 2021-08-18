package ce301_lemnariu_dan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.pow;

class Board {
    List<Card> stock;
    List<List<Card>> foundations;
    List<List<Card>> piles;
    List<Card> discard;
    List<Card> undiscovered;
    int count;

    Board(){
        stock= new ArrayList<>();
        discard = new ArrayList<>();

        piles = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            piles.add(new ArrayList<>());

        foundations = new ArrayList<>();
        for (int i=0; i<4;i++)
            foundations.add(new ArrayList<>());

        undiscovered = new ArrayList<>();

        count=0;

        this.Deal();
    }

    Board(Board b) {
        this.stock =new ArrayList<>();
        if(b.stock!=null)
            for (int i=0; i<b.stock.size(); i++)
                this.stock.add(new Card(b.stock.get(i)));

        this.discard = new ArrayList<>();
        if(b.discard!=null)
            for (int i=0; i<b.discard.size(); i++)
                this.discard.add(new Card(b.discard.get(i)));

        this.foundations=new ArrayList<>();
        if(b.foundations!=null)
            for (int i =0; i<b.foundations.size(); i++) {
                this.foundations.add(new ArrayList<>());
                for (int j = 0; j < b.foundations.get(i).size(); j++)
                    this.foundations.get(i).add(new Card(b.foundations.get(i).get(j)));
            }

        this.piles=new ArrayList<>();
        if(b.piles!=null)
            for (int i =0; i<b.piles.size(); i++) {
                this.piles.add(new ArrayList<>());
                for (int j = 0; j < b.piles.get(i).size(); j++)
                    this.piles.get(i).add(new Card(b.piles.get(i).get(j)));
            }

        this.undiscovered=new ArrayList<>();
        if(b.undiscovered!=null)
            for (int i=0; i<b.undiscovered.size();i++)
                this.undiscovered.add(new Card(b.undiscovered.get(i)));

        this.count = b.count;

    }

    //Deal method

    public void Deal()
    {
        //creating list of cards

        java.util.List<Card> List = new ArrayList<>();
        for (int i = 1; i < 14; i++)
            for (int j = 0; j < 4; j++) {
                List.add(new Card(i, j, false));
                undiscovered.add(new Card(i,j,true));
            }


        //creating list to remember the positions in the pile/ a list will be deleted from this one when full

        List<Integer> SortingList = new ArrayList<>();

        for (int i = 0; i < 8; i++)
            SortingList.add(i, i);

        //dealing the cards

        Random RandomSeed = new Random();

        int seed = RandomSeed.nextInt();

        Global.ps.println("Seed Number:" + seed + "\n");

        Random rand = new Random(seed);

        while (!List.isEmpty()) {

            int in = rand.nextInt(List.size());
            int out = rand.nextInt(SortingList.size());
            piles.get(SortingList.get(out)).add(0, List.get(in));

            //card dealt must be removed from the list and the list resized

            List.remove(in);

            //if one of the piles becomes full it will be removed from the list and the list will be resized

            if (SortingList.get(out) == 7) {
                if (piles.get(SortingList.get(out)).size() == 24)
                    SortingList.remove(out);
            }
            else if (piles.get(SortingList.get(out)).size() == SortingList.get(out) + 1)
                SortingList.remove(out);
        }

        //creating the board object

        stock = new ArrayList<>(piles.get(7));

        piles.remove(7);

        for (int i = 0; i < piles.size(); i++) {
            piles.get(i).get(0).Flip();
            undiscovered.remove(piles.get(i).get(0));
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Board))
            return false;

        Board other = (Board) o;

        return this.stock.equals(other.stock) && this.discard.equals(other.discard) && this.foundations.equals(other.foundations) && this.piles.equals(other.piles);
    }

    //win check method

    public boolean Win (){
        for(int i=0; i<foundations.size(); i++)
            if(foundations.get(i).size()==0)
                return false;
            else
                if (foundations.get(i).get(0).nr!=13)
                    return false;
        return true;
    }

    public int Heuristic(){

        if(this.Win())
            return 10000;

        int score=0;

        //more cards on the foundation

        int valueFondation = 19;

        for(int i=0; i<foundations.size(); i++)
            for(int j=foundations.get(i).size()-1; j>-1;j--)
                if(foundations.get(i).get(j).nr==1|| foundations.get(i).get(j).nr==2)
                    score+=valueFondation;
                else {
                    for (int z = 0; z < foundations.size(); z++)
                        if ((z!=j) && (z - j) % 2 != 0)
                            if(foundations.get(z).size()<foundations.get(i).get(j).nr-1)
                                valueFondation -= 9;
                    score+=valueFondation;
                    valueFondation=19;
                }

        //less cards in the stack and discard

        score+=((24-stock.size())-discard.size())*15;

        //less cards face down in the piles

        int faceDownCards=0;

        for(int i=0; i<piles.size(); i++){
            int FaceDownPerPile=0;
            for (int j=0; j<piles.get(i).size();j++)
                if (!piles.get(i).get(j).faceUp) {
                    faceDownCards++;
                    FaceDownPerPile++;
                }
            score+=64-pow(2,FaceDownPerPile);
        }

        score+=(21-faceDownCards)*15;

        //less empty piles

        int emptyPiles = 0;

        for(int i=0; i<piles.size(); i++) {
            if (piles.get(i).size() == 0)
                emptyPiles++;
        }

        int nrOfKings=0;

        for(int i=0; i<piles.size(); i++)
            for (int j=0; j<piles.get(i).size()-1;j++)
                if(piles.get(i).get(j).nr==13)
                    nrOfKings++;

       if((discard.size()!=0) && (discard.get(0).nr==13))
           nrOfKings++;

       score+=(7-emptyPiles+nrOfKings);

        return score;
    }

    // checking if a card can be moved on one of foundations from the piles

    public List<Board> MovePilesToFoundation(){
        List<Board> NewBoardStates = new ArrayList<>();

        for(int c=0; c<piles.size(); c++){
            if(piles.get(c).size()>0) {
                for (int j = 0; j < foundations.size(); j++) {
                    if (piles.get(c).get(0).suit == j) {

                        //if the foundation is empty;
                        if (foundations.get(j).size() == 0) {
                            if (piles.get(c).get(0).nr == 1) {
                                Board temp = new Board(this);
                                temp.foundations.get(j).add(0, temp.piles.get(c).get(0));
                                temp.piles.get(c).remove(0);
                                temp.count=0;
                                NewBoardStates.add(temp);
                            }
                        }
                        //if it already has elements
                        else {
                            if (piles.get(c).get(0).nr == foundations.get(j).get(0).nr + 1) {
                                Board temp = new Board(this);
                                temp.foundations.get(j).add(0, temp.piles.get(c).get(0));
                                temp.piles.get(c).remove(0);
                                temp.count=0;
                                NewBoardStates.add(temp);
                            }
                        }
                    }
                }
            }
        }
        return NewBoardStates;
    }

    //checking if a card can be moved on one of foundations from the discard pile

    public List<Board> MoveDiscardToFoundation(){
        List<Board> NewBoardStates = new ArrayList<>();

        if(discard.size()>0) {
            for (int j = 0; j < 4; j++) {
                if (discard.get(0).suit == j) {

                    //if the foundation is empty;
                    if (foundations.get(j).size() == 0) {
                        if (discard.get(0).nr == 1) {
                            Board temp = new Board(this);
                            temp.foundations.get(j).add(0, temp.discard.get(0));
                            temp.discard.remove(0);
                            temp.count=0;
                            NewBoardStates.add(temp);
                        }
                    }
                    else {
                        //if it already has elements
                        if (discard.get(0).nr == foundations.get(j).get(0).nr + 1) {
                            Board temp = new Board(this);
                            temp.foundations.get(j).add(0, temp.discard.get(0));
                            temp.discard.remove(0);
                            temp.count=0;
                            NewBoardStates.add(temp);
                        }
                    }
                }
            }
        }
        return NewBoardStates;
    }

    //checking if i can put a card here from the discard pile;(only if colours differ and the numbers are consecutive)

    public  List<Board> MoveDiscardToPiles(){
        List<Board> NewBoardStates = new ArrayList<>();

        if(discard.size() > 0) {
            for (int i = 0; i < piles.size(); i++) {
                if (piles.get(i).size() > 0) {
                    if ((discard.get(0).nr == (piles.get(i).get(0).nr - 1)) && (discard.get(0).isBlack() != piles.get(i).get(0).isBlack())) {
                        Board temp = new Board(this);
                        temp.piles.get(i).add(0,temp.discard.get(0));
                        temp.discard.remove(0);
                        temp.count=0;
                        NewBoardStates.add(temp);
                    }
                }
                else
                    if(discard.get(0).nr==13){
                        Board temp = new Board(this);
                        temp.piles.get(i).add(0,temp.discard.get(0));
                        temp.discard.remove(0);
                        temp.count=0;
                        NewBoardStates.add(temp);
                    }
            }
        }
        return NewBoardStates;
    }

    //drawing another card from the stock

    public List<Board> MoveStackToDiscard(){
        List<Board> NewBoardStates = new ArrayList<>();

        if(stock.size()>0) {
            Board temp = new Board(this);
            temp.discard.add(0,temp.stock.get(0));
            temp.stock.remove(0);
            NewBoardStates.add(temp);
        }
        else if(discard!=null){
            Board temp = new Board(this);
            for (int i=0; i<temp.discard.size(); i++)
                temp.stock.add(new Card(temp.discard.get(i)));
            temp.discard=new ArrayList<>();
            temp.count++;
            NewBoardStates.add(temp);
        }

        return NewBoardStates;
    }

    //moving cards between the piles

    public List<Board> MovePilesToPiles(){
        List<Board> NewBoardStates = new ArrayList<>();
        for (int i=0; i<piles.size(); i++)
            for(int j=0; j<piles.size(); j++)
                if((i!=j) && (piles.get(i).size()>0)) {
                    if (piles.get(j).size() > 0) {
                        for (int x = 0; x < piles.get(i).size(); x++) {
                            //looking only at face up cards
                            if (piles.get(i).get(x).faceUp)
                                //move them only on a pile with a different colour and ascending number
                                if (piles.get(i).get(x).nr == piles.get(j).get(0).nr - 1 && piles.get(i).get(x).isBlack() != piles.get(j).get(0).isBlack()) {
                                    Board temp = new Board(this);
                                    int t = x;
                                    while (t >= 0) {
                                        temp.piles.get(j).add(0, temp.piles.get(i).get(t));
                                        temp.piles.get(i).remove(t);
                                        t--;
                                    }
                                    NewBoardStates.add(temp);
                                }
                        }
                    }
                    else {
                        for (int x = 0; x < piles.get(i).size()-1; x++) {
                            //looking only at face up cards
                            if (piles.get(i).get(x).faceUp)
                                //move them only if they are kings
                                if (piles.get(i).get(x).nr == 13) {
                                    Board temp = new Board(this);
                                    int t = x;
                                    while (t >= 0) {
                                        temp.piles.get(j).add(0, temp.piles.get(i).get(t));
                                        temp.piles.get(i).remove(t);
                                        t--;
                                    }
                                    NewBoardStates.add(temp);
                                }
                        }
                    }
                }
        return NewBoardStates;
    }

    public List<Board> predictCard(){
        List<Board> outcomes=new ArrayList<>();
        if((!discard.isEmpty()) && (!discard.get(0).IsFaceUp())){
            for (int i=0; i<undiscovered.size(); i++){
                Board temp = new Board(this);
                temp.discard.remove(0);
                temp.discard.add(0,temp.undiscovered.get(i));
                temp.undiscovered.remove(i);
                outcomes.add(temp);
            }
            return outcomes;
        }
        for (int y=0; y<piles.size(); y++)
            if((!piles.get(y).isEmpty()) && (!piles.get(y).get(0).IsFaceUp())){
                for(int i=0; i<undiscovered.size(); i++) {
                    Board temp = new Board(this);
                    temp.piles.get(y).remove(0);
                    temp.piles.get(y).add(0,temp.undiscovered.get(i));
                    temp.undiscovered.remove(i);
                    outcomes.add(temp);
                }
                return outcomes;
            }

        outcomes.add(this);
        return outcomes;
    }

    public boolean isNonDeterministic(){

        if((!discard.isEmpty()) && (!discard.get(0).IsFaceUp()))
            return true;

        for (int y=0; y<piles.size(); y++)
            if((!piles.get(y).isEmpty()) && (!piles.get(y).get(0).IsFaceUp()))
                return true;

        return false;
    }

    public void revealCard(){
        if((!discard.isEmpty()) && (!discard.get(0).IsFaceUp())) {
            discard.get(0).Flip();
            undiscovered.remove(discard.get(0));
            return;
        }

        for (int y=0; y<piles.size(); y++)
            if((!piles.get(y).isEmpty()) && (!piles.get(y).get(0).IsFaceUp())){
                piles.get(y).get(0).Flip();
                undiscovered.remove(piles.get(y).get(0));
                return;
            }

        return;
    }

    @Override
    public String toString() {
        String BoardState= new String();

        BoardState+="{" +stock.size();
        if(stock.size()<10)
            BoardState+=' ';
        BoardState+="} ";

        if(discard.size()>0)
            BoardState+=discard.get(0).toString();
        else
            BoardState+="[   ]";

        BoardState+="     ";

        for(int i=0; i<foundations.size(); i++)
            if(foundations.get(i).size()>0)
                BoardState+=foundations.get(i).get(0).toString();
            else
                BoardState+="[   ]";

        BoardState+='\n';
        BoardState+='\n';

        boolean NeedsToLookAgain=true;
        int depth=0;
        while (NeedsToLookAgain) {
            NeedsToLookAgain=false;
            for (int i=0; i<piles.size(); i++)
                if(piles.get(i).size()>depth) {
                    BoardState += piles.get(i).get(piles.get(i).size()-depth-1).toString();
                    NeedsToLookAgain = true;
                }
                else {
                    BoardState += "     ";
                }
            BoardState+='\n';
            depth++;
        }
        BoardState+='\n';

        return BoardState;
    }

    public String toString(Board b) {
        String BoardState= new String();

        BoardState+="{" +stock.size();
        if(stock.size()<10)
            BoardState+=' ';
        BoardState+="} ";

        if(discard.size()>0)
            BoardState+=discard.get(0).toString();
        else
            BoardState+="[   ]";

        BoardState+="     ";

        for(int i=0; i<foundations.size(); i++)
            if(foundations.get(i).size()>0)
                BoardState+=foundations.get(i).get(0).toString();
            else
                BoardState+="[   ]";

        BoardState+="     ";

        if(discard.size() > b.discard.size())
            BoardState+="Drawn a card";
        if(stock.size() > b.stock.size())
            BoardState+="Reset the Stock";

        if(discard.size() < b.discard.size()) {
            for (int i = 0; i < foundations.size(); i++)
                if (foundations.get(i).size() > b.foundations.get(i).size())
                    BoardState += "Moved a card from the Discard to the " + (i+1) + "th Foundation ";
            for (int i=0; i < piles.size(); i++)
                if (piles.get(i).size() > b.piles.get(i).size())
                    BoardState += "Moved a card from the Discard to the " + (i+1) + "th Pile ";
        }

        for (int y=0; y < piles.size(); y++)
            if (piles.get(y).size() < b.piles.get(y).size()){
                for (int i = 0; i < foundations.size(); i++)
                    if (foundations.get(i).size() > b.foundations.get(i).size())
                        BoardState += "Moved a card from the " + (y+1) + "th Pile to the " + (i+1) + "th Foundation ";
                for (int i=0; i < piles.size(); i++)
                    if (piles.get(i).size() > b.piles.get(i).size())
                        BoardState += "Moved a card from the " + (y+1) + "th Pile to the " + (i+1) + "th Pile ";
            }


        BoardState+='\n';
        BoardState+='\n';

        boolean NeedsToLookAgain=true;
        int depth=0;
        while (NeedsToLookAgain) {
            NeedsToLookAgain=false;
            for (int i=0; i<piles.size(); i++)
                if(piles.get(i).size()>depth) {
                    BoardState += piles.get(i).get(piles.get(i).size()-depth-1).toString();
                    NeedsToLookAgain = true;
                }
                else {
                    BoardState += "     ";
                }
            BoardState+='\n';
            depth++;
        }
        BoardState+='\n';

        return BoardState;
    }
}