package io.revx.core.search.filter;

import java.util.ArrayList;

public class Node {
  protected String data;

  protected ArrayList<Node> children;

  // constructors
  public Node() {
    children = new ArrayList<Node>(0);
    data = null;
  }

  public Node(String str) {
    children = new ArrayList<Node>(0);
    data = str;
  }

  public void setChild(Node n) {
    children.add(n);
  }

  public void setData(String str) {
    // children this node has
    data = str;
  }

  public Node getChild(int i) {
    return children.get(i);
  }

  public String getData() {
    return data;
  }

  public ArrayList<Node> getChildren() {
    return children;
  }

  public int noOfChildren() {
    return children.size();
  }

  public String toString() {
    return "" + data.toString() + "\n";
  }
}
