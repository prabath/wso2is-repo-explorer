cd /Users/prabath/wso2is-repo-explorer/git/wso2is-repo-explorer
touch update.index.log
git pull
cd ../../svn/updates
svn up
 printf '%s %s\n' "$(date)" " svn updated successfully" >> update.index.log
for file in ./**/*.zip
do
  unzip -o -d "../unzipped" "$file"
done
printf '%s %s\n' "$(date)" " files unzipped successfully" >> update.index.log
cd ../unzipped
find .  -type f ! -name '*.jar' -delete
tree -if | grep ".jar" > ../../git/wso2is-repo-explorer/src/indexes/updates
cd ..
rm -rf unzipped
printf '%s %s\n' "$(date)" " removed unziped directory" >> update.index.log
cd ../git/wso2is-repos
./rex.sh update
printf '%s %s\n' "$(date)" " git repos updated" >> update.index.log
cp -r .repodata/wso2* ../wso2is-repo-explorer/src/indexes/
cd ../wso2is-repo-explorer
while read number; do
  version_old=$number
done <./src/indexes/version
increment=1 
version_new=$(($version_old + $increment))
echo "$version_new" > ./src/indexes/version
printf '%s %s\n' "$(date)" " version updated" >> update.index.log
git add .
git commit -m "automatic updates to indexes"
git push
printf '%s %s\n' "$(date)" " pushed to git" >> update.index.log
