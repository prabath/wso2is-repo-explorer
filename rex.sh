#!/bin/bash

docker run -v $(pwd):/identity-repos -it --rm  prabath/wso2is-git-introspection $1 $2