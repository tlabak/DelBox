# -*- coding: utf-8 -*-
"""
Created on Sun Apr 10 14:40:59 2022

@author: suhai
"""

import pyrebase
import time
import bluetooth

bluetooth_addr = "00:14:03:05:59:36" # The address from the HC-06 sensor
bluetooth_port = 1 # Channel 1 for RFCOMM
bluetoothSocket = bluetooth.BluetoothSocket (bluetooth.RFCOMM)
bluetoothSocket.connect((bluetooth_addr,bluetooth_port))


config = {
  "apiKey": "XaCFKhMrdkSUDzqO4CSIiFJVUnS9WqhOae1k1Rii",
  "authDomain": "porchbox-5a575.firebaseapp.com",
  "databaseURL": "https://porchbox-5a575-default-rtdb.firebaseio.com",
  "storageBucket": "porchbox-5a575.appspot.com"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()

tempF = 72.0
x_ax = 0.0
y_ax = 0.0
z_ax = 9.0
servo = 0



print("ready for data")
while 1:
    try:
        received_data = bluetoothSocket.recv(1024)
        time.sleep(1.5)
        d_str = str(received_data.decode())
        #print(d_str)
        
        data_sep = d_str.split('$',5)
        
        print(data_sep)
        
        if len(data_sep) < 4:
            continue
        
        try:
            float(data_sep[0])
            tempF = float(data_sep[0])
        except:
            continue
        
        try:
            float(data_sep[1])
            x_ax = float(data_sep[1])
        except:
            continue
            
        try:
            float(data_sep[2])
            y_ax = float(data_sep[2])
        except:
            continue
        
        try:
            float(data_sep[3])
            z_ax = float(data_sep[3])
        except:
            continue
        
        try:
            int(data_sep[4])
            servo = int(data_sep[4])
        except:
            continue
        
        print("TEMP", tempF)
        print("X:  ", x_ax, "m/s^2")
        print("Y:  ", y_ax, "m/s^2")
        print("Z:  ", z_ax, "m/s^2")
            
        if int(servo) == 0:
            print("UNLOCKED")
        else:
            print("LOCKED")
            
        if z_ax < 8.5:
            box = "OPEN"
            print("BOX IS", box)
        else:
            box = "CLOSED"
            print("BOX IS:", box)
            
        data = {
            "Temperature": tempF,
            "X": x_ax,
            "Y": y_ax,
            "Z": z_ax,
            "Servo State": servo,
            "Box State": box,
            }
            
        db.child("PorchBox").child("1-set").set(data)
        #db.child("PorchBox").child("2-push").push(data)
        flag_obj = db.child("PorchBox").child("3-LockFlag").get()
        print(flag_obj.val())
        
        f = open("flagfile.txt", 'r+')
        
        if f.read() == "l" or flag_obj.val() == 1:
            bluetoothSocket.send('l')
            db.child("PorchBox").child("3-LockFlag").set(0)
            f.truncate(0)
            f.close()
            
    except KeyboardInterrupt:
        print("keyboard interrupt detected")
        break
bluetoothSocket.close()
