from readline import parse_and_bind
from sqlite3 import Timestamp
from pymongo import MongoClient
from datetime import datetime, timedelta
from pprint import pprint as p
from flask import Flask, request, jsonify
import json
from bson.json_util import dumps
import asyncio
from kasa import SmartPlug

from connection import *

import requests

app=Flask(__name__)

cluster=MongoClient("mongodb+srv://230GRP4:HklMriJ6iK8iU8n5@cluster0.wl3na.mongodb.net/Plugs?retryWrites=true&w=majority")
#db = cluster.test
db=cluster["Plugs"]
collection=db["UsageData"]
def accessDatabase(name,t1,t2):
    return collection.find({
    "name": name,
    "$and": [{"date/time": {"$gt": int(t1)}}, {"date/time": {"$lt": int(t2)}}]})

@app.route('/getplugdata/<name>&<timeStart>&<timeEnd>', methods=["GET"])
def getPlugData(name,timeStart,timeEnd):
    #gets all readings for a given plug between a given time range
    # t1=datetime(int(timeStart[:4]), int(timeStart[4:6]), int(timeStart[6:8]), int(timeStart[8:10]), int(timeStart[10:12]), int(timeStart[12:14]), int(timeStart[14:20]))
    # t2=datetime(int(timeEnd[:4]), int(timeEnd[4:6]), int(timeEnd[6:8]), int(timeEnd[8:10]), int(timeEnd[10:12]), int(timeEnd[12:14]), int(timeEnd[14:20]))
    return dumps(accessDatabase(name,timeStart,timeEnd))

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

@app.route('/profiles/', methods=["GET"])
def getPlugProfiles():
    string = ""
    results =  db["Profiles"].find()
    for record in results:
        string = string + record["_id"] + "|" + record["description"] + "|" + record["applianceName"] + "|" + record["plugName"] + "|" + record["plugIP"] + "#"
    string = string[:-1]
    return dumps(string)

@app.route('/applianceProfile/', methods=["GET"])
def getApplianceProfiles():
    string = ""
    results =  db["Appliances"].find()
    for record in results:
        string = string + record["_id"] + "|" + record["permOn"] + "|" + record["timeUntilDisable"] + "#"
    string = string[:-1]
    return dumps(string)

@app.route('/addProfile/<name>&<description>&<applianceName>&<plugName>&<ip>')
def addPlugProfile(name,description,applianceName,plugName,ip):
    post={"_id": name,"description": description, "applianceName": applianceName, "plugName": plugName, "plugIP": ip}
    db["Profiles"].insert_one(post)
    return dumps("Done")

@app.route('/addApplianceProfile/<name>&<permOn>&<timeUntilDisable>')
def addApplianceProfile(name,permOn,timeUntilDisable):
    post={"_id": name,"permOn": permOn, "timeUntilDisable": timeUntilDisable}
    db["Appliances"].insert_one(post)
    return dumps("Done")

@app.route('/deleteProfile/<name>')
def deletePlugProfile(name):
    db["Profiles"].delete_many({"_id":name})
    return dumps("Done")

@app.route('/deleteApplianceProfile/<name>')
def deleteApplianceProfile(name):
    db["Appliances"].delete_many({"_id":name})
    return dumps("Done")

@app.route('/updatePName/<name>&<PName>&<ip>')
def updateProfilesPName(name,PName,ip):
    db["Profiles"].update_one({"_id": name},{"$set": {"plugName": PName, "plugIP":ip}})
    return dumps("Done")

def getMonth(currDay,currMonth):
    #determines if the month has changed
    if (currDay>=29 and currMonth==2) or (currDay>=31 and (currMonth==4 or currMonth==6 or currMonth==9 or currMonth==11)) or currDay>=32:
        return True
    else:
         return False

def getDaysInMonths(months):
    days=[31,28,31,30,31,30,31,31,30,31,30,31]
    total=0
    for i in range(months-1):
        total+=days[i]
    return total


def emptyList(points):
    #returns list of zeros of a given length
    ret=[]
    for i in range(points):
        ret.append(0)
    return ret

def timeVal(timestr,option):
    #gets integer value for time string
    if option==2:
        for i in range(24-len(timestr)):#ensure time str always has 24 length
            timestr+="0"
        return int(datetime(int(timestr[:4]), int(timestr[5:7]), int(timestr[8:10]), int(timestr[11:13]), int(timestr[14:16]), int(timestr[17:19]),int(timestr[20:23])).strftime("%Y%m%d%H%M%S"))
    else:
        return int(datetime(int(timestr[:4]), int(timestr[4:6]), int(timestr[6:8]), int(timestr[8:10]), int(timestr[10:12]), int(timestr[12:14]),int(timestr[14:20])).strftime("%Y%m%d%H%M%S"))

