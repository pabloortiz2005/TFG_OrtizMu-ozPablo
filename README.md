# ChatoChat 🤖💬

ChatoChat es una aplicación de mensajería instantánea para Android con integración de inteligencia artificial. Permite chatear entre usuarios en tiempo real, recibir notificaciones push y hablar con una IA mediante OpenAI.

## ✨ Funcionalidades

- 📱 Chat entre usuarios en tiempo real (Firebase Realtime Database)
- 🔔 Notificaciones push con Firebase Cloud Messaging (FCM)
- 🟢 Estado en línea de los usuarios
- 🤖 Chat con IA integrada usando `gpt-3.5-turbo` de OpenAI (Suele negar el permiso por no ser de un plan de pago)
- 💬 Diseño moderno con RecyclerView y actividades separadas

## 🛠️ Tecnologías utilizadas

- Kotlin
- Firebase Realtime Database
- Firebase Cloud Messaging (FCM)
- OpenAI API (Chat Completions)
- OkHttp + Retrofit

## 🚀 Vías Futuras

Este proyecto tiene múltiples posibilidades de expansión para mejorar la experiencia del usuario y aprovechar tecnologías más avanzadas pero para ello hacen falta fondos:

### 🖼 Subida de Imágenes
Permitir que los usuarios puedan enviar y recibir imágenes dentro del chat. Las imágenes se almacenarían en **Firebase Storage** y se mostrarían en el chat con previsualización.

### 🗺️ Integración con API de Google Maps
Permitir compartir ubicaciones en tiempo real o puntos específicos, integrando la **API de Google Maps** para mostrar mapas interactivos directamente en el chat.

### 🎙 Envío y reproducción de audios
Habilitar la grabación de mensajes de voz directamente desde la app y permitir su reproducción en el chat. Los archivos de audio también se guardarían en **Firebase Storage**.

### 🤖 Implementación de IA (Asistente inteligente)
Implementar el sistema de **IA** personalizado que pueda responder preguntas frecuentes, asistir al usuario dentro del chat, o incluso actuar como moderador automático en función de ciertos comportamientos.
