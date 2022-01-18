from kasa import SmartPlug
from pprint import pformat as pf
from time import *
import subprocess
import os
import asyncio
#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
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

            #https://raspberrypi.stackexchange.com/questions/29783/how-to-setup-network-manager-on-raspbian

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

        iSsidStart = networksArr[0].find("SSID",12) #find string index of SSID's
        iSsidEnd = networksArr[0].find("MODE",iSsidStart) -2

        for i in range(len(networksArr)-2):
            ssids.append((networksArr[i+1])[iSsidStart:iSsidEnd].strip())#gets ssid of networks
            if((networksArr[i+1])[0] == "*"): #finds current network - labelled with star
                currSSID = ssids[i]

        return ssids,True,currSSID

    print("No SSIDS found")
    return None,False,None

def getDevIPs(attempts=3,attemptDelay = 5):
    cDevsOnNet = "arp -a" #lists visible devices on network - some devices are invisible until pinged

    for i in range(attempts):
        sleep(attemptDelay)
        devices = subprocess.check_output(cDevsOnNet.split())
        devices = devices.decode("UTF-8")
        if(len(devices.strip())>0):
            break

    if(len(devices.strip())<=0):
        print("No devices found on this network")
        return None,None,False

    devicesArr = devices.split("\n") #array of devices info
    devIPs = []
    devMACs = []

    for i in range(len(devicesArr)-1):
        devicesArr[i] = devicesArr[i].split()
        devIPs.append((devicesArr[i][1])[1:-1]) #get ip of each device
        devMACs.append(devicesArr[i][3]) #get mac of each address

    return devIPs,devMACs,True

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
            return connected
        sleep(attemptDelay)
        
    print("Failed to connect to " + network)
    return connected

#finds SSID of smart plug
def findSpSSID():
    spFound = False
    spSSID = None
    arrSSIDs,SSIDsFound,currNet = getSSIDs()
    if(SSIDsFound):
        for k in range(len(arrSSIDs)):
            if "TP-LINK_Smart Plug" in arrSSIDs[k]: #checks all SSIDS to see if they are a smart plug network
                spFound = True
                spSSID = arrSSIDs[k]
                return spSSID,spFound,currNet

    print("Cannot find smart plug network")
    return spSSID,spFound,currNet

#uses kasa discover to ping smart devices on this network
#without this, device would be invisible when we call getDevIPS
def attemptDiscover(attempts=3,attemptDelay=5):
    for i in range(attempts):
        sleep(attemptDelay)
        result = subprocess.check_output("kasa discover".split())
        result = result.decode("UTF-8")
        if "==" in result: #found smartplug
            return True
    
    print("Could not find any smart devices on this network")
    return False

#reads power every second
async def readPower(plug):
    i=0
    while(True):
        if(i==10):
            i=0
            decision = input("Enter F to change state, Q to quit, or anything else to continue")
            if(decision=="Q"):
                break
            elif(decision=="F"):
                if(plug.is_on):
                    await plug.turn_off()
                else:
                    await plug.turn_on()

        await plug.update()
        power = await plug.current_consumption()
        print(power)
        sleep(1)
        i += 1

def main():
    plugSSID,plugFound,homeNet = findSpSSID()
    homePass = input("What is the password to " + homeNet + "?")

    if plugFound:
        if(connectTo(plugSSID)):
            devIPs,devMACs,devsFound = getDevIPs()
            if(devsFound):
                plug = SmartPlug(devIPs[0])
                plugMAC = devMACs[0]
                asyncio.run(plug.wifi_join(homeNet,homePass)) #auto detect home network, pass as input

                if(connectTo(homeNet,homePass)):

                    plugConnected = True
                    if(attemptDiscover()):

                        homeDevIPs, homeDevMACs,devIPsFound = getDevIPs()
                        if(devIPsFound):
                            newIPFound = False
                            for i in range(len(homeDevMACs)):
                                if(plugMAC == homeDevMACs[i]):
                                    plug = SmartPlug(homeDevIPs[i])
                                    newIPFound = True
                                    break

                            if(plugConnected and newIPFound):
                                asyncio.run(readPower(plug))

    

    return homeNet,homePass

if __name__ == "__main__":
    mainNet,mainPass = main()
    print("Program terminated")

    #supposedly connects back to home network if program fails when on SP network but havent tested this
    _ssids,_passed,currNet = getSSIDs()
    if(currNet != mainNet):
        connectTo(mainNet,mainPass)