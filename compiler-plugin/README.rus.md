
О плагине
=========

[This document in English](https://github.com/urmuzov/closure-maven/blob/master/compiler-plugin/README.md)

Плагин инкапсулирует Google Closure-Compiler для вызова во время сборки артефакта.
 

Точки входа
-----------

Для компиляции исходных кодов для Google Closure-Compiler нужно предоставить:

* файлы с исходными кодами (эти файлы содержат директивы `goog.provide`)
* "**точки входа**" (файлы не содержащие `goog.provide`)

Конечно существует случай когда исходные коды не разбиваются на пакеты, а лежат прямо в точке входа, но такой случай не рассматривается.

Если нужно получить несколько скомпилированных js-файлов использующих общую базу кода (например декстопная и мобильная версии или по js-файлу на каждую страницу сайта), нужно определить несколько точек входа, каждая из которых скомпилируется в независимый js-файл.

Задачей плагина является подключение только одной точки входа при каждом проходе компиляции. Это делается путем именования всех точек входа по одному шаблону (по-умолчанию `*.entry.js` параметр `entryFileMask`).

Подобный подход дает возможность "отсекать" то, что точно не используется на странице, подключающий эту точку входа, а значить экономить на размере получаемого файла.
Наример мобильная версия вашего приложения находится в файле `mobile.entry.js`, а десктопная в `desktop.entry.js`. Если компилировать их раздельно получатся два файла `mobile.js` и `desktop.js`, содержащие только необходимый код для каждой из версий. В противном случае можно определить одну точку входа `main.entry.js` и получить файл `main.js` содержащий код всего приложения. В любом случа **хотя бы одна** точка входа должна быть определена.

Конфигурация
------------

Список опций плагина:

<table>
<tr>
  <th>Название</th>
  <th>Тип</th>
  <th>Значение по-умолчанию</th>
  <th>Описание</th>
</tr>
<tr>
  <td>entryFileMask</td>
  <td>string</td>
  <td>*.entry.js</td>
  <td>маска имен файлов для фильтрации точек входа</td>
</tr>
<tr>
  <td>stopOnWarnings</td>
  <td>boolean</td>
  <td>true</td>
  <td>остановка компиляции артефакта, если closure-compiler вернул хоть один warning</td>
</tr>
<tr>
  <td>stopOnErrors</td>
  <td>boolean</td>
  <td>true</td>
  <td>остановка компиляции артефакта, если closure-compiler вернул хоть один error</td>
</tr>
<tr>
  <td>loggingLevel</td>
  <td>string</td>
  <td>WARNING</td>
  <td>Один из <code>java.util.logging.Level</code><br>[<code>ALL</code>, <code>CONFIG</code>, <code>FINE</code>, <code>FINER</code>, <code>FINEST</code>, <code>INFO</code>, <code>OFF</code>, <code>SEVERE</code>, <code>WARNING</code>]</td>
</tr>
<tr>
  <td>compilationLevel</td>
  <td>string</td>
  <td>ADVANCED_OPTIMIZATIONS</td>
  <td>Один из <code>com.google.javascript.jscomp.CompilationLevel</code><br>[<code>WHITESPACE_ONLY</code>, <code>SIMPLE_OPTIMIZATIONS</code>, <code>ADVANCED_OPTIMIZATIONS</code>]</td>
</tr>
<tr>
  <td>warningLevel</td>
  <td>string</td>
  <td>VERBOSE_EXTRA</td>
  <td>Один из <code>com.google.javascript.jscomp.WarningLevel</code><br>[<code>QUIET</code>, <code>DEFAULT</code>, <code>VERBOSE</code>] или <code>VERBOSE_EXTRA</code>, который расширяет <code>VERBOSE</code> категориями <code>accessControl</code>, <code>strictModuleDepCheck</code>, <code>visibility</code>. Подробнее о категорях <a href="http://code.google.com/p/closure-compiler/wiki/Warnings">здесь</a></td>
</tr>
<tr>
  <td>formatting</td>
  <td>string</td>
  <td>null</td>
  <td><code>null</code> или один из [<code>PRETTY_PRINT</code>, <code>PRINT_INPUT_DELIMITER</code>]</td>
</tr>
<tr>
  <td>manageClosureDependencies</td>
  <td>boolean</td>
  <td>true</td>
  <td><a href="http://code.google.com/p/closure-compiler/wiki/ManageClosureDependencies">Подробнее об этой опции</a></td>
</tr>
<tr>
  <td colspan="4">TODO: дописать</td>
</tr>
<tr>
  <th colspan="4">Опции отладки</th>
</tr>
<tr>
  <td>debug</td>
  <td>boolean</td>
  <td>false</td>
  <td>Перевод компилятора в режим отладки с меньшей обфускаций имен и вывзод дополнительной информации при компиляции</td>
</tr>
<tr>
  <td>logSourceFiles</td>
  <td>boolean</td>
  <td>false</td>
  <td>вывод списка всех использованных при компиляции файлов с исходными кодами</td>
</tr>
<tr>
  <td>logExternFiles</td>
  <td>boolean</td>
  <td>false</td>
  <td>вывод списка всех использованных при компиляции файлов с экстернами</td>
</tr>
</table>