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
db = cluster.test
db=cluster["Plugs"]
collection=db["UsageData"]
##   IMPORTANT INFO   ##

    #Using python-kasa instead of pyHS100
        #install using pip install python-kasa
        #this provides the wifi_join smart plug method

    #uses a linux network manager NMCLI
        #allows use of command line commands to scan for and connect to networks
        #install using
            #apt-get update
            #apt-get upgrade
            #apt-get install network-manager

            #IF FAILS
                #try "sudo apt-get install network-manager"
                #sudo runs command with all privileges by default

    #uses asyncio
        #only using it because python-kasa does so cant run any of its methods without
        #install using pip install asyncio

    #I think those three are the only extra things that need installing, let me know if im wrong



##   HOW IT WORKS   ##

    #Scans available networks
    #Finds current network
    #Finds smart plug network
    #Connects to smart plug network
    #Scans for devices on smart plug network
        #Should be only smart plug
    #Get plug IP and MAC
    #Create plug object and use wifi_join to connect plug to home network
    #Connect pi back to home network
    #Find new IP of plug by scanning for known MAC
    #create new plug object, with plug and pi both on home network
    #Can then manipulate the plug however you like

    #This program gets power usage every second, and gives user option to turn off/on the plug every 10 seconds
        #F to change state, anything else to continue seeing usage


##   Things to note   ##

    #SP still detects power for about 30 seconds after turning off the plug via the program


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

def getPlugsOnNet():
    plugsFound = asyncio.run(Discover.discover())
    return plugsFound.values()

def connPlugToHome(plugs,ssid,password):
    for plug in plugs:
        asyncio.run(actOnPlugs(plug,ssid,password))

async def actOnPlugs(plug,ssid,password):
    alias = input("Give this plug a name: ")
    await plug.set_alias(alias)
    await plug.wifi_join(ssid,password)

#reads power every second
async def readPower(plugs):
    plugTasks = []
    for plug in plugs:
        plugTasks.append(asyncio.create_task(readSingle(plug)))
    
    for task in plugTasks:
        await task


async def readSingle(plug):
    while(True):
        try:
            await plug.update()
            power = await plug.current_consumption()
            
            print(plug.alias + " is currently using: " + str(power) + " W")
        except:
            print("ERROR: " + plug.alias + " not found")

        post={"name": plug.alias,"Power": power, "date/time": datetime.now()}
        collection.insert_one(post)

        await asyncio.sleep(1)


def connectPlug(plugSSID,homeNet,homePass):
    if(connectTo(plugSSID)):
        connPlugToHome(getPlugsOnNet(),homeNet,homePass)
        connectTo(homeNet,homePass)
            
def main():
    plugSSIDs,homeNet,found = findSpSSIDs()
    if(found):
        homePass = input("What is the password to " + homeNet + "?")
        for x in plugSSIDs:
            connectPlug(x,homeNet,homePass)

    discoveredPlugs = asyncio.run(Discover.discover())
    connectedPlugs = discoveredPlugs.values()
    for plug in connectedPlugs:
        print(plug.alias)

    asyncio.run(readPower(connectedPlugs))
            
       
    return homeNet,homePass

if __name__ == "__main__":
    mainNet,mainPass = main()
    print("Program terminated")

    #supposedly connects back to home network if program fails when on SP network but havent tested this
    _ssids,_passed,currNet = getSSIDs()
    if(currNet != mainNet):
        connectTo(mainNet,mainPass)



#when connect to plug, pi joins smart plug network, then connects to original network

#have to wait for one to connect, before doing anything

#2 connected and giving data
#try to connect another one, must stop the other two from transferring data temporarily

#handle when plug disconnects  
    #try and except, until we rediscover devices

#Dont ask for wifi password if no devices to connect
#COnnect device to device
