import numpy as np
import matplotlib.pyplot as plt

def draw_centroids_arrows(primary_path, number_centroids, number_generation, time_in_day, use_time, reference_data):
    path = primary_path + str(number_centroids) + "-" + str(number_generation) + ".npy"
    vector_path = primary_path + "-vector" + str(number_centroids) + "-" + str(number_generation) + ".npy"

    raw_data = np.load(path)
    vectorized_data = np.load(vector_path)
    

    if use_time:
        start_times = reference_data[:, 0]
        start_time = vectorized_data[:, 0]

        start_time = start_time - np.min(start_times)

        start_time_between_0_and_1 = start_time / np.max(start_times)
        
        colors = [plt.cm.Reds(start_date) for start_date in start_time_between_0_and_1]

    else:
        colors = [plt.cm.Reds(1) for i in range(len(reference_data[:, 0]))]

    for point, color in zip(raw_data, colors):
        start_latitude = point[1]
        start_longitude = point[2]
        stop_latitude = point[4]
        stop_longitude = point[5]

        print("Arrow : ", start_longitude, start_latitude, stop_longitude, stop_latitude)
        plt.arrow(start_longitude, start_latitude, stop_longitude - start_longitude, stop_latitude - start_latitude, \
                  alpha=0.7, width=0.0001, head_width=0.001, fc="black", ec="red")
