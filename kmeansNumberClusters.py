import pandas as pd
import numpy as np
from sklearn.cluster import KMeans as KMeansK
import matplotlib.pyplot as plt

import sklearn

from DataParsed import DataParsed
from Boundaries import Boundaries

if __name__ == "__main__" :
    FILE_NAME = 'BIT Mobility/Noleggi_Parma_2022.csv'

    boundaries = Boundaries(False)

    df = pd.read_csv(FILE_NAME)
    mobility_data = boundaries.filter_pandas_data(df)

    time_in_day = True
    include_time = True
    num_iter = 30

    dataParsed = DataParsed(mobility_data, time_in_day)
    X =  dataParsed.standardize_data(mobility_data, include_time)

    print(X)
    np.random.shuffle(X)

    scores = []
    ranged = range(2, 10, 1)
    for i in ranged:
        kmeans = KMeansK(n_clusters=i, max_iter=num_iter, n_init=10)
        labels = kmeans.fit_predict(X)

        scores.append(sklearn.metrics.calinski_harabasz_score(X, labels))
        print("Done for number of cluser :", i)

    plt.plot(ranged, scores)
    plt.xlabel('Number of Clusters')
    plt.ylabel('Calinski_harabasz_score')
    plt.title('Find the right number of clusters')
    plt.show()