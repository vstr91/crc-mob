package br.com.vostre.circular.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.util.JsonUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.utils.ToolbarUtils;
import br.com.vostre.circular.viewModel.BaseViewModel;

import static br.com.vostre.circular.utils.ToolbarUtils.PICK_FILE;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public Toolbar toolbar;
    Menu menu;

    BaseViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewModel = ViewModelProviders.of(this).get(BaseViewModel.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        /*if(iniciaModoCamera){
            getMenuInflater().inflate(R.menu.realidade_aumentada, menu);
        } else{
            getMenuInflater().inflate(R.menu.main, menu);
        }*/

        this.menu = menu;

        ToolbarUtils.preparaMenu(menu, this, this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
            /*case R.id.icon_config:
                intent = new Intent(this, Parametros.class);
                startActivity(intent);
                break;*/
            case R.id.textViewBadgeMsg:
            case R.id.msg:
            case R.id.icon_msg:
                System.out.println("AAAAAAA");
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        ToolbarUtils.onMenuItemClick(v, this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_FILE) {

            if(data != null){
                try {
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());

                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder dados = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        dados.append(line).append('\n');
                    }

                    JSONObject arrayObject = new JSONObject(dados.toString());
                    JSONArray paises = arrayObject.getJSONArray("paises");
                    JSONArray empresas = arrayObject.getJSONArray("empresas");
                    JSONArray onibus = arrayObject.getJSONArray("onibus");
                    JSONArray estados = arrayObject.getJSONArray("estados");
                    JSONArray cidades = arrayObject.getJSONArray("cidades");
                    JSONArray bairros = arrayObject.getJSONArray("bairros");
                    JSONArray paradas = arrayObject.getJSONArray("paradas");
                    JSONArray itinerarios = arrayObject.getJSONArray("itinerarios");
                    JSONArray horarios = arrayObject.getJSONArray("horarios");
                    JSONArray paradasItinerarios = arrayObject.getJSONArray("paradas_itinerarios");
                    JSONArray secoesItinerarios = arrayObject.getJSONArray("secoes_itinerarios");
                    JSONArray horariosItinerarios = arrayObject.getJSONArray("horarios_itinerarios");
                    JSONArray pontosInteresse = arrayObject.getJSONArray("pontos_interesse");
                    JSONArray mensagens = arrayObject.getJSONArray("mensagens");
                    JSONArray parametros = arrayObject.getJSONArray("parametros");
                    JSONArray usuarios = arrayObject.getJSONArray("usuarios");

                    DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");

                    if(paises.length() > 0){

                        int total = paises.length();
                        List<Pais> lstPaises = new ArrayList<>();

                        for(int i = 0; i < total; i++){
                            Pais pais = new Pais();
                            JSONObject obj = paises.getJSONObject(i);

                            pais.setId(obj.getString("id"));
                            pais.setNome(obj.getString("nome"));
                            pais.setSigla(obj.getString("sigla"));
                            pais.setSlug(obj.getString("slug"));
                            pais.setEnviado(obj.getBoolean("enviado"));
                            pais.setAtivo(obj.getBoolean("ativo"));
                            pais.setDataCadastro(dtf.parseDateTime(obj.getString("dataCadastro")));
                            pais.setUltimaAlteracao(dtf.parseDateTime(obj.getString("ultimaAlteracao")));

                            if(!obj.optString("programadoPara", "").isEmpty()){
                                pais.setProgramadoPara(dtf.parseDateTime(obj.getString("programadoPara")));
                            }

                            lstPaises.add(pais);

                        }

                        viewModel.add(lstPaises, "pais");

                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

}
