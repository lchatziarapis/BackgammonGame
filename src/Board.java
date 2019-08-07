import javax.swing.*;
import java.util.LinkedList;
import java.util.Random;


/**
 * The Master class that handles the board
 * refactored
 */

public class Board {

    public static BoardState current_state;
    public static BoardState old_state;
    public static int selected = -5;
    public static Chip_view selected_view = null;
    public LinkedList<Chip_view>[] views = null;
    public static int[] dice = new int[2];
    public static int currently_playing = 1;
    private boolean core_active = false;
    private boolean used1 = false;
    private boolean used2 = false;
    private boolean Double = false;
    private int usages = 4;
    public AI_Core core;

    /**
     * The board constructor
     */
    public Board() {

        init();
    }

    /**
     * The board initializer. It store the correct chip representation
     * in the proper column
     */
    private void init() {
        byte[][] temp = new byte[24][];

        for (int i = 0; i < 24; i++)
            temp[i] = new byte[0];

        temp[0] = new byte[2];
        temp[23] = new byte[2];
        for (int i = 0; i < 2; i++) {
            temp[0][i] = 2;
            temp[23][i] = 1;
        }

        temp[5] = new byte[5];
        temp[11] = new byte[5];
        temp[12] = new byte[5];
        temp[18] = new byte[5];
        for (int i = 0; i < 5; i++) {
            temp[5][i] = 1;
            temp[11][i] = 2;
            temp[12][i] = 1;
            temp[18][i] = 2;
        }

        temp[7] = new byte[3];
        temp[16] = new byte[3];
        for (int i = 0; i < 3; i++) {
            temp[7][i] = 1;
            temp[16][i] = 2;
        }

        current_state = new BoardState(temp, (byte) 1);

    }

    /**
     * Return the chip_views tha visualize the chips
     * @return the chip_views list
     */
    public LinkedList<Chip_view>[] GetViews() {
        if (views == null)
            Create_Views();
        return views;
    }

    /**
     * Construct the chip_view objects
     */
    public void Create_Views() {
        this.views = new LinkedList[26];
        for(int i = 0; i < 26; i++){
            views[i] = new LinkedList<>();
        }
        Update_Views();
    }

    /**
     * Update the chip_view Objects
     */
    public void Update_Views() {
        byte[][] chips = current_state.GetColumns();
        int[] pos;
        for (int i = 0; i < 24; i++) {
            views[i].clear();
            for (int j = 0; j < chips[i].length; j++) {
                pos = GetColumnPosStart(i);
                if (i < 12)
                    if(chips[i].length>4)
                        views[i].add(new Chip_view((byte) i, chips[i][0], pos[0], pos[1] +(20 * j)));
                    else
                        views[i].add(new Chip_view((byte) i, chips[i][0], pos[0], pos[1] + (40 * j)));
                else
                    if(chips[i].length>4)
                        views[i].add(new Chip_view((byte) i, chips[i][0], pos[0], pos[1] - (20 * j)));
                    else
                        views[i].add(new Chip_view((byte) i, chips[i][0], pos[0], pos[1] - (40 * j)));
            }
        }

        views[24].clear();
        int capt = current_state.GetCaptured(1);
        for (int i = 0; i < capt; i++) {
            if(capt > 3)
                views[24].add(new Chip_view((byte) 30, (byte) 1, 350, 400 - (20 * i)));
            else
                views[24].add(new Chip_view((byte) 30, (byte) 1, 350, 400 - (40 * i)));
        }
        views[25].clear();
        capt = current_state.GetCaptured(2);
        for (int i = 0; i < capt; i++) {
            if(capt > 3)
                views[25].add(new Chip_view((byte) -10, (byte) 2, 350, 95 + (20 * i)));
            else
                views[25].add(new Chip_view((byte) -10, (byte) 2, 350, 95 + (40 * i)));
        }


    }

    /**
     * Roll the dice, update the movement variables
     * Update the AI_Core
     */
    public void RollDice() {
        Double = false;
        Random generator = new Random();
        int floor = 1;
        int sealing = 7;
        dice = new int[]{generator.nextInt(sealing - floor) + floor, generator.nextInt(sealing - floor) + floor};
        MainWindow.dice_1.setIcon(new ImageIcon(MainWindow.dice[dice[0] - 1]));
        MainWindow.dice_2.setIcon(new ImageIcon(MainWindow.dice[dice[1] - 1]));
        if (core_active)
            core.updateDice(dice);
        if (dice[0] == dice[1]) {
            usages = 4;
            Double = true;
        }
        used1 = false;
        used2 = false;
    }

