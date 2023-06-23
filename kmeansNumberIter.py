import pandas as pd
import numpy as np
from sklearn.cluster import KMeans as KMeansK
import matplotlib.pyplot as plt

import sklearn

from DataParsed import DataParsed
from Boundaries import Boundaries

if __name__ == "__main__" :
    boundaries = Boundaries(False)

    number_clusters = 350
    time_in_day = True
    include_time = True


    df = pd.read_csv('BIT Mobility/Noleggi_Parma_2022.csv')
    mobility_data = boundaries.filter_pandas_data(df)
    dataParsed = DataParsed(mobility_data, time_in_day)
    X =  dataParsed.standardize_data(mobility_data, include_time)
    np.random.shuffle(X)

    scores = []
    ranged = range(1, 30, 1)
    for i in ranged:
        kmeans = KMeansK(n_clusters=number_clusters, max_iter=i, n_init=30)
        labels = kmeans.fit_predict(X)

        scores.append(sklearn.metrics.calinski_harabasz_score(X, labels))
        print("Done for number of iter :", i)

    plt.plot(ranged, scores)
    plt.xlabel('Number of iterations')
    plt.ylabel('Calinski_harabasz_score')
    plt.title('Find the right number of iterations')
    plt.show()