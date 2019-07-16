#!/bin/bash
## export REX_ONLINE=false to work on offline mode.
if [ -z "$REX_ONLINE" ] ; then
  online_mode=true
else
  if [ "$REX_ONLINE" == "true" ] 
  then
	  online_mode=true
  else
	  online_mode=false
  fi
fi

cd /
mkdir -p /identity-repos/.repodata

## clone, list, update, update-index functions are not availble for the offline mode.
if [ "$online_mode" = true ]
then
	## carries the latest version of the rex.sh.
	file="/identity-repos/.repodata/version.rex"
	if [ ! -f "$file" ]
	then
		echo "version.rex file not found inside .repodata"
		wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/version
		mv /version /identity-repos/.repodata/version.rex
	fi

	## carries the latest version of the docker image.
	file="/identity-repos/.repodata/version.docker"
	if [ ! -f "$file" ]
	then
    	## there is no reason you should not be having this file.
		## probably you are using an older version of this tool.
		echo "version.docker file not found inside .repodata"
		wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/version
		mv /version /identity-repos/.repodata/version.docker
	fi

	## carries the latest version of indexes.
	## if the version in the git repo is larger than the local version, the tool
	## will pull the updated indexes.
	file="/identity-repos/.repodata/version.index"
	if [ ! -f "$file" ]
	then
    	## there is no reason you should not be having this file.
		## probably you are using an older version of this tool.
		echo "version.index file not found inside .repodata"
		wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/indexes/version
		mv /version /identity-repos/.repodata/version.index
	fi

	## find the local rex version.
	version_old=$(cat /identity-repos/.repodata/version.rex)
	## get the latest docker image version from the git repo.
	wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/version
	mv /version /identity-repos/.repodata/version.rex
	version_new=$(cat /identity-repos/.repodata/version.rex)
	if [ "$version_old" -lt "$version_new" ]
	then
		echo "A new version of the rex.sh is available..."
		echo "Get the latest from https://github.com/prabath/wso2is-repo-explorer/raw/master/rex.sh and make it executable."
		touch /identity-repos/.repodata/rex."$version_new"
		exit 1
	fi

	#file= "/identity-repos/.repodata/rex."$version_new
	if [ -f "/identity-repos/.repodata/rex."$version_new ]
	then
		echo "A new version of the rex.sh is available..."
		echo "Get the latest from https://github.com/prabath/wso2is-repo-explorer/raw/master/rex.sh and make it executable."
		exit 1
	fi

	## find the local docker image version.
	version_old=$(cat /identity-repos/.repodata/version.docker)
	## get the latest docker image version from the git repo.
	wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/version
	mv /version /identity-repos/.repodata/version.docker
	version_new=$(cat /identity-repos/.repodata/version.docker)
	if [ "$version_old" -lt "$version_new" ]
	then
		echo "A new Docker image is available and ready to update..."
		touch /identity-repos/.repodata/image.update
		echo "Please re-run the tool..."
		exit 1
	fi
    
	## find the local version.
	version_old=$(cat /identity-repos/.repodata/version.index)
	## get the latest version from the git repo.
	wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/indexes/version
	mv /version /identity-repos/.repodata/version.index
	version_new=$(cat /identity-repos/.repodata/version.index)
	if [ "$version_old" -lt "$version_new" ]
	then
		## removing .repodata will automatically, trigger the tool to get updated indexes.
		echo "Updated indexs are available..."
  		rm /identity-repos/.repodata/updates
  		rm /identity-repos/.repodata/wso2
  		rm /identity-repos/.repodata/wso2-components
  		rm /identity-repos/.repodata/wso2-extensions
  		rm /identity-repos/.repodata/wso2-extensions-components	
		echo "Cleaned current indexes..."
	fi

	## if .repodata directory present - remove it when we do an update-index.
	## we store all the indexing files in the .repodata directory.
	if [ "$1" == "update-index" ] 
	then
		rm /identity-repos/.repodata/updates
		rm /identity-repos/.repodata/wso2
		rm /identity-repos/.repodata/wso2-components
		rm /identity-repos/.repodata/wso2-extensions
		rm /identity-repos/.repodata/wso2-extensions-components
	fi

	## if the .repodata directory is not present, we need to create it
	## and pull data from the wso2is-repo-explorer git repo.
	file="/identity-repos/.repodata/wso2"
	if [ ! -f "$file" ]
	then
		echo "Pulling updated indexes from the git repo..."
		cd /
		## pull the file, which containes all the wum update details.
		wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/indexes/updates
		## pull the file, which containes all the file details from wso2 git org.
		wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/indexes/wso2
		## pull the file, which containes all the components details from wso2-extensions git org.
		wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/indexes/wso2-components
		## pull the file, which containes all the file details from wso2-extensions git org.
		wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/indexes/wso2-extensions	
		## pull the file, which containes all the components details from wso2-extensions git org.
		wget -q https://raw.githubusercontent.com/prabath/wso2is-repo-explorer/master/src/indexes/wso2-extensions-components

		## copy all the indexing files from the root to the local directory.
		cp /updates /identity-repos/.repodata/updates
		cp /wso2 /identity-repos/.repodata/wso2
		cp /wso2-components /identity-repos/.repodata/wso2-components
		cp /wso2-extensions /identity-repos/.repodata/wso2-extensions
		cp /wso2-extensions-components /identity-repos/.repodata/wso2-extensions-components

		echo "Updated with the latest indexes..."
		echo ""
	fi

	## before a clone or an update - first we build a list of all avaiable repos.
	if [ "$1" == "" ] ||[ "$1" == "clone" ] ||[ "$1" == "list" ] || [ "$1" == "update" ]
	then
		echo "Generating the list of repos..."
		echo ""
		for  n in 1 2 3 4 5
		do
		## get the repo list from the wso2 git org.
		curl -s -u $gituser:$gitpassword  https://api.github.com/orgs/wso2/repos\?type\=all\&\page\=$n\&per_page\=100 | jq --raw-output '.[] | (.clone_url)'| grep  "security\|identity\|auth\|provisioning\|user|\userstore\|saml\|oauth\|charon\|directory\|product-is\|carbon-kernel\|charon\|carbon-secvault\|carbon-commons\|balana" >>/y.txt
		## get the repo list from the wso2-extensions git org.
		curl -s -u $gituser:$gitpassword  https://api.github.com/orgs/wso2-extensions/repos\?type\=all\&\page\=$n\&per_page\=100 | jq --raw-output '.[] | (.clone_url)' | grep "security\|identity\|auth\|provisioning\|user|\userstore\|saml\|oauth\|charon\|directory\|product-is\|carbon-kernel" >>/z.txt
		clear
		done
	fi

	if [ "$1" == "" ] || [ "$1" == "list" ]
	then
		echo ""
		echo "Identity/Security repos under WSO2:"
		echo ""
		## y.txt has the latest repo list.  
		cat /y.txt
		rm /y.txt
		echo ""
		echo "Identity/Security repos under WSO2 Extensions:" 
		echo ""
		## z.txt has the latest repo list. 
		cat /z.txt
		rm /z.txt
	fi

	if [ "$1" == "clone" ] 
	then
		mkdir -p /identity-repos/wso2
		cd /identity-repos/wso2
		input="/y.txt"
		
		## clone all the repos from wso2 git org.
		while read -r line; do
			git clone "$line"
		done < $input

		## wso2 identity server uses, 4.4.x branch of carbon-kernel.
		cd carbon-kernel
		git checkout 4.4.x
		cd ..

		## updating indexes.
		tree -fi > /identity-repos/.repodata/wso2

		## update the wso2-components index. 
		## we need to do some cleaning first.
		tree -fi -d > /wso2-components.tmp.1
		sed "/src/d" /wso2-components.tmp.1 > /wso2-components.tmp.2
		sed "/.feature/d" /wso2-components.tmp.2 > /identity-repos/.repodata/wso2-components
		rm /wso2-components.tmp.1 /wso2-components.tmp.2 /y.txt

		mkdir /identity-repos/wso2-extensions
		cd /identity-repos/wso2-extensions
		## z.txt has the latest updated repo list.
		input="/z.txt"

		## clone all the repos from wso2-extensions git org.
		while read -r line; do
			git clone "$line"
		done < $input

		## updating indexes.
		tree -fi > /identity-repos/.repodata/wso2-extensions
		## update the wso2-components index. 
		## we need to do some cleaning first.
		tree -fi -d > /wso2-extensions-components.tmp.1
		sed "/src/d" /wso2-extensions-components.tmp.1 > /wso2-extensions-components.tmp.2
		sed "/.feature/d" /wso2-extensions-components.tmp.2 > /identity-repos/.repodata/wso2-extensions-components
		rm /wso2-extensions-components.tmp.1 /wso2-extensions-components.tmp.2 /z.txt
	fi

	if [ "$1" == "update" ] 
	then
		## updates all the git repos under wso2 git org.
		mkdir -p /identity-repos/wso2
		cd /identity-repos/wso2

		for file in */ ; do 
			if [[ -d "$file" && ! -L "$file" ]]; then
				## file is a directory.
				## remove the forward slash from the directory name.
				file1=$(echo "$file" | sed "s|/||g")
				## y.txt has the latest repo list. 
				## remove all existing repos from the latest one - so we know new ones. 
				sed "/$file1/d" /y.txt > /y.tmp
				echo "$file1";
				## get the updated repo list - by removing the current updated repo.
				## at the end of the day y.txt will only have the new repo list, we have to clone.
				mv /y.tmp /y.txt
				cd "$file"; 
				## update the repo.
				git pull;
				cd ..;
			fi; 
		done

		## start cloning the new repos.
		cd /identity-repos/wso2
		input="/y.txt"

		while read -r line; do
			git clone "$line"
		done < $input

		## wso2 identity server uses, 4.4.x branch of carbon-kernel.
		cd carbon-kernel
		git checkout 4.4.x
		cd ..

		## updating indexes.
		tree -fi > /identity-repos/.repodata/wso2

		## update the wso2-components index. 
		## we need to do some cleaning first.
		tree -fi -d > /wso2-components.tmp.1
		sed "/src/d" /wso2-components.tmp.1 > /wso2-components.tmp.2
		sed "/.feature/d" /wso2-components.tmp.2 > /identity-repos/.repodata/wso2-components
		rm /wso2-components.tmp.1 /wso2-components.tmp.2 /y.txt

		## updates all the git repos under wso2-extensions git org.

		mkdir -p /identity-repos/wso2-extensions
		cd /identity-repos/wso2-extensions
		for file in */ ; do 
			if [[ -d "$file" && ! -L "$file" ]]; then
				## file is a directory.
				## remove the forward slash from the directory name.
				file1=$(echo "$file" | sed "s|/||g")
				## z.txt has the latest repo list. 
				## remove all existing repos from the latest one - so we know new ones.
				sed "/$file1/d" /z.txt > /z.tmp  
				echo "$file1";
				## get the updated repo list - by removing the current updated repo.
				## at the end of the day z.txt will only have the new repo list, we have to clone.
				mv /z.tmp /z.txt
				cd "$file"; 
				## update the repo.
				git pull;
				cd ..;
			fi; 
		done
	
		## start cloning the new repos.
		cd /identity-repos/wso2-extensions
		input="/z.txt"

		while read -r line; do
			git clone "$line"
		done < $input

		## updating indexes.
		tree -fi > /identity-repos/.repodata/wso2-extensions
		## update the wso2-components index. 
		## we need to do some cleaning first.
		tree -fi -d > /wso2-extensions-components.tmp.1
		sed "/src/d" /wso2-extensions-components.tmp.1 > /wso2-extensions-components.tmp.2
		sed "/.feature/d" /wso2-extensions-components.tmp.2 > /identity-repos/.repodata/wso2-extensions-components
		rm /wso2-extensions-components.tmp.1 /wso2-extensions-components.tmp.2 /z.txt
	fi