    /**
     * Given a column, answer if it's possible to move with both dice
     * @param moving the origin column
     * @param state the origin state
     * @param dice the current dice
     * @param playing the current player
     * @return true or false for each dice
     */
    public static boolean[] ValidColumnsToMove(byte moving, BoardState state, int[] dice, int playing) {

        byte[][] Column_State = state.GetColumns();

        int move0 = dice[0];
        int move1 = dice[1];
        int first, second;

        boolean[] move = new boolean[2];

        //Translate the move depending on the player playing
        if (playing == 1) {
            if (moving == 30) {
                first = 24 - move0;
                second = 24 - move1;
            } else {
                first = moving - move0;
                second = moving - move1;
            }
        } else {
            if (moving == -10) {
                first = move0 - 1;
                second = move1 - 1;
            } else {
                first = moving + move0;
                second = moving + move1;
            }
        }

        if (first > 23 || first < 0)
            move[0] = true;
        else if (Column_State[first].length == 0 ||
                (Column_State[first].length > 1 && Column_State[first][0] == playing)
                || (Column_State[first].length == 1))
            move[0] = true;
        else
            move[0] = false;

        if (second > 23 || second < 0)
            move[1] = true;
        else if (Column_State[second].length == 0 ||
                (Column_State[second].length > 1 && Column_State[second][0] == playing)
                || (Column_State[second].length == 1))
            move[1] = true;
        else
            move[1] = false;

        return move;
    }

    /**
     * Same as the above but for only one dice
     * @param moving
     * @param dice
     * @param columns
     * @return
     */
    public static boolean ValidateNewMove(byte moving, int dice, byte[][] columns) {

        int first;
        int player;
        if (moving == (byte) 30) {
            first = 24 - dice;
            player = 1;
        } else if (moving == (byte) -10) {
            first = dice - 1;
            player = 2;
        } else if (columns[moving][0] == 1) {
            first = moving - dice;
            player = 1;
        } else {
            first = moving + dice;
            player = 2;
        }
        //wow that's one big statement
        return (first > 23) || (first < 0) || columns[first].length == 0
                || (columns[first].length == 1
                || columns[first].length > 1 && (columns[first][0] == player));

    }

    /**
     * Given coordinates return the proper column
     * @param x x-plane coordinates
     * @param y y-plane coordinates
     * @return the column that matches the coordinates
     */
    public int GetColumn(int x, int y) {

        if (y >= 95 && y < 450) {
            if (x >= 35 && x < 340)
                return ((x - 45) / 50);
            else if (x >= 400 && x <= 690)
                return (((x - 405) / 50) + 6);
        } else if (y >= 500 && y < 830) {
            if (x >= 35 && x < 340)
                return (23 - ((x - 45) / 50));
            else if (x >= 400 && x <= 690)
                return (17 - ((x - 405) / 50));
        }
        return -1;
    }

    /**
     * Return the coordinates that match a column number
     * @param col the column number
     * @param cols chips in column optional argument
     * @return the column coordinates
     */
    public int[] GetColumnPos(int col, int cols) {

        int chipsInColumn = 0;
        if (cols == -1 && current_state != null)
            chipsInColumn = current_state.GetChipsColumn(col);
        else if (cols != -1)
            chipsInColumn = cols;

        if (col < 6)
            return new int[]{45 + (50 * (col)), 55 + 40 * ((chipsInColumn))};
        else if (col < 12)
            return new int[]{405 + (50 * (col - 6)), 55 + (40 * (chipsInColumn))};
        else if (col < 18)
            return new int[]{405 + (50 * (17 - col)), 665 - (40 * (chipsInColumn))};
        else
            return new int[]{45 + (50 * (23 - col)), 665 - (40 * (chipsInColumn))};
    }

    /**
     * Same as the above but does not account for the chips
     * that are in the column
     * @param col the column requested
     * @return the coordinates of that column
     */
    public int[] GetColumnPosStart(int col) {

        if (col < 6)
            return new int[]{45 + (50 * (col)), 95 };
        else if (col < 12)
            return new int[]{405 + (50 * (col - 6)), 95 };
        else if (col < 18)
            return new int[]{405 + (50 * (17 - col)), 625};
        else
            return new int[]{45 + (50 * (23 - col)), 625 };
    }

    /**
     * Set the selected view
     * @param view The view to be set
     */
    public static void SetSelected(Chip_view view) {
        selected = view.column;
        selected_view = view;
    }

