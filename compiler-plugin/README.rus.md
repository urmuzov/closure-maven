О плагине
=========

[This document in English](https://github.com/urmuzov/closure-maven/blob/master/compiler-plugin/README.md)

Плагин инкапсулирует Google Closure-Compiler для вызова во время сборки артефакта.
 
Конфигурация
============

Конфигурация плагина происходит следующим образом:
            
    ...
    <plugin>
        <groupId>com.github.urmuzov</groupId>
        <artifactId>closure-compiler-maven-plugin</artifactId>
        <version>1.0.1</version>
        <configuration>
            ... здесь задаются опции ...
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

Каждая опция из списка опций (см. ниже) превращается в тег вида `<названиеОпции>значение</названиеОпции>` и добавляется в тег `<configuration>`
Например:

        <configuration>
            <debug>true</debug>
            <compilationLevel>WHITESPACE_ONLY</compilationLevel>
            ...
        </configuration>

Тег `<executions>` определяет в какую фазу сборки проекта будет запускаться плагин, если убрать этот тег плагин нужно будет запускать из командной строки вручную.

Подходы к организации исходных кодов
====================================

Простой вариант
---------------

В самом простом случае для компиляции нужно указать только папки/файлы с исходными js-файлами (см. опцию `sources`) и задать один или более проход, в котором указать выходной файл (см. опции `passes`, `outputFile`). Это все что нужно, чтобы передать файлы на компиляцию. Не забывайте про экспорт нужных вам символов, чтобы не получить пустой файл на выходе.

Этот пример проиллюстрирован в [примере 1](https://github.com/urmuzov/closure-maven/tree/master/examples/compiler-plugin-example1)

Сложный вариант
---------------

Например, вы разрабатываете сложное приложение, которое должно иметь интерфейс для настольных браузеров и для мобильных платформ (или по одному js-файлу на каждую страницу сайта). JavaScript логика такого приложения одинакова и там, и там, различаются только код, ответственный за построение интерфейса.

Поэтому вы хотите настроить компилятор таким образом, чтобы он использовал весь ваш код, но компилировал только необходимую его часть как для настольной, так и для мобильной версии. Для организации такого процесса помогут так называемые "**точки входа**", каждая из которых скомпилируется в независимый js-файл.

Задачей плагина является подключение только одной точки входа при каждом проходе компиляции. Это делается путем именования всех точек входа по одному шаблону (по-умолчанию `*.entry.js` параметр `entryFileMask`).

Подобный подход дает возможность "отсекать" то, что точно не используется на странице, подключающий эту точку входа, а значить экономить на размере получаемого файла.

Наример мобильная версия вашего приложения находится в файле `mobile.entry.js`, а десктопная в `desktop.entry.js`. Если компилировать их раздельно получатся два файла `mobile.js` и `desktop.js`, содержащие только необходимый код для каждой из версий.

Этот пример проиллюстрирован в [примере 2](https://github.com/urmuzov/closure-maven/tree/master/examples/compiler-plugin-example2)


Проходы компиляции
==================

Компиляция одной точки входа называется "проходом" (pass).

Проходы можно сконфигурировать двумя способами:

- Через опции `simplePasses`, `simplePassesEntriesDir`, `simplePassesOutputDir`. Такие проходы называются "простыми проходами"
- Через опцию `passes`, задающую все параметры прохода

Ни `simplePasses`, ни `passes` не являются обязательными, главное чтобы в результате конфигурации получился бы хотя бы
один проход любого типа.

Опция `passes` должна содержать элементы `pass`, внутри которых тоже могут быть опции из списка ниже. Может ли опция находиться теге `configuration` или теге `pass` также указано в таблице.

Результирующая конфигурация, применяемая при компиляции прохода получается при помощи наследования не указанных в теге `pass` опций из тега `configuration` и переопределением указанных.

        <configuration>
            <debug>true</debug>
            <compilationLevel>WHITESPACE_ONLY</compilationLevel>
            ...
            <passes>
                <pass>
                    <compilationLevel>ADVANCED_OPTIMIZATIONS</compilationLevel>
                    <entryFile>${basedir}/src/main/resources/desktop.entry.js</entryFile>
                    <outputFile>${project.build.directory}/${project.build.finalName}/desktop.js</outputFile>
                </pass>
                <pass>
                    <entryFile>${basedir}/src/main/resources/mobile.entry.js</entryFile>
                    <outputFile>${project.build.directory}/${project.build.finalName}/mobile.js</outputFile>
                </pass>
            </passes>
        </configuration>

В примере выше `desktop.js` будет скомпилирован с уровнем `ADVANCED_OPTIMIZATIONS`, а `mobile.js` с уровнем `WHITESPACE_ONLY`.
    
Список опций
============

<table>
<tr>
  <th>Название</th>
  <th>Тип</th>
  <th>Значение по-умолчанию</th>
  <th>&lt;conf?</th>
  <th>&lt;pass&gt;?</th>
  <th>Описание</th>
</tr>
<tr>
  <td>entryFileMask</td>
  <td>string</td>
  <td>*.entry.js</td>
  <td>+</td>
  <td></td>
  <td>маска имен файлов для фильтрации точек входа</td>
</tr>
<tr>
  <td>stopOnWarnings</td>
  <td>boolean</td>
  <td>true</td>
  <td>+</td>
  <td></td>
  <td>остановка компиляции артефакта, если closure-compiler вернул хоть один warning</td>
</tr>
<tr>
  <td>stopOnErrors</td>
  <td>boolean</td>
  <td>true</td>
  <td>+</td>
  <td></td>
  <td>остановка компиляции артефакта, если closure-compiler вернул хоть один error</td>
</tr>
<tr>
  <td>simplePasses</td>
  <td>string</td>
  <td>null</td>
  <td>+</td>
  <td></td>
  <td>
  Простая конфигурация проходов компиляции. В качестве значения, строка разделенная пробелами, запятыми или точками с запятой<br>
  Например: <code>desktop mobile</code> означает два прохода, для точек входа <code>desktop</code> и <code>mobile</code>, файлы которых компилятор будет искать в директории <code>simplePassesEntriesDir</code> (см. опцию ниже) согласно маске из <code>entryFileMask</code>, а сгенерированный файл класть в директорию <code>simplePassesOutputDir</code>
  </td>
</tr>
<tr>
  <td>simplePasses<br>EntriesDir</td>
  <td>string (path)</td>
  <td><i><b>обязательная</b>, если указана simplePasses</i></td>
  <td>+</td>
  <td></td>
  <td>Директория для поиска точек входа, при простой компиляции</td>
</tr>
<tr>
  <td>simplePasses<br>OutputDir</td>
  <td>string (path)</td>
  <td><i><b>обязательная</b>, если указана simplePasses</i></td>
  <td>+</td>
  <td></td>
  <td>Директория куда будут генерироваться скомпилированные точки входа, при простой компиляции</td>
</tr>
<tr>
  <td>passes</td>
  <td>list</td>
  <td><i>пустой список</i></td>
  <td>+</td>
  <td></td>
  <td>
  Список проходов компиляции.<br>
  Список должен состоять из тегов <code>&lt;pass>опции_прохода&lt;/pass></code>
  </td>
</tr>
<tr>
  <td>entryFile</td>
  <td>string (path)</td>
  <td>null</td>
  <td></td>
  <td>+</td>
  <td>Путь до дочки входа, например <code>${basedir}/src/main/resources/desktop.entry.js</code></td>
</tr>
<tr>
  <td>outputFile</td>
  <td>string (path)</td>
  <td><b>обязательная опция</b></td>
  <td></td>
  <td>+</td>
  <td>Путь для записи скомпилированного файла, например <code>${project.build.directory}/<br>${project.build.finalName}/desktop.js</code></td>
</tr>
<tr>
  <td>sources</td>
  <td>list</td>
  <td><i>пустой список</i></td>
  <td>+</td>
  <td>+</td>
  <td>
  Список файлов и директорий, из которых будут извлекаться исходные коды для компиляции.<br>
  Список должен состоять из тегов <code>&lt;source>путь_до_папки_или_файла&lt;/source></code>
  </td>
</tr>
<tr>
  <td>externs</td>
  <td>list</td>
  <td><i>пустой список</i></td>
  <td>+</td>
  <td>+</td>
  <td>
  Список файлов и директорий, из которых будут извлекаться экстерны.<br>
  Список должен состоять из тегов <code>&lt;extern>путь_до_папки_или_файла&lt;/extern></code>
  </td>
</tr>
<tr>
  <td>loggingLevel</td>
  <td>string</td>
  <td>WARNING</td>
  <td>+</td>
  <td>+</td>
  <td>Один из <code>java.util.logging.Level</code><br>[<code>ALL</code>, <code>CONFIG</code>, <code>FINE</code>, <code>FINER</code>, <code>FINEST</code>, <code>INFO</code>, <code>OFF</code>, <code>SEVERE</code>, <code>WARNING</code>]</td>
</tr>
<tr>
  <td>compilationLevel</td>
  <td>string</td>
  <td>ADVANCED_<br>OPTIMIZATIONS</td>
  <td>+</td>
  <td>+</td>
  <td>Один из <code>com.google.javascript.jscomp.CompilationLevel</code><br>[<code>WHITESPACE_ONLY</code>, <code>SIMPLE_OPTIMIZATIONS</code>, <code>ADVANCED_OPTIMIZATIONS</code>]</td>
</tr>
<tr>
  <td>warningLevel</td>
  <td>string</td>
  <td>VERBOSE_<br>EXTRA</td>
  <td>+</td>
  <td>+</td>
  <td>Один из <code>com.google.javascript.jscomp.WarningLevel</code><br>[<code>QUIET</code>, <code>DEFAULT</code>, <code>VERBOSE</code>] или <code>VERBOSE_EXTRA</code>, который расширяет <code>VERBOSE</code> категориями <code>accessControl</code>, <code>strictModuleDepCheck</code>, <code>visibility</code>. Подробнее о категорях <a href="http://code.google.com/p/closure-compiler/wiki/Warnings">здесь</a></td>
</tr>
<tr>
  <td>formatting</td>
  <td>string</td>
  <td>null</td>
  <td>+</td>
  <td>+</td>
  <td><code>null</code> или один из [<code>PRETTY_PRINT</code>, <code>PRINT_INPUT_DELIMITER</code>]</td>
</tr>
<tr>
  <td>manageClosure<br>Dependencies</td>
  <td>boolean</td>
  <td>true</td>
  <td>+</td>
  <td>+</td>
  <td><a href="http://code.google.com/p/closure-compiler/wiki/ManageClosureDependencies">Подробнее об этой опции</a></td>
</tr>
<tr>
  <td>generateExports</td>
  <td>boolean</td>
  <td>true</td>
  <td>+</td>
  <td>+</td>
  <td>Экспортирование символов, отмеченных <code>@export</code></td>
</tr>
<tr>
  <td>addDefaultExterns</td>
  <td>boolean</td>
  <td>false</td>
  <td>+</td>
  <td>+</td>
  <td>Подключение экстернов входящих в стандартную поставку компилятора</td>
</tr>
<tr>
  <th colspan="6">Опции отладки</th>
</tr>
<tr>
  <td>debug</td>
  <td>boolean</td>
  <td>false</td>
  <td>+</td>
  <td></td>
  <td>Перевод компилятора в режим отладки с меньшей обфускаций имен и вывзод дополнительной информации при компиляции</td>
</tr>
<tr>
  <td>logSourceFiles</td>
  <td>boolean</td>
  <td>false</td>
  <td>+</td>
  <td></td>
  <td>вывод списка всех использованных при компиляции файлов с исходными кодами</td>
</tr>
<tr>
  <td>logExternFiles</td>
  <td>boolean</td>
  <td>false</td>
  <td>+</td>
  <td></td>
  <td>вывод списка всех использованных при компиляции файлов с экстернами</td>
</tr>
</table>
Примечание: Переносы в названиях опций и значениях по-умолчанию сделаны только с целью форматирования текста, в конфигурации они не допускаются

Примеры конфигурации
====================

Простой пример 1
----------------

[Здесь](https://github.com/urmuzov/closure-maven/tree/master/examples/compiler-plugin-example1) вы можете найти README файл с описанием примера

Пример из архетипа
------------------

[Архетип](https://github.com/urmuzov/closure-maven/tree/master/archetype) всегда содержит актуальную версию настроек плагина.

Пример ниже получен при сборке проекта, сгенерированного архетипом с подставленными значениями свойств (такой вид можно получить при помощи `mvn --debug ... install`)

    <configuration>
        <addDefaultExterns>true</addDefaultExterns>
        <compilationLevel>ADVANCED_OPTIMIZATIONS</compilationLevel>
        <debug>false</debug>
        <entryFileMask>*.entry.js</entryFileMask>
        <externs>
          <extern>/home/username/project/target/closure/externs</extern>
        </externs>
        <formatting>null</formatting>
        <generateExports>true</generateExports>
        <logExternFiles>false</logExternFiles>
        <logSourceFiles>false</logSourceFiles>
        <loggingLevel>WARNING</loggingLevel>
        <manageClosureDependencies>true</manageClosureDependencies>
        <mavenProject default-value="${project}"/>
        <simplePasses>index</simplePasses>
        <simplePassesEntriesDir>/home/username/project/target/closure/javascript</simplePassesEntriesDir>
        <simplePassesOutputDir>/home/username/project/target/project</simplePassesOutputDir>
        <sources>
          <source>/home/username/project/target/closure/javascript</source>
        </sources>
        <stopOnErrors>true</stopOnErrors>
        <stopOnWarnings>true</stopOnWarnings>
        <warningLevel>VERBOSE_EXTRA</warningLevel>
    </configuration>
