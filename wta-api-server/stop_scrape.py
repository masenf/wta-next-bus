import wta
import json
from bs4 import BeautifulSoup

filename = "landmarks.json"
num_requests = 0

w = wta.Wta_Interface()
w.get_initial_params()
num_requests += 1

def main():
    print("Indexing WTA Landmarks to {}".format(filename))

    result = make_landmarks_dict()

    print("Completed in a mere {} requests".format(num_requests))
    print("Serializing to JSON")
    with open(filename,'w') as f:
        json.dump(result,f)
        print("Process complete")

def make_landmarks_dict():
    """ return a triply nested structure:
        { landmark => { location => ( stop_id, stop_name ) ... """
    global num_requests
    result = {}
    landmarks = get_landmarks()
    for l in landmarks:
        result[l[0]] = {}
        locations = get_location_by_landmarkid(l[1])
        for loc in locations:
            num_requests += 1
            print("[Scraper] Sending request {} for {}/{}...".format(num_requests,l[0],loc[0]))
            result[l[0]][loc[0]] = w.StopLookupClosest([loc[1]])
    return result

def accumulate_tuples_from_select(select):
    result = []
    for opt in select.findChildren('option'):
        if opt.get('value') != '0':
            result.append((opt.contents[0], opt.get('value')))
    return result

def get_landmarks():
    global num_requests
    body = w.get(w.server + w.query_url + "?.a=iLocationLookup")
    num_requests += 1
    soup = BeautifulSoup(body)
    select = soup.find('select',{'name':"SelectStartType"})
    return accumulate_tuples_from_select(select)

def get_location_by_landmarkid(landmark_id):
    global num_requests
    # prepare the values
    values = {'.s' : w.s,
              '.a' : 'iLocationLookupAddress',
              'FormState' : 'SelectStartType',
              'MaxDistance' : '1000',
              'StartGeo' : '0',
              'SelectStartType' : str(landmark_id),
              'StartDetail' : '',
              'Start' : '' }
    body = w.post(w.server + w.query_url, values)
    num_requests += 1
    soup = BeautifulSoup(body)
    select = soup.find('select',{'name':"SelectStart"})
    return accumulate_tuples_from_select(select)

if __name__ == "__main__":
    main()
