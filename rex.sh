#!/bin/bash

docker run -v $(pwd):/identity-repos -it --rm  prabath/rex $1 $2 $3 $4 $5 $6 $7
