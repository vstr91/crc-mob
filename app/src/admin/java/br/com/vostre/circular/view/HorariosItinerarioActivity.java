package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.databinding.ActivityHorariosItinerarioBinding;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;

public class HorariosItinerarioActivity extends BaseActivity {

    ActivityHorariosItinerarioBinding binding;
    HorariosItinerarioViewModel viewModel;

    RecyclerView listHorarios;
    RecyclerView listHorariosCadastrados;
    List<HorarioItinerarioNome> horarios;
    HorarioItinerarioAdapter adapter;
    HorarioItinerarioAdapter adapterCadastrados;

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_horarios_itinerario);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(HorariosItinerarioViewModel.class);

        Itinerario itinerario = new Itinerario();
        itinerario.setId(getIntent().getStringExtra("itinerario"));
        viewModel.setItinerario(itinerario.getId());


        viewModel.horarios.observe(this, horariosObserver);
        viewModel.horariosCadastrados.observe(this, horariosCadastradosObserver);
        viewModel.itinerario.observe(this, itinerarioObserver);

        binding.setView(this);
        binding.setViewModel(viewModel);
        setTitle("Horários Itinerário");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        tabHost = binding.tabs;
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Todos");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Todos");
        tabHost.addTab(spec);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Cadastrados");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Cadastrados");
        tabHost.addTab(spec2);

        listHorarios = binding.listHorarios;
        listHorariosCadastrados = binding.listHorariosCadastrados;

        adapter = new HorarioItinerarioAdapter(horarios, this, viewModel, true);
        adapterCadastrados = new HorarioItinerarioAdapter(viewModel.horarios.getValue(), this, viewModel, false);

        listHorarios.setAdapter(adapter);
        listHorariosCadastrados.setAdapter(adapterCadastrados);

    }

    public void onClickBtnProcessarImagem(View v){
        Intent i = new Intent(getApplicationContext(), HorarioPorImagemActivity.class);
        i.putExtra("itinerario", getIntent().getStringExtra("itinerario"));
        startActivity(i);
    }

    public void onClickBtnInvalidar(View v){
        viewModel.invalidarHorarios(getIntent().getStringExtra("itinerario"));
        viewModel.retorno.observe(this, retornoObserver);
    }

    Observer<List<HorarioItinerarioNome>> horariosObserver = new Observer<List<HorarioItinerarioNome>>() {
        @Override
        public void onChanged(List<HorarioItinerarioNome> horarios) {
            adapter.horarios = horarios;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<List<HorarioItinerarioNome>> horariosCadastradosObserver = new Observer<List<HorarioItinerarioNome>>() {
        @Override
        public void onChanged(List<HorarioItinerarioNome> horarios) {
            adapterCadastrados.horarios = horarios;
            adapterCadastrados.notifyDataSetChanged();
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

            if(retorno == 2){
                Toast.makeText(getApplicationContext(), "Horários invalidados!", Toast.LENGTH_SHORT).show();
            } else if(retorno == -1){
                Toast.makeText(getApplicationContext(),
                        "Erro ao invalidar horários. Por favor tente novamente.",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

}
