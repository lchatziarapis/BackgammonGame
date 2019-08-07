
public class State_Tree {

	private Node root;
    private Tree_Node processing=new Tree_Node();
	static public int processed = 0;
	
	public State_Tree(Node root) {
		this.root = root;
		root.setDepth(0);
	}
	
	public Node getRoot() {
		return root;
	}
	
	public void setRoot(Node root) {
		if(root.parent!=null)
      		root.parent.children.remove(root);
        this.root.purge();
        this.root = root;
    }

    //Stitch the nodes together horizontally
	public void AddMultipleNodes(BoardState[] additions, Node parent, int depth){

        Tree_Node previous = new Tree_Node();
		if(additions == null || additions.length == 0) {
			parent.addChild(new Tree_Node(new BoardState(((Tree_Node)parent.parent).getColumn_State()),depth));
			return;
		}
		processed +=additions.length;
        for (BoardState addition : additions) {

            if(processing.depth==(parent.depth+1)){
                previous = processing;
            }

			Tree_Node temp = new Tree_Node(addition, depth);
            previous.nextRight = temp;
			parent.addChild(temp);
            previous = temp;
		}

        processing = previous;

	}

	public void addNode(Node nodeToAdd, Node parent){
		parent.addChild(nodeToAdd);
		if(nodeToAdd instanceof Tree_Node) nodeToAdd.setDepth(parent.depth +1);
		else if(nodeToAdd instanceof Chance_Node){
            nodeToAdd.setDepth(parent.depth);
        } //sta chance node de theloume n auksanoume to depth gt mas niazei gia to minimax
	}
}
