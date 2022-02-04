from readline import parse_and_bind
from pymongo import MongoClient
from datetime import datetime
from pprint import pprint as p
from flask import Flask, request, jsonify
import json
from bson.json_util import dumps
import requests

import asyncio
from connection import currUsageTest

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
@app.route('/calculateaverageusage/<name>&<timeStart>&<timeEnd>', methods=["GET"])
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
#for x in cursor:
#    p(x)



def getLiveData(name):
    #return the current usage data for a given plug
    return dumps(Collection.findOne({
        "name": name,
        "$orderby": {"date/time" : -1}}))

def checkForAlerts():
    #return any alerts to the app- the app can then act on these alerts
    #example: name required
    pass

def namePlug(name):
    #receive name given to the plug
    #can be adapted to receive any other important settings for the plug
    pass

def changePlugSettings():
    #take parameters with all the settings for the plug
    pass

def getConnectPlugs():
    #return jsonify(plugs['name'])
    pass


def getHighestUsage(data):
    #assumes plugs are left on constantly
    ret=[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
    readings=[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
    for x in data:#go through data returned by getPlugData()
        y=x["date/time"]
        y=repr(y)
        hour=int(y[22:24])
        ret[hour]+=x["Power"]#array of 24 hours
        readings[hour]+=1
    for i in range(24):
        if readings[i]!=0:
            ret[i]/=readings[i]
    return ret


@app.route('/generatereport/<name>&<timeStart>&<timeEnd>', methods=["GET"])
def generateReport(name,timeStart,timeEnd):
    data=requests.get('http://127.0.0.1:5000/getplugdata/'+name+'&'+timeStart+'&'+timeEnd)#return all data
    #return getHighestUsage(data)#identify times when used- return hourly period during the day where most usage
    data=json.loads(data.text)
    return dumps(getHighestUsage(data))
    #return dumps(usageByHour)
    # for each type of device have list of reasons why could be using lot of power (either a lot in short period of time or more than usual over longer period)- items can be grouped together
    #currentavg=calculateAverageUsage(name,timeStart,timeEnd)*(timeEnd-timeStart)#compare total used this time period to previous time period (e.g. this month to last month)
    #previousavg=calculateAverageUsage(name,timeStart-(timeEnd-timeStart),timeStart)
#generateReport("test1","20210120134725000000","20230120134733186000")
#print(calculateAverageUsage("testAsync",datetime(2022, 1, 21, 17, 3, 25, 0),datetime(2022, 1, 22, 13, 47, 33, 186000)))
#cursor=getPlugData("test1",datetime(2022, 1, 20, 13, 47, 25, 0),datetime(2022, 1, 20, 13, 47, 33, 186000))
#for x in cursor:
#    p(x)

@app.route('/usageTest/<ip>',methods=["GET"])
def usageTest(ip):
    power = asyncio.run(currUsageTest(ip))
    return dumps(power)


if __name__=='__main__':
    app.run(port=5000,host='0.0.0.0')
