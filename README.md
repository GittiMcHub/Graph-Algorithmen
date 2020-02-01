# Graph algorithm implementations in Java with visualization and compared to JUNG Graph Lib

This project is the result out of the practical tasks in third semester B.Sc. Applied Information Science "Graph Theoretical Concepts and Algorithms" course.
As this lecture was given at a german university the following documentation is translated with deepl.com.

## Resources
- JUNG library http://jung.sourceforge.net/
- MessageConsole https://tips4java.wordpress.com/2008/11/08/message-console/ 
- LimitLinesDocumentListener https://github.com/kentcdodds/Java-Helper/blob/master/src/com/kentcdodds/javahelper/extras/LimitLinesDocumentListener.java
- http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/shortestpath/DijkstraShortestPath.html
- https://de.wikipedia.org/wiki/Dijkstra-Algorithmus


## Build
```
./gradlew buildJar
```
## Tests
Somehow the tests will not be successfull when ran via graldew, therfore the test task is simply commented out. When running the tests in the IDE (IntelliJ in my case) they are running just fine.

## Run
```
java -jar java-graph-algorithms-1.0.jar
```

# Implementation
## Data structures
### Vertex
Used as data type for vertices in the library.
- getName() → String (if no name given, Object HashCode)
- getMark() → Integer
- setMark(Integer) → void

### Edge
Used as data type for vertices in the library.
- getName() → String (if no name given, Object HashCode)
- getWeight() → Integer

## Algorithms

### BFS
BFS Algorithm
- getDistanceAndPath() → HashMap<String, Object> („distance“ → Integer, „path“ → String)

### Dijkstra
Returns distance and path using the Dijkstra algorithm for a weighted graph.
- getPath(Vertex, Vertex) → List<Vertex> ! UnreachableVertexException
- getDistance(Vertex, Vertex) → Integer ! UnreachableVertexException

### FloydWarshall
Returns distance and path using the Floyd-Warshall algorithm for a weighted graph.
- getPath(Vertex, Vertex) → List<Vertex> ! UnreachableVertexException
- getDistance(Vertex, Vertex) → Integer ! UnreachableVertexException


### FordFulkerson
Outputs the maximum flow within the transferred network.
- getMaxFlow() → Integer

### EdmondKarp
Outputs the maximum flow within the transferred network.
Unlike the Ford-Fulkerson algorithm, this algorithm uses the BFS algorithm to find a path from the source to the sink.
- getMaxFlow() → Integer

## .gka Fileformat

### Undirected graphs
```
<name node1>[ -> <name node2> [(<edge name>)] [: <edgeweight>]];
```
### Directed graphs
```
<name node1>[ -- <name node2> [(<edge name>)] [: <edgeweight>]];
```

### Format regex:
```
^([a-zA-ZÄÖÜäöü0-9]+)(\s(--|->)\s([a-zA-ZÄÖÜäöü0-9]+)(\s\(([a-zA-Z0-9ÄÖÜäöü0-9]+)\)\s{0,1}){0,1}(\s[:]\s([0-9]+)){0,1}){0,1};
```

## Utils 
For reading and writing GKA files
### GKAGraphDescription
Describes the graph from the GKA file as a whole and contains the GKAGraphComponentDescription objects.
- isDirected() → boolean
- isWeighted() → boolean
- doesEdgesHaveNames() → boolean
- addComponent(GKAGraphComponentDescription) → void
- getComponents() → List<GKAGraphComponentDescription>

### GKAGraphComponentDescription
Describes one line of the GKA file at a time.
- getSourceVertexName() → String
- hasTarget() → boolean
- getTargetVertexName() → String
- doesEdgeHasWeight() → boolean
- getEdgeWeight() → Integer
- doesEdgeHasName() → boolean
- getEdgeName() → String
- isEdgeDirected() → boolean


### GraphFactory
Creates weakly contiguous, plain directed graph, with two nodes alias source (aka source) and sink (aka target), where the source may have only outgoing edges and the sink may have only incoming edges. In addition, no loops or multiple edges may occur.
This means:
1. there must be at least 2 vertices with at least one edge
2. for n knots, there must be at least n-1 edges
3. at n knots there may be a maximum of (n-2)² + (n-2) edges
4. there must be a way from source to sink.

First the edges and vertices are created.
Then a blacklist is created, where all loops and all incoming and outgoing combinations from the source and from the sink are entered.
Then a coherent graph is created with a path from the source, through all vertices to the sink. The edges in between are chosen randomly. The selected combination is included in the blacklist to avoid multiple edges.
Then all other edges are randomly connected with vertices.

- createRandomNetwork(Integer, Integer) → Graph ! IllegalArgumentException
- createBigNetJUNGGraph() → Graph


