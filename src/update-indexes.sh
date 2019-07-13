cd /Users/prabath/wso2is-repo-explorer/git/wso2is-repo-explorer
git pull
cd ../../svn/updates
svn up
for file in ./**/*.zip
do
  unzip -o -d "../unzipped" "$file"
done
cd ../unzipped
tree -if | grep ".war" > ../../git/wso2is-repo-explorer/src/indexes/updates.war
find .  -type f ! -name '*.jar' -delete
tree -if | grep ".jar" > ../../git/wso2is-repo-explorer/src/indexes/updates
cd ..
rm -rf unzipped
cd ../git/wso2is-repos
./rex.sh update
cp -r .repodata/wso2* ../wso2is-repo-explorer/src/indexes/
cd ../wso2is-repo-explorer
git add .
git commit -m "updates to indexes"
git push
