REM react_rest_build

@echo off

echo --------------------------------------
echo ----------react-rest builder----------
echo --------------------------------------
echo.

echo cleaning static folder
del /S /f /q restapi\src\main\resources\static\
rmdir /s /q restapi\src\main\resources\static\
echo.
echo.

echo building react app
call npm run build --prefix reactui
echo.
echo.

echo copying react app to static folder
xcopy reactui\build\* restapi\src\main\resources\static\ /s /e
echo.
echo.

echo building rest api war file
echo mvn -Dmaven.repo.local=C:\Users\19019-JPZ\.m2\repository clean install -f restapi -Dmaven.test.skip=true 
call mvn -Dmaven.repo.local=C:\Users\19019-JPZ\.m2\repository clean install -f restapi -Dmaven.test.skip=true 
echo.
echo.

echo cleaning static folder
del /S /f /q restapi\src\main\resources\static\
rmdir /s /q restapi\src\main\resources\static\
echo.
echo.

echo --------------------------------------
echo ----------react-rest builder----------
echo --------------------------------------