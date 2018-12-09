import numpy as np 
import pandas as pd
import glob
from pandas import read_csv
import re

import matplotlib.pyplot as plt

# S is a probability

files = glob.glob('./*.csv')
mean = np.zeros(len(files))
std = np.zeros(len(files))
for i,file_name in enumerate(files):
	S = re.findall("\d+\.\d+", file_name)[0]
	print(S)
	df = read_csv(file_name, header=None, usecols=[4])
	df["day"] = np.arange(100)
	df.columns = ["smittade", "day"]
	
	smitt_array = np.asarray(df["smittade"])

	mean[i] = smitt_array.mean()
	std[i] = smitt_array.std()


print(mean)
print(std)

df_final = pd.DataFrame()
df_final['mean']=mean
print(df_final)
plt.title("Konfidens intervallet")
plt.errorbar(df_final.index,df_final, xerr=0.5, yerr=2*std, linestyle='')
plt.show()

# Calculate the mean of the data

# Calculate the standard deviation

# Calculate margin of the error 2 * std