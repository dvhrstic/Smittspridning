import numpy as np 
import pandas as pd
import glob
from pandas import read_csv
import re

import matplotlib.pyplot as plt

# S is a probability

files = sorted(glob.glob('./*.csv'))
mean = np.zeros(len(files))
std = np.zeros(len(files))
probabilities=[]
for i,file_name in enumerate(files):
	S = re.findall("\d+\.\d+", file_name)[0]
	probabilities.append(S)
	print(file_name)
	df = read_csv(file_name, header=None, usecols=[4])
	df["day"] = np.arange(100)
	df.columns = ["smittade", "day"]
	smitt_array = np.asarray(df["smittade"])
	mean[i] = smitt_array.mean() /20
	std[i] = smitt_array.std() /20
	print(i)

# width of the bars
barWidth = 0.3
 
# Choose the height of the blue bars
bars1 = mean
 
# Choose the height of the error bars (bars1)
yer1 = std
  
# The x position of bars
r1 = np.arange(len(bars1))
#r2 = [x + barWidth for x in r1]
 
# Create blue bars
plt.bar(r1 + 0.3, bars1, width = barWidth, color = 'blue',edgecolor = 'black', yerr=yer1, capsize=5, label='Andel sjuka')

# general layout
plt.xticks([r + barWidth for r in range(len(bars1))], probabilities, rotation=45)
plt.ylabel('Andel sjuka (%)')
plt.xlabel('Smittsannolikhet')
plt.title("Andel sjuka med konfidensgrad 95 %")
plt.plot(r1+0.5,np.ones(len(mean))*50, 'r-')
 
# Show graphic
plt.show()
