mysql -u admin -ppassword <<EOF
use testDB;
DELETE IGNORE FROM Person;
EOF