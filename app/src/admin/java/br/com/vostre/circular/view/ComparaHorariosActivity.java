package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TabHost;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityComparaHorariosBinding;
import br.com.vostre.circular.databinding.ActivityHorariosItinerarioBinding;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.view.adapter.HorarioItinerarioReduzidoAdapter;
import br.com.vostre.circular.viewModel.ComparaHorariosViewModel;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;

public class ComparaHorariosActivity extends BaseActivity {

    ActivityComparaHorariosBinding binding;
    ComparaHorariosViewModel viewModel;

    RecyclerView listHorariosProcessados;
    RecyclerView listHorariosAtuais;
    List<HorarioItinerarioNome> horariosProcessados;
    List<HorarioItinerarioNome> horariosAtuais;
    HorarioItinerarioReduzidoAdapter adapterProcessados;
    HorarioItinerarioReduzidoAdapter adapterAtuais;

    AppCompatActivity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compara_horarios);
        super.onCreate(savedInstanceState);

        binding.setView(this);
        binding.setViewModel(viewModel);
        setTitle("Horários Itinerário");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        viewModel = ViewModelProviders.of(this).get(ComparaHorariosViewModel.class);

        Itinerario itinerario = new Itinerario();
        itinerario.setId(getIntent().getStringExtra("itinerario"));
        viewModel.setItinerario(itinerario.getId());

        horariosProcessados = (List<HorarioItinerarioNome>) getIntent().getSerializableExtra("horariosProcessados");

        ctx = this;

        adapterAtuais = new HorarioItinerarioReduzidoAdapter(viewModel.horariosAtuais.getValue(), this, null, false, null);

        viewModel.itinerario.observe(this, itinerarioObserver);
        viewModel.horariosAtuais.observe(this, horariosObserver);

        listHorariosProcessados = binding.listHorarios;
        listHorariosAtuais = binding.listHorariosAtuais;

        listHorariosAtuais.setAdapter(adapterAtuais);

    }

    public void onClickBtnProcessarImagem(View v){
        Intent i = new Intent(getApplicationContext(), HorarioPorImagemActivity.class);
        i.putExtra("itinerario", getIntent().getStringExtra("itinerario"));
        startActivity(i);
    }

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {
//            Toast.makeText(getApplicationContext(), viewModel.itinerario.getValue().getNomeBairroPartida()+" | AAAAAAAAAAAAA >> "
//                    +binding.textViewBairroPartida.getText(), Toast.LENGTH_SHORT).show();
            binding.setItinerario(itinerario);
        }
    };

    Observer<List<HorarioItinerarioNome>> horariosObserver = new Observer<List<HorarioItinerarioNome>>() {
        @Override
        public void onChanged(List<HorarioItinerarioNome> horarios) {
            adapterAtuais.horarios = horarios;

            adapterProcessados = new HorarioItinerarioReduzidoAdapter(horariosProcessados, ctx, null, false, horarios);
            listHorariosProcessados.setAdapter(adapterProcessados);

            adapterAtuais.notifyDataSetChanged();
        }
    };

}
