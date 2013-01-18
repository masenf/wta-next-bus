wta-next-bus
============

Python Wrapper / Android Application for accessing Whatcom Transit bus schedules

Python wrapper is written with bottle.py. It recieves input as GET query variables,
connects to WTA's undocumented HTML-based* API to issue the command, parses the 
result, finally returning clean JSON. This is a wrapper between WTA's proprietary data format
and a consistent API. Ideally, when WTA changes their code, applications
implementing this code should continue to work.

There is the main API (wta.py) which does the HTTP calls to WTA and returns python
objects. Additionally, there is the web layer which exports this API via
JSON and provides caching (routes.py).

The Android Application: "wta-droid" connects to a server running the Python wrapper 
to fulfill its data needs.

Currently only two functions are implemented at the Python layer:

    bottle route          WTA ".a" param
    ------------          --------------
    
    /location?q=          LocationLookupAddress
        Return a list of tuples containing ( stopid, location name )
    
    /times?stopid=        NextBusMatch
        Return a list of tuples containing ( stop time, route #, route name, destination sign )

* Does not conform to any HTML standards I'm familiar with, nor is anything semantic.
