from collections import Counter
import json
import os

PATH = os.path.dirname(os.path.realpath(__file__))

CACHE_FILE = os.path.join(PATH,"landmarks.json")

class SearchCache(object):

    DISCARD = ['at', 'and', '@', 'rd', 'st', 'ave', 'dr', 'pkwy']
    SUBS = { 'nb' : 'northbound',
             'sb' : 'southbound',
             'eb' : 'eastbound',
             'wb' : 'westbound',
             'ridge' : 'ridgeway'}
    def __init__(self, filename=CACHE_FILE):
        raw = None
        with open(filename) as f:
            raw = json.load(f)
        # index the data
        self.stops = {}
        self.terms = {}
        count = 0
        print("Building the SearchCache")
        for landmark, locations in raw.items():
            for loc, stops in locations.items():
                for t in self.filter_discard(loc.lower().split(" ")):
                    for stop in stops:
                        self.add_term(t,stop)
                for stop in stops:
                    count += 1
                    self.stops[stop[0]] = tuple(stop)
                    for t in self.filter_discard(stop[1].lower().split(" ")):
                        self.add_term(t, stop)
        print("Cached {} entries".format(count))
    def add_term(self, term, stop):
        if term not in self.terms:
            self.terms[term] = set()
        self.terms[term].add(tuple(stop))
    def filter_discard(self, terms):
        return filter(lambda x: x not in SearchCache.DISCARD, terms)
    def query(self, q, numresults=10):
        results = []
        qterms = self.filter_discard(q.lower().split(" "))
        for qt in qterms:
            if qt in SearchCache.SUBS:
                qt = SearchCache.SUBS[qt]
            if qt in self.terms:
                results += list(self.terms[qt])
        count = Counter(results).most_common(numresults)
        return [x[0] for x in count]
