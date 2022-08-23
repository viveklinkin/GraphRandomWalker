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
			StringBuffer sb = new StringBuffer();
			int currNodeInd = (int)(Math.random() * (nodes.length));
			while(nodeTypes[currNodeInd] != pattern.charAt(0)) {
				currNodeInd = (int)(Math.random() * (nodes.length));
			}
			sb.append(nodes[currNodeInd]);
			for(int j = 1; j < walkSize; j++) {
				sb.append(Constants.sep);
				
				currNodeInd = getNextNode(currNodeInd, pattern.charAt(j%pattern.length()), walkType);
				
				sb.append(currNodeInd);
			}
			res[i] = sb.toString();
		}
		return res;
	}
	
	public int getNextNode(int currNodeInd, char nextPat, int walkType) {
		if(getDegree(currNodeInd) == 0) {
			throw new RuntimeException("No outgoing nodes from " + nodes[currNodeInd]);
		}
		if(getNodesOfType(currNodeInd, nextPat).length == 0) {
			throw new RuntimeException("No next node of type " + nextPat + " found for " + currNodeInd);
		}
		
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
			throw new RuntimeException("No next node of type " + nextPat + " found for " + currNodeInd);
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
			throw new RuntimeException("No next node of type " + nextPat + " found for " + currNodeInd);
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
		double[] wts = getWeightsOfType(currNodeInd, nextPat);
		double[] cumSum = getCumSum(wts);
		double randomNum = Math.random() * ((cumSum[cumSum.length-1]));
		
		int nextNode = floorSearch(cumSum, 0, cumSum.length - 1, randomNum);
		return candidates[nextNode];
	}

	private int getNextUniformNode(int currNodeInd, char nextPat) {
		// TODO Auto-generated method stub
		int[] candidates = getNodesOfType(currNodeInd, nextPat);
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
		List<String> lines1 = new ArrayList<String>();
		List<String> lines2 = new ArrayList<String>();
		while ((line = reader1.readLine()) != null) {
	         lines1.add(line);
		}
		while ((line = reader2.readLine()) != null) {
	         lines2.add(line);
		}
		
		Collections.sort(lines1, new Comparator<String>() {
		    @Override
		    public int compare(String o1, String o2) {
		    	String[] o1obj = o1.split(Constants.sep);
		    	String[] o2obj = o2.split(Constants.sep);
		    	
		    	int i1 = Integer.parseInt(o1obj[0]);
		    	int i2 = Integer.parseInt(o2obj[0]);
		    	
		    	if(i1 == i2) {
		    		throw new RuntimeException("Node duplicates found!");
		    	}
		    	
		        return new Integer(i1).compareTo(i2);
		    }
		});
		
		Collections.sort(lines2, new Comparator<String>() {
		    @Override
		    public int compare(String o1, String o2) {
		    	String[] o1obj = o1.split(Constants.sep);
		    	String[] o2obj = o2.split(Constants.sep);
		    	
		    	int i1 = Integer.parseInt(o1obj[0]);
		    	int i2 = Integer.parseInt(o2obj[0]);
		    	
		    	int j1 = Integer.parseInt(o1obj[1]);
		    	int j2 = Integer.parseInt(o2obj[1]);
		    	
		    	
		    	if(i1 == i2) {
		    		if(j1 == j2) {
		    			throw new RuntimeException("Edge duplicates found!");
		    		}
		    		return new Integer(j1).compareTo(j2);
		    	}
		    	
		        return new Integer(i1).compareTo(i2);
		    }
		});
		
		
		System.out.println("Creating CSR");
		res.init(lines1.size(), lines2.size());
		System.out.println("Constructing CSR");
		
		for(int i = 0; i < lines1.size(); i++) {
			String x = lines1.get(i);
			String[] spl = x.split(Constants.sep);
			
			res.getNodes()[i] = Integer.parseInt(spl[0]);
			res.getNodeTypes()[i] = spl[1].charAt(0);
			
		}
		int offset = 0;
		int i = 1;
		res.getNodes()[0] = offset;
		for(int j = 0; j < lines2.size(); j++) {
			String x = lines2.get(j);
			String[] spl = x.split(Constants.sep);
			int n1 = Integer.parseInt(spl[0]);
			int n2 = Integer.parseInt(spl[1]);
			double wt = Double.parseDouble(spl[2]);
			
			while(res.getNodes()[i-1] != n1) {
				res.getOffset()[i++] = offset;
			}
			res.getEdges()[j] = n2;
			res.getWt()[j] = wt;
			
			offset++;
		}
		res.getOffset()[i] = lines2.size() - 1;		
		
		System.out.println("Completed: (V,E) (" + res.nodeSize() + "," + res.edgeSize() + ")");
		
		return res;
	}
}
