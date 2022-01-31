import requests
from datetime import datetime
timeStart=20220120134725000000
timeEnd=20220120134733186000
#d1=datetime.strptime(2022 1, 20, 13, 47, 25, 0)
#d2=datetime.strptime(2022, 1, 20, 13, 47, 33, 186000)
url='http://127.0.0.1:5000/getplugdata/test1&'+str(timeStart)+'&'+str(timeEnd)
output=requests.get(url)
for x in output:
    print(x)