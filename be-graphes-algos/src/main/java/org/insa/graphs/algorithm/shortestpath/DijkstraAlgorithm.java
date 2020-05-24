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
    
    protected Label createLabel(Node node)
    {
    	return new Label(node);
    }

    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = this.getInputData();
        Graph graph = data.getGraph();

        final int nbNodes = graph.size();
        
        Label[] labels = new Label[nbNodes];
        
        for (int i = 0; i < nbNodes; ++i)
        {
        	labels[i] = createLabel(graph.get(i));
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
        				successorLabel.setPredecessorArc(successorArc);
    					heap.insert(successorLabel);
        				labels[successorNode.getId()] = successorLabel;
        			}
        		}
        	}
        }
        
        if (labels[data.getDestination().getId()].getPredecessorArc() == null)
        {
        	return new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        
        ArrayList<Arc> arcs = new ArrayList<Arc>();
        
        Node currentNode = data.getDestination();
        while (!currentNode.equals(data.getOrigin()))
        {
        	Arc arc = labels[currentNode.getId()].getPredecessorArc();
        	arcs.add(0, arc);
        	currentNode = arc.getOrigin();
        }
        Path shortestPath = new Path(data.getGraph(), arcs);
        return new ShortestPathSolution(data, Status.OPTIMAL, shortestPath);
    }

}
