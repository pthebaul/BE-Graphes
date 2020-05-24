package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.model.Node;

import static org.insa.graphs.model.GraphStatistics.NO_MAXIMUM_SPEED;

public class AStarAlgorithm extends DijkstraAlgorithm {
	
	private Node destination;

	@Override
	protected Label createLabel(Node node)
	{
		double distanceToDestination = node.getPoint().distanceTo(this.destination.getPoint());
		if (this.data.getMode().equals(Mode.LENGTH))
		{
			return new LabelStar(node, distanceToDestination);
		}
		else if (this.data.getMode().equals(Mode.TIME))
		{
			return new LabelStar(node, distanceToDestination / getMaxSpeed());
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
	
	private double getMaxSpeed()
	{
		int dataMaxSpeed = this.data.getMaximumSpeed();
		int graphMaxSpeed = this.data.getGraph().getGraphInformation().getMaximumSpeed();
		
		int maxSpeedKmph;
		
		if (dataMaxSpeed == NO_MAXIMUM_SPEED && graphMaxSpeed == NO_MAXIMUM_SPEED)
		{
			maxSpeedKmph = 130;
		}
		else if (dataMaxSpeed == NO_MAXIMUM_SPEED ^ graphMaxSpeed == NO_MAXIMUM_SPEED)
		{
			maxSpeedKmph = Math.max(dataMaxSpeed, graphMaxSpeed);
		}
		else
		{
			maxSpeedKmph = Math.min(dataMaxSpeed, graphMaxSpeed);
		}
		
		return kmphToMps(maxSpeedKmph);
	}
	
	private double kmphToMps(int speedKmph)
	{
		return speedKmph / 3.6;
	}
	
    public AStarAlgorithm(ShortestPathData data) {
        super(data);
        this.destination = data.getDestination();
    }

}
