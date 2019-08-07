
public class Tree_Node extends Node{

	private BoardState state;

	
	private boolean terminal;
	public Tree_Node nextRight;

	public Tree_Node(){
		super();
		
	}
	public Tree_Node(BoardState state, int depth){
		this();
		this.state = state;
        this.depth = depth;
		terminal = false;
	}

	public void purge(){
        super.purge();
		state = null;
        nextRight = null;
	}

	public double getValue(){
		return state.Evaluate();
	}

	public Tree_Node getNextRight(){
		return this.nextRight;
	}

	public BoardState getColumn_State() {
		return state;
	}

	public void setTerminal(){
		terminal = true;
	}

	public boolean isTerminal(){
		return terminal;
	}
}
