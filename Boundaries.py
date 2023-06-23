import numpy as np

class Boundaries:

    def __init__(self, should_modify_bounds) -> None:
        self.boundaries = [[44.7595, 10.260], [44.8389, 10.385]]

        self.should_modify_bounds = should_modify_bounds

    def can_be_modified(self) -> bool:
        return self.should_modify_bounds

    def check_position(self, position, index):
        if not self.should_modify_bounds:
            return

        if position < self.boundaries[0][index]:
            self.boundaries[0][index] = position
        elif position > self.boundaries[1][index]:
            self.boundaries[1][index] = position

    
    def in_bounds(self, lat, lon):
        return lat > self.boundaries[0][0] and lat < self.boundaries[1][0] \
              and lon > self.boundaries[0][1] and lon < self.boundaries[1][1]
    
    def filter_pandas_data(self, data) -> np.array:
        filtered_data = []

        for index, row in data.iterrows():
            if self.should_modify_bounds:
                    self.check_position(row["start_latitude"], 0)
                    self.check_position(row["stop_latitude"], 0)
                    self.check_position(row["start_longitude"], 1)
                    self.check_position(row["stop_longitude"], 1)
                    filtered_data.append(row)

            elif self.in_bounds(row["start_latitude"], row["start_longitude"]) \
                and self.in_bounds(row["stop_latitude"], row["stop_longitude"]):
                filtered_data.append(row)

        return np.array(filtered_data)

    def min_lon(self):
        return self.boundaries[0][1]
    
    def min_lat(self):
        return self.boundaries[0][0]

    def max_lon(self):
        return self.boundaries[1][1]

    def max_lat(self):
        return self.boundaries[1][0]

    def print(self):
        print("min_lat : ", self.boundaries[0][0], "max_lat :" , self.boundaries[1][0], \
               " min_lon : ", self.boundaries[0][1], "max_lon :" , self.boundaries[1][1])