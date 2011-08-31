package cs2010.bst;

import java.util.Stack;
import java.util.LinkedList;

public class SplayTree {
	private class Node {
		private int key;
		private Node left;
		private Node right;

		public Node(int k) {
			key = k;
			left = right = null;
		}

		public int getKey() {
			return key;
		}

		public Node getLeft() {
			return left;
		}

		public Node getRight() {
			return right;
		}

		public void setKey(int k) {
			key = k;
		}

		public void setLeft(Node l) {
			left = l;
		}

		public void setRight(Node r) {
			right = r;
		}

		public Node rotateLeft() {
			assert(this.right != null);
			Node newRoot = this.right;
			Node orphanLeft = newRoot.getLeft();

			newRoot.setLeft(this);
			this.setRight(orphanLeft);
			return newRoot;
		}

		public Node rotateRight() {
			assert(this.left != null);
			Node newRoot = this.left;
			Node orphanRight = newRoot.getRight();

			newRoot.setRight(this);
			this.setLeft(orphanRight);
			return newRoot;
		}
	}

	enum DIRECTION {
		LEFT, RIGHT
	}

	private Node root;

	public SplayTree() {
		root = null;
	}

	/* Zig rotations:
	 *
	 *       5                  Splay                 -20
	 *     /   \                 -20                    \
	 *   -20    200             ----->                   5
	 *                                                    \
	 *                                                    200
	 *
	 * Zig-Zig rotations:
	 *
	 *            5             Splay         -100
	 *         /     \          -100          /    \
	 *       -20      357       ----->      -123   -20
	 *       / \                                    / \
	 *    -100 -5                                 -50  5
	 *    /  \                                        / \
	 *  -123 -50                                     -5  357
	 *
	 * Zig-Zag rotations:
	 *
	 *           5              Splay                   -5
	 *        /     \            -5                   /    \
	 *      -20     357         ----->              -20     5
	 *      /  \                                    /        \
	 *    -100  -5                                -100       357
	 *    /  \                                    /  \
	 * -123  -50                                -123  50
	 *
	 */

	public void insert(int k) {
		Node cur;
		Node parent;
		Node grandparent;

		Stack<Node> st = new Stack<Node>();
		Stack<DIRECTION> dirs = new Stack<DIRECTION>();
		int tmpKey;
		DIRECTION curDir = DIRECTION.LEFT;
		DIRECTION parentDir, grandparentDir;

		if (root == null) {
			cur = new Node(k);
			root = cur;
			return;
		}

		cur = root;
		parent = null;
		while (cur != null) {
			parent = cur;
			tmpKey = cur.getKey();
			st.push(parent);

			if (k < tmpKey) {
				cur = cur.getLeft();
				curDir = DIRECTION.LEFT;
				dirs.push(DIRECTION.LEFT);
			} else if (k == tmpKey) {
				return;
			} else {
				cur = cur.getRight();
				curDir = DIRECTION.RIGHT;
				dirs.push(DIRECTION.RIGHT);
			}
		}

		cur = new Node(k);
		if (curDir == DIRECTION.LEFT)
			parent.setLeft(cur);
		else
			parent.setRight(cur);

		st.push(cur);
		dirs.push(curDir);

		/* Hacks to splay the node up to root... */
		while (!st.isEmpty()) {
			cur = st.pop();
			curDir = dirs.pop();

			if (st.size() >= 2) {
				parent = st.pop();
				grandparent = st.pop();
				parentDir = dirs.pop();
				grandparentDir = dirs.pop();

				if (parentDir == grandparentDir) {
					/* Zig-Zig rotation */
					if (parentDir == DIRECTION.LEFT) {
						grandparent.rotateRight();
						cur = parent.rotateRight();
					} else {
						grandparent.rotateLeft();
						cur = parent.rotateLeft();
					}
				} else {
					/* Zig-Zag rotation */
					if (parentDir == DIRECTION.LEFT)
						cur = parent.rotateRight();
					else
						cur = parent.rotateLeft();

					if (grandparentDir == DIRECTION.LEFT) {
						grandparent.setLeft(cur);
						cur = grandparent.rotateRight();
					} else {
						grandparent.setRight(cur);
						cur = grandparent.rotateLeft();
					}
				}

				if (!st.isEmpty()) {
					Node ggp = st.peek();
					DIRECTION ggpdir = dirs.peek();

					if (ggpdir == DIRECTION.LEFT)
						ggp.setLeft(cur);
					else
						ggp.setRight(cur);
				}

				/*
				 * Handle case where we pop off great grand parent if
				 * st.size() == 1, so that we can rotate this new node
				 * to the root.
				 */
				st.push(cur);
				dirs.push(curDir);

			} else if (st.size() == 1) {
				/* Zig rotation */
				parent = st.pop();
				parentDir = dirs.pop();

				if (parentDir == DIRECTION.LEFT)
					cur = parent.rotateRight();
				else
					cur = parent.rotateLeft();
			} else {
				/* Nothing */
			}
		}

		root = cur;
	}

	public void printInner(Node n) {
		if (n == null)
			return;

		System.out.printf("Key: %d, lc: ", n.getKey());
		if (n.getLeft() != null)
			System.out.print(n.getLeft().getKey());
		else
			System.out.print("null");
		System.out.print(", rc: ");

		if (n.getRight() != null)
			System.out.print(n.getRight().getKey());
		else
			System.out.print("null");
		System.out.println();
		printInner(n.getLeft());
		printInner(n.getRight());
	}

	public void print() {
		if (root == null) {
			System.out.println("Empty tree");
			return;
		}

		printInner(root);
	}

	public static void main(String args[]) {
		/* Only test
		 *
		 * Insert the following keys:
		 * 13, 7, 3, 56, 9, 58, 162, 33
		 *
		 * This should print:
		 *
		 *              33
		 *            /    \
		 *           13    162
		 *          /       /
		 *         9      56
		 *        /         \
		 *       3           58
		 *        \
		 *         7
		 */
	}
}
