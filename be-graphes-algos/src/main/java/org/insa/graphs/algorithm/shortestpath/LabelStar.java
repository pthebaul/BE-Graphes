package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class LabelStar extends Label {
	
	private double estimatedCostToDestination;
	
	public double getHeuristic()
	{
		return this.estimatedCostToDestination;
	}

	public LabelStar(Node node) {
		this(node, 0);
	}
	
	public LabelStar(Node node, double estimatedCostToDestination)
	{
		super(node);
		this.estimatedCostToDestination = estimatedCostToDestination;
	}
	
	@Override
	public double getTotalCost()
	{
		return this.costFromOrigin + this.estimatedCostToDestination;
	}
	
	@Override
	public double getAlternateCost()
	{
		return this.estimatedCostToDestination;
	}
	
	@Override
	public String toString()
	{
		return "Node " + this.node.getId()
		+ ", totalCost = " + this.getTotalCost()
		+ ", costFromOrigin = " + this.costFromOrigin
		+ ", estimatedCostToDestination = " + this.estimatedCostToDestination
		+ ((this.predecessorArc == null) ? ", no father" : ", father = " + this.predecessorArc.getOrigin().getId());
	}
}
