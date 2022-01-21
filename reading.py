from pymongo import MongoClient
from datetime import datetime
from pprint import pprint as p
from multimethod import multimethod
from multipledispatch import dispatch

cluster=MongoClient("mongodb+srv://230GRP4:HklMriJ6iK8iU8n5@cluster0.wl3na.mongodb.net/Plugs?retryWrites=true&w=majority&ssl=true&ssl_cert_reqs=CERT_NONE")
db = cluster.test
db=cluster["Plugs"]
collection=db["UsageData"]
@dispatch(str,datetime,datetime)
def getPlugData(name,timeStart,timeEnd):
    #gets all readings for a given plug between a given time range
    return collection.find({
    "name": name,
    "$and": [{"date/time": {"$gt": timeStart}}, {"date/time": {"$lt": timeEnd}}]})
@dispatch(str)
def getPlugData(name):
    #gets all readings for a given plug
    return collection.find({
    "name": name,
    })
def calculateAverageUsage(name,timeStart,timeEnd):
    #calculates the average usage over a given time
    cursor=getPlugData(name,timeStart,timeEnd)
    readings=0
    totalpower=0 
    for x in cursor:
        totalpower+=x["Power"]
        readings+=1
    return totalpower/readings


#print(calculateAverageUsage("testAsync",datetime(2022, 1, 21, 17, 3, 25, 0),datetime(2022, 1, 22, 13, 47, 33, 186000)))
#cursor=getPlugData("test1",datetime(2022, 1, 20, 13, 47, 25, 0),datetime(2022, 1, 20, 13, 47, 33, 186000))
#for x in cursor:
#    p(x)