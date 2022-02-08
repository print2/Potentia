import requests
from datetime import datetime
import json
timeStart=20210120134725000000
timeEnd=20230120134733186000
#d1=datetime.strptime(2022 1, 20, 13, 47, 25, 0)
#d2=datetime.strptime(2022, 1, 20, 13, 47, 33, 186000)
url='http://127.0.0.1:5000/generatereport/test1&'+str(timeStart)+'&'+str(timeEnd)
output=requests.get(url)
#print(output.text)
output=json.loads(output.text)
for x in output:
  print(x)