import matplotlib.pyplot as plt


if __name__ == "__main__":
    # Data
    data = [961, 682, 432, 246, 252, 256, 338, 324, 656, 969, 770, 936, 1196, 1808, 1237, 1565, 1619, 1695, 2263, 1952, 1621, 1235, 1155, 1052]

    print(len(data))
    # Hour labels
    hours = list(range(24))

    # Plotting the bar chart
    plt.bar(hours, data)

    # Labeling the axes and giving a title
    plt.xlabel('Hour')
    plt.ylabel('Number of Scooters')
    plt.title('Number of Scooters Used per Hour')

    # Displaying the plot
    plt.show()