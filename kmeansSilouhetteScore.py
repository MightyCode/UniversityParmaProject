import pandas as pd
import numpy as np
from sklearn.cluster import KMeans as KMeansK
from sklearn.metrics import silhouette_score

from DataParsed import DataParsed
from Boundaries import Boundaries

if __name__ == "__main__" :
    boundaries = Boundaries(False)

    df = pd.read_csv('BIT Mobility/Noleggi_Parma_2022.csv')
    mobility_data = boundaries.filter_pandas_data(df)
    dataParsed = DataParsed(mobility_data)
    X =  dataParsed.standardize_data(mobility_data)
    np.random.shuffle(X)


    num_clusters = [10, 20, 30]
    result = []

    for num in num_clusters:
        num_clusters = 30
        num_iterations = 100
        num_generations = 3

        labels = np.zeros((num_generations, X.shape[0]))

        for i in range(num_generations):
            kmeans = KMeansK(n_clusters=num_clusters, max_iter=num_iterations, n_init=1)
            labels[i] = kmeans.fit_predict(X)
            print("Done for number generation :", i)

        scores = []
        for i in range(num_generations):
            score = silhouette_score(X, labels[i])
            scores.append(score)
            print("Done for silhouette_score :", i)

        average_score = np.mean(scores)

        result.append(average_score)

    i = 0
    for num in num_clusters:
        print("Average Silhouette Coefficient for clusters(", num, ") : ", score[i])
        i += 1