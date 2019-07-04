#!/bin/bash

if [ "$1" == "update-tree" ]
then
  rm /identity-repos/wso2.tree
  rm /identity-repos/wso2-extensions.tree
  rm /identity-repos/wso2.tree.dir
  rm /identity-repos/wso2-extensions.tree.dir
fi

file="/identity-repos/wso2.tree"
if [ -f "$file" ]
then
	echo ""
else
    cd /
    wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/wso2-extensions.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/wso2.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/wso2-extensions.tree.dir
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/wso2.tree.dir
	cp /wso2.tree /identity-repos/wso2.tree
	cp /wso2-extensions.tree /identity-repos/wso2-extensions.tree
	cp /wso2.tree.dir /identity-repos/wso2.tree.dir
	cp /wso2-extensions.tree.dir /identity-repos/wso2-extensions.tree.dir
fi

if [ "$1" == "" ] ||[ "$1" == "clone" ] ||[ "$1" == "list" ] || [ "$1" == "update" ]
then
	for  n in 1 2 3 4 5
	do
	curl -u $gituser:$gitpassword  https://api.github.com/orgs/wso2/repos\?type\=all\&\page\=$n\&per_page\=100 | jq --raw-output '.[] | (.clone_url)'| grep --color  "security\|identity\|auth\|provisioning\|user|\userstore\|saml\|oauth\|charon\|carbon-kernel\|directory\|product-is" >>/y.txt
	clear
	done
fi

if [ "$1" == "clone" ] 
then
	mkdir -p /identity-repos/wso2
	cd /identity-repos/wso2
	input="/y.txt"

	while read -r line; do
  		git clone "$line"
	done < $input

	tree -fi > /identity-repos/wso2.tree
	tree -fi -d > /identity-repos/wso2.tree.dir.tmp.1
	sed "/src/d" /identity-repos/wso2.tree.dir.tmp.1 > /identity-repos/wso2.tree.dir.tmp.2
	sed "/.feature/d" /identity-repos/wso2.tree.dir.tmp.2 > /identity-repos/wso2.tree.dir
	rm /identity-repos/wso2.tree.dir.tmp.1
	rm /identity-repos/wso2.tree.dir.tmp.2
	rm /y.txt
fi

if [ "$1" == "update" ] 
then
	mkdir -p /identity-repos/wso2
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

	cd /identity-repos/wso2
	input="/y.txt"

	while read -r line; do
  		git clone "$line"
	done < $input

	tree -fi > /identity-repos/wso2.tree
	tree -fi -d > /identity-repos/wso2.tree.dir.tmp.1
	sed "/src/d" /identity-repos/wso2.tree.dir.tmp.1 > /identity-repos/wso2.tree.dir.tmp.2
	sed "/.feature/d" /identity-repos/wso2.tree.dir.tmp.2 > /identity-repos/wso2.tree.dir
	rm /identity-repos/wso2.tree.dir.tmp.1
	rm /identity-repos/wso2.tree.dir.tmp.2

	rm /y.txt
fi

cd /
 
if [ "$1" == "" ] ||[ "$1" == "clone" ] ||[ "$1" == "list" ] || [ "$1" == "update" ]
then
	for  n in 1 2 3 4 5
	do
	curl -u $gituser:$gitpassword  https://api.github.com/orgs/wso2-extensions/repos\?type\=all\&\page\=$n\&per_page\=100 | jq --raw-output '.[] | (.clone_url)' | grep --color  "security\|identity\|auth\|provisioning\|user|\userstore\|saml\|oauth\|charon\|carbon-kernel\|directory\|product-is" >>/z.txt
	clear
	done
fi


if [ "$1" == "clone" ] 
then
	mkdir /identity-repos/wso2-extensions
	cd /identity-repos/wso2-extensions
	input="/z.txt"

	while read -r line; do
		git clone "$line"
	done < $input

	tree -fi > /identity-repos/wso2-extensions.tree
	tree -fi -d > /identity-repos/wso2-extensions.tree.dir.tmp.1
	sed "/src/d" /identity-repos/wso2-extensions.tree.dir.tmp.1 > /identity-repos/wso2-extensions.tree.dir.tmp.2
	sed "/.feature/d" /identity-repos/wso2-extensions.tree.dir.tmp.2 > /identity-repos/wso2-extensions.tree.dir
    rm /identity-repos/wso2-extensions.tree.dir.tmp.1
	rm /identity-repos/wso2-extensions.tree.dir.tmp.2


	rm /z.txt
fi

if [ "$1" == "update" ] 
then
	mkdir -p /identity-repos/wso2-extensions
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
	tree -fi > /identity-repos/wso2-extensions.tree
	tree -fi -d > /identity-repos/wso2-extensions.tree.dir.tmp.1
	sed "/src/d" /identity-repos/wso2-extensions.tree.dir.tmp.1 > /identity-repos/wso2-extensions.tree.dir.tmp.2
	sed "/.feature/d" /identity-repos/wso2-extensions.tree.dir.tmp.2 > /identity-repos/wso2-extensions.tree.dir
    rm /identity-repos/wso2-extensions.tree.dir.tmp.1
	rm /identity-repos/wso2-extensions.tree.dir.tmp.2
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

if [ "$1" == "find" ] && [ "$2" == "-j" ] && [ "$3" != "" ]
then
        cd /identity-repos
		grep "$3" wso2.tree.dir > /results.wso2.dir
		sed -i -e 's|/|/tree/master/|2' /results.wso2.dir
		sed -i -e 's|.|https://github.com/wso2|1' /results.wso2.dir
		grep "$3" wso2-extensions.tree.dir > /results.wso2.extensions.dir
		sed -i -e 's|/|/tree/master/|2' /results.wso2.extensions.dir
		sed -i -e 's|.|https://github.com/wso2-extensions|1' /results.wso2.extensions.dir
		cat /results.wso2.dir >> /results.wso2.extensions.dir
        cat /results.wso2.extensions.dir
        rm /results.wso2.extensions.dir
		rm /results.wso2.dir
elif [ "$1" == "find" ] && [ "$2" != "" ]
then
        cd /identity-repos
		grep "$2" wso2.tree > /results.wso2
		sed -i -e 's|/|/tree/master/|2' /results.wso2
		sed -i -e 's|.|https://github.com/wso2|1' /results.wso2
		grep "$2" wso2-extensions.tree > /results.wso2.extensions
		sed -i -e 's|/|/tree/master/|2' /results.wso2.extensions
		sed -i -e 's|.|https://github.com/wso2-extensions|1' /results.wso2.extensions
		cat /results.wso2 >> /results.wso2.extensions
        cat /results.wso2.extensions
        rm /results.wso2.extensions
		rm /results.wso2
        #find . -name  "$2" -type d > /results.txt
        #sed -i -e 's|/|/tree/master/|3' /results.txt
        #sed -i -e 's|./wso2/|https://github.com/wso2/|g' /results.txt
        #sed -i -e 's|./wso2-extensions/|https://github.com/wso2-extensions/|g' /results.txt
fi