# Design decisions
## Custom Vertex and Edge Classes
The library uses generics for vertices and edges. Since the vertices can be marked in our algorithms and we want to store the weight at an edge, we have written separate classes for each to map the properties desired for the tasks.

## Abstraction layer between GKA files and selected library
The reading and interpreting of the "*.gka" files should run independently of the selected library. This way, if we decide to use other libraries during the implementation or for the following tasks, we don't have to adapt the code for parsing and interpreting the "*.gka" files anymore, but have to write another "translation" from the abstraction classes to the graph types of the library in the GraphFactory class.
Therefore the classes GKAGraphDescription and GKAGraphComponentDescription were implemented, where GKAGraphDescription is the graph itself and GKAGraphComponentDescription is a component according to the GKA file format. These classes allow easy access to the properties of the graph described in the .gka file.

## Selection of the JUNG library
In addition to a simple interface and already implemented algorithms, the JUNG library offers easy to use components for visualizing the graphs.

## BFS algorithm
You call the BFS class with a source graph as parameter. method getDistanceAndPath gets a Source Vertex and a Target Vertex as parameter and returns a hash with the path (Key="path") and the distance (Key="distance"). The method first calls the method mark. The method mark marks all vertices as it is defined for the BFS algorithm.
The mark method in turn calls the calculatePath method. CalculatePath goes backwards from the target vertex to the source vertex and calculates the shortest path (arbitrary if there are several).
The distance and the path are then hashed. Before this is returned, reset() is executed to reset all markers.

## Dijkstra algorithm
The class is Mutable designed to fill a cache for performance reasons. This cache should ensure that the table for a source vertex is not rebuilt every time, but that the results from a previous query can be used.
Only source vertices that have not yet been queried require further execution of the algorithm.
The class offers the interfaces getPath and getDistance with the parameters Source and Target Vertex, which first try to read the result from the cache. If there is no table for the given source vertex, a new table is created. The method createNewTable is responsible for this.
First and foremost a two-dimensional integer array with the name "information" is used, which has three row indexes with N (N=number of vertices of the graph) columns each to record the distance, the predecessor and the status.

| V0 | V0+n | Vn+1 |
|---|---|---|
| DISTANCE | 0 | Integer.MAX_VALUE | Integer.MAX_VALUE |
| PREVIOUS | 0 | -1 | -1 |
| OK | false | false | false |

The help BiMap numbersToVerticies stores the index for each vertex and allows easy access via the index → Vertex or Vertex → Index.
The list "leftToCheckVertecies" contains all vertices that still have the OK value false, which are then processed for the algorithm.
The first vertex to be checked is taken, the one with the lowest distance is selected and its value is set to OK, so that "leftToCheckVertecies" is reduced by this vertex. Then the outgoing edges are taken from this vertex and the distance of the node as well as its predecessor are entered for each neighboring vertex.
Then the next vertex to be checked is taken.
If the distance is queried, it can be read from the information table whether the target vertex is connected to the source vertex. If this is not the case, the distance is Integer.MAX_VALUE and an UnreachableException is thrown. If the vertices are not found in the table, they are also not in the graph.

## Floyd Warshall algorithm
For each graph there are two matrices (two-dimensional arrays NxN, where N is the number of vertices): distance and transit. In the first, information about the shortest distance for each node pair is stored. In the second, one of the predecessors is stored for each node pair (not necessarily the first neighbor of the target). The shortest path can be determined with the recursive function described below.
There is a BiMap that assigns a number to all vertices. These numbers correspond to the indexes in the table and are used for easy access Index → Vertex and Vertex → Index.
The class gets the graph as parameter and immediately calls the method that fills both tables using the Flozyed-Warshall algorithm. The algorithm was implemented exactly as in the task.
The getDistance method finds a field in the matrix distance with the row index as source and the column index as target and passes the value of the field. If the value is Integer.Max/2 (infinite quasi), then an UnreachableVertexException is thrown. The same happens if Target or Source do not belong to the graph.
The getPath method works with the Matrix Transit, where the field with the row index of the SourceVertex and column index of the TargetVertex contains the predecessor on the way. The method also checks whether vertices belong to the graph or are unreachable (field value = -1).  
If everything is fine, then the source vertex is added to the path list.
Then the function recursivPath is called with the predecessor as source and target vertex as target. The method works recursively. It determines the new predecessor and calls itself twice with the following parameters: on source predecessor, predecessor target. If the predecessor is -1, Source, or Target, the recursion is at the end. RekursivPath returns the list of vertices which together with the SourceVertex form the return result of the getPath method.  


# Authors

* **GittiMcHub** - [GittiMcHub](https://github.com/GittiMcHub)
* **crazy-pancake** - [crazy-pancake](https://github.com/crazy-pancake)

# License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
