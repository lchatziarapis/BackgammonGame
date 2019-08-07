import java.util.HashSet;

public class MoveConstructor {

    public static HashSet<BoardState> found_states = new HashSet<>();
    public static double a, b, mean;
    public static int total;
    public static int illegals;
    public static int mode;
    public static int moves;
    public static int deepest_layer;
    private static int furthest, oohome;
    private static byte player;
    public static boolean found = false;

    /**
     * This function generates all the available legal moves given a dice, a player and a state
     *
     * @param state   the state from which the moves will be generated
     * @param dice    the dice to be used
     * @param playerp the player who plays currently
     * @param mode    mode 0 to not fill the a,b,mean values and mode 1 to fill them
     */
    public static void AvailableMoves(BoardState state, int[] dice, byte playerp, int mode) {
        MoveConstructor.mode = mode;
        found = false;
        deepest_layer = 0;
        found_states.clear();
        CleanSlate();

        state.playing = player = playerp;

        //If not double
        if (dice[0] != dice[1]) {

            boolean[] current_chips;
            int captured = state.GetCaptured(player);

            //If there is ONE captured chip
            if (captured == 1) {

                boolean[] move;

                //Find how that chip can move
                if (player == 1)
                    move = Board.ValidColumnsToMove((byte) 30, state, dice, 1);
                else
                    move = Board.ValidColumnsToMove((byte) -10, state, dice, 2);

                //If it can;t then return
                if (!move[0] && !move[1]) {
                    found = false;
                    return;
                }

                //If it can move by the first dice
                if (move[0]) {
                    //Move it and find if any chips will be able to move with the next dice
                    BoardState new_state = new BoardState(state);
                    new_state.ReleaseChip(player, dice[0]);
                    deepest_layer = 1;
                    byte[] chips = state.GetPlayerChips(player);
                    current_chips = PopulateNext(chips, dice[1], new_state.GetColumns());
                    boolean moved = false;
                    //Check if any chip will be able to move with the second dice
                    for (boolean ch : current_chips) {
                        if (ch)
                            moved = true;
                    }

                    //If at least one can move
                    if (moved)
                        TryMoveSingle(new_state, current_chips, chips, dice[0], dice[1], false, 2);
                        //if none can move and if the captured chip is not able to move with the second dice first
                    else if (!move[1]) {
                        if (DuplicateCheck(new_state)) {
                            if (mode == 1)
                                Eval(new_state.Evaluate());
                        }
                        found = true;
                        return;
                        //If the captured chip is able to move with the second dice first, repeat the above
                    } else {
                        BoardState new_state_2 = new BoardState(state);
                        new_state_2.ReleaseChip(player, dice[1]);
                        byte[] chips_2 = new_state_2.GetPlayerChips(player);

                        current_chips = PopulateNext(chips_2, dice[0], new_state_2.GetColumns());
                        moved = false;
                        for (boolean ch : current_chips) {
                            if (ch)
                                moved = true;
                        }

                        if (moved)
                            TryMoveSingle(new_state_2, current_chips, chips, dice[1], dice[0], false, 2);
                        else {
                            //If we can't use both dice make the legal move
                            if (dice[0] > dice[1])
                                if (DuplicateCheck(new_state)) {
                                    if (mode == 1)
                                        Eval(new_state.Evaluate());
                                } else {
                                    if (DuplicateCheck(new_state_2)) {
                                        if (mode == 1)
                                            Eval(new_state_2.Evaluate());
                                    }
                                }
                        }

                        found = true;
                        return;
                    }
                }
                //If the captured chip can move by the second dice
                if (move[1]) {

                    BoardState new_state = new BoardState(state);
                    new_state.ReleaseChip(player, dice[1]);
                    deepest_layer = 1;
                    byte[] chips = state.GetPlayerChips(player);
                    current_chips = PopulateNext(chips, dice[0], new_state.GetColumns());
                    boolean moved = false;
                    for (boolean ch : current_chips) {
                        if (ch)
                            moved = true;
                    }

                    if (moved)
                        TryMoveSingle(new_state, current_chips, chips, dice[0], dice[0], false, 2);
                    else if (!move[0]) {
                        if (DuplicateCheck(new_state)) {
                            if (mode == 1)
                                Eval(new_state.Evaluate());
                        }
                        found = true;
                        return;
                    }
                }

                if (total != 0 || (!found_states.isEmpty() && found_states.size() > 0))
                    found = true;

                return;
                //If the captured chips are more tha one
            } else if (captured > 1) {

                boolean[] move;

                if (player == 1)
                    move = Board.ValidColumnsToMove((byte) 30, state, dice, 1);
                else
                    move = Board.ValidColumnsToMove((byte) -10, state, dice, 2);

                if (!move[0] && !move[1]) {
                    found = false;
                    return;
                }

                BoardState new_state = new BoardState(state);

                if (move[0] && move[1]) {
                    new_state.ReleaseChip(player, dice[0]);
                    new_state.ReleaseChip(player, dice[1]);
                } else {
                    if (move[0] && !move[1] && dice[0] > dice[1]) {
                        new_state.ReleaseChip(player, dice[0]);
                    } else if (move[1] && !move[0] && dice[1] > dice[0]) {
                        new_state.ReleaseChip(player, dice[1]);
                    } else if (move[0] && dice[0] < dice[1]) {
                        new_state.ReleaseChip(player, dice[0]);
                    } else if (move[1] && dice[1] < dice[0]) {
                        new_state.ReleaseChip(player, dice[1]);
                    }
                }

                if (mode == 0)
                    found_states.add(new_state);
                else {
                    Eval(new_state.Evaluate());
                    new_state.purge();
                }

                if (total != 0 || (!found_states.isEmpty() && found_states.size() > 0))
                    found = true;
                return;
            }
            //If there are no captured chips then try move chips by using the first dice first
            byte[] chips = state.GetPlayerChips(player);
            current_chips = PopulateNext(chips, dice[0], state.GetColumns());

            TryMoveSingle(state, current_chips, chips, dice[0], dice[1], false, 1);

            chips = state.GetPlayerChips(player);
            current_chips = PopulateNext(chips, dice[1], state.GetColumns());

            //If there are no captured chips then try move chips by using the second dice first
            TryMoveSingle(state, current_chips, chips, dice[1], dice[0], false, 1);
        }
        //TODO refine the double constructor to cut back the moves at least in half
        //If double
        else {

            boolean[] current_chips;
            int moves = 4;
            BoardState capt_state = null;
            int captured = state.GetCaptured(player);

            if (captured > 0) {

                boolean move;
                if (player == 1)
                    move = Board.ValidateNewMove((byte) 30, dice[0], state.GetColumns());
                else
                    move = Board.ValidateNewMove((byte) -10, dice[0], state.GetColumns());

                if (!move) {
                    found = false;
                    return;
                }

                capt_state = new BoardState(state);
                while (captured != 0 && moves != 0) {
                    capt_state.ReleaseChip(player, dice[0]);
                    captured--;
                    moves--;
                }

                if (moves == 0) {
                    if (mode == 0) {
                        found_states.add(capt_state);
                    } else {
                        Eval(capt_state.Evaluate());
                    }
                    found = true;
                    return;
                }
            }

            int diced = dice[0];
            byte[] chips;
            BoardState new_state;
            if (moves != 4) {
                new_state = new BoardState(capt_state);
            } else {
                new_state = new BoardState(state);
            }

            current_chips = PopulateNext(chips = new_state.GetPlayerChips(player), diced, new_state.GetColumns());
            deepest_layer = moves;
            TryMoveDouble(new_state, current_chips, chips, diced, 0, moves);

        }

        if ((mode == 0 && !found_states.isEmpty()) || (total != 0 && mode == 1))
            found = true;
    }

