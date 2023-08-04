# MobilityViewer

## How to launch the project

I used IntelliJ IDEA, so it's recommended to use it. 
In the "project to launch" screen of IntelliJ select the pom.xml file.
Of dependencies downloads and other stuff will be done by the editor.

My library is located at MobilityViewer/mightylib/
The project is located at MobilityViewer/project/

Now the project open, with IntelliJ, you will have to give the path of Java in 
File/Project Structure/Project Settings/SDK.
The language level should be 11 or higher.

Right click on resources folder, "Mark directory as", "Resources root"
Right click on src folder, "Mark directory as", "Sources root"

Then to launch the app, click on the green right arrow at the left of method main inside the
MobilityViewer/project/main/Main.java file.

## How the app is working

A file is used to list all resources that will be used.

The application consists of a menu leading to several display screens. The buttons displayed in the
menu depend on the resource category selected. The basic category is the general ones.
It's not related to a file, it is used to show the map of Parma, the reduced graph 
and another scene that show path computed by Dijkstra algorithm.

## Type of file
For now, two types of files are supported.
- File with needs to generate the paths. It contains start and end points including start
and end time. The system includes a scene to generate the paths, 
and a scene to show the simulation of movements. Buttons leads to generation of paths scene,
visualisation of paths (with time).

- File with paths and no time information. The only thing we can do is to show path individually.

For both type of files, as it contains the geographic start and end points, additional buttons lead to scene
for start / end matrix computation and full matrix computation. These scenes permit analysis of major points
in the city.

## Existing resources
The current existing resources are:
- scooters moves, type of file: start/end (time)
- bike data, type of file: paths (no time)

## Adding new resources
Inside resources/data folder, displayableResources.json dictates how the app will behave.

Look after the both existing files to understand how to add a new one.