else
	echo "The tool is running in offline mode."
	echo "clone, list, update, update-index functions are not availble for the offline mode."
	echo ""
fi

if [ "$1" == "find" ] && [ "$2" == "-c" ] && [ "$3" != "" ]
then
## find repos by the component name.
## we only search in the wso2-components, when we see -c argument with find.

        cd /identity-repos/.repodata

		## first wso2 git org.
		grep "$3" wso2-components > /wso2-components.results
		sed -i -e 's|/|/tree/master/|2' /wso2-components.results
		sed -i -e 's|.|https://github.com/wso2|1' /wso2-components.results

        ## now, wso2-extensions git org.
		grep "$3" wso2-extensions-components > /wso2-extensions-components.results
		sed -i -e 's|/|/tree/master/|2' /wso2-extensions-components.results
		sed -i -e 's|.|https://github.com/wso2-extensions|1' /wso2-extensions-components.results

		## merge the results
		cat /wso2-components.results >> /wso2-extensions-components.results
 
        ## fix the git url of carbon-kernel repo to reflect the 4.4.x branch.
		sed -i -e 's|carbon-kernel/tree/master|carbon-kernel/tree/4.4.x|1' /wso2-extensions-components.results
		cat /wso2-extensions-components.results | grep -vE "jsp|html|jag|gif|png|images|pom.xml|css|src|.xml|.md|.java|.properties|modules|LICENSE"
		rm /wso2-extensions-components.results /wso2-components.results
		echo ""
		echo "If you didn't find what you want, try to refine your search criteria. For example, instead of org.wso2.balana, try just, balana"
    	echo ""    
		echo "Also to filter the results you can use the following:"
		echo "./rex.sh find $3 | grep -vE 'STRING1|STRING2|STRING3'"
		echo ""
