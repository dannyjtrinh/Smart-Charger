Smart Charger Service
by Danny Trinh

The Android application creates a service sending the battery percentage to a UDP client every 2 seconds.
As a result, the application itself does not need to be on for the battery service to run correctly.
The service will restart itself upon an application force close or OS cleanup and will only truely close if the user clicks
the disable service button. It will be active in the lockscreen because of a partial wakelock and because
of this wakelock, it bypasses doze. 
A status box displays whether or not the service is active.


                                  ---------------------------------
                                  | Battery Query                 |
                                  ---------------------------------
                                  |  _    -----------------       |
                                  | |_|  |Activate Service |      |
                                  |       -----------------       |
                                  |       -----------------       |
                                  |      |   Deactivate    |      |
                                  |      |    Service      |      |
                                  |       -----------------       |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |                               |
                                  |-------------------------------|
                                  |                        _      |
                                  |     <       ---       |_|     |
                                  |                               |
                                  ---------------------------------

The client is a python UDP client with several functions. It receives the UDP data packet and determines whether
or not to turn on the charger for the phone. If the battery percentage is <= 80% the charger will turn on.
If the battery percentage is at 100%, the charger will turn off. By default the battery charger will function
as a regular battery charger unless packets are read from the service app. To run the app just enter
python battClient.py.