    /**
     * Give the coordinates you desire to move a chip,
     * validate them, and make the move if it's legal
     * @param x the x-plane coordinates
     * @param y the y-plane coordinates
     */
    public void Move(int x, int y) {
        if(used1 && used2 || Double && usages==0)
            return;
        int column = GetColumn(x, y);
        if(current_state.GetCaptured(1)!=0 && selected_view.column!=30)
            return;
        if (!Double) {
            if(column == -1){
                if(withdrawCheck()){
                    if(selected_view.column-dice[0] < 0 && !used1){
                        used1 = true;
                        current_state.MoveChip(selected_view.column,dice[0]);
                    }
                    else if(selected_view.column-dice[1] < 0 && !used2){
                        used2 = true;
                        current_state.MoveChip(selected_view.column,dice[1]);
                    }
                    else
                        return;
                    views[selected_view.column].remove(selected_view);
                    selected = -5;
                    selected_view = null;
                    return;
                }
                else
                    return;
            }

            if (selected_view.column == 30) {
                if (24 - dice[0] == column && used1 || 24 - dice[1] == column && used2 || 24 - (dice[0] + dice[1]) == column && used1 && used2)
                    return;
                else if (24 - dice[0] == column && !used1) {
                    if (ValidateNewMove((byte) 30, dice[0], current_state.GetColumns())) {
                        used1 = true;
                        current_state.MoveChip(30, dice[0]);
                    }
                } else if (24 - dice[1] == column && !used2) {
                    if (ValidateNewMove((byte) 30, dice[1], current_state.GetColumns())) {
                        used2 = true;
                        current_state.MoveChip(30, dice[1]);
                    }
                } else if (24 - (dice[0] + dice[1]) == column && !used1 && !used2) {
                    if (ValidateNewMove((byte) 30, dice[1], current_state.GetColumns())) {
                        current_state.MoveChip(30, dice[1]);
                        if( ValidateNewMove((byte)(24-dice[1]), dice[0], current_state.GetColumns())){
                            used2 = true;
                            used1 = true;
                            current_state.MoveChip(24-dice[1], dice[0]);
                        }
                        else{
                            revert();
                            return;
                        }
                    }
                    else if( ValidateNewMove((byte) 30, dice[0], current_state.GetColumns())){
                        current_state.MoveChip(30, dice[0]);
                        if( ValidateNewMove((byte)(24-dice[0]), dice[1], current_state.GetColumns())){
                            used2 = true;
                            used1 = true;
                            current_state.MoveChip(24-dice[0], dice[1]);
                        }
                        else{
                            revert();
                            return;
                        }
                    }
                }
            } else if (selected_view.column - dice[0] != column && selected_view.column - dice[1] != column
                    && selected_view.column - (dice[0] + dice[1]) != column
                    ||(selected_view.column - dice[0] == column && used1
                    || selected_view.column - dice[1] == column && used2))
                return;
            else if (selected_view.column - dice[0] == column && !used1) {
                if (ValidateNewMove(selected_view.column, dice[0], current_state.GetColumns())) {
                    used1 = true;
                    current_state.MoveChip(selected_view.column, dice[0]);
                } else
                    return;
            } else if (selected_view.column - dice[1] == column && !used2){
                if (ValidateNewMove(selected_view.column, dice[1], current_state.GetColumns())) {
                    used2 = true;
                    current_state.MoveChip(selected_view.column, dice[1]);
                } else
                    return;
            }else if (selected_view.column - (dice[0] + dice[1]) == column && !used1 && !used2){
                    if (ValidateNewMove(selected_view.column, dice[1], current_state.GetColumns())) {
                        current_state.MoveChip(selected_view.column, dice[1]);
                        if( ValidateNewMove((byte)(selected_view.column-dice[1]), dice[0], current_state.GetColumns())){
                            used2 = true;
                            used1 = true;
                            current_state.MoveChip(selected_view.column-dice[1], dice[0]);
                        }
                        else{
                            revert();
                            return;
                        }
                    }
                    else if( ValidateNewMove((byte) selected_view.column, dice[0], current_state.GetColumns())){
                        current_state.MoveChip(selected_view.column, dice[0]);
                        if( ValidateNewMove((byte)(selected_view.column-dice[0]), dice[1], current_state.GetColumns())){
                            used2 = true;
                            used1 = true;
                            current_state.MoveChip(selected_view.column-dice[0], dice[1]);
                        }
                        else{
                            revert();
                            return;
                        }
                    }

            }
            else return;
        } else {
            if(column == -1){
                if(withdrawCheck()){
                    if(selected_view.column-dice[0] < 0 && usages!=0){
                        usages--;
                        current_state.MoveChip(selected_view.column,dice[0]);
                    }
                    //This needs some thought apparently. Currently it's wrong
                    else if((selected_view.column - (dice[0] * usages)) < 0){
                        int needed = -1 ;
                        for(int i=1; i <= usages;i++){
                            if(selected_view.column - (dice[0] * i) < 0){
                                needed = i;
                                break;
                            }
                        }

                        if(needed == -1)
                            return;

                        for(int i = 0; i < needed;i++){
                            if (ValidateNewMove((byte)(selected_view.column -(dice[0]*i)), dice[0] , current_state.GetColumns())) {
                                current_state.MoveChip(selected_view.column - dice[0] * i, dice[0]);
                                usages--;
                            }
                            else{
                                revert();
                                return;
                            }
                        }
                    }
                    else
                        return;

                    views[selected_view.column].remove(selected_view);
                    selected = -5;
                    selected_view = null;
                    return;
                }
                else
                    return;
            }
            if(selected_view.column == 30){
                if (usages != 0 && 24 - dice[0] != column
                        && (24 - column)/dice[0] > usages || usages == 0) {
                    return;
                } else {
                    if(24 - dice[0] == column) {
                        if (ValidateNewMove((byte)30, dice[0], current_state.GetColumns())) {
                            current_state.MoveChip(30, dice[0]);
                            usages--;
                        } else
                            return;
                    }
                    else if(current_state.GetCaptured(1) > 1){
                        if(ValidateNewMove((byte) 30, dice[0], current_state.GetColumns())){
                            current_state.MoveChip(30, dice[0]);
                            usages--;
                        }
                        else
                            return;
                    }
                    else if((24 - column)/dice[0] <= usages){
                        int needed = (24 - column)/dice[0];
                        for(int i = 0; i < needed;i++){
                            if(i == 0) {
                                if (ValidateNewMove((byte) 30, dice[0], current_state.GetColumns())) {
                                    current_state.MoveChip(30 , dice[0]);
                                    usages--;
                                } else {
                                    revert();
                                    return;
                                }
                            }
                            else{
                                if (ValidateNewMove((byte) (24-dice[0]*i), dice[0], current_state.GetColumns())) {
                                    current_state.MoveChip((24-dice[0]*i) , dice[0]);
                                    usages--;
                                } else {
                                    revert();
                                    return;
                                }
                            }
                        }
                    }
                    else
                        return;
                }
            }
            else if (usages != 0 && selected_view.column - dice[0] != column
                    && (selected_view.column - column)/dice[0] > usages || usages == 0) {
                return;
            } else {
                if(selected_view.column - dice[0] == column) {
                    if (ValidateNewMove(selected_view.column, dice[0], current_state.GetColumns())) {
                        current_state.MoveChip(selected_view.column, dice[0]);
                        usages--;
                    } else
                        return;
                }
                else if((selected_view.column - column)/dice[0] <= usages){
                    int needed = (selected_view.column - column)/dice[0];
                    for(int i = 0; i < needed;i++){
                        if (ValidateNewMove((byte)(selected_view.column -(dice[0]*i)), dice[0] , current_state.GetColumns())) {
                            current_state.MoveChip(selected_view.column - dice[0] * i, dice[0]);
                            usages--;
                        }
                        else{
                            revert();
                            return;
                        }
                    }
                }
            }
        }

        int[] pos = GetColumnPos(column, -1);
        if (pos == null) return;

        selected_view.column = (byte) column;
        selected_view.Move(pos[0], pos[1]);
        selected = -5;
        selected_view = null;
    }

