#!/usr/bin/bash

# Run the production frontend app

source ./env.txt

fuser -k 9000/tcp || true
http-server production-frontend/dist/ -p 9000
