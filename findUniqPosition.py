import pandas as pd
import matplotlib.pyplot as plt
import math

if __name__ == "__main__":
    uniq_positions = []
    test = set()

    df = pd.read_csv('BIT Mobility/Noleggi_Parma_2022.csv')
    
    number_scooter_moves = len(df)

    for index, row in df.iterrows():        
        found_start: bool = False
        found_stop : bool = False
        for uniq_position in uniq_positions:
            if math.isclose(uniq_position[0], row["start_latitude"]) \
                and math.isclose(uniq_position[1], row["start_longitude"]):
                found_start = True
                uniq_position[2] += 1
                if found_stop:
                    break
            elif math.isclose(uniq_position[0], row["stop_latitude"]) \
                and math.isclose(uniq_position[1], row["stop_longitude"]):
                found_stop = True
                uniq_position[3] += 1
                if found_start:
                    break
        
        test.add(row["start_latitude"])
        test.add(row["stop_latitude"])

        if not found_start:
            uniq_positions.append([row["start_latitude"], row["start_longitude"], 1, 0])
            if math.isclose(row["start_latitude"], row["stop_latitude"]) \
                and math.isclose(row["start_longitude"], row["stop_longitude"]) :
                uniq_positions[-1][3] += 1
                found_stop = True

        if not found_stop:
            uniq_positions.append([row["stop_latitude"], row["stop_longitude"], 0, 1])

        print("Number case different : ", index * 2, "Number of different start lat", len(test))

        """start_lat = []
        start_lon = []

        stop_lat = []
        stop_lon = []

        # Display
        for uniq_position in uniq_positions:
            if uniq_position[2] > 0:
                start_lat.append(uniq_position[0])
                start_lon.append(uniq_position[1])

            if uniq_position[3] > 0:
                stop_lat.append(uniq_position[0])
                stop_lon.append(uniq_position[1])"""

        
        plt.scatter(stop_lon, stop_lat, color='magenta', s=4)
        plt.scatter(start_lon, start_lat, color='green', s=4)

        # Set the axis labels and title
        plt.xlabel('Longitude')
        plt.ylabel('Latitude')
        plt.title('Node Positions and Roads in Parma')

        # Display the plot
        plt.show()