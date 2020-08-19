package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean retorno = super.onCreateOptionsMenu(menu);

        if(menu != null){
            menu.getItem(0).setVisible(false);
        }

        return retorno;
    }

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

    public void onClickBtnSobre(View v){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Termos de Uso e Informações")
                .setMessage(R.string.text_termos).setNeutralButton("Entendi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

                dialog.show();
    }

}
