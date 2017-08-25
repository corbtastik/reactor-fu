#!/bin/bash
DATA=$1
curl -v -u spongebob:krabbypatty -H "Content-Type: text/plain" -X POST -d "$DATA" http://localhost:8081/echo