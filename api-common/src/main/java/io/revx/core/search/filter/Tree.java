package io.revx.core.search.filter;

public class Tree {

  private Node root;

  public Tree() {
    setRoot(null);
  }

  public Tree(Node n) {
    setRoot(n);
  }

  public Tree(String str) {
    setRoot(new Node(str));
  }

  protected Node getRoot() {
    return root;
  }

  protected void setRoot(Node n) {
    root = n;
  }

  public boolean isEmpty() {
    return getRoot() == null;
  }

  public String getData() {
    if (!isEmpty())
      return getRoot().getData();
    return null;
  }

  public Object getChild(int i) {
    return root.getChild(i);
  }

  public void setData(String str) {
    if (!isEmpty())
      getRoot().setData(str);
  }

  public void insert(Node p, Node c) {
    if (p != null)
      p.setChild(c);
  }

  public void print() {
    pretrav();
  }

  public void pretrav() {
    pretrav(getRoot());
  }

  protected void pretrav(Node t) {
    if (t == null)
      return;
    for (int i = 0; i < t.noOfChildren(); i++)
      pretrav(t.getChild(i));
  }

}
