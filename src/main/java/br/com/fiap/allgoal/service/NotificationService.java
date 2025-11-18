package br.com.fiap.allgoal.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Async
    public void enviarNotificacao(String email, String mensagem) {
        try {
            Thread.sleep(2000);
            System.out.println("--------------- NOTIFICAÇÃO ASSÍNCRONA ---------------");
            System.out.println("ENVIANDO EMAIL PARA: " + email);
            System.out.println("MENSAGEM: " + mensagem);
            System.out.println("------------------------------------------------------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}