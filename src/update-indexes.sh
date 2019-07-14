cd /Users/prabath/wso2is-repo-explorer/git/wso2is-repo-explorer
touch /Users/prabath/wso2is-repo-explorer/update.index.log
git pull
cd ../../svn/updates
svn up >> /Users/prabath/wso2is-repo-explorer/update.index.log
 printf '%s %s\n' "$(date)" " svn updated successfully" >> /Users/prabath/wso2is-repo-explorer/update.index.log
for file in ./**/*.zip
do
  unzip -o -d "../unzipped" "$file"
done
printf '%s %s\n' "$(date)" " files unzipped successfully" >> /Users/prabath/wso2is-repo-explorer/update.index.log
cd ../unzipped
find .  -type f ! -name '*.jar' -delete
tree -if | grep ".jar" > ../../git/wso2is-repo-explorer/src/indexes/updates
cd ..
rm -rf unzipped
printf '%s %s\n' "$(date)" " removed unziped directory" >> /Users/prabath/wso2is-repo-explorer/update.index.log
cd ../git/wso2is-repos
./rex.sh update >> /Users/prabath/wso2is-repo-explorer/update.index.log
printf '%s %s\n' "$(date)" " git repos updated" >> /Users/prabath/wso2is-repo-explorer/update.index.log
cp -r .repodata/wso2* ../wso2is-repo-explorer/src/indexes/
cd ../wso2is-repo-explorer
while read number; do
  version_old=$number
done <./src/indexes/version
increment=1 
version_new=$(($version_old + $increment))
echo "$version_new" > ./src/indexes/version
printf '%s %s %s\n' "$(date)" " version updated " "$version_new" >> /Users/prabath/wso2is-repo-explorer/update.index.log
git add .
git commit -m "automatic updates to indexes" >> /Users/prabath/wso2is-repo-explorer/update.index.log
git push >> /Users/prabath/wso2is-repo-explorer/update.index.log
printf '%s %s\n' "$(date)" " pushed to git" >> /Users/prabath/wso2is-repo-explorer/update.index.log
