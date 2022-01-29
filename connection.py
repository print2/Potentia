from kasa import SmartPlug
from kasa import Discover
from pprint import pformat as pf
from time import *
import subprocess
import os
import asyncio
from datetime import datetime
from pymongo import MongoClient

cluster=MongoClient("mongodb+srv://230GRP4:HklMriJ6iK8iU8n5@cluster0.wl3na.mongodb.net/Plugs?retryWrites=true&w=majority&ssl=true&ssl_cert_reqs=CERT_NONE")
db=cluster["Plugs"]
collection=db["UsageData"]

def getNetworksInfo(attempts=3,attemptDelay=5):
    cListNets = "nmcli dev wifi list" #lists available networks
    cRescan = "nmcli dev wifi rescan" #rescans for networks

    for i in range(attempts):
        os.system(cRescan) #os.system performs given command on command line
        sleep(1)
        networks = subprocess.check_output(cListNets.split()) #subprocess.check_output executes given command (args as array) and returns the commands output
        networks = networks.decode("UTF-8")#decodes output
        if(len(networks.strip())>0):
            return networks,True
            break
        sleep(attemptDelay)
   
    print("No networks found")
    return None,False

def getSSIDs():
    networks,netsFound = getNetworksInfo()

    if(netsFound):
        networksArr = networks.split("\n") #arr of diff networks and their info
        ssids = []

        iSsidStart = networksArr[0].find("SSID") #find string index of SSID's
        iSsidEnd = networksArr[0].find("MODE",iSsidStart) -2

        for i in range(len(networksArr)-2):
            ssids.append((networksArr[i+1])[iSsidStart:iSsidEnd].strip())#gets ssid of networks
            if((networksArr[i+1])[0] == "*"): #finds current network - labelled with star
                currSSID = ssids[i]

        return ssids,currSSID

    print("No SSIDS found")
    return None,None

def connectTo(network,password="''",attempts=3,attemptDelay=5):
    connected = False
    print("Connecting to " + network)
    cConn = ["nmcli","dev","wifi","connect",network,"password",password] #command to connect to a network
    for i in range(attempts):
        result = subprocess.check_output(cConn)
        result = result.decode("UTF-8")
        if "success" in result: #if connection was successful
            print("Connected to " + network + " on attempt: " + str(i + 1))
            connected = True
            break
        sleep(attemptDelay)
       
    if(not connected):
        print("Failed to connect to " + network)

    return connected

#finds SSID of smart plugs
def findSpSSIDs():
    spFound = False
    spSSIDs = []
    arrSSIDs,currNet = getSSIDs()
    if len(arrSSIDs)>0:
        for k in range(len(arrSSIDs)):
            if "TP-LINK_Smart Plug" in arrSSIDs[k]: #checks all SSIDS to see if they are a smart plug network
                spFound = True
                spSSIDs.append(arrSSIDs[k])

    return spSSIDs,currNet,spFound

async def getPlugsOnNet():
    plugsFound = await Discover.discover()
    return plugsFound

async def connPlugToHome(plugs,ssid,password):
    for plug in plugs:
        await actOnPlugs(plug,ssid,password)

async def actOnPlugs(plug,ssid,password):
    alias = input("Give this plug a name: ")
    await plug.set_alias(alias)
    await plug.wifi_join(ssid,password)

async def readSingle(plug):
    while(True):
        try:
            await plug.update()
            power = await plug.current_consumption()
            
            print(plug.alias + " is currently using: " + str(power) + " W")
        except:
            pass

        post={"name": plug.alias,"Power": power, "date/time": datetime.now()}
        collection.insert_one(post)

        await asyncio.sleep(1)

async def detectNewPlug(prevPlugs):
    newPlugs = await getPlugsOnNet()
    for ip in newPlugs:
        if ip not in prevPlugs:
            return newPlugs[ip]

async def connectPlug(plugSSID,homeNet,homePass,currPlugs):
    if(connectTo(plugSSID)):
        plugsOnNet = await getPlugsOnNet()
        await connPlugToHome(plugsOnNet.values(),homeNet,homePass)
        connectTo(homeNet,homePass)
        return await detectNewPlug(currPlugs)

async def connToAll(homePass):
    plugsToConnect,homeNet,found = findSpSSIDs()
    print("checking for new smart plugs")

    if(found):
        print("found new smart plug")
        for ssid in plugsToConnect:
            plugsOnNet = await getPlugsOnNet()
            plug = await connectPlug(ssid,homeNet,homePass,plugsOnNet)
            asyncio.ensure_future(readSingle(plug),loop=event_loop)

async def scanForPlugs(homePass):
    connectedPlugs = await getPlugsOnNet()
    for ip in connectedPlugs:
        asyncio.ensure_future(readSingle(connectedPlugs[ip]),loop=event_loop)

    while True:
        await connToAll(homePass)
        await asyncio.sleep(60)

def main():
    global event_loop
    event_loop = asyncio.get_event_loop()

    _ssids,homeNet,_found = findSpSSIDs()

    homePass = input("What is the password to " + homeNet + "?")

    event_loop.run_until_complete(scanForPlugs(homePass))
    event_loop.close()

if __name__ == "__main__":
    main()
    print("Program terminated")



#delay when plug connects before it reads - seems to pause all threads*