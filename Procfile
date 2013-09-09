web: java $JAVA_OPTS -jar target/microblog*.jar db migrate microblog.yml && java $JAVA_OPTS -Ddw.http.port=$PORT -Ddw.http.adminPort=$PORT -jar target/microblog*.jar server microblog.yml
