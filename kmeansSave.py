import pandas as pd
import numpy as np
from sklearn.cluster import KMeans as KMeansK
import matplotlib.pyplot as plt

from DataParsed import DataParsed
from Boundaries import Boundaries

if __name__ == "__main__" :
    boundaries = Boundaries(False)

    max_iter = 10
    time_in_day = True
    include_time = True

    df = pd.read_csv('BIT Mobility/Noleggi_Parma_2022.csv')
    mobility_data = boundaries.filter_pandas_data(df)

    dataParsed = DataParsed(mobility_data, time_in_day)
    print(dataParsed)
    X =  dataParsed.standardize_data(mobility_data, include_time)
    print(X)

    restored = dataParsed.restore_data(X, include_time)

    print(restored)
    np.random.shuffle(X)

    for num in [350]:
        for generation in range(5):
            kmeansK = KMeansK(n_clusters=num, max_iter=max_iter, n_init=10)
            labels = kmeansK.fit_predict(X)
            centroids = kmeansK.cluster_centers_
            
            print("Done for clusters(", num, ") : ", generation)

            out_file_name = "out/bit-mobility-centroid"

            if not include_time:
                out_file_name += "-withouttime"
            elif time_in_day:
                out_file_name += "-inday"
            else :
                out_file_name += "-normal"

            end_file_name = str(num) + "-" + str(generation)

            np.save(out_file_name + "-vector" + end_file_name, centroids)
            np.savetxt(out_file_name + "-vector" + end_file_name + ".txt", centroids, delimiter=', ', fmt='%s')

            good_format = dataParsed.restore_data(centroids, include_time)
            np.save(out_file_name + end_file_name, good_format)

            fmt = '%s, %.8f, %.8f, %s, %.8f, %.8f'

            # Save the data using np.savetxt
            np.savetxt(out_file_name + end_file_name + ".txt", good_format, delimiter=', ', fmt=fmt)