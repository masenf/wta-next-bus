import urllib.parse
import urllib.request
import re
import time
import os
from bs4 import BeautifulSoup
class NoResultsFound(Exception):
    pass
class HTTP_Client(object):
    def get(self, url):
        return urllib.request.urlopen(url).read()
    def post(self, url, data):
        postdata = bytearray(urllib.parse.urlencode(data),"UTF-8")
        return urllib.request.urlopen(url, postdata).read()
class Wta_Interface(HTTP_Client):
    def __init__(self):
        self.server = "http://tripplanner.ridewta.com"
        self.query_url = "/hiwire"
        os.environ['TZ'] = "America/Los_Angeles"
        time.tzset()
    def get_initial_params(self):
        body = self.get(self.server)
        soup = BeautifulSoup(body)
        links = soup.findAll("a", {'accesskey':'accesskeys-tabs'})
        href = links[1].get("href")
        match = re.match(".*\.s=([a-z0-9]+)&",href)
        self.s = match.group(1)
        return self.s
    def LocationLookupAddress(self, search_string):
        # prepare the values
        values = {'.s' : self.s,
                  '.a' : 'iLocationLookupAddress',
                  'FormState' : '0',
                  'MaxDistance' : '1000',
                  'StartGeo' : '0',
                  'Start' : search_string,
                  'StartDetail' : search_string,
                  'SB' : 'Search' }
        print("Sending request 1...")
        body = self.post(self.server + self.query_url, values)
        soup = BeautifulSoup(body)
        found = soup.findAll("form", {'id':'StopLookupClosest'})
        if len(found) == 0:
            # no results found, pull suggestions
            sugg = soup.findAll("select", {'id':'MatchOrigin'})
            if len(sugg) == 0:
                # no suggestions
                locations = []
            else:
                locations = list(map(lambda x: x.get('value'), sugg[0].findChildren("option")))
        else:
            form = found[0]
            locations = [(found[0].findChildren(attrs={'name' : "StartGeo"})[0].get('value'))]
        return locations
    def StopLookupClosest(self, locations):
        # fetch the relevant data
        stops_clean = set()
        req = 1
        values = {'.s' : self.s,
                  '.a' : 'iStopLookupClosest',
                  'FormState' : '0',
                  'MaxDistance' : '1000',
                  'StartGeo' : '',
                  'Start' : '',
                  'StartDetail' : '',
                  'SB' : 'Go' }
        for sgeo in locations:
            req += 1
            values['StartGeo'] = sgeo
            
            print("Sending request {}...".format(req))
            body = self.post(self.server + self.query_url, values)
            soup = BeautifulSoup(body)
            stops = soup.findAll(attrs={'class':('ZebraLight', 'ZebraDark')})
            for stop in stops:
                m = re.search("Stop\s*#:\s*([0-9]+)\s*</b>\s*<br/>\s(.+)\s<br/>",stop.prettify().replace("\n",""))
                if m is not None:
                    stops_clean.add(m.groups())
        return list(stops_clean)
    def NextBusMatch(self, stop, lookuptime=None, numresults=5):
        """ stop - the numerical stop id 
            lookuptime - unix timestamp in UTC to lookup, default = Now
            numresults - maximum 15 (hard limit)

            return a list of tuples ( PST TIME, DESTINATION SIGN )"""
        if lookuptime is None:
            lookuptime = time.time()
        else:
            lookuptime = int(time)
        date = time.strftime("%m-%d-%Y", time.localtime(lookuptime))
        hour = time.strftime("%I00", time.localtime(lookuptime)).lstrip("0")
        minute = time.strftime("%M", time.localtime(lookuptime))
        md_lookup = { "AM" : "a", "PM" : "p" }
        meridiem = md_lookup[time.strftime("%p", time.localtime(lookuptime))]
        values = { '.a' : 'iNextBusMatch',
                   '.s' : self.s,
                   'ShowTimes' : '1',
                   'NumStopTimes': str(numresults),
                   'GetSchedules' : '0',
                   'NextBusText' : str(stop),
                   'Date' : date,
                   'HourDropDown' : hour,
                   'MinuteDropDown' : minute,
                   'MeridiemDropDown' : meridiem,
                   'SB' : 'Next Bus' }
        encval = urllib.parse.urlencode(values)
        url = self.server + self.query_url + "?" + encval
        print("Sending request 1...")
        body = self.get(url)
        soup = BeautifulSoup(body)
        try:
            stop_rows = soup.findAll("table")[1].findAll(attrs={'class':('ZebraDark','ZebraLight')})
            stop_times = []
            for row in stop_rows:
                stop_times.append(tuple(map(lambda x: x.text.strip(), row.findChildren("td"))))
            return stop_times
        except IndexError:
            return []
