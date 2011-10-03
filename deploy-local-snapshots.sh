mvn -DaltDeploymentRepository=urmuzov-snapshots::default::file:../maven-repository/snapshots clean deploy &&
cd ../maven-repository && ./update-directory-index.sh