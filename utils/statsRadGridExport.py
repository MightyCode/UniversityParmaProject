import pandas as pd
import matplotlib.pyplot as plt

def doAnalysisOnFile(month, year, version=-1):
    fileName = str(month).strip()
    if version >= 0:
        fileName += "-" + str(version)
    fileName +=  " " + str(year).strip() + ".csv"

    df = pd.read_csv('Data/Bici a postazione fissa/RadGridExport ' + fileName)
    
    total = len(df)

    different_reasons = set()

    for index, row in df.iterrows(): 
        different_reasons.add(row['Azione'])

    print(different_reasons)
    stats = {}

    for reason in different_reasons:
        stats[reason] = {
            "number" : 0
        }

    for index, row in df.iterrows(): 
        stats[row['Azione']]["number"] += 1

    sorted_reasons = sorted(different_reasons, key=lambda reason: stats[reason]["number"], reverse=True)

    print("Total case :", total)
    for reason in sorted_reasons:
        print(reason, ":", round(stats[reason]["number"]/ total * 100, 2), "% <-", stats[reason]["number"])
    
    reasons = [reason for reason in sorted_reasons]
    case_numbers = [stats[reason]["number"] for reason in sorted_reasons]

    plt.bar(reasons, case_numbers)
    plt.xlabel("Reasons")
    plt.ylabel("Number of Cases")
    plt.title("Cases by Reason")
    plt.xticks(rotation=90)
    plt.show()

# Run in UniversityParmaProject folder
if __name__ == "__main__":
    doAnalysisOnFile("ott", 2022)