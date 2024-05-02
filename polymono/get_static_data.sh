class_name=$1

./run.sh $class_name 

echo "Running normal"
cd ./../both/testcase
time java $class_name
time java -Xint $class_name

echo "Running monoOutput"
cd ./../../polymono/polymonoOutput
time java $class_name
time java -Xint $class_name

echo "Running bothOutput"
cd ./../../both/bothOutput
time java $class_name
time java -Xint $class_name
