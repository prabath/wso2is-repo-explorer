#!/bin/bash

file=".repodata/rex.7"
if [ -f "$file" ]
then
    rm -rf .repodata/rex.7
fi

file=".repodata/image.update"
if [ -f "$file" ]
then
	echo "There is an updated Docker image available..."
    rm -rf .repodata/image.update
    echo "Pulling the updated Docker image available..."
	docker pull prabath/rex
fi

docker run -v $(pwd):/identity-repos -it --rm -e REX_ONLINE=$REX_ONLINE -e REX_COLOR=$REX_COLOR prabath/rex $1 $2 $3 $4 $5 $6 $7
