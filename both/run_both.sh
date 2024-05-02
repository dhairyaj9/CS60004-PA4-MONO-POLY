class_file=$1

mv ./soot ./soot1


javac -g ./testcase/$class_file.java
java -cp sootclasses-trunk-jar-with-dependencies.jar soot.Main -cp . -pp --process-dir ./testcase -f J -d Analysis/simple_method_inline_jimple

rm ./sootOutput/$class_file.class
rm ./sootOutput/$class_file.jimple


javac -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4_both.java
java -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4_both c $class_file
java -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4_both J $class_file

cp ./sootOutput/*.class ./bothOutput/

cd ./testcase
java $class_file > {$class_file}_output.txt
cd ./../sootOutput
java $class_file > {$class_file}_output.txt
cd ..

#if the diff is empty, then print "Success" else print "Fail". leave 2 lines and then print success/fail
echo ""
echo ""
diff ./testcase/{$class_file}_output.txt ./sootOutput/{$class_file}_output.txt
if [ $? -eq 0 ]
then
    echo "SUCCESS"
else
    echo "Fail"
fi

rm ./testcase/{$class_file}_output.txt
rm ./sootOutput/{$class_file}_output.txt

mv ./soot1 ./soot



