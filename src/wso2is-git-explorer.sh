#!/bin/bash
if [ "$1" == "clone" ] ||[ "$1" == "list" ] || [ "$1" == "init" ] || [ "$1" == "update" ]
then
	for  n in 1 2 3 4 5
	do
	curl -u $gituser:$gitpassword  https://api.github.com/orgs/wso2/repos\?type\=all\&\page\=$n\&per_page\=100 | jq --raw-output '.[] | (.clone_url)'| grep --color  "security\|identity\|auth\|provisioning\|user|\userstore\|saml\|oauth" >>/y.txt
	clear
	done
	tree > /identity-repos/wso2.tree
fi

if [ "$1" == "clone" ] || [ "$1" == "init" ]
then
	mkdir /identity-repos/wso2
	cd /identity-repos/wso2
	input="/y.txt"

	while read -r line; do
  		git clone "$line"
	done < $input

	rm /y.txt
fi

if [ "$1" == "update" ] 
then
	cd /identity-repos/wso2

	for file in */ ; do 
  		if [[ -d "$file" && ! -L "$file" ]]; then
		  	file1=$(echo "$file" | sed "s|/||g")
		  	sed "/$file1/d" /y.txt > /y.tmp
			echo "$file1";
			mv /y.tmp /y.txt
    		cd "$file"; 
			git pull;
			cd ..;
  		fi; 
	done

	cat /y.txt

	cd /identity-repos/wso2
	input="/y.txt"

	while read -r line; do
  		git clone "$line"
	done < $input
	tree > /identity-repos/wso2.tree
	rm /y.txt
fi

cd /
 
if [ "$1" == "clone" ] ||[ "$1" == "list" ] || [ "$1" == "init" ] || [ "$1" == "update" ]
then
	for  n in 1 2 3 4 5
	do
	curl -u $gituser:$gitpassword  https://api.github.com/orgs/wso2-extensions/repos\?type\=all\&\page\=$n\&per_page\=100 | jq --raw-output '.[] | (.clone_url)' | grep --color  "security\|identity\|auth\|provisioning\|user|\userstore\|saml\|oauth" >>/z.txt
	clear
	done
fi


if [ "$1" == "clone" ] || [ "$1" == "init" ]
then
	mkdir /identity-repos/wso2-extensions
	cd /identity-repos/wso2-extensions
	input="/z.txt"

	while read -r line; do
		git clone "$line"
	done < $input

	tree > /identity-repos/wso2-extensions.tree
	rm /z.txt
fi

if [ "$1" == "update" ] 
then
	cd /identity-repos/wso2-extensions
	for file in */ ; do 
  		if [[ -d "$file" && ! -L "$file" ]]; then
		    file1=$(echo "$file" | sed "s|/||g")
		  	sed "/$file1/d" /z.txt > /z.tmp  
			echo "$file1";
			mv /z.tmp /z.txt
    		cd "$file"; 
			git pull;
			cd ..;
  		fi; 
	done
 
	cd /identity-repos/wso2-extensions
	input="/z.txt"

	while read -r line; do
  		git clone "$line"
	done < $input

	tree > /identity-repos/wso2-extensions.tree
	rm /z.txt
fi

if [ "$1" == "" ] || [ "$1" == "list" ]
then
	echo ""
	echo "Identity/Security repos under WSO2:" 
	cat /y.txt
    rm /y.txt
	echo ""
	echo "Identity/Security repos under WSO2 Extensions:" 
	cat /z.txt
    rm /z.txt
fi

if [ "$1" == "find" ] || [ "$2" != "" ]
then
        cd /identity-repos
        find . -name  "$2" -type d > /results.txt
        sed -i -e 's|/|/tree/master/|3' /results.txt
        sed -i -e 's|./wso2/|https://github.com/wso2/|g' /results.txt
        sed -i -e 's|./wso2-extensions/|https://github.com/wso2-extensions/|g' /results.txt
        cat /results.txt
        rm /results.txt
fi
