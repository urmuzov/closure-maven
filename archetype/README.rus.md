О архетипе
==========

Этот архетип предназначен для создания базовой структуры JavaScript проекта использующего Google Closure Library.

В архетип интегрированы следующие средства:

* closure-maven/compiler-plugin - Closure Compiler
* closure-maven/packages/closure-library - Closure Library
* closure-maven/package-plugin - упаковка JS в артефакты
* [Web Resource Optimizer for Java (wro4j)](http://code.google.com/p/wro4j/) - склеивание, минификация и другие операции с JS и CSS файлами
* [jsdoctk-plugin](http://code.google.com/p/jsdoctk-plugin/) - JSDoc
* [Maven JSTools Plugin](http://dev.abiss.gr/mvn-jstools/) - JSLint

Генерация проекта
=================

Для работы требуется maven версии 3 и выше.

Генерация проекта происходит при помощи команды:

    mvn -DarchetypeRepository=http://urmuzov.github.com/maven-repository/releases/ \
        -DarchetypeGroupId=com.github.urmuzov \
        -DarchetypeArtifactId=closure-package-maven-archetype \
        -DarchetypeVersion=1.0.2 \
        -DgroupId=my.test.group \
        -DartifactId=test-artifact \
        -Dversion=1.0.0-SNAPSHOT \
        -Dpackage=my.test.pkg \
        archetype:generate

где

* `archetypeRepository` - URL maven-репозитория с архетипом
* `archetypeGroupId`, `archetypeArtifactId` - идентификатор группы и артефакта
* `archetypeVersion` - версия ахретипа (по возможности используйте последнюю доступную)
* `groupId`, `artifactId`, `version` - идентификатор группы, артефакта и версия вашего нового проекта
* `package` - основной пакет вашего проекта, он же будет использоваться в качестве closure-package

Сборка прокта
=============

Сборка проекта должна осуществляться с указанием одного из профилей:

* `compiled` - уровень компиляции ADVANCED_OPTIMIZATIONS
* `merged` - уровень компиляции WHITESPACE_ONLY, форматирование PRETTY_PRINT
* `sources` - компиляцтя происходит с уровнем ADVANCED_OPTIMIZATIONS, но в html будут подключены файлы с исходными кодами. Удобно для дебага.
* `sources-no-compile` - компиляции не происходит, просто в html подключаются файлы с исходными кодами. Используется также для дебага.
* `jar` - сборка jar-архива для распространения

