cd org.facilelogin.wso2is.repo.explorer
mvn clean install
cd ..
docker build -t rex .
docker tag rex prabath/rex
docker push prabath/rex
