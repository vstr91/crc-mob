package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityHorariosBinding;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.view.adapter.HorarioAdapter;
import br.com.vostre.circular.viewModel.HorariosViewModel;

public class HorariosActivity extends BaseActivity {

    ActivityHorariosBinding binding;
    HorariosViewModel viewModel;

    RecyclerView listHorarios;
    List<Horario> horarios;
    HorarioAdapter adapter;

    boolean carregado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_horarios);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(HorariosViewModel.class);
        viewModel.horarios.observe(this, horariosObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Horários");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listHorarios = binding.listHorarios;

        adapter = new HorarioAdapter(horarios, this);

        listHorarios.setAdapter(adapter);

    }

    public void onFabClick(View v){
        viewModel.popularHorarios();
    }

    Observer<List<Horario>> horariosObserver = new Observer<List<Horario>>() {
        @Override
        public void onChanged(List<Horario> horarios) {
            adapter.horarios = horarios;
            adapter.notifyDataSetChanged();

            if(horarios.size() == 0){
                binding.fab.setVisibility(View.VISIBLE);
            } else{
                binding.fab.setVisibility(View.GONE);
            }

        }
    };

}
