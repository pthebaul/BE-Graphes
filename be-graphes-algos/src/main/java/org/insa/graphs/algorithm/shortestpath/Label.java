package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class Label implements Comparable<Label>{

	private Node node;
	private boolean mark;
	private double cost;
	private Arc father;
	
	public Label(Node node)
	{
		this.node = node;
		this.mark = false;
		this.cost = Double.POSITIVE_INFINITY;
		this.father = null;
	}
	
	public double getCost()
	{
		return this.cost;
	}
	
	public void setCost(double cost)
	{
		this.cost = cost;
	}
	
	public boolean isMarked()
	{
		return this.mark;
	}
	
	public void mark()
	{
		this.mark = true;
	}
	
	public Node getNode()
	{
		return this.node;
	}
	
	public void setFather(Arc father)
	{
		this.father = father;
	}
	
	public Arc getFather()
	{
		return this.father;
	}

	@Override
	public int compareTo(Label other)
	{
		if (this.getCost() < other.getCost())
			return -1;
		else if (this.getCost() == other.getCost())
			return 0;
		else
			return +1;
	}
}
