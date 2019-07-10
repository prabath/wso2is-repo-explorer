for file in ./**/*.zip
do
  unzip -d "../unzipped" "$file"
done
cd ../unzipped
find .  -type f ! -name '*.jar' -delete
tree -if > ../wso2is-repo-explorer/src/patch.tree
cd ..
rm -rf unzipped
cd wso2is-repo-explorer
git add .
git commit -m "updates"
git push
