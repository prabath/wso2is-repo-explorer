cd /Users/prabath/wso2is-repo-explorer/git/wso2is-repo-explorer
git pull
cd ../../svn/updates
svn up
for file in ./**/*.zip
do
  unzip -o -d "../unzipped" "$file"
done
cd ../unzipped
tree -if | grep "authenticationendpoint.war\|oauth2.war\|wso2.war\|accountrecoveryendpoint.war\|api#identity#consent-mgt#v1.0.war\|api#identity#entitlement.war\|api#identity#oauth2#dcr#v1.0.war\|api#identity#oauth2#v1.0.war\|api#identity#recovery#v0.9.war\|api#identity#user#v1.0.war\|emailotpauthenticationendpoint.war\|scim2.war\|smsotpauthenticationendpoint.war\|x509certificateauthenticationendpoint.war" > ../../git/wso2is-repo-explorer/src/indexes/updates.war
sed -i -e 's|.war|.zip|g' ../../git/wso2is-repo-explorer/src/indexes/updates.war
sed -i -e 's|./||1' ../../git/wso2is-repo-explorer/src/indexes/updates.war

input="../../git/wso2is-repo-explorer/src/indexes/updates.war"
while read -r line; do
    dir=$(echo "$line" | sed -n 's/.*\(^\(WSO2-CARBON-PATCH-[0-9].[0-9].[0-9]-[0-9][0-9][0-9][0-9]\)\).*/\1/p'
  	unzip -o -d "../war/$dir" "$line"
done < $input

cd ../war
tree -if | grep "pom.properties" > ../../git/wso2is-repo-explorer/src/indexes/updates.war

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
