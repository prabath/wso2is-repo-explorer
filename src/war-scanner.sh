cd ../unzipped
tree -if | grep "authenticationendpoint.war\|oauth2.war\|wso2.war\|accountrecoveryendpoint.war\|api#identity#consent-mgt#v1.0.war\|api#identity#entitlement.war\|api#identity#oauth2#dcr#v1.0.war\|api#identity#oauth2#v1.0.war\|api#identity#recovery#v0.9.war\|api#identity#user#v1.0.war\|emailotpauthenticationendpoint.war\|scim2.war\|smsotpauthenticationendpoint.war\|x509certificateauthenticationendpoint.war" > ../../git/wso2is-repo-explorer/src/indexes/updates.war
mkdir ../war
input="../../git/wso2is-repo-explorer/src/indexes/updates.war"
while read -r line; do
    dir=$(echo "$line" | sed -n 's/.*\(WSO2-CARBON-UPDATE-[0-9].[0-9].[0-9]-[0-9]\{4\}\).*/\1/p'
  	unzip -o -d "../war/$dir" "$line"
done < $input

cd ../war
tree -if | grep "pom.properties" > ../../git/wso2is-repo-explorer/src/indexes/updates.war
