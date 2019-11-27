PLAYDIR="play-java-jpa-example"

cd ${PLAYDIR} && sbt run -Dconfig.resource=test.conf -Dhttp.port=5555