package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDashboardBinding;
import br.com.vostre.circular.databinding.ActivitySobreBinding;
import br.com.vostre.circular.viewModel.DashboardViewModel;

public class DashboardActivity extends BaseActivity {

    ActivityDashboardBinding binding;
    DashboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Dashboard");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        viewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        viewModel.acessos.observe(this, acessosObserver);
        viewModel.itinerarios.observe(this, itinerariosObserver);
        viewModel.paradas.observe(this, paradasObserver);
        viewModel.cidades.observe(this, cidadesObserver);
        viewModel.horarios.observe(this, horariosObserver);

    }

    public void onClickCardAcesso(View v){
        Toast.makeText(getApplicationContext(), "Acesso", Toast.LENGTH_SHORT).show();
    }

    public void onClickCardItinerario(View v){

    }

    public void onClickCardParada(View v){

    }

    public void onClickCardCidade(View v){

    }

    public void onClickCardHorario(View v){

    }

    Observer<Integer> acessosObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer acessos) {
            binding.textViewAcessos.setText(String.valueOf(acessos));
        }
    };

    Observer<Integer> itinerariosObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer itinerarios) {
            binding.textViewItinerarios.setText(String.valueOf(itinerarios));
        }
    };

    Observer<Integer> paradasObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer paradas) {
            binding.textViewParadas.setText(String.valueOf(paradas));
        }
    };

    Observer<Integer> cidadesObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer cidades) {
            binding.textViewCidades.setText(String.valueOf(cidades));
        }
    };

    Observer<Integer> horariosObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer horarios) {
            binding.textViewHorarios.setText(String.valueOf(horarios));
        }
    };

}
