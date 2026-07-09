@echo off

mvn clean package || exit /b 1

move /Y target\praty-0.1.0.jar target\praty.jar || exit /b 1

copy /Y target\praty.jar C:\praty\ || exit /b 1

echo Build completed successfully!