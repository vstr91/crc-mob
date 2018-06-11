package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;
import br.com.vostre.circular.databinding.ActivityHorariosItinerarioBinding;

public class HorariosItinerarioActivity extends BaseActivity {

    ActivityHorariosItinerarioBinding binding;
    HorariosItinerarioViewModel viewModel;

    RecyclerView listHorarios;
    List<HorarioItinerarioNome> horarios;
    HorarioItinerarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_horarios_itinerario);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(HorariosItinerarioViewModel.class);

        Itinerario itinerario = new Itinerario();
        itinerario.setId(getIntent().getStringExtra("itinerario"));
        viewModel.setItinerario(itinerario.getId());


        viewModel.horarios.observe(this, horariosObserver);
        viewModel.itinerario.observe(this, itinerarioObserver);

        binding.setView(this);
        binding.setViewModel(viewModel);
        setTitle("Horários Itinerário");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listHorarios = binding.listHorarios;

        adapter = new HorarioItinerarioAdapter(horarios, this, viewModel);

        listHorarios.setAdapter(adapter);

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

}
