wta-next-bus
============

Python Wrapper / Android Application for accessing Whatcom Transit bus schedules

Python wrapper is written with bottle.py. It recieves input as GET query variables,
connects to WTA's undocumented HTML-based* API to issue the command, parses the 
result, finally returning clean JSON.

The Android Application: "WTA Next Bus" connects to the Python wrapper to fulfill its 
data needs.

Currently only two functions are implemented at the Python layer:

    bottle route          WTA ".a" param
    ------------          --------------
    
    /location?q=          LocationLookupAddress
        Return a list of tuples containing ( stopid, location name )
    
    /times?stopid=        NextBusMatch
        Return a list of tuples containing ( stop time, route #, route name, destination sign )