def getDelta(timestr,option):
    if option==1:
        return timedelta(days=int(timestr[6:8])+getDaysInMonths(int(timestr[4:6]))+365*int(timestr[:4]),hours=int(timestr[8:10]),minutes=int(timestr[10:12]),seconds=int(timestr[12:14]),milliseconds=int(timestr[14:20]))
    else:
        for i in range(24-len(timestr)):#ensure time str always has 24 length
            timestr+="0"
        return timedelta(days=int(timestr[8:10])+getDaysInMonths(int(timestr[5:7]))+365*int(timestr[:4]), hours=int(timestr[11:13]), minutes=int(timestr[14:16]), seconds=int(timestr[17:19]), milliseconds=int(timestr[20:23]))
@app.route('/getdatapoints/<name>&<timeStart>&<timeEnd>&<numberOfPoints>', methods=["GET"])
def getDataPoints(name,numberOfPoints,timeDiff):
    timeEnd = int(time())
    timeStart = timeEnd - timeDiff
    #get all readings and produce an average for each portion of the time period
    #to be displayed on the graph
    data=requests.get('http://0.0.0.0:5000/getplugdata/'+name+'&'+str(timeStart)+'&'+str(timeEnd))#return all data
    # data = accessDatabase(name,str(timeStart),str(timeEnd))
    #data=getPlugData(name,timeStart,timeEnd)
    #data=getPlugData(name,timeStart,timeEnd)
    data=json.loads(data.text)
    #print(timeStart)
    # timeStart=getDelta(timeStart,1)
    # timeEnd=getDelta(timeEnd,1)
    numberOfPoints=int(numberOfPoints)

    i=0
    ret=[]

    if len(data)==0:
        return emptyList(int(numberOfPoints))

    # period=(timeEnd-timeStart)/int(numberOfPoints)#gets time in ms
    period=(timeDiff)/int(numberOfPoints)
    currTime=timeStart+period

    # if currTime>data[i]["date/time"]:
    #     # print(currTime)
    #     # print(data[i]["date/time"]["$date"])
    #     # print(getDelta(data[i]["date/time"]["$date"],2))
    #     print("test2")
    #     print(currTime)
    #     print(data[i]["date/time"])
    #     return []
    while True:#calculate each data point
        ptscount=0
        ptstotal=0
        # print(currTime)
        while data[i]["date/time"]<currTime:#collect all data in given portion of time period
            ptscount+=1
            ptstotal+=data[i]["Power"]
            i+=1
            if i>=len(data):
                ret.append(ptstotal/ptscount)
                numberOfPoints-=1
                return ret+emptyList(numberOfPoints)
        currTime+=period
        if ptscount==0:
            ret.append(0)
        else:
            ret.append(ptstotal/ptscount)
        numberOfPoints-=1

@app.route('/getdatapoints2/<name>&<timeStart>&<timeEnd>', methods=["GET"])
def getDataPoints2(name,timeStart,timeEnd):
    #get all readings and produce an average for each hour during the time period
    #to be displayed on the graph
    data=requests.get('http://0.0.0.0:5000/getplugdata/'+name+'&'+timeStart+'&'+timeEnd)#return all data
    data=json.loads(data.text)
    currHour=int(timeStart[8:10])
    currDay= int(timeStart[6:8])
    currMonth=int(timeStart[4:6])
    currYear=int(timeStart[:4])
    i=0
    ret=[]
    end=datetime(int(timeEnd[:4]), int(timeEnd[4:6]), int(timeEnd[6:8]), int(timeEnd[8:10]),0, 0, 0)
    length=len(data)
    while True:
                    totalhourusage=0
                    readingsnum=0
                    try:
                        while data[i]["date/time"].hour==currHour and data[i]["date/time"].day==currDay and data[i]["date/time"].month==currMonth and data[i]["date/time"].year==currYear:#go through all readings in 1 hour
                            totalhourusage+=int(data[i]["Power"])
                            readingsnum+=1
                            i+=1#get next reading
                            if i>=length:#reached end of readings
                                break
                    except:
                        pass
                    if readingsnum>0:
                        ret.append(totalhourusage/readingsnum)
                    else:
                        ret.append(0)
                    if end <= datetime(currYear,currMonth,currDay,currHour,0,0,0):#reached end of time period
                        return dumps(ret)#finish
                    currHour+=1#next hour
                    if currHour>=24:#next day
                        #new day
                        currDay+=1
                        currHour=0
                        month=getMonth(currDay,currMonth)
                        if getMonth(currDay,currMonth):#next month
                            currDay=1
                            currMonth+=1
                            if currMonth==13:#next year
                                currYear+=1
                                currMonth=1
                                currHour=0

#get all data points for each hour
#when time changes, reset total hour usage and readings num
#add zeros to list until next hour is reached




