package datatypes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import constatnts.Constants;

public class CSR {
	int[] nodes;
	char[] nodeTypes;
	int[] offset;
	int[] edges;
	double[] wt;
	
	public static final int WEIGHTED_WALK = 0;
	public static final int UNIFORM_WALK = 1;
	
	public CSR (){}
	
	public void initNodes(int size) {
		nodes = new int[size];
		offset = new int[size + 1];
		nodeTypes = new char[size];
		
		System.out.println("Created node set");
	}
	
	public int[] getNodes() {
		return nodes;
	}

	public double[] getWt() {
		return wt;
	}

	public void setWt(double[] wt) {
		this.wt = wt;
	}

	public void setNodes(int[] nodes) {
		this.nodes = nodes;
	}

	public char[] getNodeTypes() {
		return nodeTypes;
	}

	public void setNodeTypes(char[] nodeTypes) {
		this.nodeTypes = nodeTypes;
	}

	public int[] getOffset() {
		return offset;
	}

	public void setOffset(int[] offset) {
		this.offset = offset;
	}

	public int[] getEdges() {
		return edges;
	}

	public void setEdges(int[] edges) {
		this.edges = edges;
	}

	public void initEdges(int size) {
		edges = new int[size];
		wt = new double[size];
		
		System.out.println("Created node set");
	}
	
	public void init(int V, int E) {
		initNodes(V);
		initEdges(E);
	}

	public int nodeSize() {
		return nodes.length;
	}

	public int edgeSize() {
		return edges.length;
	}
	
	public int getDegree(int nodeInd) {
		return offset[nodeInd + 1] - offset[nodeInd];
	}
	
	public String[] getWalks(int walkSize, int numWalks, String pattern, int walkType) {
		String[] res = new String[numWalks];
		
		for(int i = 0; i < numWalks; i++) {
			
			if(i % 1000 == 0)
				System.out.println("checkpoint " + i);
			StringBuffer sb = new StringBuffer();
			int currNodeInd = (int)(Math.random() * (nodes.length));
			while(nodeTypes[currNodeInd] != pattern.charAt(0)) {
				currNodeInd = (int)(Math.random() * (nodes.length));
			}
			sb.append(nodes[currNodeInd]);
			boolean failFlag = false;
			for(int j = 1; j < walkSize; j++) {
				sb.append(Constants.sep);
				
				currNodeInd = getNextNode(currNodeInd, pattern.charAt(j%pattern.length()), walkType);
				
				if(currNodeInd == -1) {
					failFlag = true;
					i--;
					System.out.println("Failed walk no:" + i + " node:" + currNodeInd + " nodeType:" + pattern.charAt(j%pattern.length()));
					
					break;
				}
				
				sb.append(currNodeInd);
			}
			if(!failFlag) {
				res[i] = sb.toString();
			}
		}
		return res;
	}
	
	public int getNextNode(int currNodeInd, char nextPat, int walkType) {
//		if(getDegree(currNodeInd) == 0) {
//			throw new RuntimeException("No outgoing nodes from " + nodes[currNodeInd]);
//		}
//		if(getNodesOfType(currNodeInd, nextPat).length == 0) {
//			throw new RuntimeException("No next node of type " + nextPat + " found for " + currNodeInd);
//		}
		
		int nextNode = -1; 
		switch(walkType) {
			case WEIGHTED_WALK:
				nextNode = getNextWeightedNode(currNodeInd, nextPat);break;
			default: 
				nextNode = getNextUniformNode(currNodeInd, nextPat);
		}
		
		
		return nextNode;
	}
	
	private int[] getNodesOfType(int currNodeInd, char nextPat) {
		// TODO Auto-generated method stub
		int count = 0;
		for(int i = offset[currNodeInd]; i < offset[currNodeInd+1]; i++) {
			if(nodeTypes[edges[i]] == nextPat) {
				count++;
			}
		}
		
		if(count == 0) {
			return new int[0];
		}
		
		int[] res = new int[count];
		int ind = 0;
		for(int i = offset[currNodeInd]; i < offset[currNodeInd] + getDegree(currNodeInd); i++) {
			if(nodeTypes[edges[i]] == nextPat) {
				res[ind++] = edges[i];
			}
		}
		
		return res;
	}
	
	private double[] getWeightsOfType(int currNodeInd, char nextPat) {
		// TODO Auto-generated method stub
		int count = 0;
		for(int i = offset[currNodeInd]; i < offset[currNodeInd] + getDegree(currNodeInd); i++) {
			if(nodeTypes[edges[i]] == nextPat) {
				count++;
			}
		}
		
		if(count == 0) {
			return new double[0];
		}
		
		double[] res = new double[count];
		int ind = 0;
		for(int i = offset[currNodeInd]; i < offset[currNodeInd] + getDegree(currNodeInd); i++) {
			if(nodeTypes[edges[i]] == nextPat) {
				res[ind++] = wt[i];
			}
		}
		
		return res;
	}
	
