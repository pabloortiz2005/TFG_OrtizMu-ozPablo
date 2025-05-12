package com.pablo.tfg_chatochat.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Nuevo token generado: " + token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage mensajeRemoto) {
        super.onMessageReceived(mensajeRemoto);

        Log.d("FCM", "Mensaje recibido de: " + mensajeRemoto.getFrom());

        if (mensajeRemoto.getNotification() != null) {
            Log.d("FCM", "Notificación: " + mensajeRemoto.getNotification().getTitle() + " - " + mensajeRemoto.getNotification().getBody());
        }

        if (!mensajeRemoto.getData().isEmpty()) {
            Log.d("FCM", "Datos: " + mensajeRemoto.getData());
        }
    }
}
