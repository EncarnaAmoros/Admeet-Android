package com.taesua.admeet.admeet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.ad_meet.conference.Conference;
import com.appspot.ad_meet.conference.model.Comment;
import com.appspot.ad_meet.conference.model.CommentCollection;
import com.appspot.ad_meet.conference.model.CommentQueryForm;
import com.appspot.ad_meet.conference.model.ConferenceCollection;
import com.appspot.ad_meet.conference.model.WrappedBoolean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Evento extends ActionBarActivity {

    private List<com.appspot.ad_meet.conference.model.Conference> conferencias_atendidas = new ArrayList<com.appspot.ad_meet.conference.model.Conference>();
    private String websafeKey;
    private boolean asiste=false;

    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;
    private TextView t6;
    private TextView t7;
    private TextView t8;
    private TextView t9;
    private TextView t10;
    private TextView t11;
    private DrawerLayout drawerLayout = null;
    private ListView listView;
    private ExpandableHeightListView listViewComentarios;

    private List<com.appspot.ad_meet.conference.model.Comment> listacomments = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        listView = (ListView) findViewById(R.id.list_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final String[]  opciones = { "Eventos", "Filtros", "Publicar", "Perfil" };

        MenuAdapter adapter = new MenuAdapter(Evento.this, opciones);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = null;

                if(opciones[arg2].equals("Eventos"))
                    intent = new Intent(Evento.this,MainActivity.class);
                else if(opciones[arg2].equals("Filtros"))
                    intent = new Intent(Evento.this,Filtros.class);
                else if(opciones[arg2].equals("Publicar"))
                    intent = new Intent(Evento.this,CrearEvento.class);
                else
                    intent = new Intent(Evento.this,EditarPerfil.class);


                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

                drawerLayout.closeDrawers();
            }
        });

        //LIST VIEW COMENTARIOS
        listViewComentarios = (ExpandableHeightListView) findViewById(R.id.listViewComentarios);


        // Mostramos el botón en la barra de la aplicación
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu48);




        t1 = (TextView)findViewById(R.id.textviewnombre);
        t1.setText(this.getIntent().getExtras().getString("name"));
        t1.setVisibility(View.INVISIBLE);

        t2 = (TextView)findViewById(R.id.textviewlugar);
        t2.setText(this.getIntent().getExtras().getString("city"));
        t2.setVisibility(View.INVISIBLE);

        t3 = (TextView)findViewById(R.id.textviewdescripcion);
        t3.setText(this.getIntent().getExtras().getString("description"));
        t3.setVisibility(View.INVISIBLE);

        t4 = (TextView)findViewById(R.id.textviewfecha);
        t4.setText(this.getIntent().getExtras().getString("fecha"));
        t4.setVisibility(View.INVISIBLE);

        t5 = (TextView)findViewById(R.id.textViewNPersonas);
        t5.setText(this.getIntent().getExtras().getString("asientos"));
        t5.setVisibility(View.INVISIBLE);

        t6 = (TextView)findViewById(R.id.textViewcategoria);
        t6.setText(this.getIntent().getExtras().getString("categorias"));
        t6.setVisibility(View.INVISIBLE);

        t7 = (TextView)findViewById(R.id.textview2);
        t7.setVisibility(View.INVISIBLE);

        t8 = (TextView)findViewById(R.id.textViewTituloCategoria);
        t8.setVisibility(View.INVISIBLE);

        t9 = (TextView)findViewById(R.id.textViewTituloFecha);
        t9.setVisibility(View.INVISIBLE);

        t10 = (TextView)findViewById(R.id.textViewTituloLugar);
        t10.setVisibility(View.INVISIBLE);

        t11 = (TextView)findViewById(R.id.textViewTituloNPersonas);
        t11.setVisibility(View.INVISIBLE);

        TextView t12 = (TextView)findViewById(R.id.textViewCreador);
        if(this.getIntent().getExtras().getString("creador").contains("@"))
            t12.setText(this.getIntent().getExtras().getString("creador").split("T")[0]);
        else
            t12.setText(this.getIntent().getExtras().getString("creador"));
        t12.setVisibility(View.INVISIBLE);

        websafeKey = this.getIntent().getExtras().getString("websafeKey");
        System.out.println("SU KEY ES " + websafeKey);

        GetEventosAsisto get = new GetEventosAsisto();
        get.execute();

        GetComentarios getComents = new GetComentarios();
        getComents.execute();


        Button b = (Button) findViewById(R.id.buttonApuntarse);
        b.setVisibility(View.INVISIBLE);

        //para quitar el focus automatico al abrir la actividad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void onBackPressed() {
        Intent intent = new Intent(Evento.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    private class GetComentarios extends AsyncTask<Void, Void, CommentCollection>
    {
        public GetComentarios() { }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected CommentCollection doInBackground(Void ... unused)
        {
            CommentCollection messages = null;
            try
            {
                CommentQueryForm form = new CommentQueryForm();
                form.setSafeKey(websafeKey);
                Conference.GetComments create = ConferenceUtils.getComentarios(form);
                messages = create.execute();
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

            return messages;
        }

        @Override
        protected void onPostExecute(CommentCollection result)
        {
            int tam=0;

            if(result!=null)
                listacomments = result.getItems();
            if(listacomments!=null)
                tam = listacomments.size();

            System.out.println("EL TAM ES " + tam);
            rellenaListViewComentarios(listacomments, tam);

        }
    }

    public void rellenaListViewComentarios(List<com.appspot.ad_meet.conference.model.Comment> listacomentarios,int tam)
    {

        //String textos[] = new String[tam];
        ArrayList<String> textos = new ArrayList<>();

        for(int i=0;i<tam;i++) {
            textos.add(listacomentarios.get(i).getComment());
        }

        //GUARRADAS INCOMING

        listViewComentarios.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,textos));
        listViewComentarios.setExpanded(true);

        /*
        ComentariosAdapter adapter;
        // Inicializamos el adapter.
        adapter = new ComentariosAdapter(Evento.this,textos);
        // Asignamos el Adapter al ListView, en este punto hacemos que el
        // ListView muestre los datos que queremos.
        listViewComentarios.setAdapter(adapter);
        */
    }
    private class Apuntarse extends AsyncTask<Void, Void,WrappedBoolean>
    {

        public Apuntarse() { }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected WrappedBoolean doInBackground(Void ... unused)
        {
             WrappedBoolean registrado = null;
            try
            {
                Conference.RegisterForConference apunt = ConferenceUtils.registrarseEvento(websafeKey);
                registrado = apunt.execute();

            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

            return registrado;
        }

        @Override
        protected void onPostExecute(WrappedBoolean result)
        {
            System.out.println("EL BOOLEANO DE APUNTARSE ES " + result.getResult());
        }
    }

    private class Desapuntarse extends AsyncTask<Void, Void,WrappedBoolean>
    {

        public Desapuntarse() { }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected WrappedBoolean doInBackground(Void ... unused)
        {
            WrappedBoolean desapuntado = null;
            try
            {
                Conference.UnregisterFromConference desap = ConferenceUtils.desregistrarseEvento(websafeKey);
                desapuntado = desap.execute();

            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

            return desapuntado;
        }

        @Override
        protected void onPostExecute(WrappedBoolean result)
        {
            System.out.println("EL BOOLEANO DE DESAPUNTARSE ES " + result.getResult());
        }
    }

    private class GetEventosAsisto extends AsyncTask<Void, Void, ConferenceCollection>
    {
        private final ProgressDialog dialog = new ProgressDialog(Evento.this);
        public GetEventosAsisto() { }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            this.dialog.setMessage("Cargando...");
            this.dialog.show();
         }
        @Override
        protected ConferenceCollection doInBackground(Void ... unused)
        {
            ConferenceCollection messages = null;
            try
            {
                Conference.GetConferencesToAttend create = ConferenceUtils.getEventosAsisto();
                messages = create.execute();

            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

            return messages;
        }

        @Override
        protected void onPostExecute(ConferenceCollection result)
        {
            Button x = (Button) findViewById(R.id.buttonApuntarse);
            if(result==null || result.getItems()==null) {

                //APUNTARSE
                x.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Apuntarse apuntarse = new Apuntarse();
                        apuntarse.execute();

                        Intent intent = new Intent(Evento.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            else {
                conferencias_atendidas = result.getItems();
                for(com.appspot.ad_meet.conference.model.Conference c:conferencias_atendidas)
                {
                    System.out.println("LA C ES " + c.getWebsafeKey());
                    if(c.getWebsafeKey().equals(websafeKey)) //ya atiende a esta conferencia, puede desapuntarse
                    {
                        System.out.println("YA ATIENDE A ESTA CONFERENCIA, PODRA DESAPUNTARSE");
                        asiste=true;
                        break;
                    }

                }

                if(asiste) {
                    System.out.println("HA ENTRADO EN DESAPUNTARSE");
                    x.setText("Desapuntarse -");
                    x.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Desapuntarse desapuntarse = new Desapuntarse();
                            desapuntarse.execute();
                            Intent intent = new Intent(Evento.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                else {
                    //APUNTARSE
                    x.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Apuntarse apuntarse = new Apuntarse();
                            apuntarse.execute();

                            Intent intent = new Intent(Evento.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }

            t1.setVisibility(View.VISIBLE);
            t2.setVisibility(View.VISIBLE);
            t3.setVisibility(View.VISIBLE);
            t4.setVisibility(View.VISIBLE);
            t5.setVisibility(View.VISIBLE);
            t6.setVisibility(View.VISIBLE);
            t7.setVisibility(View.VISIBLE);
            t8.setVisibility(View.VISIBLE);
            t9.setVisibility(View.VISIBLE);
            t10.setVisibility(View.VISIBLE);
            t11.setVisibility(View.VISIBLE);
            x.setVisibility(View.VISIBLE);
            dialog.dismiss();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eventos, menu);
        return true;
    }

    // Mostramos el botón en la barra de la aplicación
    //getActionBar().setDisplayHomeAsUpEnabled(true);
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(listView)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(listView);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}