#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Author: Nicolas Le Sommer
Version: 1.0
License: GPLv3
"""

import pycurl
import json
import io

# -------------------------------------------------------------------------------
# API URL
# -------------------------------------------------------------------------------
USER = "r401"
PASSWORD = "But2R041"
API_URL = f"http://{USER}:{PASSWORD}@localhost:8081/activities/"


# -------------------------------------------------------------------------------
# HTTP Response codes
# -------------------------------------------------------------------------------
class HTTP_RESPONSE_CODES:
    STATUS_CODE_400 = "Bad Request"
    STATUS_CODE_401 = "Unauthorized"
    STATUS_CODE_403 = "Forbidden"
    STATUS_CODE_404 = "Not Found"
    STATUS_CODE_405 = "Method Not Allowed"
    STATUS_CODE_409 = "Conflict"


class API_RESPONSE_CODE:
    SUCCESS = "success"
    FAILURE = "failure"
    UNAUTORIZED_OPS = "unauthorized operation"
    UNVALID_DATA = "unvalid data"
    NO_DATA = "data does not exist"


# -------------------------------------------------------------------------------
# Validation data
# -------------------------------------------------------------------------------
valid_data_1 = {
    "date": "01/01/2025",
    "description": "act1",
    "distance": 770,
    "freq_max": 103,
    "freq_min": 98,
    "data": [
        {
            "time": "13:00:00",
            "cardio_frequency": 99,
            "latitude": 47.644795,
            "longitude": -2.776605,
            "altitude": 18,
        },
        {
            "time": "13:00:05",
            "cardio_frequency": 100,
            "latitude": 47.646870,
            "longitude": -2.778911,
            "altitude": 18,
        },
        {
            "time": "13:00:10",
            "cardio_frequency": 102,
            "latitude": 47.646197,
            "longitude": -2.780220,
            "altitude": 18,
        },
        {
            "time": "13:00:15",
            "cardio_frequency": 100,
            "latitude": 47.646992,
            "longitude": -2.781068,
            "altitude": 17,
        },
        {
            "time": "13:00:20",
            "cardio_frequency": 98,
            "latitude": 47.647867,
            "longitude": -2.781744,
            "altitude": 16,
        },
        {
            "time": "13:00:25",
            "cardio_frequency": 103,
            "latitude": 47.648510,
            "longitude": -2.780145,
            "altitude": 16,
        },
    ],
}


valid_data_2 = {
    "date": "01/01/2025",
    "description": "act2",
    "distance": 770,
    "freq_max": 103,
    "freq_min": 98,
    "data": [
        {
            "time": "13:00:00",
            "cardio_frequency": 99,
            "latitude": 47.644795,
            "longitude": -2.776605,
            "altitude": 18,
        },
        {
            "time": "13:00:25",
            "cardio_frequency": 103,
            "latitude": 47.648510,
            "longitude": -2.780145,
            "altitude": 16,
        },
    ],
}

unvalid_data_1 = {
    "date": "01/01/2025",
    "description": "act3",
    "distance": 770,
    "freq_max": 103,
    "freq_min": 98,
    "data": [
        {
            "time": "13:00:00",
            "cardio_frequency": -1,
            "latitude": 47.644795,
            "longitude": -2.776605,
            "altitude": 18,
        },
        {
            "time": "13:00:25",
            "cardio_frequency": 400,
            "latitude": 47.648510,
            "longitude": -2.780145,
            "altitude": 16,
        },
    ],
}

unvalid_data_2 = {
    "date": "01/01/2025",
    "description": "act4",
    "distance": 770,
    "freq_max": 103,
    "freq_min": 98,
    "data": [
        {
            "time": "13:00:00",
            "cardio_frequency": 180,
            "latitude": 147.644795,
            "longitude": -2.776605,
            "altitude": 18,
        },
        {
            "time": "13:00:25",
            "cardio_frequency": 103,
            "latitude": 47.648510,
            "longitude": -222.780145,
            "altitude": 16,
        },
    ],
}

unvalid_data_3 = {
    "date": "01/01/2025",
    "description": "act5",
    "distance": 770,
    "freq_max": 300,
    "freq_min": -1,
    "data": [
        {
            "time": "13:00:00",
            "cardio_frequency": 99,
            "latitude": 47.644795,
            "longitude": -2.776605,
            "altitude": 18,
        },
        {
            "time": "13:00:25",
            "cardio_frequency": 103,
            "latitude": 47.648510,
            "longitude": -2.780145,
            "altitude": 16,
        },
    ],
}

unvalid_data_4 = {
    "date": "01/01/2025",
    "description": "",
    "distance": 770,
    "freq_max": 100,
    "freq_min": 80,
    "data": [
        {
            "time": "13:00:00",
            "cardio_frequency": 99,
            "latitude": 47.644795,
            "longitude": -2.776605,
            "altitude": 18,
        },
        {
            "time": "13:00:25",
            "cardio_frequency": 103,
            "latitude": 47.648510,
            "longitude": -2.780145,
            "altitude": 16,
        },
    ],
}

# -------------------------------------------------------------------------------
# Requests
# -------------------------------------------------------------------------------
# the file must be empty
test_0 = {
    "desc": "Check if file is empty",
    "method": "GET",
    "url": API_URL,
    "request_payload": None,
    "response_code": 200,
    "response_payload": [],
}

test_1 = {
    "desc": "Add a new element",
    "method": "POST",
    "url": API_URL,
    "request_payload": valid_data_1,
    "response_code": 200,
    "response_payload": API_RESPONSE_CODE.SUCCESS,
}

test_2 = {
    "desc": "Add a new element",
    "method": "POST",
    "url": API_URL,
    "request_payload": valid_data_2,
    "response_code": 200,
    "response_payload": API_RESPONSE_CODE.SUCCESS,
}

# data already added in test_2
test_3 = {
    "desc": "Add an element that is already present in the file",
    "method": "POST",
    "url": API_URL,
    "request_payload": valid_data_2,
    "response_code": 409,
    "response_payload": API_RESPONSE_CODE.FAILURE,
}
# data inserted in test1 and test 2
test_4 = {
    "desc": "Check if data have been inserted successfuly",
    "method": "GET",
    "url": API_URL,
    "request_payload": None,
    "response_code": 200,
    "response_payload": [valid_data_1, valid_data_2],
}

# unvalid data
test_5 = {
    "desc": "Add a unvalid data",
    "method": "POST",
    "url": API_URL,
    "request_payload": unvalid_data_1,
    "response_code": 400,
    "response_payload": API_RESPONSE_CODE.UNVALID_DATA,
}

test_6 = {
    "desc": "Add a unvalid data",
    "method": "POST",
    "url": API_URL,
    "request_payload": unvalid_data_2,
    "response_code": 400,
    "response_payload": API_RESPONSE_CODE.UNVALID_DATA,
}

test_7 = {
    "desc": "Add a unvalid data",
    "method": "POST",
    "url": API_URL,
    "request_payload": unvalid_data_3,
    "response_code": 400,
    "response_payload": API_RESPONSE_CODE.UNVALID_DATA,
}

test_8 = {
    "desc": "Add a unvalid data",
    "method": "POST",
    "url": API_URL,
    "request_payload": unvalid_data_4,
    "response_code": 400,
    "response_payload": API_RESPONSE_CODE.UNVALID_DATA,
}
# we try to make a post on path activities/act1
test_9 = {
    "desc": "Unauthorized operation",
    "method": "POST",
    "url": f"{API_URL}act1",
    "request_payload": valid_data_1,
    "response_code": 405,
    "response_payload": API_RESPONSE_CODE.UNAUTORIZED_OPS,
}

# we delete act1
test_10 = {
    "desc": "Remove an element that is present in the file",
    "method": "DELETE",
    "url": f"{API_URL}act1",
    "request_payload": None,
    "response_code": 200,
    "response_payload": API_RESPONSE_CODE.SUCCESS,
}
# we delete act1 again
test_11 = {
    "desc": "Remove an element that is not present in the file",
    "method": "DELETE",
    "url": f"{API_URL}act1",
    "request_payload": "",
    "response_code": 404,
    "response_payload": API_RESPONSE_CODE.NO_DATA,
}
# we delete the list
test_12 = {
    "desc": "Remove all the elements",
    "method": "DELETE",
    "url": f"{API_URL}",
    "request_payload": "",
    "response_code": 200,
    "response_payload": API_RESPONSE_CODE.SUCCESS,
}

requests = [
    test_0,
    test_1,
    test_2,
    test_3,
    test_4,
    test_5,
    test_6,
    test_7,
    test_8,
    test_9,
    test_10,
    test_11,
    test_12,
]


# ------------------------------------------------------------------------------
# Colors
# ------------------------------------------------------------------------------
class bcolors:
    HEADER = "\033[95m"
    OKBLUE = "\033[94m"
    OKCYAN = "\033[96m"
    OKGREEN = "\033[92m"
    WARNING = "\033[93m"
    FAIL = "\033[91m"
    ENDC = "\033[0m"
    BOLD = "\033[1m"
    UNDERLINE = "\033[4m"


# -------------------------------------------------------------------------------
# functions
# -------------------------------------------------------------------------------
def validate_coordinates(data):
    for entry in data:
        lat = entry.get("latitude")
        lon = entry.get("longitude")
        if not (-90 <= lat <= 90):
            raise ValueError(f"Invalid latitude: {lat}")
        if not (-180 <= lon <= 180):
            raise ValueError(f"Invalid longitude: {lon}")
    print("All GPS coordinates are valid.")


def validate_heart_rate(data):
    for entry in data:
        freq = entry.get("cardio_frequency")

        if not (15 <= freq <= 220):
            raise ValueError(f"Invalid heart rate: {freq}")
    print("Heart rate is valid.")


def check_json_body(expected, returned):
    expected = json.dumps(expected, sort_keys=True, indent=2, ensure_ascii=False)
    returned = json.dumps(returned, sort_keys=True, indent=2, ensure_ascii=False)
    return expected == returned


# -------------------------------------------------------------------------------
# HTTP request using cURL
# -------------------------------------------------------------------------------


def http_request(method, url, payload=None):
    buffer = io.BytesIO()
    c = pycurl.Curl()
    c.setopt(c.URL, url)
    c.setopt(c.WRITEDATA, buffer)
    c.setopt(pycurl.HTTPHEADER, ["Accept: application/json"])
    c.setopt(c.VERBOSE, False)  # Enables detailed request output

    if method.upper() in ["POST", "PUT"]:
        c.setopt(c.CUSTOMREQUEST, method.upper())
        json_payload = json.dumps(payload, ensure_ascii=False).encode("utf-8")
        c.setopt(c.POSTFIELDS, json_payload)
        c.setopt(c.HTTPHEADER, ["Content-Type: application/json; charset=utf-8"])
    elif method.upper() == "DELETE":
        c.setopt(c.CUSTOMREQUEST, "DELETE")
    elif method.upper() == "GET":
        pass  # Default is GET
    else:
        raise ValueError(f"Unsupported HTTP method: {method}")

    # print request
    c.setopt(pycurl.DEBUGFUNCTION, lambda t, s: print(f"DEBUG: {s.decode()}"))

    try:
        c.perform()
        status_code = c.getinfo(pycurl.RESPONSE_CODE)
        c.close()

        body = json.loads(buffer.getvalue().decode("utf-8"))
        # print(f"\n{method.upper()} {url}")
        # print(f"Status Code: {status_code}")
        # print("Response:", body)
        return status_code, body
    except pycurl.error as e:
        print(f"cURL error: {e}")
        return None, None


# -------------------------------------------------------------------------------
# Main
# -------------------------------------------------------------------------------

if __name__ == "__main__":
    score = 0
    request_number = len(requests)
    for req in requests:
        print(f"{bcolors.OKGREEN}{req['desc']}{bcolors.ENDC}")
        status_code, resp_body = http_request(
            req["method"], req["url"], req["request_payload"]
        )
        if status_code == req["response_code"]:
            score = score + 1
            body_check = check_json_body(req["response_payload"], resp_body)
            print(
                f"{bcolors.OKBLUE}Request: {req['method']} {req['url']} -> Response code:{status_code}, body valid: {body_check} {bcolors.ENDC}"
            )
        else:
            print(
                f"{bcolors.FAIL}Request: {req['method']} {req['url']} -> Expected status code: {req['response_code']}, returned status code: {status_code} {bcolors.ENDC}"
            )
    print(f"\n{bcolors.OKGREEN} Score={score}/{request_number}{bcolors.ENDC}")