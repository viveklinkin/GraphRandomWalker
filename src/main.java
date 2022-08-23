import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import datatypes.CSR;

// Disclaimer - meant only for directed heterogeneous graphs
// TODO: add support for undirected.

public class main {
	
	public static String pattern = "aaabbc"; 
	/* 
	 * Pattern for walks. Make sure the 
	 * values are same as the node types specified in the nodes file.
	 */
	public static int walkCount = 1000; // Number of walks
	public static int walkSize = 12; // Number of steps
	
	public static String nodesFile = "nodes.csv";  // input format - CSV - nodeNumber, nodeType
	
	/*
	 * nodeNumber should be integer running continuously from 0
	 * node Type should be a single character - case sensitive. 
	 */
	
	public static String edgesFile = "edges.csv"; // input format - CSV - startNode, endNode, wt
	
	/*
	 * Not built to spot discrepancies in node vs edges. Will throw unknown exceptions.
	 */
	
	public static String outputFile = "walks.csv";
	
	public static void main(String[] args) throws IOException {
		
		CSR graph = CSR.getCSR(nodesFile, edgesFile);
		
		String[] walks = graph.getWalks(walkSize, walkCount, pattern, CSR.UNIFORM_WALK);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile)));
		
		for(int i = 0; i < walks.length; i++) {
			bw.write(walks[i]);
			bw.write("\n");
		}
		bw.flush();
		bw.close();
	}

}
