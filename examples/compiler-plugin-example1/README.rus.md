О примере
=========

Этот пример представляет собой самое простое использование компилятора. Проект был сгенерирован при помощи архетипа для Java Web Application.

Нужно скомпилировать файлы находящиеся в директории `${basedir}/src/main/resources/`, а полученный код как файл `main.js` подключить в файл `index.html`. Для этого в `pom.xml` были добавлены следующие строки:

    <build>
        <plugins>
            <plugin>
                <groupId>com.github.urmuzov</groupId>
                <artifactId>closure-compiler-maven-plugin</artifactId>
                <version>${project.version}</version>
                <configuration>
                    <addDefaultExterns>true</addDefaultExterns>
                    <passes>
                        <pass>
                            <sources>
                                <source>${basedir}/src/main/resources/</source>
                            </sources>
                            <outputFile>${project.build.directory}/${project.build.finalName}/main.js</outputFile>
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

* `addDefaultExterns` необходим, чтобы компилятор знал, что такое `document`, `document.body`...
* `pass` - единственный проход
  * `sources` > `source` - единственная папка, в которой лежат исходники
  * `outputFile` - файл куда будет компилироваться результат

Обратите внимание, что хоть `generateExports` и включена по-умолчанию, но без указания пути к Google Closure-Library в опции `sources` (или просто копировании исходников в папку `${basedir}/src/main/resources/`) директива

    /**
     * @export
     */
    function onLoad() {
    ...

будет компилироваться с ошибкой

    21.09.2011 10:58:02 com.google.javascript.jscomp.LoggerErrorManager println
    SEVERE: /home/username/closure-maven/examples/compiler-plugin-example1/src/main/webapp/main.entry.js:6: ERROR - variable goog is undeclared
    function onLoad() {
             ^

    21.09.2011 10:58:02 com.google.javascript.jscomp.LoggerErrorManager printSummary
    WARNING: 1 error(s), 0 warning(s)

причиной этому скорее всего служит внутреннее преобразование этой дерективы в вызов `goog.exportSymbol`, но достоверной информации на этот счет у меня нет.
Экспорт выполнен вручную при помощи `window['onLoad'] = onLoad;`.

Также бесполезно подключать к этому проекту packages/closure-library, поскольку для его работы необходим package-plugin, который здесь тоже не подключен.