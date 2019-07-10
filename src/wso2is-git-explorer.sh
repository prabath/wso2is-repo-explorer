#!/bin/bash

file="/identity-repos/wso2.tree"

if [ -f "$file" ]
then
  rm /identity-repos/wso2.tree
  rm /identity-repos/patch.tree
  rm /identity-repos/wso2-extensions.tree
  rm /identity-repos/wso2.tree.dir
  rm /identity-repos/wso2-extensions.tree.dir
fi

file="/identity-repos/.repodata/wso2.tree"

if [ "$1" == "update-tree" ] && [ -f "$file" ]
then
  rm /identity-repos/.repodata/wso2.tree
fi

file="/identity-repos/.repodata/wso2.tree"
if [ -f "$file" ]
then
	echo ""
else
    cd /
	mkdir -p /identity-repos/.repodata
    wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/wso2-extensions.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/wso2.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/patch.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/wso2-extensions.tree.dir
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/wso2.tree.dir
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/is520.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/is530.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/is540.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/is541.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/is550.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/is560.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/is570.tree
	wget https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/is580.tree

	cp /is520.tree /identity-repos/.repodata/is520.tree
	cp /is530.tree /identity-repos/.repodata/is530.tree
	cp /is530.tree /identity-repos/.repodata/is540.tree
	cp /is530.tree /identity-repos/.repodata/is541.tree
	cp /is530.tree /identity-repos/.repodata/is550.tree
	cp /is530.tree /identity-repos/.repodata/is560.tree
	cp /is530.tree /identity-repos/.repodata/is570.tree
	cp /is530.tree /identity-repos/.repodata/is580.tree
	cp /wso2.tree /identity-repos/.repodata/wso2.tree
	cp /patch.tree /identity-repos/.repodata/patch.tree
	cp /wso2-extensions.tree /identity-repos/.repodata/wso2-extensions.tree
	cp /wso2.tree.dir /identity-repos/.repodata/wso2.tree.dir
	cp /wso2-extensions.tree.dir /identity-repos/.repodata/wso2-extensions.tree.dir
fi

if [ "$1" == "" ] ||[ "$1" == "clone" ] ||[ "$1" == "list" ] || [ "$1" == "update" ]
then
    echo "Generating the list of repos..."
	echo ""
	for  n in 1 2 3 4 5
	do
	curl -s -u $gituser:$gitpassword  https://api.github.com/orgs/wso2/repos\?type\=all\&\page\=$n\&per_page\=100 | jq --raw-output '.[] | (.clone_url)'| grep  "security\|identity\|auth\|provisioning\|user|\userstore\|saml\|oauth\|charon\|directory\|product-is\|carbon-kernel\|charon\|carbon-secvault\|carbon-commons\|balana" >>/y.txt
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

	cd carbon-kernel
	git checkout 4.4.x
	cd ..

	tree -fi > /identity-repos/.repodata/wso2.tree
	tree -fi -d > /identity-repos/.repodata/wso2.tree.dir.tmp.1
	sed "/src/d" /identity-repos/.repodata/wso2.tree.dir.tmp.1 > /identity-repos/.repodata/wso2.tree.dir.tmp.2
	sed "/.feature/d" /identity-repos/.repodata/wso2.tree.dir.tmp.2 > /identity-repos/.repodata/wso2.tree.dir
	rm /identity-repos/.repodata/wso2.tree.dir.tmp.1
	rm /identity-repos/.repodata/wso2.tree.dir.tmp.2
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

	cd carbon-kernel
	git checkout 4.4.x
	cd ..

	tree -fi > /identity-repos/.repodata/wso2.tree
	tree -fi -d > /identity-repos/.repodata/wso2.tree.dir.tmp.1
	sed "/src/d" /identity-repos/.repodata/wso2.tree.dir.tmp.1 > /identity-repos/.repodata/wso2.tree.dir.tmp.2
	sed "/.feature/d" /identity-repos/.repodata/wso2.tree.dir.tmp.2 > /identity-repos/.repodata/wso2.tree.dir
	rm /identity-repos/.repodata/wso2.tree.dir.tmp.1
	rm /identity-repos/.repodata/wso2.tree.dir.tmp.2

	rm /y.txt
