class_file=$1

mv ./soot ./soot1


javac -g ./testcase/$class_file.java

cd testcase
java $class_file > {$class_file}_output.txt
cd ..

java -cp sootclasses-trunk-jar-with-dependencies.jar soot.Main -cp . -pp --process-dir ./testcase -f J -d Analysis/simple_method_inline_jimple

rm ./sootOutput/$class_file.class
rm ./sootOutput/$class_file.jimple


javac -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4.java
echo ""
echo ""
echo "retrieving static data from normal class file"
echo ""
java -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4 c $class_file NoPolytoMono NoMethodInlining cha
java -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4 c $class_file NoPolytoMono NoMethodInlining pta
echo ""
echo ""
echo "poly to mono transformation on class file"
echo ""
java -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4 c $class_file doPolytoMono NoMethodInlining pta

cp ./sootOutput/*.class ./testcase/
cp ./sootOutput/*.class ./polymonoOutput/

echo ""
echo ""
echo "retrieving static data from PolytoMono transformed class file"
echo ""
java -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4 c $class_file NoPolytoMono NoMethodInlining cha
java -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4 c $class_file NoPolytoMono NoMethodInlining pta

cd sootOutput
java $class_file > {$class_file}_output.txt
cd ..

echo ""
echo ""
diff ./testcase/{$class_file}_output.txt ./sootOutput/{$class_file}_output.txt
if [ $? -eq 0 ]
then
    echo "PolyToMono SUCCESS"
else
    echo "Fail"
fi

cd ./../both
./run_both.sh $class_file
cp ./sootOutput/*.class ./../polymono/testcase/
cd ./../polymono


echo ""
echo ""
echo "retrieving static data from PolytoMono + MethodInline transformed class file"
echo ""
java -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4 c $class_file NoPolytoMono NoMethodInlining cha
java -cp .:sootclasses-trunk-jar-with-dependencies.jar PA4 c $class_file NoPolytoMono NoMethodInlining pta


cd sootOutput
java $class_file > {$class_file}_output.txt
cd ..

echo ""
echo ""
diff ./testcase/{$class_file}_output.txt ./sootOutput/{$class_file}_output.txt
if [ $? -eq 0 ]
then
    echo "PolyToMono SUCCESS"
else
    echo "Fail"
fi

rm ./testcase/{$class_file}_output.txt
rm ./sootOutput/{$class_file}_output.txt

mv ./soot1 ./soot





