import requests
import matplotlib.pyplot as plt
import pandas as pd

import plotCentroidsArrow

from DataParsed import DataParsed
from Boundaries import Boundaries

if __name__ == "__main__":
    # Define the relation ID for Parma
    relation_id = 43452

    boundaries = Boundaries(False)

    just_highways = False
    
    time_in_day = True
    include_time = False

    # Just
    overpass_query = None
    # Expend bound if necessary

    # Read pick up and deposit 
    df = pd.read_csv('BIT Mobility/Noleggi_Parma_2022.csv')
    mobility_data = boundaries.filter_pandas_data(df)
    dataParsed = DataParsed(mobility_data)
    standardized_data = dataParsed.standardize_data(mobility_data)
    
    number_scooter_moves = len(df)
    print("Number of case", number_scooter_moves)

    percentage = 0.1   

    print("Updated bounds :", end="")
    boundaries.print()
    
    if just_highways:
        overpass_query = f"""
            [out:json];
            relation({relation_id});
            (._;>;);
            out body;
        """
    else :
        overpass_query = f"""
            [out:json];
            area[name="Parma"]->.a;
            (
            way(area.a)["highway"~"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential)$"];
            node(w)->.nodes;
            way(bn.nodes)["highway"~"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential)$"];
            );
            out body;
            >;
            out skel qt;
        """

    # Send the query to the Overpass API endpoint
    response = requests.get(f"https://overpass-api.de/api/interpreter?data={overpass_query}")
    if response.status_code != 200:
        print("Error when fetching data of Parma streets : ", response.status_code)
        
        quit()

    # Parse the response data
    data = response.json()

    nodes = {}
    roads = {}
    
    percentage = 0.1
    element_done = 0 
    number = len(data["elements"])

    print("Number of nodes : ", number)

    node_lon = []
    node_lat = []

    for element in data["elements"]:
        if (element["type"] == "node"):
            if boundaries.in_bounds(element['lat'], element['lon']):
                nodes[element["id"]] = element
                node_lon.append(element['lon'])
                node_lat.append(element['lat'])

        element_done +=1
        if (element_done > percentage * number):
            print("Done ", (100 * percentage), "%")
            percentage += 0.1

    element_done = 0
    percentage = 0.1

    for element in data["elements"]:
        if (element["type"] == "way"):
            nodes[element["type"]] = element

            road_lon = []
            road_lat = []
            for node_id in element['nodes']:
                if node_id not in nodes.keys():
                    continue

                corresponding_node = nodes[node_id]
                road_lon.append(corresponding_node['lon'])
                road_lat.append(corresponding_node['lat'])
            

            plt.plot(road_lon, road_lat, color='yellow', linewidth=1)  # Plot the road as a gray line

        element_done += 1
        if (element_done > percentage * number):
            print("Done ", (100 * percentage), "%")
            percentage += 0.1

    plt.scatter(node_lon, node_lat, color='blue', s=2)

    plt.scatter(mobility_data[:, DataParsed.IN_STOP_LON_COL], mobility_data[:, DataParsed.IN_STOP_LAT_COL], color='magenta', s=4)
    plt.scatter(mobility_data[:, DataParsed.IN_START_LON_COL], mobility_data[:, DataParsed.IN_START_LAT_COL], color='green', s=4)

    out_file_name = "out/bit-mobility-centroid"

    if not include_time:
        out_file_name += "-withouttime"
    elif time_in_day:
        out_file_name += "-inday"
    else :
        out_file_name += "-normal"

    #plotCentroidsArrow.draw_centroids_arrows(out_file_name, 350, 0, time_in_day, include_time, standardized_data)

    # Set the axis labels and title
    plt.xlabel('Longitude')
    plt.ylabel('Latitude')
    plt.title('Node Positions and Roads in Parma')

    # Display the plot
    plt.show()