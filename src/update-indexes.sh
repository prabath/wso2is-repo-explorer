cd /Users/prabath/wso2is-repo-explorer/svn/updates
svn up
for file in ./**/*.zip
do
  unzip -d "../unzipped" "$file"
done
cd ../unzipped
find .  -type f ! -name '*.jar' -delete
tree -if > ../../git/wso2is-repo-explorer/src/indexes/updates
cd ..
rm -rf unzipped
cd ../git/wso2is-repos
./rex update
cp .repodata/wso2 ../wso2is-repo-explorer/src/*
cd ../wso2is-repo-explorer
git pull
git add .
git commit -m "updates to indexes"
git push