def getLiveData(name):
    #return the current usage data for a given plug
    return dumps(collection.findOne({
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

def checkForHighUsage():
    #called once a minute
    #call getConnected
    #for plug in list, check data usage in last hour
    #if sudden increase from low level (not 0), recommend plug turned off (if settings allow for that)
    #return plug name and data points for usage in last hour
    #user then receives a notification asking them if they want to turn off- turn off called if option chosen, or no option chosen within 10 minutes
    pass

def usageByHour(data):
    #identify times when used- return average usage per hour
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

@app.route('/checkstandby/<name>')
def checkStandBy(name):
    data=accessDatabase(name,datetime.now()-timedelta(minutes=1),datetime.now())
    #assumption may change during testing
    if 5*data[len(data)-1]["Power"]<data[0]["Power"]:#if last reading more than 500% lower than first
        return dumps("True")#start standby timer
    return dumps("False")




@app.route('/generatereport/<name>&<timeStart>&<timeEnd>', methods=["GET"])
def generateReport(name,timeStart,timeEnd):
    data=requests.get('http://192.168.43.134:5000/getplugdata/'+name+'&'+timeStart+'&'+timeEnd)#return all data
    data=json.loads(data.text)
    return dumps(getUsageByHour(data))
    #return dumps(usageByHour)
    # for each type of device have list of reasons why could be using lot of power (either a lot in short period of time or more than usual over longer period)- items can be grouped together- Joel research
    #currentavg=calculateAverageUsage(name,timeStart,timeEnd)*(timeEnd-timeStart)#compare total used this time period to previous time period (e.g. this month to last month)
    #previousavg=calculateAverageUsage(name,timeStart-(timeEnd-timeStart),timeStart)
#generateReport("test1","20210120134725000000","20230120134733186000")
#print(calculateAverageUsage("testAsync",datetime(2022, 1, 21, 17, 3, 25, 0),datetime(2022, 1, 22, 13, 47, 33, 186000)))
#cursor=getPlugData("test1",datetime(2022, 1, 20, 13, 47, 25, 0),datetime(2022, 1, 20, 13, 47, 33, 186000))
#for x in cursor:
#    p(x)

@app.route('/usageTest/<ip>',methods=["GET"])
def usageTest(ip):
    power = asyncio.run(getUsageTest(ip))
    return power

@app.route('/turnOff/<ip>',methods=["GET"])
def turnPlugOff(ip):
    result = asyncio.run(asyncTurnPlugOff(ip))
    return result

@app.route('/turnOn/<ip>',methods=["GET"])
def turnPlugOn(ip):
    result = asyncio.run(asyncTurnPlugOn(ip))
    return result

@app.route('/getUnconnected/',methods=["GET"])
def getUnconnected():
    list = asyncio.run(getPlugsToConnect())
    return list

@app.route('/getConnected/',methods=["GET"])
def getConnected():
    list = asyncio.run(getConnectedPlugs())
    return list

@app.route('/connectSingle/<password>&<network>&<ssid>',methods=["GET"])
def connectSingle(password,network,ssid):
    plugInfo = asyncio.run(connOnePlug(password.replace('~',' '),network.replace('~',' '),ssid.replace('~',' '))) #params cant contain ~ or this failes
    return plugInfo

@app.route('/getNetwork/',methods=["GET"])
def getPiNetwork():
    _ssids,currSSID = getSSIDs()
    return dumps(currSSID)

@app.route('/changeAlias/<ip>&<alias>',methods=["GET"])
def changePlugAlias(ip,alias):
    result = asyncio.run(changeAlias(ip,alias.replace("~"," ")))
    return result

@app.route('/readUsage/<ip>',methods=["GET"])
def readPlugUsage(ip):
    result = asyncio.run(readPlugData(ip))
    return result

@app.route('/isOn/<ip>',methods=["GET"])
def isOn(ip):
    result = asyncio.run(getIsOn(ip))
    return result

@app.route('/getGraphPoints/<ip>&<numPoints>&<timeDiff>',methods=["GET"])
def getGraphPoints(ip,numPoints,timeDiff):
    datapoints = asyncio.run(aGetGraphPoints(ip,numPoints,timeDiff))

    return dumps(datapoints)

async def aGetGraphPoints(alias,numPoints,timeDiff):
    dataPoints = getDataPoints(alias,numPoints,int(timeDiff))

    dataPointsStr = ""
    for point in dataPoints:
        dataPointsStr = dataPointsStr + format(point,'.2f') + "|"
    
    if(len(dataPointsStr) != 0):
        dataPointsStr = dataPointsStr[:-1]

    return dataPointsStr

if __name__=='__main__':
    app.run(port=5000,host='0.0.0.0')
