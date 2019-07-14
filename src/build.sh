git pull
cd org.facilelogin.wso2is.repo.explorer
mvn clean install
cd ..
docker build -t rex .
docker tag rex prabath/rex
docker push prabath/rex
cd ..
while read number; do
  version_old=$number
done <./src/version
increment=1 
version_new=$(($version_old + $increment))
echo "$version_new" > ./src/version
git add .
git commit -m "automatic updates to indexes"
git push
