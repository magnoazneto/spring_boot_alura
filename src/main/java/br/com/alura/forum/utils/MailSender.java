package br.com.alura.forum.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailSender {

    @Value("${spring.profiles.active}")
    private String profile;

    public MailSender() {

    }

    public void enviar(){
        System.out.println("ENVIO DE EMAIL EM DEV");

        if(profile.equals("dev")) {
            this.enviarEmailPeloServidorSMTP();
        }
    }

    private void enviarEmailPeloServidorSMTP(){
        System.out.println("ENVIO DE EMAIL EM PROD");
    }
}
