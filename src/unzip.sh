for file in ./**/*.zip
do
  unzip -d "../unzipped" "$file"
done

find ../unzipped -type f ! -name '*.jar' -delete