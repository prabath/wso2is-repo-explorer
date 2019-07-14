cd /Users/prabath/wso2is-repo-explorer/git/wso2is-repo-explorer
git pull
cd ../../svn/updates
svn up
for file in ./**/*.zip
do
  unzip -o -d "../unzipped" "$file"
done
cd ../unzipped
find .  -type f ! -name '*.jar' -delete
tree -if | grep ".jar" > ../../git/wso2is-repo-explorer/src/indexes/updates
cd ..
rm -rf unzipped
cd ../git/wso2is-repos
./rex.sh update
cp -r .repodata/wso2* ../wso2is-repo-explorer/src/indexes/
cd ../wso2is-repo-explorer
while read number; do
  version_old=$number
done <./src/indexes/version
increment=1 
version_new=$(($version_old + $increment))
echo "$version_new" > ./src/indexes/version
git add .
git commit -m "automatic updates to indexes"
git push
