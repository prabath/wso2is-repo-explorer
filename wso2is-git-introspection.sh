#!/bin/bash
for  n in 1 2 3 4 5
do
curl -u $gituser:$gitpassword  https://api.github.com/orgs/wso2/repos\?type\=all\&\page\=$n\&per_page\=100 | jq --raw-output '.[] | (.clone_url)'| grep --color  "security\|identity\|auth\|provisioning\|user|\userstore\|saml\|oauth" >>/y.txt
clear
done

if [ "$1" == "clone" ]
then
	mkdir /identity-repos/wso2
	cd /identity-repos/wso2
	input="/y.txt"

	while read -r line; do
  		git clone "$line"
	done < $input
fi

cd /
 
for  n in 1 2 3 4 5
do
curl -u $gituser:$gitpassword  https://api.github.com/orgs/wso2-extensions/repos\?type\=all\&\page\=$n\&per_page\=100 | jq --raw-output '.[] | (.clone_url)' | grep --color  "security\|identity\|auth\|provisioning\|user|\userstore\|saml\|oauth" >>/z.txt
clear
done

if [ "$1" == "clone" ]
then
	mkdir /identity-repos/wso2-extensions
	cd /identity-repos/wso2-extensions
	input="/z.txt"

	while read -r line; do
		git clone "$line"
	done < $input
fi

if [ "$1" == "" ]
then
	echo ""
	echo "Identity/Security repos under WSO2:" 
	cat /y.txt
	echo ""
	echo "Identity/Security repos under WSO2 Extensions:" 
	cat /z.txt
fi
