package net.rodrigobrito.chat_ado.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rodrigo on 27/11/16.
 */

@IgnoreExtraProperties
public class Mensagem {
    public String nome;
    public String mensagem;
    public String date;

    public Mensagem() {}

    public Mensagem(String nome, String mensagem) {
        this.nome = nome;
        this.mensagem = mensagem;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        this.date = sdf.format(new Date());
    }

    public Mensagem(String nome, String mensagem, String date) {
        this.nome = nome;
        this.mensagem = mensagem;
        this.date = date;
    }
}