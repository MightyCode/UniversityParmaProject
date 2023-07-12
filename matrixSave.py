import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.sparse import lil_matrix

from DataParsed import DataParsed
from Boundaries import Boundaries

NUMBER_CELLS_BY_COORD = 1000

if __name__ == "__main__":
    boundaries = Boundaries(False)

    df = pd.read_csv('Data/BIT Mobility/Noleggi_Parma_2022.csv')
    mobility_data = boundaries.filter_pandas_data(df)

    diff_lon = boundaries.max_lon() - boundaries.min_lon()
    diff_lat = boundaries.max_lat() - boundaries.min_lat()

    number_cells_lon = int(np.ceil(diff_lon * NUMBER_CELLS_BY_COORD))
    number_cells_lat = int(np.ceil(diff_lat * NUMBER_CELLS_BY_COORD))

    matrix = lil_matrix((number_cells_lat * number_cells_lon, number_cells_lat * number_cells_lon))

    print("Matrix size : ", matrix.shape)

    for data in mobility_data:
        start_time_column = data[DataParsed.IN_START_TIME_COL]
        stop_time_column = data[DataParsed.IN_STOP_TIME_COL]

        start_latitude_column = data[DataParsed.IN_START_LAT_COL]
        stop_latitude_column = data[DataParsed.IN_STOP_LAT_COL]

        start_longitude_column = data[DataParsed.IN_START_LON_COL]
        stop_longitude_column = data[DataParsed.IN_STOP_LON_COL]

        start_lat_cell = int((start_latitude_column - boundaries.min_lat()) * NUMBER_CELLS_BY_COORD)
        start_lon_cell = int((start_longitude_column - boundaries.min_lon()) * NUMBER_CELLS_BY_COORD)

        #print(start_lat_cell, start_lon_cell)

        end_lat_cell = int((stop_latitude_column - boundaries.min_lat()) * NUMBER_CELLS_BY_COORD)
        end_lon_cell = int((stop_longitude_column - boundaries.min_lon()) * NUMBER_CELLS_BY_COORD)

        #print(end_lat_cell, end_lon_cell)

        matrix[start_lat_cell * number_cells_lon + start_lon_cell, 
               end_lat_cell * number_cells_lon + end_lon_cell] += 1

    matrix = matrix.tocsr()  # Convert to Compressed Sparse Row (CSR) format

    # Plot
    plt.imshow(matrix.toarray(), cmap='hot', interpolation='nearest')
    plt.colorbar()
    plt.title("Matrix Heatmap")
    plt.show()

    U, S, V = np.linalg.svd(matrix.toarray())

    plt.plot(np.arange(1, len(S) + 1), S, 'bo-')
    plt.xlabel("Singular Value Index")
    plt.ylabel("Singular Value")
    plt.title("Singular Values (Diagonal Matrix D)")
    plt.grid(True)
    plt.show()

    for column in range(10):
        print(str(column) + " component : ")
        for i in range(len(U[column])):
            if U[column, i] > 0:
                print(i % number_cells_lon, int(i / number_cells_lon))

    rank = 2

    U_truncated = U[:, :rank]
    S_truncated = S[:rank]
    V_truncated = V[:rank, :]

    matrix_approximation = U_truncated @ np.diag(S_truncated) @ V_truncated

    plt.imshow(matrix_approximation, cmap='hot', interpolation='nearest')
    plt.colorbar()
    plt.title(f"Maximum Rank Approximation (Rank {rank})")
    plt.show()

    np.save("out/matrix-" + str(NUMBER_CELLS_BY_COORD), matrix.toarray())