package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = this.getInputData();
        Graph graph = data.getGraph();

        final int nbNodes = graph.size();
        
        Label[] labels = new Label[nbNodes];
        
        for (int i = 0; i < nbNodes; ++i)
        {
        	labels[i] = new Label(graph.get(i));
        }
        
        labels[data.getOrigin().getId()].setCost(0);
        
        BinaryHeap<Label> heap = new BinaryHeap<Label>();
        heap.insert(labels[data.getOrigin().getId()]);
        
        this.notifyOriginProcessed(data.getOrigin());
        
        while (!labels[data.getDestination().getId()].isMarked() && !heap.isEmpty())
        {
        	Label current = heap.deleteMin();
        	current.mark();
        	this.notifyNodeMarked(current.getNode());
        	labels[current.getNode().getId()] = current;
        	
        	for (Arc successorArc : current.getNode().getSuccessors())
        	{
        		if (!data.isAllowed(successorArc))
        			continue;
        		
        		Node successorNode = successorArc.getDestination();
        		double weight = data.getCost(successorArc);
        		Label successorLabel = labels[successorNode.getId()];
        		
        		if (!successorLabel.isMarked())
        		{
        			if (successorLabel.getCost() > current.getCost() + weight)
        			{
        				if (successorLabel.getCost() == Double.POSITIVE_INFINITY)
        				{
        					this.notifyNodeReached(successorLabel.getNode());
        					if (successorLabel.getNode().equals(data.getDestination()))
        					{
        						this.notifyDestinationReached(data.getDestination());
        					}
        				}
        				try
        				{
        					heap.remove(successorLabel);
        				}
        				catch (ElementNotFoundException e) {}

        				successorLabel.setCost(current.getCost() + weight);
        				successorLabel.setFather(successorArc);
    					heap.insert(successorLabel);
        				labels[successorNode.getId()] = successorLabel;
        			}
        		}
        	}
        }
        
        ShortestPathSolution solution;
        if (labels[data.getDestination().getId()].isMarked())
        {
	        ArrayList<Arc> Arcs = new ArrayList<Arc>();
	        Node current = data.getDestination();
	        while (current.compareTo(data.getOrigin()) != 0)
	        {
	        	Arc arc = labels[current.getId()].getFather();
	        	Arcs.add(0, arc);
	        	current = arc.getOrigin();
	        }
	        Path shortestPath = new Path(data.getGraph(), Arcs);
	        solution = new ShortestPathSolution(data, Status.OPTIMAL, shortestPath);
        }
        else
        {
        	solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        
        return solution;
    }

}
