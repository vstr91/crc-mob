package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.BuildConfig;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivitySobreBinding;
import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.viewModel.SobreViewModel;

public class SobreActivity extends BaseActivity {

    ActivitySobreBinding binding;
    SobreViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sobre);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Sobre");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        viewModel = ViewModelProviders.of(this).get(SobreViewModel.class);
        viewModel.parametros.observe(this, parametrosObserver);
        binding.setVersao(BuildConfig.VERSION_NAME);
        binding.setDataBuild(DateTimeFormat.forPattern("MMMM/YY").print(BuildConfig.TIMESTAMP));

    }

    Observer<ParametroInterno> parametrosObserver = new Observer<ParametroInterno>() {
        @Override
        public void onChanged(ParametroInterno parametros) {
            binding.setParametros(parametros);
        }
    };

    @BindingAdapter("app:dataAcesso")
    public static void setDataAcesso(TextView textView, DateTime l){

        if(l != null){
            textView.setText(DateTimeFormat.forPattern("dd/MM/YYYY")
                    .print(l));
        }

    }

    @BindingAdapter("app:dataBaseDados")
    public static void setDataBaseDados(TextView textView, DateTime l){

        if(l != null){
            textView.setText(DateTimeFormat.forPattern("MMMM/YY")
                    .print(l));
        }

    }

}
