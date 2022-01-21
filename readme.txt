INFORMATION

    Potentia 0.1.2

    Developed by Michael Print, Sebastian Smith, Nuria Torres Ramon, Joel Wilding

DEPENDENCIES

    INSTALLATION

        Raspberry Pi: 
            '   sudo apt update   '
            '   sudo apt upgrade   '

        Python-Kasa: 
            '   python3 -m pip install python-kasa   '

        Network-Manager(nmcli):
            '   sudo apt install network-manager   '
            '   sudo apt install network-manager network-manager-gnome openvpn \
            openvpn-systemd-resolved network-manager-openvpn \
            network-manager-openvpn-gnome   '
            '   sudo apt purge openresolv dhcpcd5   '
        
        python.asyncio: 
            '   python3 -m pip install asyncio   '

        pyMongo:
            '   python3 -m pip install pyMongo   '
            '   python3 -m pip install pyMongo[srv]'

    COMMON ISSUES AND FIXES

        Raspberry Pi:
            May need to install latest version of python: '   sudo apt-get install python3.7   '
        
        Python-kasa:
            Version 0.4.1 can cause issues with 'kasa.Discover.discover()' - downgrade to 0.4.0 until fixed: '   python3 -m pip install python-kasa=0.4.0   '
    
        Network-Manager(nmcli):
            Wifi connection issues caused by purging dhcpcd5 - resolved by using nmcli to connect via command line: '   nmcli dev wifi connect {SSID} password {PASSWORD}   '

        pyMongo:
            Cluster connection closed - check that device IPs are whitelisted on mongoDB

        General:
            Program will not work with networks that need extra authentication steps after a password e.g. eduroam

METHOD

    Scans available networks
    Finds current (home) network
    Finds smart plug networks
    If smart plug network found, connect to smart plug network
        Get plug object and use wifi_join to connect plug to home network
        Connect pi back to home network
    Discover smart plug objects on home network using kasa.Discover.discover()
    Asynchronously get power consumption from plug objects
    Upload plug power consumption to database

KNOWN ISSUES

    Smart Plug still detects power for a few seconds after being turned off
    Smart plug networks detected for connected plugs

