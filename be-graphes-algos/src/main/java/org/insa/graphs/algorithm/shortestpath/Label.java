package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class Label implements Comparable<Label>{

	protected Node node;
	protected boolean mark;
	protected double costFromOrigin;
	protected Arc predecessorArc;
	
	public Label(Node node)
	{
		this.node = node;
		this.mark = false;
		this.costFromOrigin = Double.POSITIVE_INFINITY;
		this.predecessorArc = null;
	}
	
	public double getCost()
	{
		return this.costFromOrigin;
	}
	
	public void setCost(double cost)
	{
		this.costFromOrigin = cost;
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
	
	public void setPredecessorArc(Arc father)
	{
		this.predecessorArc = father;
	}
	
	public Arc getPredecessorArc()
	{
		return this.predecessorArc;
	}
	
	public double getTotalCost()
	{
		return this.costFromOrigin;
	}
	
	public double getAlternateCost()
	{
		return this.costFromOrigin;
	}

	@Override
	public int compareTo(Label other)
	{
		if (Math.abs(this.getTotalCost() - other.getTotalCost()) > 1e-6)
		{
			return Double.compare(this.getTotalCost(), other.getTotalCost());
		}
		else
		{
			return Double.compare(this.getAlternateCost(), other.getAlternateCost());
		}
	}
	
	@Override
	public String toString()
	{
		return "Node " + this.node.getId()
		+ ", costFromOrigin = " + this.costFromOrigin
		+ ((this.predecessorArc == null) ? ", no father" : ", father = " + this.predecessorArc.getOrigin().getId());
	}
}
