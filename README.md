# University Parma Project

Project composed in two parts :

- Java visualisation part. All of this part will be described in the README inside the MobilityViewer folder.

- Python analysis part. It contains files to computed clusters, matrices and pictures of Parma.


## Scooters
### Clusters
You can launch the analysis parts to know more about clusters.
The data are parsed in three representations.
Raw data (start point / time, end point / time), time in day, no time used.
The moves of scooters can't be represented using clusters for all three representations.
In **kmeansNumberCluster** script, Calinski-Harabasz score is used to know which is the best number of clusters to used
which is one currently. 

### Plotting
**plotCentroidsArrow** is a script that displays the clusters in the map of Parma. For now, it is pointless as the uses of clusters are not relevant.
**plotRawData** is a script that downloads OpenStreetMap data of Parma in order to display the map, then plots the start points and end points of scooter.

### Matrix
**matrixSave** is a script that displays the matrix representation of scooters moves.
Java app is better for these tasks.