package br.com.vostre.circular.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityPaisesBinding;
import br.com.vostre.circular.databinding.ActivityPushBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseBairro;
import br.com.vostre.circular.utils.APIUtils;
import br.com.vostre.circular.view.adapter.PaisAdapter;
import br.com.vostre.circular.view.form.FormPais;
import br.com.vostre.circular.viewModel.PaisesViewModel;
import br.com.vostre.circular.viewModel.PushViewModel;

public class PushActivity extends BaseActivity {

    ActivityPushBinding binding;
    PushViewModel viewModel;

    final int RESULT_ITINERARIO = 178;
    final int RESULT_PARADA = 175;
    final int RESULT_POI = 172;

    String iti = "";
    String par = "";
    String poi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_push);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(PushViewModel.class);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Mensagens Push");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        binding.radioButtonMenu.setOnClickListener(listener);
        binding.radioButtonItinerarios.setOnClickListener(listener);
        binding.radioButtonParadas.setOnClickListener(listener);
        binding.radioButtonDetalhesItinerario.setOnClickListener(listener);
        binding.radioButtonDetalhesParada.setOnClickListener(listener);
        binding.radioButtonDetalhesPoi.setOnClickListener(listener);

    }

    public void onClickBtnEnviar(View v){
        String titulo = binding.editTextTitulo.getText().toString();
        String descricao = binding.editTextDescricao.getText().toString();

        if(!titulo.isEmpty() && !descricao.isEmpty()){

            int id = binding.radioGroup2.getCheckedRadioButtonId();

            switch(id){
                case R.id.radioButtonItinerarios:

                    APIUtils.criaNotificacaoPush(titulo, descricao, "itinerario",
                            "Usuarios", 1, 1, "", getApplicationContext());

                    Toast.makeText(getApplicationContext(), "Notificação de Itinerário enviada!", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.radioButtonParadas:

                    APIUtils.criaNotificacaoPush(titulo, descricao, "parada",
                            "Usuarios", 1, 1, "", getApplicationContext());

                    Toast.makeText(getApplicationContext(), "Notificação de Parada enviada!", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.radioButtonMapa:

                    APIUtils.criaNotificacaoPush(titulo, descricao, "mapa",
                            "Usuarios", 1, 1, "", getApplicationContext());

                    Toast.makeText(getApplicationContext(), "Notificação de Mapa enviada!", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.radioButtonMensagem:

                    APIUtils.criaNotificacaoPush(titulo, descricao, "mensagem",
                            "Usuarios", 1, 1, "", getApplicationContext());

                    Toast.makeText(getApplicationContext(), "Notificação de Mensagem enviada!", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.radioButtonDetalhesItinerario:

                    if(!iti.isEmpty()){
                        APIUtils.criaNotificacaoPush(titulo, descricao, "detalhe_itinerario",
                                "Usuarios", 1, 1, iti, getApplicationContext());

                        Toast.makeText(getApplicationContext(), "Notificação de Detalhe de Itinerário enviada!", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getApplicationContext(), "Um itinerário precisa ser selecionado!", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.radioButtonDetalhesParada:

                    if(!par.isEmpty()){
                        APIUtils.criaNotificacaoPush(titulo, descricao, "detalhe_parada",
                                "Usuarios", 1, 1, par, getApplicationContext());

                        Toast.makeText(getApplicationContext(), "Notificação de Detalhe de Parada enviada!", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getApplicationContext(), "Uma parada precisa ser selecionada!", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.radioButtonDetalhesPoi:

                    if(!poi.isEmpty()){
                        APIUtils.criaNotificacaoPush(titulo, descricao, "detalhe_poi",
                                "Usuarios", 1, 1, poi, getApplicationContext());

                        Toast.makeText(getApplicationContext(), "Notificação de Ponto de Interesse enviada!", Toast.LENGTH_SHORT).show();

                    } else{
                        Toast.makeText(getApplicationContext(), "Um ponto de interesse precisa ser selecionado!", Toast.LENGTH_SHORT).show();
                    }

                    break;
                default:
                    APIUtils.criaNotificacaoPush(titulo, descricao, "menu",
                            "Usuarios", 1, 1, "", getApplicationContext());

                    Toast.makeText(getApplicationContext(), "Notificação de Tela Inicial enviada!", Toast.LENGTH_SHORT).show();

                    break;
            }

        } else{
            Toast.makeText(getApplicationContext(), "Título e descrição precisam ser informados!", Toast.LENGTH_SHORT).show();
        }

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent i;

            switch(view.getId()){
                case R.id.radioButtonItinerarios:
                    break;
                case R.id.radioButtonParadas:
                    break;
                case R.id.radioButtonDetalhesItinerario:
                    i = new Intent(getApplicationContext(), ListaItinerariosActivity.class);
                    startActivityForResult(i, RESULT_ITINERARIO);
                    break;
                case R.id.radioButtonDetalhesParada:
                    i = new Intent(getApplicationContext(), ListaParadasActivity.class);
                    startActivityForResult(i, RESULT_PARADA);
                    break;
                case R.id.radioButtonDetalhesPoi:
                    i = new Intent(getApplicationContext(), ListaPontosInteresseActivity.class);
                    startActivityForResult(i, RESULT_POI);
                    break;
                default:
                    break;
            }

        }
    };

    public void onClickBtnAtualizacao(View v){
        APIUtils.forcaAtualizacaoPush(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){

            final String id;

            switch(requestCode){
                case RESULT_ITINERARIO:
                    id = data.getStringExtra("itinerario");

                    viewModel.carregarItinerario(id);
                    viewModel.itinerario.observe(this, itinerarioObserver);
                    break;
                case RESULT_PARADA:
                    id = data.getStringExtra("parada");

                    viewModel.carregarParada(id);
                    viewModel.parada.observe(this, paradaObserver);
                    break;
                case RESULT_POI:
                    id = data.getStringExtra("poi");

                    viewModel.carregarPontoInteresse(id);
                    viewModel.poi.observe(this, poiObserver);
                    break;
            }

        }

    }

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {

            if(itinerario != null){
                binding.textViewItinerario.setText(itinerario.getNomeCompleto());
                iti = itinerario.getItinerario().getId();

                par = "";
                poi = "";

                binding.textViewParada.setText("");
                binding.textViewPoi.setText("");
            }

        }
    };

    Observer<ParadaBairro> paradaObserver = new Observer<ParadaBairro>() {
        @Override
        public void onChanged(ParadaBairro parada) {

            if(parada != null){
                binding.textViewParada.setText(parada.getParada().getNome()+" ("+parada.getNomeBairroComCidade()+")");
                par = parada.getParada().getId();

                iti = "";
                poi = "";

                binding.textViewItinerario.setText("");
                binding.textViewPoi.setText("");
            }

        }
    };

    Observer<PontoInteresseBairro> poiObserver = new Observer<PontoInteresseBairro>() {
        @Override
        public void onChanged(PontoInteresseBairro poin) {

            if(poi != null){
                binding.textViewPoi.setText(poin.getPontoInteresse().getNome()+" ("+poin.getNomeBairroComCidade()+")");
                poi = poin.getPontoInteresse().getId();

                par = "";
                iti = "";

                binding.textViewParada.setText("");
                binding.textViewItinerario.setText("");
            }

        }
    };

}
