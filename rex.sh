#!/bin/bash

docker run -v $(pwd):/identity-repos -it --rm  prabath/rex $1 $2