elif [ "$1" == "find" ] && [ "$2" != "" ]
then
## find repos by the provided search string.

        cd /identity-repos/.repodata

		## first wso2 git org.
		grep "$2" wso2 > /wso2.results
		sed -i -e 's|/|/tree/master/|2' /wso2.results
		sed -i -e 's|.|https://github.com/wso2|1' /wso2.results

        ## now, wso2-extensions git org.
		grep "$2" wso2-extensions > /wso2.extensions.results
		sed -i -e 's|/|/tree/master/|2' /wso2.extensions.results
		sed -i -e 's|.|https://github.com/wso2-extensions|1' /wso2.extensions.results

		## merge the results
		cat /wso2.results >> /wso2.extensions.results

        ## fix the git url of carbon-kernel repo to reflect the 4.4.x branch.
		sed -i -e 's|carbon-kernel/tree/master|carbon-kernel/tree/4.4.x|1' /wso2.extensions.results

        cat /wso2.extensions.results
        rm /wso2.extensions.results /wso2.results

		echo ""
		echo "If you didn't find what you want, try to refine your search criteria. For example, instead of org.wso2.balana, try just, balana"
        echo ""
		echo "Also to filter the results you can use the following:"
		echo "./rex.sh find $2 | grep -vE 'STRING1|STRING2|STRING3'"
		echo ""
fi

if [ "$1" == "updates" ] 
then
java -cp org.facilelogin.wso2is.repo.explorer-1.0.0.jar org.facilelogin.wso2is.repo.explorer.RepoExplorer $2 $3 $4 $5
fi