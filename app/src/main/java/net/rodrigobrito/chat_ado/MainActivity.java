package net.rodrigobrito.chat_ado;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.samsao.messageui.views.MessagesWindow;

import net.rodrigobrito.chat_ado.model.Mensagem;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private String nome = "Anonimo";

    private MessagesWindow messagesWindow;
    private FirebaseDatabase database;
    private DatabaseReference mensagens;
    private EditText editTextNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context mContext = this;

        //Firebase
        database = FirebaseDatabase.getInstance();
        mensagens = database.getReference("mensagens");

        //Chat de interface
        messagesWindow = (MessagesWindow) findViewById(R.id.chat);

        Button sendButton = (Button) findViewById(R.id.message_box_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextTexto = (EditText) findViewById(R.id.message_box_text_field);
                Mensagem msg = new Mensagem(MainActivity.this.nome, editTextTexto.getText().toString());
                enviarMensagem(msg);
            }
        });

        //Atualiza nome do usuário
        editTextNome = (EditText) findViewById(R.id.nome);
        this.nome = editTextNome.getText().toString();

        //Evento para atualizar o username
        editTextNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MainActivity.this.nome = editTextNome.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                MainActivity.this.nome = editTextNome.getText().toString();
            }
        });

        //Eventos do Firebase
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Mensagem msg = dataSnapshot.getValue(Mensagem.class);
                exibirMensagem(msg);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                //Quando o dado é alterado
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                // Registro removido
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                // Posição modificada
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                // Operação cancelada (Falha)
            }
        };
        mensagens.addChildEventListener(childEventListener);
    }

    /**
     * Salva a mensagem no banco de dados e exibe no chat
     * @param msg mensagem a ser enviada
     */
    public void enviarMensagem(final Mensagem msg){
        String key = mensagens.push().getKey();
        mensagens.child(key).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                limparTextoEnvio();
            }
        });
    }

    /**
     * Exibe no chat nova mensagem recebida
     * @param msg mensagem a ser exibida
     */
    public void exibirMensagem(Mensagem msg){
        if(msg.nome == this.nome){
            messagesWindow.sendMessage(msg.nome+" ("+msg.date+")\n"+msg.mensagem);
        }else {
            messagesWindow.receiveMessage(msg.nome+" ("+msg.date+")\n"+msg.mensagem);
        }
    }

    public void limparTextoEnvio(){
        EditText editTextTexto = (EditText) findViewById(R.id.message_box_text_field);
        editTextTexto.setText("");
    }
}
