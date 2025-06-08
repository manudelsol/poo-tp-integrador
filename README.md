# 📅 Sistema de Gestión de Eventos

Aplicación Java con interfaz gráfica Swing para gestionar eventos y asistentes.

## 🚀 Características

- **Gestión de Eventos**: Crear, editar, eliminar y listar eventos
- **Gestión de Asistentes**: Agregar y eliminar participantes de eventos
- **Calendario Visual**: Integración con JCalendar para selección de fechas
- **Filtros**: Ver eventos futuros, pasados o todos
- **Persistencia**: Almacenamiento en archivos CSV
- **Validación**: Formularios con validación en tiempo real

## 📋 Requisitos

- **Java 11 o superior**
- **JCalendar 1.4** (incluido en `lib/jcalendar-1.4.jar`)

## 🛠️ Compilación y Ejecución

### Opción 1: Script Automático
```bash
./compilar.sh
```

## 📁 Estructura del Proyecto

```
tp-integrador/
├── src/
│   ├── main/
│   │   └── Main.java                  # Punto de entrada
│   ├── modelo/
│   │   ├── Evento.java               # Clase de evento
│   │   └── Asistente.java            # Clase de asistente
│   ├── servicio/
│   │   └── GestorEventos.java        # Lógica de negocio
│   ├── persistencia/
│   │   └── PersistenciaArchivos.java # Manejo de archivos CSV
│   ├── excepciones/
│   │   ├── EventoException.java      # Excepción de eventos
│   │   └── PersistenciaException.java # Excepción de persistencia
│   └── gui/
│       ├── VentanaPrincipal.java     # Ventana principal
│       ├── DialogoEvento.java        # Formulario de eventos
│       └── DialogoDetallesEvento.java # Gestión de asistentes
├── lib/
│   └── jcalendar-1.4.jar            # Biblioteca de calendario
├── build/                            # Clases compiladas
├── eventos.csv                       # Datos de eventos
├── asistentes.csv                    # Datos de asistentes
├── compilar.sh                       # Script de compilación
└── README.md                         # Esta documentación
```

## 🎯 Funcionalidades

### 📅 Gestión de Eventos
- **Crear**: Formulario con validación de campos obligatorios
- **Editar**: Modificar eventos existentes
- **Eliminar**: Confirmación antes de eliminar
- **Filtrar**: Vista por eventos futuros, pasados o todos

### 👥 Gestión de Asistentes
- **Agregar**: Formulario con ID, nombre y email
- **Listar**: Tabla con todos los asistentes del evento
- **Eliminar**: Remover asistentes de eventos específicos

### 🗓️ Calendario Visual (JCalendar)
- **Interfaz intuitiva**: Calendario desplegable
- **Formato**: dd/MM/yyyy
- **Validación**: Fechas mínimas para eventos nuevos
- **Localización**: Soporte para español

## 💾 Persistencia de Datos

### Formato CSV
- **eventos.csv**: ID, nombre, fecha, ubicación, descripción
- **asistentes.csv**: ID, nombre, email, evento_id

### Ventajas del formato CSV
- Compatible con Excel, LibreOffice, Google Sheets
- Fácil edición manual si es necesario
- Estándar de la industria para intercambio de datos
- Legible por humanos

## 🔧 Dependencias Externas

### JCalendar 1.4
- **Propósito**: Componente visual de calendario
- **Ubicación**: `lib/jcalendar-1.4.jar`
- **Razón de uso**: Mucho más sencillo que implementar un calendario propio
- **Componentes utilizados**: `JDateChooser`

## ⚠️ Notas de Compatibilidad

### macOS
El componente JCalendar puede generar warnings en macOS debido a incompatibilidades menores con el Look & Feel nativo. La funcionalidad no se ve afectada.

## 🎨 Interfaz de Usuario

### Características del GUI
- **Look & Feel**: Nativo del sistema operativo
- **Validación en tiempo real**: Los botones se habilitan/deshabilitan según validación
- **Mensajes informativos**: Diálogos para confirmaciones y errores
- **Navegación intuitiva**: Tablas con selección y botones contextuales

## 📊 Arquitectura

### Layers de la aplicación
1. **Presentación**: `gui.*` - Interfaces de usuario
2. **Negocio**: `servicio.*` - Lógica de aplicación
3. **Modelo**: `modelo.*` - Entidades de dominio
4. **Persistencia**: `persistencia.*` - Acceso a datos
5. **Excepciones**: `excepciones.*` - Manejo de errores


## ☕️ Proyecto desarrollado a base de cafeína y pocas horas de sueño para Programación Orientada a Objetos.



### Tecnologías Utilizadas
- **Java 11+**: Lenguaje principal
- **Swing**: Framework de GUI
- **JCalendar**: Componente de calendario
- **CSV**: Formato de persistencia
- **Maven Central**: Repositorio de dependencias 