package ca.ucalgary.dblab.netdriller.analysis;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ca.ucalgary.dblab.netdriller.data.GraphEdge;
import ca.ucalgary.dblab.netdriller.data.GraphNode;
import ca.ucalgary.dblab.netdriller.data.Network;
import ca.ucalgary.dblab.netdriller.data.NodeMetric;

import edu.uci.ics.jung.algorithms.blockmodel.StructurallyEquivalent;
import edu.uci.ics.jung.algorithms.blockmodel.VertexPartition;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.metrics.Metrics;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.graph.Graph;

public class MetricFinder {
	public NodeMetric[] ranks;
	public Collection<Set<GraphNode>> groups;
	
	public NodeMetric[] findBetweenness(Graph<GraphNode, GraphEdge> graph)
	{
		BetweennessCentrality<GraphNode, GraphEdge> ranker = new BetweennessCentrality<GraphNode, GraphEdge>(graph, true, false);
		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();
//		ranker.printRankings(true, true);

		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), ranker.getVertexRankScore(v));
		
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}
	
	public NodeMetric[] findEigenvector(Graph<GraphNode, GraphEdge> graph)
	{
		EigenvectorCentrality<GraphNode, GraphEdge> ranker = new EigenvectorCentrality<GraphNode, GraphEdge>(graph);
		ranker.evaluate();

		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), (Double)ranker.getVertexScore(v));
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}
	
	public NodeMetric[] findDegree(Graph<GraphNode, GraphEdge> graph)
	{
		DegreeScorer<GraphNode> ranker = new DegreeScorer<GraphNode>(graph);
		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), ranker.getVertexScore(v));
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}

	public NodeMetric[] findCloseness(Graph<GraphNode, GraphEdge> graph)
	{
		ClosenessCentrality<GraphNode, GraphEdge> ranker = new ClosenessCentrality<GraphNode, GraphEdge>(graph);
		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), ranker.getVertexScore(v));
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}
	//Shermin: Added a function for finding the authority
	public NodeMetric[] findAuthority(Graph<GraphNode, GraphEdge> graph)
	{
		HITS<GraphNode, GraphEdge> ranker=new HITS<GraphNode, GraphEdge> (graph);
		
		ranker.evaluate();

		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), (Double)ranker.getVertexScore(v).authority);
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}	
	//Shermin: Added a function for finding the hub
	public NodeMetric[] findHub(Graph<GraphNode, GraphEdge> graph)
	{
		HITS<GraphNode, GraphEdge> ranker=new HITS<GraphNode, GraphEdge> (graph);
		
		ranker.evaluate();

		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), (Double)ranker.getVertexScore(v).hub);
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}	
	public NodeMetric[] findClusteringCoeff(Graph<GraphNode, GraphEdge> graph)
	{
		Map<GraphNode, Double> coeff = Metrics.clusteringCoefficients(graph);

		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), (Double)coeff.get(v));
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}
	
	
	//Vertices i and j are structurally equivalent iff the set of i's neighbors is identical to the set of j's neighbors, with the exception of i and j themselves.
	public Map<String, GraphNode[]> findStructurallyEQ (Graph<GraphNode, GraphEdge> graph)
	{
		StructurallyEquivalent<GraphNode, GraphEdge> equivalents = new StructurallyEquivalent<GraphNode, GraphEdge>();
		VertexPartition<GraphNode, GraphEdge> equiv = equivalents.transform(graph);
		groups = equiv.getVertexPartitions();
		
		HashMap<String, GraphNode[]> resultMap = new HashMap<String, GraphNode[]>();
		int groupNum = 0;
		
		GraphNode[] oneSet;
		for(Set<GraphNode> eqSet: groups)
		{
			oneSet = new GraphNode[eqSet.size()];
			int i = 0;
			for(GraphNode gn : eqSet)
				oneSet[i++] = gn;
			resultMap.put("Group " + (groupNum++), oneSet);
		}
		return resultMap;
	}
	
	public double findDensity(Network network)
	{
		double sum = 0;
		for(int i = 0; i < network.getRow(); i++)
			for(int j = 0; j < network.getColumn(); j++)
				sum += network.getMatrix()[i][j];
		
		return (sum/(network.getColumn()*network.getRow()));
	}
	
}
