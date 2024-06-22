pushd agent || exit -1
./gradlew assemble || exit -1
popd || exit -1
pip install . || exit -1