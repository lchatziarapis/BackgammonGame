import java.util.LinkedList;

public class BoardState {

    private byte red_count, green_count;

    private byte[][] Chip_List;
    public LinkedList<Byte> red, green;

    //The number of chips in captivity, we do not care about anything else
    public byte red_captured,green_captured, playing;
    public boolean moved = false;
    public int evaluation = -25;

    /**
     * Primary Constructor
     * @param Column_state Column states
     * @param playing the player currently playing
     */
    public BoardState(byte[][] Column_state, byte playing){

        Chip_List = Column_state;
        red = new LinkedList<>();
        green = new LinkedList<>();

        for(int i=0;i<24;i++){

            if(Chip_List[i].length>0)
                if(Chip_List[i][0] == 2)
                    red.add((byte) i);
                else if(Chip_List[i][0] == 1)
                    green.add((byte)i);

        }

        red_captured = 0;
        green_captured = 0;
        red_count = 15;
        green_count = 15;
        this.playing = playing;
    }

    /**
     * Deep copy constructor
     * @param state the state to copy
     */
    public BoardState(BoardState state){

        playing = state.playing;
        red_count = state.red_count;
        green_count = state.green_count;
        red_captured = state.red_captured;
        green_captured = state.green_captured;

        byte[][] states = state.GetColumns();
        red = new LinkedList<>();
        green = new LinkedList<>();

        this.Chip_List = new byte[24][];

        for(int i=0; i < 24; i++){

            int length = states[i].length;
            Chip_List[i] = new byte[length];
            if(length!=0) {

                byte content = states[i][0];
                for(int p=0; p < length; p++)
                    Chip_List[i][p] = content;

                if (states[i][0] == 2)
                    red.add((byte)i);

                else if(states[i][0] == 1)
                    green.add((byte) i);

            }
        }
        red_captured = state.red_captured;
        green_captured = state.green_captured;
        this.evaluation = state.evaluation;

    }

    /**
     * Release the memory retained by the object
     */
    public void purge(){
        Chip_List = null;
        if(red != null)
            red.clear();
        if(green!=null)
            green.clear();
        red = null;
        green = null;
    }

    /**
     * Return the column state
     * @return the column states
     */
    public byte[][] GetColumns(){
        return Chip_List;
    }

    //Redundant
    public boolean ValidColumn(int column){
        return Chip_List[column][0] == playing;
    }

    public int GetChipsColumn(int column){
        return Chip_List[column].length;
    }

    public byte[] GetPlayerChips(int player){
        if(player == 2) {
            byte[] red_r = new byte[red.size()];
            for(int i=0;i < red.size();i++){
                red_r[i] = red.get(i);
            }
            return red_r;
        }
        else{
            byte[] green_r = new byte[green.size()];
            for(int i=0;i < green.size();i++){
                green_r[i] = green.get(i);
            }
            return green_r;
        }
    }

    public int GetCaptured(int player){
        if(player == 2 )
            return red_captured;
        else
            return green_captured;
    }

    public int GetOpCaptured(int player) {
        if (player == 2)
            return green_captured;
        else
            return red_captured;
    }

    public int GetWithdrawn(int player) {
        if (player == 2)
            return 15-red_count;
        else
            return 15-green_count;
    }

    public int GetOpWithdrawn(int player) {
        if (player == 2)
            return 15-green_count;
        else
            return 15-red_count;
    }

    private void Withdraw(int player){

        if(player == 2) {
            red_count--;
        }
        else {
            green_count--;
        }
    }

    private void Capture(int column){

        if(Chip_List[column][0] == 1) {
            green_captured++;
            green.removeFirstOccurrence((byte)column);
        }
        else{
            red_captured++;
            red.removeFirstOccurrence((byte)column);
        }
    }

    public void ReleaseChip(int player,int move){
        if(player == 1)
            MoveChip(30, move);
        else
            MoveChip(-10, move);
    }

    private void release(int player){
        if(player == 1)
            green_captured--;
        else
            red_captured--;
    }

    /**
     * Move the chip from an origin with the given dice
     * @param from the origin
     * @param dice the dice
     */
    public void MoveChip(int from, int dice){
        int new_pos;

        if(playing == 2) {

            if(from != (-10)){

                new_pos = from+dice;
                moved = true;

                if(Chip_List[from].length == 1) {
                    Chip_List[from] = new byte[0];
                    red.removeFirstOccurrence((byte)from);
                }
                else {
                    int length = Chip_List[from].length;

                    Chip_List[from] = new byte[length - 1];

                    for (int i = 0; i <(length-1);i++){
                        Chip_List[from][i] = playing;
                    }
                }

            }
            else{
                release(playing);
                new_pos = dice - 1;
                moved = true;
            }

            if (new_pos > 23)
                Withdraw(playing);

            else if (Chip_List[new_pos].length == 0) {

                Chip_List[new_pos] = new byte[]{playing};
                red.add((byte)new_pos);
            }

            else if(Chip_List[new_pos].length==1 && Chip_List[new_pos][0] == 1){
                Capture(new_pos);
                Chip_List[new_pos][0] = playing;
                red.add((byte)new_pos);
            }
            else{
                int length = Chip_List[new_pos].length;
                Chip_List[new_pos] = new byte[length+1];
                for(int i=0; i < length;i++){
                    Chip_List[new_pos][i] = 2;
                }
                Chip_List[new_pos][length] = 2;
            }
        }
        else{

            if(from != (30)){
                new_pos = from-dice;
                moved = true;
                if(Chip_List[from].length == 1) {
                    Chip_List[from] = new byte[0];
                    green.removeFirstOccurrence((byte)from);
                }
                else {
                    int length = Chip_List[from].length;
                    Chip_List[from] = new byte[length-1];
                    for (int i = 0; i <(length-1);i++){
                        Chip_List[from][i] = playing;
                    }
                }
            }
            else{
                release(playing);
                new_pos = 24 - dice;
                moved = true;
            }

            if (new_pos < 0)
                Withdraw(playing);

            else if (Chip_List[new_pos].length == 0) {
                Chip_List[new_pos] = new byte[]{playing};
                green.add((byte)new_pos);
            }
            else if(Chip_List[new_pos].length==1 && Chip_List[new_pos][0] == 2) {
                Capture(new_pos);
                Chip_List[new_pos][0] = playing;
                green.add((byte) new_pos);
            }
            else{
                int length = Chip_List[new_pos].length;
                Chip_List[new_pos] = new byte[length+1];
                for(int i=0; i < length;i++){
                    Chip_List[new_pos][i] = 1;
                }
                Chip_List[new_pos][length] =1;
            }
        }
    }

    /**
     * Evaluate the state
     * @return the evaluation
     */
    public int Evaluate(){
        this.evaluation = Heuristic.HeuristicValue(this);
        return evaluation;

    }

    /**
     * Self-explanatory
     * @param state to be compared against
     * @return true if identical
     */
    public boolean equals(Object state){
        if(state instanceof BoardState){
            BoardState temp = (BoardState)state;
            if(temp.GetCaptured(1) == GetCaptured(1) && temp.GetCaptured(2) == GetCaptured(2) && temp.playing == playing){
                for(int i=0; i <24;i++){
                    if(temp.Chip_List[i].length != Chip_List[i].length)
                        return false;
                    if(Chip_List[i].length !=0){
                        if(temp.Chip_List[i][0]!=Chip_List[i][0])
                            return false;
                    }

                }
                return true;
            }
        }
        return false;
    }
}