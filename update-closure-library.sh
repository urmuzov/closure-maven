# temporary directory
mkdir closure-new
mkdir closure-new/javascript
mkdir closure-new/css
echo Directories created

# checking out new closure-library
svn checkout http://closure-library.googlecode.com/svn/trunk/
echo New sources checked out

# copy .js files
cd trunk/closure
find . | grep -i "\.js$" | grep -iv "_test\.js$" | xargs -i cp --parents {} ../../closure-new/javascript/
cd ../..
rm -rf closure-new/javascript/goog/demos # demos breaks compilation
echo js files copied

# copy .css files
cd trunk/closure/css
find . | grep -i "\.css" | xargs -i cp --parents {} ../../../closure-new/css
cd ../goog/css
find . | grep -i "\.css" | xargs -i cp --parents {} ../../../../closure-new/css
cd ../../../..
echo css files copied

# switching closure-library
rm -rf packages/closure-library/src/main/resources/com/github/urmuzov/closuremaven/closurelibrarypackage/*
echo Old closure-library removed
cp -r closure-new/* packages/closure-library/src/main/resources/com/github/urmuzov/closuremaven/closurelibrarypackage/
echo New closure-library moved

# cleaning up
rm -rf closure-new trunk