fi

cd /
 
if [ "$1" == "" ] ||[ "$1" == "clone" ] ||[ "$1" == "list" ] || [ "$1" == "update" ]
then
	for  n in 1 2 3 4 5
	do
	curl -s -u $gituser:$gitpassword  https://api.github.com/orgs/wso2-extensions/repos\?type\=all\&\page\=$n\&per_page\=100 | jq --raw-output '.[] | (.clone_url)' | grep "security\|identity\|auth\|provisioning\|user|\userstore\|saml\|oauth\|charon\|directory\|product-is\|carbon-kernel" >>/z.txt
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

	tree -fi > /identity-repos/.repodata/wso2-extensions.tree
	tree -fi -d > /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.1
	sed "/src/d" /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.1 > /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.2
	sed "/.feature/d" /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.2 > /identity-repos/.repodata/wso2-extensions.tree.dir
    rm /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.1
	rm /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.2


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
	tree -fi > /identity-repos/.repodata/wso2-extensions.tree
	tree -fi -d > /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.1
	sed "/src/d" /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.1 > /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.2
	sed "/.feature/d" /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.2 > /identity-repos/.repodata/wso2-extensions.tree.dir
    rm /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.1
	rm /identity-repos/.repodata/wso2-extensions.tree.dir.tmp.2
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

if [ "$1" == "patches" ] 
then
java -cp org.facilelogin.wso2is.repo.explorer-1.0.0.jar org.facilelogin.wso2is.repo.explorer.RepoExplorer $2 $3
fi

if [ "$1" == "find" ] && [ "$2" == "-j" ] && [ "$3" != "" ]
then
        cd /identity-repos/.repodata
		grep "$3" wso2.tree.dir > /results.wso2.dir
		sed -i -e 's|/|/tree/master/|2' /results.wso2.dir
		sed -i -e 's|.|https://github.com/wso2|1' /results.wso2.dir
		grep "$3" wso2-extensions.tree.dir > /results.wso2.extensions.dir
		sed -i -e 's|/|/tree/master/|2' /results.wso2.extensions.dir
		sed -i -e 's|.|https://github.com/wso2-extensions|1' /results.wso2.extensions.dir
		cat /results.wso2.dir >> /results.wso2.extensions.dir

		sed -i -e 's|carbon-kernel/tree/master|carbon-kernel/tree/4.4.x|1' /results.wso2.extensions.dir
        cat /results.wso2.extensions.dir
		echo ""
		echo "If you didn't find what you want, try to refine your search criteria. For example, instead of org.wso2.balana, try just, balana"
        echo ""
		rm /results.wso2.extensions.dir
		rm /results.wso2.dir
elif [ "$1" == "find" ] && [ "$2" != "" ]
then
        cd /identity-repos/.repodata
		grep "$2" wso2.tree > /results.wso2
		sed -i -e 's|/|/tree/master/|2' /results.wso2
		sed -i -e 's|.|https://github.com/wso2|1' /results.wso2
		grep "$2" wso2-extensions.tree > /results.wso2.extensions
		sed -i -e 's|/|/tree/master/|2' /results.wso2.extensions
		sed -i -e 's|.|https://github.com/wso2-extensions|1' /results.wso2.extensions
		cat /results.wso2 >> /results.wso2.extensions

		sed -i -e 's|carbon-kernel/tree/master|carbon-kernel/tree/4.4.x|1' /results.wso2.extensions

        cat /results.wso2.extensions
        rm /results.wso2.extensions
		rm /results.wso2

		echo ""
		echo "If you didn't find what you want, try to refine your search criteria. For example, instead of org.wso2.balana, try just, balana"
        echo ""
        #find . -name  "$2" -type d > /results.txt
        #sed -i -e 's|/|/tree/master/|3' /results.txt
        #sed -i -e 's|./wso2/|https://github.com/wso2/|g' /results.txt
        #sed -i -e 's|./wso2-extensions/|https://github.com/wso2-extensions/|g' /results.txt
fi