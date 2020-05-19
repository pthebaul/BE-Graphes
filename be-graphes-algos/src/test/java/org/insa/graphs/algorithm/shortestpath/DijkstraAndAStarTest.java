package org.insa.graphs.algorithm.shortestpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.GraphReader;
import org.junit.BeforeClass;
import org.junit.Test;

public class DijkstraAndAStarTest {
	
	private enum Algo {Dijkstra, BellmanFord, AStar};
	
	private class Solutions
	{
		public ShortestPathSolution shortestSolution;
		public ShortestPathSolution fastestSolution;
		public Solutions(ShortestPathSolution shortest, ShortestPathSolution fastest)
		{
			this.shortestSolution = shortest;
			this.fastestSolution = fastest;
		}
	}
	
	private final static String mapsDirectory = "C:/3MIC-IR/BE-Graphes/Maps/";
	private final static String mapExtension = ".mapgr";
	
	private Graph graph;
	private Node origin, destination;
	
	private static Graph getGraph(String mapName) throws IOException {
		final GraphReader reader = new BinaryGraphReader(new DataInputStream(
               new BufferedInputStream(new FileInputStream(mapsDirectory + mapName + mapExtension))));
		Graph result = reader.read();
		reader.close();
		return result;
	}
	
	private static ArcInspector lengthInspector;
	private static ArcInspector timeInspector;
	
	@BeforeClass
	public static void initialisation() {
		List<ArcInspector> arcInspectors = ArcInspectorFactory.getAllFilters();
		DijkstraAndAStarTest.lengthInspector = arcInspectors.get(0);
		DijkstraAndAStarTest.timeInspector = arcInspectors.get(2);
	}
	
	public Solutions runAlgorithm(Algo algo)
	{
		ShortestPathAlgorithm shortAlgorithm;
		ShortestPathAlgorithm fastAlgorithm;
		ShortestPathData dataLength = new ShortestPathData(this.graph, this.origin, this.destination, DijkstraAndAStarTest.lengthInspector);
		ShortestPathData dataTime = new ShortestPathData(this.graph, this.origin,this. destination, DijkstraAndAStarTest.timeInspector);
		
		switch (algo)
		{
		case Dijkstra:
			shortAlgorithm = new DijkstraAlgorithm(dataLength);
			fastAlgorithm = new DijkstraAlgorithm(dataTime);
			break;
		case BellmanFord:
			shortAlgorithm = new BellmanFordAlgorithm(dataLength);
			fastAlgorithm = new BellmanFordAlgorithm(dataTime);
			break;
		case AStar:
		default:
			throw new IllegalArgumentException();
		}
		
		ShortestPathSolution shortestSolution = shortAlgorithm.run();
		ShortestPathSolution fastestSolution = fastAlgorithm.run();
		
		return new Solutions(shortestSolution, fastestSolution);
	}
	
	private void compareSolutions(ShortestPathSolution A, ShortestPathSolution B)
	{
		// Unable to test Status, because Bellman-Ford returns Infeasible solutions for single nodes
		
		if (A.getStatus().equals(Status.INFEASIBLE) || A.getStatus().equals(Status.UNKNOWN)
				|| B.getStatus().equals(Status.INFEASIBLE) || B.getStatus().equals(Status.UNKNOWN))
			return;
		
		assertEquals(A.getPath().getArcs().size(), B.getPath().getArcs().size());
		
		if (A.getPath().getArcs().size() <= 0)
			return;
		
		assertEquals(A.getPath().getOrigin(), B.getPath().getOrigin());
		assertEquals(A.getPath().getDestination(), B.getPath().getDestination());
		assertEquals(A.getPath().getArcs().size(), B.getPath().getArcs().size());
		assertEquals(A.getPath().isValid(), B.getPath().isValid());
		assertEquals(A.getPath().getLength(), B.getPath().getLength(), 1e6);
		assertEquals(A.getPath().getMinimumTravelTime(), B.getPath().getMinimumTravelTime(), 1e6);
		assertEquals(A.getStatus(), B.getStatus());
		
	}
	
	private void testDijkstra() throws IOException
	{
		Solutions solutionsDijkstra = runAlgorithm(Algo.Dijkstra);
		Solutions solutionsBellmanFord = runAlgorithm(Algo.BellmanFord);

		compareSolutions(solutionsDijkstra.shortestSolution, solutionsBellmanFord.shortestSolution);
		compareSolutions(solutionsDijkstra.fastestSolution, solutionsBellmanFord.fastestSolution);
	}

	@Test
	public void testCarre() throws IOException {
		this.graph = getGraph("carre");
		
		// Single node
		this.origin = graph.get(9);
		this.destination = graph.get(9);
		testDijkstra();

		// Simple path
		this.origin = graph.get(8);
		this.destination = graph.get(9);
		testDijkstra();

		// Complex path
		this.origin = graph.get(9);
		this.destination = graph.get(11);
		testDijkstra();
	}
	/*
	@Test
	public void testInsa() throws IOException {
		this.graph = getGraph("insa");
		
		// Single node
		this.origin = graph.get(526);
		this.destination = graph.get(526);
		testDijkstra();

		// Easy path
		this.origin = graph.get(200);
		this.destination = graph.get(526);
		testDijkstra();

		// Hard path
		this.origin = graph.get(230);
		this.destination = graph.get(955);
		testDijkstra();
		
		// Impossible path
		this.origin = graph.get(183);
		this.destination = graph.get(864);
		testDijkstra();
	}
	
	@Test
	public void testBelgium() throws IOException {
		this.graph = getGraph("belgium");
		
		// Single node
		this.origin = graph.get(600626);
		this.destination = graph.get(600626);
		testDijkstra();

		// Easy path
		this.origin = graph.get(934886);
		this.destination = graph.get(398672);
		testDijkstra();

		// Hard path
		this.origin = graph.get(1018131);
		this.destination = graph.get(575257);
		testDijkstra();
		
		// Impossible path
		this.origin = graph.get(967283);
		this.destination = graph.get(179687);
		testDijkstra();
	}*/
}