    private static boolean[] PopulateNext(byte[] playing, int dice, byte[][] columns) {

        boolean[] moves = new boolean[playing.length];

        if (player == 2)
            furthest = 23;
        else
            furthest = 0;

        oohome = 0;

        int i = 0;
        for (byte aPlaying : playing) {

            if (player == 2) {
                if (aPlaying < furthest && aPlaying != 24) {
                    furthest = aPlaying;
                }
                if (aPlaying < 18) {
                    oohome += columns[aPlaying].length;
                }
            } else {
                if (aPlaying > furthest && aPlaying != -1) {
                    furthest = aPlaying;
                }
                if (aPlaying > 5) {
                    oohome += columns[aPlaying].length;
                }
            }

            moves[i++] = Board.ValidateNewMove(aPlaying, dice, columns);
        }

        return moves;
    }

    //Returns true if you don't withdraw
    //Should also take into account the furthest variable
    public static boolean WithdrawCheck(int position, int dice) {
        if (player == 1) {
            return (position - dice) > -1 || (furthest < 6 && position > -1);
        } else
            return (position + dice) < 24 || (furthest > 17 && position < 24);
    }

    public static boolean HomeRun(int position, int dice) {

        if (player == 2) {
            return position < 18 && (position + dice) > 17 && oohome == 1;
        } else {
            return position > 5 && (position - dice) < 6 && oohome == 1;
        }

    }

