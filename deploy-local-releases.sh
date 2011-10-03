mvn -DaltDeploymentRepository=urmuzov-releases::default::file:../maven-repository/releases clean deploy &&
cd ../maven-repository && ./update-directory-index.sh