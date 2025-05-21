package com.pablo.tfg_chatochat.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pablo.tfg_chatochat.ChatActivity;
import com.pablo.tfg_chatochat.R;

public class MessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "chat_mensajes";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM", "Nuevo token generado: " + token);

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid != null) {
            FirebaseDatabase.getInstance().getReference("Usuarios")
                    .child(uid)
                    .child("fcmToken")
                    .setValue(token);
        }
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage mensajeRemoto) {
        super.onMessageReceived(mensajeRemoto);

        Log.d("FCM", "Mensaje recibido de: " + mensajeRemoto.getFrom());

        String titulo = "Nuevo mensaje";
        String cuerpo = "";
        String uidEmisor = null;

        if (mensajeRemoto.getNotification() != null) {
            titulo = mensajeRemoto.getNotification().getTitle();
            cuerpo = mensajeRemoto.getNotification().getBody();
        }

        if (!mensajeRemoto.getData().isEmpty()) {
            Log.d("FCM", "Datos: " + mensajeRemoto.getData());
            cuerpo = mensajeRemoto.getData().get("mensaje");
            uidEmisor = mensajeRemoto.getData().get("uidEmisor");
        }

        if (uidEmisor != null) {
            mostrarNotificacion(titulo, cuerpo, uidEmisor);
        }
    }

    private void mostrarNotificacion(String titulo, String mensaje, String uidEmisor) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones de Chat",
                    NotificationManager.IMPORTANCE_HIGH
            );
            canal.setDescription("Canal para mensajes nuevos del chat");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(canal);
            }
        }

        // Crear Intent para abrir el ChatActivity con el uid del emisor
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("uidReceptor", uidEmisor);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_info)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
