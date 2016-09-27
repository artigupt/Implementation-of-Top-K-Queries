import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BTree<Key extends Comparable<Key>, Value>  {
    // max children per B-tree node = M-1
    // (must be even and greater than 2)
    private static final int M = 4;

    private Node root;       // root of the B-tree
    private int height;      // height of the B-tree
    private int N;           // number of key-value pairs in the B-tree

    // helper B-tree node data type
    private static final class Node {
        private int m;                             // number of children
        private Entry[] children = new Entry[M];   // the array of children

        // create a node with k children
        private Node(int k) {
            m = k;
        }
    }

    // internal nodes: only use key and next
    // external nodes: only use key and value
    private static class Entry {
        private int key;
        private Object val;
        private Node next;     // helper field to iterate over array entries
        public Entry(int key, Object val, Node next) {
            this.key  = key;
            this.val  = val;
            this.next = next;
        }
    }

    /**
     * Initializes an empty B-tree.
     */
    public BTree() {
        root = new Node(0);
    }
 
    /**
     * Returns true if this symbol table is empty.
     * @return <tt>true</tt> if this symbol table is empty; <tt>false</tt> otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return N;
    }

    /**
     * Returns the height of this B-tree (for debugging).
     *
     * @return the height of this B-tree
     */
    public int height() {
        return height;
    }


    /**
     * Returns the value associated with the given key.
     *
     * @param  key the key
     * @return the value associated with the given key if the key is in the symbol table
     *         and <tt>null</tt> if the key is not in the symbol table
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public int get(int key) {
        //if (key == null) throw new NullPointerException("key must not be null");
        return search(root, key, height);
    }

    private int search(Node x, int key, int ht) {
        Entry[] children = x.children;

        // external node
        if (ht == 0) {
            for (int j = 0; j < x.m; j++) {
                if (key == children[j].key){
                	return (int) children[j].val;
                }
            }
        }

        // internal node
        else {
            for (int j = 0; j < x.m; j++) {
                if (j+1 == x.m || (key < children[j+1].key))
                    return search(children[j].next, key, ht-1);
            }
        }
        return 0;
    }


    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is <tt>null</tt>, this effectively deletes the key from the symbol table.
     *
     * @param  key the key
     * @param  val the value
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public void put(int key, int val) {
        //if (key == null) throw new NullPointerException("key must not be null");
        Node u = insert(root, key, val, height); 
        N++;
        if (u == null) return;

        // need to split root
        Node t = new Node(2);
        t.children[0] = new Entry(root.children[0].key, null, root);
        t.children[1] = new Entry(u.children[0].key, null, u);
        root = t;
        height++;
    }

    private Node insert(Node h, int key, int val, int ht) {
        int j;
        Entry t = new Entry(key, val, null);

        // external node
        if (ht == 0) {
            for (j = 0; j < h.m; j++) {
                if (key < h.children[j].key) break;
            }
        }

        // internal node
        else {
            for (j = 0; j < h.m; j++) {
                if ((j+1 == h.m) || (key < h.children[j+1].key)) {
                    Node u = insert(h.children[j++].next, key, val, ht-1);
                    if (u == null) return null;
                    t.key = u.children[0].key;
                    t.next = u;
                    break;
                }
            }
        }

        for (int i = h.m; i > j; i--)
            h.children[i] = h.children[i-1];
        h.children[j] = t;
        h.m++;
        if (h.m < M) return null;
        else         return split(h);
    }

    // split node in half
    private Node split(Node h) {
        Node t = new Node(M/2);
        h.m = M/2;
        for (int j = 0; j < M/2; j++)
            t.children[j] = h.children[M/2+j]; 
        return t;    
    }

    /**
     * Returns a string representation of this B-tree (for debugging).
     *
     * @return a string representation of this B-tree.
     */
    public String toString() {
        return toString(root, height, "") + "\n";
    }

    private String toString(Node h, int ht, String indent) {
        StringBuilder s = new StringBuilder();
        Entry[] children = h.children;

        if (ht == 0) {
            for (int j = 0; j < h.m; j++) {
                s.append(indent + children[j].key + " " + children[j].val + "\n");
            }
        }
        else {
            for (int j = 0; j < h.m; j++) {
                if (j > 0) s.append(indent + "(" + children[j].key + ")\n");
                s.append(toString(children[j].next, ht-1, indent + "     "));
            }
        }
        return s.toString();
    }

    /*
    // comparison functions - make Comparable instead of Key to avoid casts
    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }
	
    private boolean eq(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }*/
    private static BTree<Integer, Integer> idIndex = new BTree<Integer, Integer>();
    private static ArrayList<BTree> attrIndex = new ArrayList<BTree>();
    
    private static HashMap<Integer, Integer> sortedIdMap = new HashMap<Integer, Integer>();
    private static ArrayList<HashMap<Integer,Integer>> sortedAttrMaps = new ArrayList<HashMap<Integer,Integer>>();
    String result = "";
	ArrayList<HashMap<Integer, String>> maps = new ArrayList<HashMap<Integer, String>>();
    public static void main(String[] args) throws IOException {
        
    	int K = Integer.parseInt(args[0]);
    	int N = Integer.parseInt(args[1]);
    	BTree objInit = new BTree();
    	objInit.init(K,N);
    	
    	Scanner in = new Scanner(System.in);
    	String command = in.next();
		int[] weights = new int[N];
		for(int i=0;i<weights.length;i++){
			weights[i] = in.nextInt();
		}
		switch(command){
			case "run1":
				
				objInit.run1(weights, K);
				break;
			case "run2":
				
				objInit.run2(weights, K);
				break;
			case "run3":
				
				objInit.run3(weights, K);
				break;
		}
    }
    
    public void init(int K, int N) throws IOException{
    	Scanner sc = new Scanner(System.in);
    	sc.next();
    	String tfile = sc.next();
    	
    	if(tfile.contains(",")){
    		String[] fileNames = tfile.split(",");
    		sc.next();
    		String condition = sc.next();
    		String[] conditions = condition.split("=");
    		for(int i=0;i<fileNames.length;i++){
        		BufferedReader reader = new BufferedReader(new FileReader(fileNames[i]));
        		String[] cols = reader.readLine().split(","); 
        		HashMap<Integer, String> tempMap = new HashMap<Integer, String>();
        		int keyColumn = 0;
        		for(int x=0;x<cols.length;x++){
        			if(cols[x].equals(conditions[i].split("\\.")[1])){
        				keyColumn = x;
        				break;
        			}
        		}
        		String line;
        		while ((line = reader.readLine()) != null) {
        			tempMap.put(Integer.parseInt(line.split(",")[keyColumn]), line);
        			
        			
        		}
        		maps.add(tempMap );
    		}
     	}
    	else{
    		BufferedReader reader = new BufferedReader(new FileReader(tfile));
    		String[] col = reader.readLine().split(",");
    		
    		for(String each:col){
    			result+=each + "\t\t";
    		}
    		result+="Score";
    		HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
    	    ArrayList<HashMap<Integer,Integer>> attrMaps = new ArrayList<HashMap<Integer,Integer>>();

    		for(int i=1;i<col.length;i++){
    	    	attrIndex.add(new BTree<Integer, Integer>());
    	    	attrMaps.add(new HashMap<>());
    	    }
    	    String line;
    	    
    		while ((line = reader.readLine()) != null) {
    		    String[] cols = line.split(",");
    		    idIndex.put(Integer.parseInt(cols[0].toString()),Integer.parseInt(cols[0].toString()));
    		    idMap.put(Integer.parseInt(cols[0].toString()),Integer.parseInt(cols[0].toString()));
    		    for(int i=0;i<attrIndex.size();i++){
    			    attrIndex.get(i).put(Integer.parseInt(cols[0].toString()), Integer.parseInt(cols[i+1].toString()));
    			    attrMaps.get(i).put(Integer.parseInt(cols[0].toString()), Integer.parseInt(cols[i+1].toString()));
    		    }
    		}
    		sortedIdMap = sortByValues(idMap);
    		for(int i=0;i<attrMaps.size();i++){
    			sortedAttrMaps.add(sortByValues(attrMaps.get(i)));
    		}
    	}
		
    }
    
    public void run1(int[] weights, int K){
		
		Comparator<String> comparator = new StringComparator();
        PriorityQueue<String> queue = new PriorityQueue<String>(K, comparator);		
		int count = sortedAttrMaps.get(0).size()-1;
		ArrayList<Integer> keyList = new ArrayList<Integer>();
		while(true){
			for(int i=0;i<sortedAttrMaps.size();i++){
				Object[] keySet =  sortedAttrMaps.get(i).keySet().toArray();
				int key = Integer.parseInt(keySet[count].toString());
				if(keyList.contains(key)){
					continue ;
				}
				keyList.add(key);
				int value = (int) attrIndex.get(i).get(key);
				value = value * weights[i];
				int score = value;
				for(int j=0;j<sortedAttrMaps.size();j++){
					if(!(j==i)){
						int innerValue = (int) attrIndex.get(j).get(key);
						innerValue = innerValue * weights[j];
						score += innerValue;
					}
				}
				queue.offer(key+ "-" +score);
				if(queue.size()>K){
	        		queue.poll();
	        	}
				
			}
			count--;
			int threshold = 0;
			for(int i=0;i<sortedAttrMaps.size();i++){
				Object[] keySet =  sortedAttrMaps.get(i).keySet().toArray();
				int key = Integer.parseInt(keySet[count].toString());
				
				threshold += (int) attrIndex.get(i).get(key) * weights[i];
			}
		 	if(Integer.parseInt(queue.peek().split("-")[1].toString()) >= threshold){
		 		break;
		 	}
		}
		
		System.out.println(result);
		while(!(queue.size() == 0)){
			String tuple = "";
			String temp = queue.poll();
			int key = Integer.parseInt(temp.split("-")[0]);
			int score = Integer.parseInt(temp.split("-")[1]);
			tuple += idIndex.get(key);
			for(int i=0;i<attrIndex.size();i++){
				tuple += "\t\t" + attrIndex.get(i).get(key);
			}
			tuple += "\t\t" + score;
			System.out.println(tuple);
		}
	}
    
    public void run2(int[] weights, int K){
    	Comparator<String> comparator = new StringComparator();
        PriorityQueue<String> queue = new PriorityQueue<String>(K, comparator);	
        
        for(int key:sortedIdMap.keySet()){
        	int score = 0;
        	for(int j=0;j<attrIndex.size();j++){
            	score = score + (int) attrIndex.get(j).get(key) * (int) weights[j];
            }
        	queue.offer(key + "-" + score);
        	if(queue.size()>K){
        		queue.poll();
        	}
        }
        System.out.println(result);
		while(!(queue.size() == 0)){
			String tuple = "";
			String temp = queue.poll();
			int key = Integer.parseInt(temp.split("-")[0]);
			int score = Integer.parseInt(temp.split("-")[1]);
			tuple += idIndex.get(key);
			for(int i=0;i<attrIndex.size();i++){
				tuple += "\t\t" + attrIndex.get(i).get(key);
			}
			tuple += "\t\t" + score;
			System.out.println(tuple);
		}
    }
    
    public void run3(int[] weights, int K){
    	for(int i=0;i<maps.size();i++){
    		for(int j=0;j<maps.size();j++){
    			if(!(i==j)){
    				for(int key:maps.get(i).keySet()){
    					if(maps.get(j).containsKey(key)){
    						//resultMap.add(maps.get(i).get(key)+","+maps.get(j).get(key));
    					}
    				}
    			}
    		}
    	}
    }
    
    private static HashMap sortByValues(HashMap map) { 
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator(){ 
        	public int compare(Object o1, Object o2){
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                   .compareTo(((Map.Entry) (o2)).getValue());
             }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
               Map.Entry entry = (Map.Entry) it.next();
               sortedHashMap.put(entry.getKey(), entry.getValue());
        } 
        return sortedHashMap;
   }
    
    public class StringComparator implements Comparator<String> {
	    public int compare(String x, String y)
	    {
	        if (Integer.parseInt(x.split("-")[1]) < Integer.parseInt(y.split("-")[1]))
	        {
	            return -1;
	        }
	        if (Integer.parseInt(x.split("-")[1]) > Integer.parseInt(y.split("-")[1]))
	        {
	            return 1;
	        }
	        return 0;
	    }

    }
    
    public void run3(){
    	Scanner sc = new Scanner(System.in);
    	
    	
    }
    
}