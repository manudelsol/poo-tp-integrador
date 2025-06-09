@echo off

echo === Compilando aplicacion Java de Gestion de Eventos ===

REM Crear directorio de clases compiladas
if not exist build mkdir build

REM Definir classpath con JCalendar (usar ; como separador en Windows)
set CLASSPATH=build;lib\jcalendar-1.4.jar

REM Compilar todas las clases Java desde src con estructura de paquetes
javac -d build -cp "%CLASSPATH%" src\*\*.java

REM Verificar si la compilacion fue exitosa
if %errorlevel% equ 0 (
    echo Compilacion exitosa!
    echo.
    echo Para ejecutar la aplicacion:
    echo java -cp "%CLASSPATH%" main.Main
    echo.
    echo Ejecutando aplicacion...
    java -cp "%CLASSPATH%" main.Main
) else (
    echo Error en la compilacion. Revise los errores de sintaxis.
    pause
    exit /b 1
)

pause 