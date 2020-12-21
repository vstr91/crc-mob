package br.com.vostre.circular.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityHorariosItinerarioBinding;
import br.com.vostre.circular.databinding.ActivityHorariosItinerarioTextoBinding;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.view.adapter.HorarioItinerarioTextoAdapter;
import br.com.vostre.circular.viewModel.HorariosItinerarioTextoViewModel;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;

public class HorariosItinerarioTextoActivity extends BaseActivity {

    ActivityHorariosItinerarioTextoBinding binding;
    HorariosItinerarioTextoViewModel viewModel;

    RecyclerView listHorarios;
    List<HorarioItinerarioNome> horarios;
    HorarioItinerarioTextoAdapter adapter;

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_horarios_itinerario_texto);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(HorariosItinerarioTextoViewModel.class);

        Itinerario itinerario = new Itinerario();
        itinerario.setId(getIntent().getStringExtra("itinerario"));
        viewModel.setItinerario(itinerario.getId());


        viewModel.horarios.observe(this, horariosObserver);
        viewModel.itinerario.observe(this, itinerarioObserver);
        viewModel.retorno.observe(this, retornoObserver);

        binding.setView(this);
        binding.setViewModel(viewModel);
        setTitle("Horários Itinerário - Texto");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        tabHost = binding.tabs;
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Cadastrar Horários");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Cadastrar Horários");
        tabHost.addTab(spec);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Cadastrados");
        spec2.setContent(R.id.tab1);
        spec2.setIndicator("Cadastrados");
        tabHost.addTab(spec2);

        listHorarios = binding.listHorarios;

        adapter = new HorarioItinerarioTextoAdapter(horarios, this, viewModel, true);

        listHorarios.setAdapter(adapter);

    }

    public void onClickBtnProcessarImagem(View v){
        Intent i = new Intent(getApplicationContext(), HorarioPorImagemActivity.class);
        i.putExtra("itinerario", getIntent().getStringExtra("itinerario"));
        startActivity(i);
    }

    public void onClickBtnAnalisar(View v){
        String[] segSex, sab, dom;

        segSex = binding.editTextSegSex.getText().toString().split(" ");
        sab = binding.editTextSab.getText().toString().split(" ");
        dom = binding.editTextDom.getText().toString().split(" ");

        //processando dados
        viewModel.processaHorarios(segSex, sab, dom, viewModel.itinerario.getValue().getItinerario().getId());

    }

    Observer<List<HorarioItinerarioNome>> horariosObserver = new Observer<List<HorarioItinerarioNome>>() {
        @Override
        public void onChanged(List<HorarioItinerarioNome> horarios) {
            adapter.horarios = horarios;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {
//            Toast.makeText(getApplicationContext(), viewModel.itinerario.getValue().getNomeBairroPartida()+" | AAAAAAAAAAAAA >> "
//                    +binding.textViewBairroPartida.getText(), Toast.LENGTH_SHORT).show();
            viewModel.iti.set(itinerario);
        }
    };

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getApplicationContext(), "Horários cadastrados!", Toast.LENGTH_SHORT).show();

            } else if(retorno == 2){
                Toast.makeText(getApplicationContext(), "Horários invalidados!", Toast.LENGTH_SHORT).show();
            } else if(retorno == -1){
                Toast.makeText(getApplicationContext(),
                        "Erro ao invalidar horários. Por favor tente novamente.",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

}
