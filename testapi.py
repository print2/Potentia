import requests
from datetime import datetime
import json
timeStart='20220215163920000000'
timeEnd=  '202202151640000000'
#d1=datetime.strptime(2022 1, 20, 13, 47, 25, 0)
#d2=datetime.strptime(2022, 1, 20, 13, 47, 33, 186000)
url='http://127.0.0.1:5000/getdatapoints/test102&'+str(timeStart)+'&'+str(timeEnd)+'&'+'4'
output=requests.get(url)
#print(output.text)
output=json.loads(output.text)
print(output)
#for x in output:
 # print(x)