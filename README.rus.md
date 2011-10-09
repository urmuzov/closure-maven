
О проекте
=========

[This document in English](https://github.com/urmuzov/closure-maven/blob/master/README.md)

Этот проект представляет собой набор утилит для работы с Google Closure Tools:

* [closure-maven/compiler-plugin](https://github.com/urmuzov/closure-maven/tree/master/compiler-plugin) плагин для Google Closure-Compiler;
* [closure-maven/package-plugin](https://github.com/urmuzov/closure-maven/tree/master/package-plugin) плагин для упаковывания javascript проектов в maven-артефакты;
* [closure-maven/packages/closure-library](https://github.com/urmuzov/closure-maven/tree/master/packages/closure-library) Google Closure-Library запакованная при помощи package-plugin;
* [closure-maven/archetype](https://github.com/urmuzov/closure-maven/tree/master/archetype) archetype для создания проекта объединяющего все выше перечисленное.

Для работы требуется **maven3 и выше**. Последнюю версию можно взять с [сайта проекта](http://maven.apache.org/download.html) или при помощи менеждера пакетов вашего дистрибутива.

Артефакты находятся в [репозитории на гитхабе](http://urmuzov.github.com/maven-repository), подробная информация о репозитории на [его странице](https://github.com/urmuzov/maven-repository)

Для подключения репозитория, добавьте следующие строки в ваш pom.xml

    ...
    <repositories>
      <repository>
        <id>urmuzov-snapshots</id>
        <url>http://urmuzov.github.com/maven-repository/snapshots</url>
      </repository>
      <repository>
        <id>urmuzov-releases</id>
        <url>http://urmuzov.github.com/maven-repository/releases</url>
      </repository>
    </repositories>
    ...

compiler-plugin
---------------

Этот плагин используется для вызова Google Closure-Compiler в цикле сборки артефакта.

Поддерживает все основные настройки, включая:

* `compilationLevel`;
* `loggingLevel`;
* `warningLevel`;
* `formatting`;
* `manageClosureDependencies`;
* `generateExports`;
* настроку директорий и файлов для компиляции;
* настофку директорий и файлов с экстернами;

Дополнинельно поддерживает:

* Несколько проходов компиляции с разными настройкаи;
* опцию `addDefaultExterns` для добавления включенных в поставку экстернов;
* опции для отладки.

Информацию по настройке плагина смотрите [здесь](https://github.com/urmuzov/closure-maven/blob/master/compiler-plugin/README.rus.md)

package-plugin
--------------

Этот плагин используется для запаковки javascript-библиотек в maven-артефакты (далее называемые closure-package). В дальнейшем можно управлять зависимостями javascript-библиотек средствами maven.

При компиляции проекта package-plugin выполняет две функции:

1. распаковывает все closure-package, которые может найти в classpath в `${project.build.directory}/closure` (откуда их в дальнейшем может взять compiler-plugin); 
2. собирает текущий проект в один или несколько closure-package согласно файлу `${project.build.directory}/classes/META-INF/closure-packages.properties`.

Информацию по настройке плагина смотрите [здесь](https://github.com/urmuzov/closure-maven/tree/master/package-plugin/README.rus.md)

packages/closure-library
---------------

Обертка превращающая Google Closure-Library в closure-package.

Подробнее на [этой странице](https://github.com/urmuzov/closure-maven/tree/master/packages/closure-library/README.rus.md)

archetype
---------

Архетип для создания проекта использующего Closure-Library, компилируемого и собираемого при помощи compiler-plugin и package-plugin.

    mvn archetype:generate \
      -DarchetypeRepository=http://urmuzov.github.com/maven-repository/releases/
      -DarchetypeGroupId=com.github.urmuzov \ 
      -DarchetypeArtifactId=closure-package-maven-archetype \
      -DarchetypeVersion=1.0.2 \
      -DgroupId=my.test.group \
      -DartifactId=test-artifact \
      -Dversion=1.0.0-SNAPSHOT \
      -Dpackage=my.test.pkg
      
Первые 4 параметра определяют где хранится ахретип, какой ахретип используется, обязательно наличие параметра `archetypeVersion` в проект будут подключены compiler-plugin, package-plugin и packages/closure-library такой же версии.
 
Следующие 4 параметра определяют название группы, артефакта, версию и пакет (опционально) в котором будет лежать стандартная структура closure-package.

Подробнее об архетипе смотрите [здесь](https://github.com/urmuzov/closure-maven/tree/master/package-plugin/README.rus.md)