    private static boolean TryMoveDouble(BoardState state, boolean[] current_chips,
                                         byte[] chips, int diced, int removal, int layer) {
        int selfhome = oohome;
        int selfFurthest = furthest;
        boolean moved = false;

        for (int i = 0; i < current_chips.length; i++) {

            if (!current_chips[i])
                continue;
            if(layer == 4){
                if (!WithdrawCheck(chips[i], diced) || !((removal != 0 && HomeRun(chips[i], diced)) || removal == 0))
                    continue;
            }
            if(layer == 1)
                removal = 0;
            oohome = selfhome;
            furthest = selfFurthest;

            if (!WithdrawCheck(chips[i], diced) && oohome < 5-layer) {
                removal = oohome;
            }
            //The chip wants to withdraw and there are 3 others outside home
            else if (!WithdrawCheck(chips[i], diced) && oohome > 5-layer
                    || (!WithdrawCheck(chips[i], diced) && removal != 0 && oohome > 4-layer)) {
                continue;
            }

            BoardState new_state = new BoardState(state);
            new_state.MoveChip(chips[i], diced);

            if (layer != 4 && moves >= layer) {
                byte[] chips_n;
                //If the next diced can be played or if it can't and another chip combination has played all dice continue
                if (TryMoveDouble(new_state,
                        PopulateNext(chips_n = new_state.GetPlayerChips(player), diced, new_state.GetColumns()),
                        chips_n, diced, removal, layer + 1) || layer < deepest_layer) {
                    continue;
                }
            }

            moved = true;
            if (deepest_layer < layer) {
                deepest_layer = layer;
                found_states.clear();
                illegals = 0;
                if (mode == 1)
                    CleanSlate();
            }
            if (!DuplicateCheck(new_state))
                continue;
            if (mode == 1)
                Eval(new_state.Evaluate());
        }

        return moved;
    }

    private static boolean TryMoveSingle(BoardState state, boolean[] current_chips,
                                         byte[] chips, int dice1, int dice2, boolean withdrawing, int layer) {
        int selfhome = oohome;
        int selfFurthest = furthest;
        int number = current_chips.length;
        int dice = dice2;
        boolean moved = false;
        if (layer == 1)
            dice = dice1;

        for (int i = 0; i < number; i++) {

            oohome = selfhome;
            furthest = selfFurthest;
            if (layer == 1) {
                withdrawing = false;

                if (!current_chips[i])
                    continue;

                if (!WithdrawCheck(chips[i], dice)) {
                    if (oohome > 1)
                        continue;
                    else
                        withdrawing = true;
                }
            } else if (layer == 2) {
                if (current_chips[i] && WithdrawCheck(chips[i], dice)) {
                        if (withdrawing) {
                            if (oohome > 1 || (oohome > 0 && !HomeRun(chips[i], dice)))
                                continue;
                        }

                        BoardState new_state = new BoardState(state);
                        new_state.MoveChip(chips[i], dice);
                        moved = true;
                        if (deepest_layer < layer) {
                            deepest_layer = layer;
                            found_states.clear();
                            if (mode == 1) {
                                CleanSlate();
                            }
                        }

                        if (!DuplicateCheck(new_state))
                            continue;

                        if (mode == 1)
                            Eval(new_state.Evaluate());

                }
                continue;
            }

            BoardState new_state = new BoardState(state);
            new_state.MoveChip(chips[i], dice);

            if (layer == 1) {
                byte[] chips2 = new_state.GetPlayerChips(player);
                boolean[] new_chips = PopulateNext(chips2, dice2, new_state.GetColumns());
                if (TryMoveSingle(new_state, new_chips, chips2, dice1, dice2, withdrawing, 2))
                    continue;
                else if (layer < deepest_layer || withdrawing)
                    continue;
            }

            if (deepest_layer < layer)
                deepest_layer = layer;

            if (!DuplicateCheck(new_state))
                continue;
            if (mode == 1)
                Eval(new_state.Evaluate());


        }
        return moved;
    }

    private static void CleanSlate() {
        mean = 0;
        total = 0;
        a = 10 ^ 4;
        b = 10 ^ 4;
    }

    private static void Eval(int eval) {
        MoveConstructor.mean += eval;
        MoveConstructor.total++;
        if (eval > MoveConstructor.a) {
            MoveConstructor.a = eval;
        }
        if (eval < MoveConstructor.b) {
            MoveConstructor.b = eval;
        }
    }

    private static boolean DuplicateCheck(BoardState state) {
        for (BoardState stored : found_states) {
            if (stored.equals(state))
                return false;
        }
        found_states.add(state);
        return true;
    }
}