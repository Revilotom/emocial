PLAYDIR="../play-java-jpa-example"

echo Building play application...

(cd $PLAYDIR && sbt dist)

cp $PLAYDIR/target/universal/*.zip ./

echo Building image..

docker build --build-arg DBPASS=$DBPASS --rm -f "Dockerfile" -t 19-tom-oliver:latest .
docker save -o theSave 19-tom-oliver:latest

echo Copying docker image...

scp -i ~/Downloads/FinalProject.pem -rC ~/Documents/training/finalProject/19-tom-oliver/deployment/theSave ec2-user@13.112.1.50:~/

rm ./theSave
rm ./*.docker_temp*

echo Starting server...

./remoteStart.sh