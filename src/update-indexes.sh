if [ -z "$REX_HOME" ] ; then
  echo "REX_HOME env variable not set!"
  exit 1
fi

while true
do
  cd $REX_HOME/git/wso2is-repo-explorer
  git pull
  cd ../../svn/updates
  svn up
  echo "svn updated successfully"
  mkdir -p ../unzipped 
  for file in ./**/*.zip
  do
      file_name=$(echo $file | sed 's/.*\///' | sed -e 's|.zip||g')
      if [ ! -d "../unzipped/$file_name" ]
	    then
        unzip -o -d "../unzipped" "$file"
      fi
  done
  echo " files unzipped successfully" 
  cd ../unzipped
  find .  -type f ! -name '*.jar' ! -name '*.war' -delete
  tree -if | grep ".jar" > ../../git/wso2is-repo-explorer/src/indexes/updates

  mkdir -p ../jars 
  for file in ./**/*.jar
  do 
      dir=$(echo "$file" | sed -n 's/.*\(WSO2-CARBON-UPDATE-[0-9].[0-9].[0-9]-[0-9]\{4\}\).*/\1/p'
      if [ ! -d "../jars/$dir" ]
	    then
  	    unzip -o -d "../jars/$dir" "$file"
      fi
  done

  mkdir -p ../wars 
  for file in ./**/*.war
  do 
      dir=$(echo "$file" | sed -n 's/.*\(WSO2-CARBON-UPDATE-[0-9].[0-9].[0-9]-[0-9]\{4\}\).*/\1/p'
      if [ ! -d "../wars/$dir" ]
	    then
  	    unzip -o -d "../wars/$dir" "$file"
      fi
  done

  cd ..
  #rm -rf unzipped
  #echo "removed unziped directory" 
  cd ../git/wso2is-repos
  ./rex.sh update 
  echo "git repos updated" 
  cp -r .repodata/wso2* ../wso2is-repo-explorer/src/indexes/
  cd ../wso2is-repo-explorer
  while read number; do
    version_old=$number
  done <./src/indexes/version
  increment=1 
  version_new=$(($version_old + $increment))
  echo "$version_new" > ./src/indexes/version
  echo "version updated" 
  git add .
  git commit -m "automatic updates to indexes" 
  git push 
  echo "pushed to git"
  cd $REX_HOME/git/prabath.github.io
  git pull
  message=$(echo Last indexed at $(date))
  sed -i '' "s/Last indexed at.*/$message/" index.md
  git add .
  git commit -m "adding last updated time for indexes" 
  git push 
  sleep 14400
done
