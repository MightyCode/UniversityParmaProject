import numpy as np

class DataParsed:
    IN_START_TIME_COL = 0
    IN_START_LAT_COL = 1
    IN_START_LON_COL = 2
    IN_STOP_TIME_COL = 3
    IN_STOP_LAT_COL = 4
    IN_STOP_LON_COL = 5

    OUT_START_TIME_COL = 0
    OUT_START_LAT_COL = 1
    OUT_START_LON_COL = 2
    OUT_STOP_TIME_COL = 3
    OUT_STOP_LAT_COL = 4
    OUT_STOP_LON_COL = 5

    OUT_START_LAT_COL_WITHOUT_TIME = 0
    OUT_START_LON_COL_WITHOUT_TIME = 1
    OUT_STOP_LAT_COL_WITHOUT_TIME = 2
    OUT_STOP_LON_COL_WITHOUT_TIME = 3

    def __init__(self, moves, use_hour_in_day: bool=False) -> None:
        self.use_hour_in_day = use_hour_in_day

        start_time_column = moves[:, DataParsed.IN_START_TIME_COL]
        stop_time_column = moves[:, DataParsed.IN_STOP_TIME_COL]

        start_latitude_column = moves[:, DataParsed.IN_START_LAT_COL]
        stop_latitude_column = moves[:, DataParsed.IN_STOP_LAT_COL]

        start_longitude_column = moves[:, DataParsed.IN_START_LON_COL]
        stop_longitude_column = moves[:, DataParsed.IN_STOP_LON_COL]

        self.start_longitude_mean = np.mean(start_longitude_column)
        self.start_longitude_std = np.std(start_longitude_column)

        self.stop_longitude_mean = np.mean(stop_longitude_column)
        self.stop_longitude_std = np.std(stop_longitude_column)

        self.start_latitude_mean = np.mean(start_latitude_column)
        self.start_latitude_std = np.std(start_latitude_column)

        self.stop_latitude_mean = np.mean(stop_latitude_column)
        self.stop_latitude_std = np.std(stop_latitude_column)

        start_time_numberized, stop_time_numberized = None, None

        if (use_hour_in_day):
            start_time_numberized, stop_time_numberized = self.numberized_time_in_day(start_time_column, stop_time_column)
        else :
            start_time_numberized, stop_time_numberized = self.numberized_time(start_time_column, stop_time_column)


        self.start_time_mean = np.mean(start_time_numberized)
        self.start_time_std = np.std(start_time_numberized)

        self.stop_time_mean = np.mean(stop_time_numberized)
        self.stop_time_std = np.std(stop_time_numberized)

    
    def numberized_time(self, start, stop):
        return [
            np.array([np.datetime64(x) for x in start]).astype(np.int64),
            np.array([np.datetime64(x) for x in stop]).astype(np.int64)
        ]
    

    def stringify_numberized_time(self, start, stop):
        return [
            start.astype(np.int64).astype('datetime64[s]'),
            stop.astype(np.int64).astype('datetime64[s]')
        ]

    def numberized_time_in_day(self, start, stop):
        return [
            np.array([np.datetime64(x) for x in start]).astype(np.int64) % (24 * 3600),
            np.array([np.datetime64(x) for x in stop]).astype(np.int64) % (24 * 3600)
        ]

    def standardize_data(self, moves, time_included:bool=True):
        start_time_column = moves[:, DataParsed.IN_START_TIME_COL]
        stop_time_column = moves[:, DataParsed.IN_STOP_TIME_COL]

        start_latitude_column = moves[:, DataParsed.IN_START_LAT_COL]
        stop_latitude_column = moves[:, DataParsed.IN_STOP_LAT_COL]
        
        start_longitude_column = moves[:, DataParsed.IN_START_LON_COL]
        stop_longitude_column = moves[:, DataParsed.IN_STOP_LON_COL]

        start_latitude_standardized = (start_latitude_column - self.start_latitude_mean) / self.start_latitude_std
        start_longitude_standardized = (start_longitude_column - self.start_longitude_mean) / self.start_longitude_std
        stop_latitude_standardized = (stop_latitude_column - self.stop_latitude_mean) / self.stop_latitude_std
        stop_longitude_standardized = (stop_longitude_column - self.stop_longitude_mean) / self.stop_longitude_std


        if (time_included):
            start_time_numberized, stop_time_numberized = None, None
            if self.use_hour_in_day:
                start_time_numberized, stop_time_numberized = self.numberized_time_in_day(start_time_column, stop_time_column)
            else:
                start_time_numberized, stop_time_numberized = self.numberized_time(start_time_column, stop_time_column)

            start_time_standardized = (start_time_numberized - self.start_time_mean) / self.start_time_std
            stop_time_standardized = (stop_time_numberized - self.stop_time_mean) / self.stop_time_std


            return np.column_stack((
                start_time_standardized, 
                start_latitude_standardized, 
                start_longitude_standardized,
                stop_time_standardized, 
                stop_latitude_standardized, 
                stop_longitude_standardized))
    
        return np.column_stack((
            start_latitude_standardized, 
            start_longitude_standardized,
            stop_latitude_standardized, 
            stop_longitude_standardized))

    
    def restore_data(self, data, time_included:bool=True):
        start_latitude_standardized = []
        stop_latitude_standardized = []

        start_longitude_standardized = []
        stop_longitude_standardized = []

        
        if time_included:
            start_time_standardized = data[:, DataParsed.OUT_START_TIME_COL]
            stop_time_standardized = data[:, DataParsed.OUT_STOP_TIME_COL]

            start_latitude_standardized = data[:, DataParsed.OUT_START_LAT_COL]
            stop_latitude_standardized = data[:, DataParsed.OUT_STOP_LAT_COL]

            start_longitude_standardized = data[:, DataParsed.OUT_START_LON_COL]
            stop_longitude_standardized = data[:, DataParsed.OUT_STOP_LON_COL]
        else:
            start_latitude_standardized = data[:, DataParsed.OUT_START_LAT_COL_WITHOUT_TIME]
            stop_latitude_standardized = data[:, DataParsed.OUT_STOP_LAT_COL_WITHOUT_TIME]

            start_longitude_standardized = data[:, DataParsed.OUT_START_LON_COL_WITHOUT_TIME]
            stop_longitude_standardized = data[:, DataParsed.OUT_STOP_LON_COL_WITHOUT_TIME]
        
        start_latitude = (start_latitude_standardized * self.start_latitude_std) + self.start_latitude_mean
        start_longitude = (start_longitude_standardized * self.start_longitude_std) + self.start_longitude_mean
        stop_latitude = (stop_latitude_standardized * self.stop_latitude_std) + self.stop_latitude_mean
        stop_longitude = (stop_longitude_standardized * self.stop_longitude_std) + self.stop_longitude_mean

        dtypes = []

        if time_included:
            start_time_numberized = (start_time_standardized * self.start_time_std) + self.start_time_mean
            stop_time_numberized = (stop_time_standardized * self.stop_time_std) + self.stop_time_mean
            
            start_time, stop_time = self.stringify_numberized_time(start_time_numberized, stop_time_numberized)
            
            dtypes = [
                ('start_time', 'datetime64[s]'), ('start_latitude', float), ('start_longitude', float),
                ('stop_time', 'datetime64[s]'), ('stop_latitude', float), ('stop_longitude', float)
            ]
        else:
            dtypes = [
                ('start_latitude', float), ('start_longitude', float),
                ('stop_latitude', float), ('stop_longitude', float)
            ]

        result = np.empty(len(start_latitude_standardized), dtype=dtypes)

        if time_included:
            result['start_time'] = start_time
            result['stop_time'] = stop_time
        
        
        result['start_latitude'] = start_latitude
        result['start_longitude'] = start_longitude
        result['stop_latitude'] = stop_latitude
        result['stop_longitude'] = stop_longitude

        return result