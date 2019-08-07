import java.util.ArrayList;

public class Node {

	protected ArrayList<Node> children;
	protected Node parent;
	protected int depth;
	protected double value;

	protected boolean val_set = false;

	public Node () {
		children=new ArrayList<>() ;
	}

	public Node addChild(Node child){
		child.parent = this;
		//child.depth = depth;
	    this.children.add(child);
	    return child;
	}

	public void purge(){
		children.forEach(Node::purge);
        children.clear();
		children = null;
		parent = null;
	}
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public ArrayList<Node> getChildren() {
		return children;
	}
	public int getChildrenSize(){
		return children.size();
	}
	public Node getChild(int index){
		return children.get(index);
	}

	public Node[] GetChildrenArray(){
		return children.toArray(new Node[children.size()]);
	}

	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public void setChildren(ArrayList<Node> children) {
		this.children.clear();
        this.children = children;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public Node GetFirstChild(){
		return children.get(0);
	}


}
