sudo docker load -i theSave
sudo docker run --log-driver=awslogs --log-opt awslogs-group=docker-logs --log-opt awslogs-stream=webapp -p 9000:9000 -d 19-tom-oliver:latest