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
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parada;
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

                    // PAISES

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

                    // ESTADOS

                    if(estados.length() > 0){

                        int total = estados.length();
                        List<Estado> lstEstados = new ArrayList<>();

                        for(int i = 0; i < total; i++){
                            Estado estado = new Estado();
                            JSONObject obj = estados.getJSONObject(i);

                            estado.setId(obj.getString("id"));
                            estado.setNome(obj.getString("nome"));
                            estado.setSigla(obj.getString("sigla"));
                            estado.setPais(obj.getString("pais"));
                            estado.setSlug(obj.getString("slug"));
                            estado.setEnviado(obj.getBoolean("enviado"));
                            estado.setAtivo(obj.getBoolean("ativo"));
                            estado.setDataCadastro(dtf.parseDateTime(obj.getString("dataCadastro")));
                            estado.setUltimaAlteracao(dtf.parseDateTime(obj.getString("ultimaAlteracao")));

                            if(!obj.optString("programadoPara", "").isEmpty()){
                                estado.setProgramadoPara(dtf.parseDateTime(obj.getString("programadoPara")));
                            }

                            lstEstados.add(estado);

                        }

                        viewModel.add(lstEstados, "estado");

                    }

                    // CIDADES

                    if(cidades.length() > 0){

                        int total = cidades.length();
                        List<Cidade> lstCidades = new ArrayList<>();

                        for(int i = 0; i < total; i++){
                            Cidade cidade = new Cidade();
                            JSONObject obj = cidades.getJSONObject(i);

                            cidade.setId(obj.getString("id"));
                            cidade.setNome(obj.getString("nome"));
                            cidade.setBrasao(obj.getString("brasao"));
                            cidade.setEstado(obj.getString("estado"));
                            cidade.setSlug(obj.getString("slug"));
                            cidade.setEnviado(obj.getBoolean("enviado"));
                            cidade.setAtivo(obj.getBoolean("ativo"));
                            cidade.setDataCadastro(dtf.parseDateTime(obj.getString("dataCadastro")));
                            cidade.setUltimaAlteracao(dtf.parseDateTime(obj.getString("ultimaAlteracao")));

                            if(!obj.optString("programadoPara", "").isEmpty()){
                                cidade.setProgramadoPara(dtf.parseDateTime(obj.getString("programadoPara")));
                            }

                            lstCidades.add(cidade);

                        }

                        viewModel.add(lstCidades, "cidade");

                    }

                    // BAIRROS

                    if(bairros.length() > 0){

                        int total = bairros.length();
                        List<Bairro> lstBairros = new ArrayList<>();

                        for(int i = 0; i < total; i++){
                            Bairro bairro = new Bairro();
                            JSONObject obj = bairros.getJSONObject(i);

                            bairro.setId(obj.getString("id"));
                            bairro.setNome(obj.getString("nome"));
                            bairro.setCidade(obj.getString("cidade"));
                            bairro.setSlug(obj.getString("slug"));
                            bairro.setEnviado(obj.getBoolean("enviado"));
                            bairro.setAtivo(obj.getBoolean("ativo"));
                            bairro.setDataCadastro(dtf.parseDateTime(obj.getString("dataCadastro")));
                            bairro.setUltimaAlteracao(dtf.parseDateTime(obj.getString("ultimaAlteracao")));

                            if(!obj.optString("programadoPara", "").isEmpty()){
                                bairro.setProgramadoPara(dtf.parseDateTime(obj.getString("programadoPara")));
                            }

                            lstBairros.add(bairro);

                        }

                        viewModel.add(lstBairros, "bairro");

                    }

                    // PARADAS

                    if(paradas.length() > 0){

                        int total = paradas.length();
                        List<Parada> lstParadas = new ArrayList<>();

                        for(int i = 0; i < total; i++){
                            Parada parada = new Parada();
                            JSONObject obj = paradas.getJSONObject(i);

                            parada.setId(obj.getString("id"));
                            parada.setNome(obj.getString("nome"));
                            parada.setLatitude(obj.getDouble("latitude"));
                            parada.setLongitude(obj.getDouble("longitude"));
                            parada.setTaxaDeEmbarque(obj.optDouble("taxaDeEmbarque", 0));
                            parada.setImagem(obj.optString("imagem", null));
                            parada.setBairro(obj.getString("bairro"));
                            parada.setSlug(obj.getString("slug"));
                            parada.setEnviado(obj.getBoolean("enviado"));
                            parada.setAtivo(obj.getBoolean("ativo"));
                            parada.setDataCadastro(dtf.parseDateTime(obj.getString("dataCadastro")));
                            parada.setUltimaAlteracao(dtf.parseDateTime(obj.getString("ultimaAlteracao")));

                            if(!obj.optString("programadoPara", "").isEmpty()){
                                parada.setProgramadoPara(dtf.parseDateTime(obj.getString("programadoPara")));
                            }

                            lstParadas.add(parada);

                        }

                        viewModel.add(lstParadas, "parada");

                    }

                    // EMPRESAS

                    if(empresas.length() > 0){

                        int total = empresas.length();
                        List<Empresa> lstEmpresas = new ArrayList<>();

                        for(int i = 0; i < total; i++){
                            Empresa empresa = new Empresa();
                            JSONObject obj = empresas.getJSONObject(i);

                            empresa.setId(obj.getString("id"));
                            empresa.setNome(obj.getString("nome"));
                            empresa.setEmail(obj.optString("email", ""));
                            empresa.setTelefone(obj.optString("telefone", ""));
                            empresa.setLogo(obj.optString("logo", ""));
                            empresa.setSlug(obj.getString("slug"));
                            empresa.setEnviado(obj.getBoolean("enviado"));
                            empresa.setAtivo(obj.getBoolean("ativo"));
                            empresa.setDataCadastro(dtf.parseDateTime(obj.getString("dataCadastro")));
                            empresa.setUltimaAlteracao(dtf.parseDateTime(obj.getString("ultimaAlteracao")));

                            if(!obj.optString("programadoPara", "").isEmpty()){
                                empresa.setProgramadoPara(dtf.parseDateTime(obj.getString("programadoPara")));
                            }

                            lstEmpresas.add(empresa);

                        }

                        viewModel.add(lstEmpresas, "empresa");

                    }

                    // ITINERARIOS

                    if(itinerarios.length() > 0){

                        int total = itinerarios.length();
                        List<Itinerario> lstItinerarios = new ArrayList<>();

                        for(int i = 0; i < total; i++){
                            Itinerario itinerario = new Itinerario();
                            JSONObject obj = empresas.getJSONObject(i);

                            itinerario.setId(obj.getString("id"));
                            itinerario.setAcessivel(obj.getBoolean("acessivel"));
                            itinerario.setTarifa(obj.getDouble("tarifa"));
                            itinerario.setTempo(dtf.parseDateTime(obj.getString("tempo")));
                            itinerario.setDistancia(obj.optDouble("distancia", 0));
                            itinerario.setObservacao(obj.optString("observacao", ""));
                            itinerario.setSigla(obj.optString("sigla", ""));
                            itinerario.setEmpresa(obj.getString("empresa"));
                            itinerario.setEnviado(obj.getBoolean("enviado"));
                            itinerario.setAtivo(obj.getBoolean("ativo"));
                            itinerario.setDataCadastro(dtf.parseDateTime(obj.getString("dataCadastro")));
                            itinerario.setUltimaAlteracao(dtf.parseDateTime(obj.getString("ultimaAlteracao")));

                            if(!obj.optString("programadoPara", "").isEmpty()){
                                itinerario.setProgramadoPara(dtf.parseDateTime(obj.getString("programadoPara")));
                            }

                            lstItinerarios.add(itinerario);

                        }

                        viewModel.add(lstItinerarios, "itinerario");

                    }

                    // HORARIOS

                    if(horarios.length() > 0){

                        int total = horarios.length();
                        List<Horario> lstHorarios = new ArrayList<>();

                        for(int i = 0; i < total; i++){
                            Horario horario = new Horario();
                            JSONObject obj = horarios.getJSONObject(i);

                            horario.setId(obj.getString("id"));
                            horario.setNome(dtf.parseDateTime(obj.getString("nome")));
                            horario.setEnviado(obj.getBoolean("enviado"));
                            horario.setAtivo(obj.getBoolean("ativo"));
                            horario.setDataCadastro(dtf.parseDateTime(obj.getString("dataCadastro")));
                            horario.setUltimaAlteracao(dtf.parseDateTime(obj.getString("ultimaAlteracao")));

                            if(!obj.optString("programadoPara", "").isEmpty()){
                                horario.setProgramadoPara(dtf.parseDateTime(obj.getString("programadoPara")));
                            }

                            lstHorarios.add(horario);

                        }

                        viewModel.add(lstHorarios, "horario");

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
