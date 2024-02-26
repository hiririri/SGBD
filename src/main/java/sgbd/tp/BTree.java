package sgbd.tp;

import java.util.List;
import java.util.stream.Stream;

public class BTree {
	private BTreeNode root;

	public BTree(int order) {
		if (order < 3) {
			throw new IllegalArgumentException("Order should be greater than or equal to 3");
		}
		root = new BTreeNode(order, this);
	}

	int search(int key) {
		return root.search(key);
	}

	public void setRoot(BTreeNode root) {
		this.root = root;
	}

	void insert(int key) {
		if (root.search(key) != -1) {
			return;
		}
		System.out.println("Insert ==> " + key);
		root.insert(key);
		System.out.println(this);
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
