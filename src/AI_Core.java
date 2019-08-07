/**

 * The core of the intelligence, A.I IS ALWAYS PLAYER 2
 *
 */
public class AI_Core {

    private double a, b;
    private int depthOfSearch, current_layer;
    public State_Tree gameTree;
    private Tree_Node index;
    public boolean state_changed = false;

    /**
     * Constructs the tree
     * @param depth the maximum depth that the core should go. Currently only 2
     * @param state The starting state
     * @param dice The starting dice
     */

    // Constructs the AI
    // Step 1: set maximum depth
    // Step 2: Create the state tree using the starting state
    // Step 3: Construct the first layer of movement
    // Step 4: construct the layers down to maximum depth
    public AI_Core(int depth, BoardState state, int[] dice) {

        depthOfSearch = depth;
        gameTree = new State_Tree(new Tree_Node(state, 0));

        MoveConstructor.AvailableMoves(state, dice, (byte) 2, 0);
        if (MoveConstructor.found)
            gameTree.AddMultipleNodes(MoveConstructor.found_states.toArray(
                    new BoardState[MoveConstructor.found_states.size()]), gameTree.getRoot(), 1);
        else
            gameTree.addNode(new Tree_Node(new BoardState(((Tree_Node) gameTree.getRoot()).getColumn_State()), gameTree.getRoot().depth + 1)
                    , gameTree.getRoot());
        index = (Tree_Node) gameTree.getRoot().GetFirstChild();
        current_layer = 0;
        updateLayers();
    }

    /**
     * Update the tree with the current state
     * @param state the current state
     */
    public void updateState(BoardState state) {
        gameTree.setRoot(new Tree_Node(new BoardState(state), gameTree.getRoot().depth + 1));
        current_layer = gameTree.getRoot().depth;
        state_changed = true;
    }

    /**
     * Update the dice with the current
     * @param dice the new/current dice
     */
    public void updateDice(int[] dice) {
        if (state_changed) {
            MoveConstructor.AvailableMoves(((Tree_Node) gameTree.getRoot()).getColumn_State()
                    , dice, (byte) 2, 0);
            if (MoveConstructor.found)
                gameTree.AddMultipleNodes(MoveConstructor.found_states.toArray(
                        new BoardState[MoveConstructor.found_states.size()]), gameTree.getRoot(), gameTree.getRoot().depth + 1);
            else {
                gameTree.addNode(new Tree_Node(new BoardState(((Tree_Node) gameTree.getRoot()).getColumn_State())
                        , gameTree.getRoot().depth + 1), gameTree.getRoot());
            }
            index = (Tree_Node) gameTree.getRoot().GetFirstChild();
            state_changed = false;
            updateLayers();
        } else {
            Node[] children = gameTree.getRoot().GetChildrenArray();
            for (Node aChildren : children) {
                Chance_Node node = (Chance_Node) aChildren;
                if (node.getDice1() == (byte) dice[0] && node.getDice2() == (byte) dice[1]
                        || node.getDice1() == (byte) dice[1] && node.getDice2() == (byte) dice[0]) {
                    gameTree.setRoot(aChildren);
                    current_layer = aChildren.depth;
                    break;
                }
            }
        }
    }

    /**
     * @param state, this state will always be a chance node, with the only exception being the first state of the game
     * @return the node chosen
     */
    public Node expectiMiniMax(Node state) {
        a = -Math.pow(10, 4);
        b = Math.pow(10, 4);
        Node maxN = expectiMax(state);
        if (maxN == null) {
            return null;
        }
        gameTree.setRoot(maxN);
        return maxN;
    }

    /**
     * The Max function
     * @param state the state to be checked
     * @return the max value state
     */
    private Node expectiMax(Node state) {
        if (state.depth == (depthOfSearch + current_layer)) {
            if (!state.val_set)
                state.setValue(((Chance_Node) state).getB());
            return state;
        }

        Node maxNode = state.GetFirstChild();

        for (Node child : state.children) {

            child.setValue(expectiMin(child).getValue());
            if (maxNode.getValue() < child.getValue()) {
                maxNode = child;
            }

            a = Math.max(a, maxNode.getValue());
        }
        state.setValue(maxNode.getValue());
        return maxNode;


    }

    /**
     * The Min function
     * @param state to be checked
     * @return the state with the lowest value
     */
    private Node expectiMin(Node state) {
        if (state.depth == (depthOfSearch + current_layer)) {
            if (!state.val_set)
                state.setValue(((Chance_Node) state).getA());
            return state;
        }
        Node minNode = state.GetFirstChild();

        for (Node child : state.children) {

            child.setValue(expectiMax(child).getValue());
            if (minNode.getValue() > child.getValue()) {
                minNode = child;
            }

            b = Math.min(b, minNode.getValue());
        }
        state.setValue(minNode.getValue());
        return minNode;
    }


    /**
     *Currently designed to construct only 2 layers.
     * Will be updated to construct more
     */
    // Should work just fine
    // USAGE: Call this method every time you want to scan the layers up until
    // the depth that you'll search
    private void updateLayers() {
        int player = 0;
        int i = 1;
        //May seem wrong but it isn't
        if (i % 2 != 0)
            player = 1;
        else
            player = 2;

        Tree_Node node = index;
        while (node != null) {
            NodeChildren(node, node.getColumn_State(), player, node.depth + 1);
            node = node.getNextRight();
        }

    }

    /**
     * Constructs the node of the requested layer from the given parent
     * @param parent the parent from whom the states will originate
     * @param state the state that is contained in the parent
     * @param player the player for whom the layer is constructed
     * @param depth the depth of the layer
     */

    private void NodeChildren(Node parent, BoardState state, int player, int depth) {

        int multiplier = 0;
        int child = 0;
        for (int i = 1; i < 7; i++) {
            for (int j = (1 + multiplier); j < 7; j++) {

                MoveConstructor.AvailableMoves(state, new int[]{i, j}, (byte) player, 1);
                Chance_Node node_c;
                if (MoveConstructor.found)
                    node_c = new Chance_Node(new byte[]{(byte) i, (byte) j}, MoveConstructor.a,
                            MoveConstructor.b, MoveConstructor.mean, MoveConstructor.total, depth);
                else if (parent instanceof Chance_Node) {
                    node_c = new Chance_Node((Chance_Node) parent);
                    node_c.dice1 = (byte)i;
                    node_c.dice2 = (byte)j;
                }
                else {
                    double value = parent.getValue();
                    node_c = new Chance_Node(new byte[]{(byte) i, (byte) j}, value,
                            value, value, 1, depth);
                }

                gameTree.addNode(node_c, parent);
                node_c.depth = depth;
                child++;
            }
            ++multiplier;
        }
    }
}