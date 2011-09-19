## Installing Closure Compiler dependency

	$ wget http://closure-compiler.googlecode.com/files/compiler-20100917.tar.gz
	$ tar xzf compiler-20100917.tar.gz
	$ mvn install:install-file -DgroupId=com.google -DartifactId=closure-compiler -Dversion=20100917 -Dpackaging=jar -Dfile=compiler.jar


## Compiling javascript sources

	<plugin>
		<groupId>glisoft</groupId>
		<artifactId>closure-compiler-maven-plugin</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<configuration>
			<compilationLevel>SIMPLE_OPTIMIZATIONS</compilationLevel>
		</configuration>
		<executions>
			<execution>
				<goals>
					<goal>compile</goal>
				</goals>
			</execution>
		</executions>
	</plugin>
