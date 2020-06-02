package org.insa.graphs.algorithm.shortestpath;

import static org.junit.Assert.assertEquals;

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
			shortAlgorithm = new AStarAlgorithm(dataLength);
			fastAlgorithm = new AStarAlgorithm(dataTime);
			break;
		default:
			throw new IllegalArgumentException();
		}
		
		ShortestPathSolution shortestSolution = shortAlgorithm.run();
		ShortestPathSolution fastestSolution = fastAlgorithm.run();
		
		return new Solutions(shortestSolution, fastestSolution);
	}
	
	private void compareSolutions(ShortestPathSolution A, ShortestPathSolution B)
	{
		assertEquals(A.getStatus(), B.getStatus());
		
		if (A.getStatus().equals(Status.INFEASIBLE) || A.getStatus().equals(Status.UNKNOWN)
				|| B.getStatus().equals(Status.INFEASIBLE) || B.getStatus().equals(Status.UNKNOWN))
			return;
		
		assertEquals(A.getPath().getArcs().size(), B.getPath().getArcs().size());
		
		if (A.getPath().getArcs().size() <= 0)
			return;
		
		assertEquals(A.getPath().getOrigin(), B.getPath().getOrigin());
		assertEquals(A.getPath().getDestination(), B.getPath().getDestination());
		assertEquals(A.getPath().isValid(), B.getPath().isValid());
		assertEquals(A.getPath().getLength(), B.getPath().getLength(), 1e6);
		assertEquals(A.getPath().getMinimumTravelTime(), B.getPath().getMinimumTravelTime(), 1e6);
		
	}
	
	private void testDijkstraAndAStar() throws IOException
	{
		Solutions solutionsBellmanFord = runAlgorithm(Algo.BellmanFord);
		
		Solutions solutionsDijkstra = runAlgorithm(Algo.Dijkstra);
		compareSolutions(solutionsDijkstra.shortestSolution, solutionsBellmanFord.shortestSolution);
		compareSolutions(solutionsDijkstra.fastestSolution, solutionsBellmanFord.fastestSolution);

		Solutions solutionsAStar = runAlgorithm(Algo.AStar);
		compareSolutions(solutionsAStar.shortestSolution, solutionsBellmanFord.shortestSolution);
		compareSolutions(solutionsAStar.fastestSolution, solutionsBellmanFord.fastestSolution);
	}

	@Test
	public void testCarre() throws IOException {
		this.graph = getGraph("carre");
		
		// Single node
		this.origin = graph.get(9);
		this.destination = graph.get(9);
		testDijkstraAndAStar();

		// Simple path
		this.origin = graph.get(8);
		this.destination = graph.get(9);
		testDijkstraAndAStar();

		// Complex path
		this.origin = graph.get(9);
		this.destination = graph.get(11);
		testDijkstraAndAStar();
	}

	@Test
	public void testInsa() throws IOException {
		this.graph = getGraph("insa");
		
		// Single node
		this.origin = graph.get(526);
		this.destination = graph.get(526);
		testDijkstraAndAStar();

		// Easy path
		this.origin = graph.get(200);
		this.destination = graph.get(526);
		testDijkstraAndAStar();

		// Hard path
		this.origin = graph.get(230);
		this.destination = graph.get(955);
		testDijkstraAndAStar();
		
		// Impossible path
		this.origin = graph.get(183);
		this.destination = graph.get(864);
		testDijkstraAndAStar();
	}
	
	@Test
	public void testHauteGaronne() throws IOException {
		this.graph = getGraph("haute-garonne");
		
		// Single node
		this.origin = graph.get(1225);
		this.destination = graph.get(1225);
		testDijkstraAndAStar();

		// Easy path
		this.origin = graph.get(10997);
		this.destination = graph.get(34992);
		testDijkstraAndAStar();

		// Hard path
		this.origin = graph.get(72601);
		this.destination = graph.get(120930);
		testDijkstraAndAStar();
		
		// Impossible path
		this.origin = graph.get(142571);
		this.destination = graph.get(78165);
		testDijkstraAndAStar();
	}
}
