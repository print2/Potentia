from readline import parse_and_bind
from pymongo import MongoClient
from datetime import datetime
from pprint import pprint as p
from flask import Flask, request, jsonify
from bson.json_util import dumps

app=Flask(__name__)

cluster=MongoClient("mongodb+srv://230GRP4:HklMriJ6iK8iU8n5@cluster0.wl3na.mongodb.net/Plugs?retryWrites=true&w=majority")
db = cluster.test
db=cluster["Plugs"]
collection=db["UsageData"]
#@dispatch(str,datetime,datetime)
@app.route('/getplugdata/<name>&<timeStart>&<timeEnd>', methods=["GET"])
def getPlugData(name,timeStart,timeEnd):
    #gets all readings for a given plug between a given time range
    print(timeStart[4:2])
    t1=datetime(int(timeStart[:4]), int(timeStart[4:6]), int(timeStart[6:8]), int(timeStart[8:10]), int(timeStart[10:12]), int(timeStart[12:14])+int(timeStart[14:20]))
    t2=datetime(int(timeEnd[:4]), int(timeEnd[4:6]), int(timeEnd[6:8]), int(timeEnd[8:10]), int(timeEnd[10:12]), int(timeEnd[12:14])+int(timeStart[14:20]))
    return dumps(collection.find({
    "name": name,
    "$and": [{"date/time": {"$gt": t1}}, {"date/time": {"$lt": t2}}]}))
@app.route('/testing/<name>',methods=["GET"])
def getPlugData2(name):
    #gets all readings for a given plug
    return dumps(collection.find({
    "name": name,
    }))
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

if __name__=='__main__':
    app.run()
