PLAYDIR="emocial"

cd ${PLAYDIR} && sbt run -Dconfig.resource=test.conf -Dhttp.port=5555