package ce301_lemnariu_dan;

class Card{
    final static int CLUBS = 0;
    final static int SPADES = 1;
    final static int HEARTHS = 2;
    final static int DIAMONDS = 3;
    int nr;
    int suit;
    boolean faceUp;

    Card(int nr, int suit, boolean face){
        this.nr=nr;
        this.suit=suit;
        this.faceUp=face;
    }

    Card(Card c) {
        this.nr=c.nr;
        this.suit=c.suit;
        this.faceUp= c.faceUp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Card))
            return false;

        Card other = (Card) o;

        return this.nr==other.nr && this.suit==other.suit && this.faceUp==other.faceUp;
    }

    public String toString() {

        if(!this.faceUp)
            return "[   ]";

        String c = new String();
        c+="[" + nr;
        if(nr<10)
            c+=' ';
        if(suit==CLUBS)
            c+="C";
        else if(suit==SPADES)
            c+="S";
        else if(suit==HEARTHS)
            c+="H";
        else if(suit==DIAMONDS)
            c+="D";
        c+="]";
        return c;
    }
    public void Flip(){
        faceUp = !faceUp;
    }

    public boolean isBlack(){
        if(suit==CLUBS||suit==SPADES)
            return true;
        else
            return false;
    }

    public boolean IsFaceUp(){
        return faceUp;
    }
}