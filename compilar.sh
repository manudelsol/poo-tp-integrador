#!/bin/bash

echo "=== Compilando aplicación Java de Gestión de Eventos ==="

# Crear directorio de clases compiladas
mkdir -p build

# Definir classpath con JCalendar
CLASSPATH="build:lib/jcalendar-1.4.jar"

# Compilar todas las clases Java desde src con estructura de paquetes
javac -d build -cp "$CLASSPATH" src/*/*.java

# Verificar si la compilación fue exitosa
if [ $? -eq 0 ]; then
    echo "Compilación exitosa!"
    echo ""
    echo "Para ejecutar la aplicación:"
    echo "java -cp \"$CLASSPATH\" main.Main"
    echo ""
    echo "Ejecutando aplicación..."
    java -cp "$CLASSPATH" main.Main
else
    echo "Error en la compilación. Revise los errores de sintaxis."
    exit 1
fi 