    /**
     * Check if the player is able to bear-off/ withdraw chips
     * @return true if he can or false
     */
    private boolean withdrawCheck(){
        byte[] chips = current_state.GetPlayerChips(1);
        for(byte chip : chips){
            if(chip > 5)
                return false;
        }
        return true;
    }

    /**
     * Revert any changes made to the board to the previous stable/legal state
     */
    public void revert() {
        current_state = new BoardState(old_state);
        used1 = false;
        used2 = false;
        usages = 4;
    }


    /**
     * Manage the move Completion
     * @return true if the move was accepted
     */
    public boolean CompleteMove() {

        if (currently_playing == 2) {
            current_state.playing = 2;
            if (!core_active) {
                this.core = new AI_Core(2, current_state, dice);
                core_active = true;
            }
            current_state = ((Tree_Node) core.expectiMiniMax(core.gameTree.getRoot())).getColumn_State();
            return true;
        } else {
            current_state.playing = 1;

                if (!Double && (!used1 || !used2) || Double && usages!=0) {

                    MoveConstructor.AvailableMoves(old_state, dice, (byte) currently_playing, 0);

                    if (!MoveConstructor.found || MoveConstructor.found_states.size() == 0) {
                        core.updateState(new BoardState(current_state));
                        return true;
                    }

                    for (BoardState state : MoveConstructor.found_states) {
                        if (state.equals(current_state)) {
                            if (core_active)
                                //Make sure the move is valid, update the tree and move on, if not return false;
                                core.updateState(current_state);
                            return true;
                        }
                    }
                    used1 = false;
                    used2 = false;
                    usages = 4;
                    current_state = new BoardState(old_state);
                    return false;
                }
                if (core_active)
                    core.updateState(current_state);

                used1 = false;
                used2 = false;
                usages = 4;
                return true;
        }
    }
}