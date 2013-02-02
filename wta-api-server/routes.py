from bottle import route, run, request, static_file, default_app
from search_cache import SearchCache
import wta

PROTOCOL_NAME = "wta.py"
PROTOCOL_VERSION = 0
PROTOCOL_ROUTES = { 'location' : { 'params' : ('q'),
                                   'return' : 'list of list',
                                   'desc'   : 'q is the search string, return a list of tuples containing (stopid, name)'},
                    'times' : { 'params' : ('stopid','time','numresults'),
                                'return' : 'list of list',
                                'desc'   : 'time is UTC unix timestamp, numresults is limited to 15, return a list of tuples containing ( PST time, route )'}}
w = None
sc = None

@route('/')
def root():
    return { 'name': PROTOCOL_NAME,
             'ver' : PROTOCOL_VERSION,
             'routes' : PROTOCOL_ROUTES }
@route("/qlocation")
def qlocation():
    """ quick location serach, checks the cache """
    try:
        q = request.query.q
    except:
        return error("PARAM", "Required parameter q was not specified")
    try:
        stops = sc.query(q)
    except:
        raise
        return error(message="Unhandled exception raised, error processing request")
    return { "location" : q,
             "stops" : stops }

@route('/location')
def location():
    try:
        q = request.query.q
    except:
        return error("PARAM", "Required parameter q was not specified")
    try:
        stops = w.StopLookupClosest(w.LocationLookupAddress(q))
    except wta.NoResultsFound:
        stops = [] 
    except:
        raise
        return error(message="Unhandled exception raised, error processing request")
    return { "location" : q,
             "stops" : stops }
@route('/times')
def times():
    try:
        stopid = int(request.query.stopid)
    except ValueError:
        return error("PARAM", "stop id must be an integer value")
    except:
        return error("PARAM", "Required parameter stopid was not specified")

    time = request.query.get('time',None)
    numresults = request.query.get('numresults', 5)

    try:
        times = w.NextBusMatch(stopid, time, numresults)
    except TypeError:
        return error("PARAM", "time must be specified as a unix timestamp in UTC")
    except:
        raise
        return error(message="Unhandled exception raised, error processing request")
    return { 'times' : times }
@route('/library')
def library():
    return static_file('landmarks.json',root='.');
def error(type="GENERAL", message="An error has occurred"):
    return { 'error' : type,
             'message' : message }

print("Server starting, initializing WTA interface")
w = wta.Wta_Interface()
w.get_initial_params()
sc = SearchCache()

def main():
    run(host='mashed-potatoes', port=8080, debug=True)
if __name__ == "__main__":
    main()
