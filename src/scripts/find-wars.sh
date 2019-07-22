  rm -rf ../wars
  mkdir -p ../wars
  war_files=$(find . -name "*.war")
  for file in $war_files
  do 
      dir=$(echo "$file" | sed -n 's/.*\(wso2is-[0-9].[0-9].[0-9]\).*/\1/p')
      file_name=$(echo "$file" | sed 's/.*\///' | sed 's/\.[^.]*$//')
      if [ ! -d "../wars/$dir/$file_name" ]
      then
        echo $file
        mkdir -p ../wars/$dir/$file_name
  	    unzip -o -d "../wars/$dir/$file_name" "$file"
      fi
  done

  cd ../wars
  find .  -type f ! -name '*.properties' -delete
  tree -if > war.properties
  cp $REX_HOME/src/org.facilelogin.wso2is.repo.explorer/target/org.facilelogin.wso2is.repo.explorer-1.0.0.jar .
  java -cp org.facilelogin.wso2is.repo.explorer-1.0.0.jar org.facilelogin.wso2is.repo.explorer.WarParser
