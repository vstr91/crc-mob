package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
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
            textView.setText(DateTimeFormat.forPattern("dd/MM/YYYY HH:mm")
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
