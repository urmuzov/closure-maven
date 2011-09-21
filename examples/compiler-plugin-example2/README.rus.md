О примере
=========

Этот пример представляет собой самое простое использование компилятора. Проект был сгенерирован при помощи архетипа для Java Web Application.

Нужно скомпилировать две точки входа `desktop.entry.js` и `mobile.entry.js`. Для этого в `pom.xml` были добавлены следующие строки:

    <build>
        <plugins>
            <plugin>
                <groupId>com.github.urmuzov</groupId>
                <artifactId>closure-compiler-maven-plugin</artifactId>
                <version>${project.version}</version>
                <configuration>
                    <addDefaultExterns>true</addDefaultExterns>
                    <sources>
                        <source>${basedir}/src/main/resources/</source>
                    </sources>
                    <passes>
                        <pass>
                            <entryFile>${basedir}/src/main/resources/desktop.entry.js</entryFile>
                            <outputFile>${project.build.directory}/${project.build.finalName}/desktop.js</outputFile>
                        </pass>
                        <pass>
                            <entryFile>${basedir}/src/main/resources/mobile.entry.js</entryFile>
                            <outputFile>${project.build.directory}/${project.build.finalName}/mobile.js</outputFile>
                        </pass>
                    </passes>
                </configuration>
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            ...

Обратите внимание, что в ващем проекте, вместо `${project.version}` **нужно** будет указывать конкретную версию плагина, поскольку у вас `${project.version}` будет версией **вашего** проекта.

Конфигурация
============

* `addDefaultExterns` - необходим, чтобы компилятор знал, что такое `document`, `document.body`...
* `sources` > `source` - единственная папка, в которой лежат исходники (для обоих проходов)
* `passes` -> `pass` - проход
  * `entryFile` - файл с точкой входа. (его по прежнему необходимо именовать по маске `*.entry.js`)
  * `outputFile` - файл куда будет компилироваться результат