	private static double[] getCumSum(double[] a) {
		double[] ret = new double[a.length + 1];
		ret[0] = 0;
		for(int i = 0; i < a.length; i++) {
			ret[i+1] = a[i] + ret[i];
		}
		return ret;
	}
	
	 private static int floorSearch(double arr[], int low,int high, double search){
		 if (low > high)
			 return -1;
		 if (search >= arr[high])
			 return high;
		 
		 int mid = (low + high) / 2;
		 if (arr[mid] == search)
			 return mid;
		 if (mid > 0 && arr[mid - 1] <= search && search < arr[mid])
			 return mid - 1;
		 if (search < arr[mid])
			 return floorSearch( arr, low,mid - 1, search);
		 return floorSearch(arr, mid + 1, high,search);
	 }

	private int getNextWeightedNode(int currNodeInd, char nextPat) {
		// TODO Auto-generated method stub
		int[] candidates = getNodesOfType(currNodeInd, nextPat);
		if(candidates.length == 0) {
			return -1;
		}
		double[] wts = getWeightsOfType(currNodeInd, nextPat);
		double[] cumSum = getCumSum(wts);
		double randomNum = Math.random() * ((cumSum[cumSum.length-1]));
		
		int nextNode = floorSearch(cumSum, 0, cumSum.length - 1, randomNum);
		return candidates[nextNode];
	}

	private int getNextUniformNode(int currNodeInd, char nextPat) {
		// TODO Auto-generated method stub
		int[] candidates = getNodesOfType(currNodeInd, nextPat);
		
		if(candidates.length == 0) {
			return -1;
		}
		
		int nextNode = (int)(Math.random() * (candidates.length));		
		return candidates[nextNode];
	}

	public static CSR getCSR(String nodesFile, String edgesFile) throws IOException {
		CSR res = new CSR();
		System.out.println("Opening files");
		
		BufferedReader reader1 = new BufferedReader(new FileReader(nodesFile));
		BufferedReader reader2 = new BufferedReader(new FileReader(edgesFile));
		
		System.out.println("Analysing files");
		
		String line = null;
		List<NodeEntry> nodesE = new ArrayList<>();
		List<EdgeEntry> edgesE = new ArrayList<>();
		
		while ((line = reader1.readLine()) != null) {
	        String[] x = line.split(Constants.sep);
	        NodeEntry x1 = new NodeEntry();
	        x1.node = Integer.parseInt(x[0]);
	        x1.nodeType = x[1].charAt(0);
	        nodesE.add(x1);
		}
		while ((line = reader2.readLine()) != null) {
			String[] x = line.split(Constants.sep);
	        EdgeEntry x1 = new EdgeEntry();
	        x1.node1 = Integer.parseInt(x[0]);
	        x1.node2 = Integer.parseInt(x[1]);
	        x1.wt = Double.parseDouble(x[2]);
	        edgesE.add(x1); 
		}
		
		Collections.sort(nodesE, new Comparator<NodeEntry>() {
		    @Override
		    public int compare(NodeEntry o1, NodeEntry o2) {
		    	
		    	if(o1.node == o2.node) {
		    		throw new RuntimeException("Node duplicates found!");
		    	}
		    	
		        return new Integer(o1.node).compareTo(o2.node);
		    }
		});
		
		Collections.sort(edgesE, new Comparator<EdgeEntry>() {
		    @Override
		    public int compare(EdgeEntry o1, EdgeEntry o2) {
		    	
		    	if(o1.node1 == o2.node1) {
		    		if(o1.node2 == o2.node2) {
		    			throw new RuntimeException("Edge duplicates found!");
		    		}
		    		return new Integer(o1.node2).compareTo(o2.node2);
		    	}
		    	
		        return new Integer(o1.node1).compareTo(o2.node1);
		    }
		});
		
		
		System.out.println("Creating CSR");
		res.init(nodesE.size(), edgesE.size());
		System.out.println("Constructing CSR");
		
		for(int i = 0; i < nodesE.size(); i++) {
	
			res.getNodes()[i] = nodesE.get(i).node;
			res.getNodeTypes()[i] = nodesE.get(i).nodeType;
			
		}
		int offset = 0;
		int i = 1;
		res.getNodes()[0] = offset;
		for(int j = 0; j < edgesE.size(); j++) {
			
			while(res.getNodes()[i-1] != edgesE.get(j).node1) {
				res.getOffset()[i++] = offset;
			}
			res.getEdges()[j] = edgesE.get(j).node2;
			res.getWt()[j] = edgesE.get(j).wt;
			
			offset++;
		}
		res.getOffset()[i] = edgesE.size() - 1;		
		
		System.out.println("Completed: (V,E) (" + res.nodeSize() + "," + res.edgeSize() + ")");
		
		return res;
	}
}
