PLAYDIR="../play-java-jpa-example"

echo Building play application...

(cd $PLAYDIR && sbt dist)
cp $PLAYDIR/target/universal/*.zip ./

echo Building image..

docker build --build-arg DBPASS=$DBPASS --rm -f "Dockerfile" -t 19-tom-oliver:latest .
docker tag 19-tom-oliver:latest 047532750968.dkr.ecr.ap-northeast-1.amazonaws.com/final-project:latest
docker push 047532750968.dkr.ecr.ap-northeast-1.amazonaws.com/final-project:latest