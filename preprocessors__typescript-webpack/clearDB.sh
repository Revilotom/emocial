mysql -u admin -ppassword <<EOF
use testDB;
DELETE FROM Person;
EOF