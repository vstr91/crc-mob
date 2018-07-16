package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.util.List;

import br.com.vostre.circleview.CircleView;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityItinerariosBinding;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.adapter.CidadeAdapter;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.listener.ItemListener;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;

public class ItinerariosActivity extends BaseActivity implements SelectListener, ItemListener {

    ActivityItinerariosBinding binding;

    RecyclerView listCidades;
    CidadeAdapter adapter;

    static AppCompatActivity ctx;

    ItinerariosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_itinerarios);
        binding.setView(this);
        super.onCreate(savedInstanceState);
        setTitle("Itinerários");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        viewModel = ViewModelProviders.of(this).get(ItinerariosViewModel.class);
        viewModel.cidades.observe(this, cidadesObserver);
        viewModel.escolhaAtual = 0;

        binding.setViewModel(viewModel);

        ctx = this;

        listCidades = binding.listCidades;
        adapter = new CidadeAdapter(viewModel.cidades.getValue(), this);
        adapter.setListener(this);

        listCidades.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @BindingAdapter("app:imagem")
    public static void setimagem(ImageView view, String imagem){
        final File brasao = new File(ctx.getApplicationContext().getFilesDir(),  imagem);

        if(brasao.exists() && brasao.canRead()){
            final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
            view.setImageDrawable(drawable);
        }

    }

    @BindingAdapter("app:imagem")
    public static void setimagem(CircleView view, String imagem){

        if(imagem != null){
            final File brasao = new File(ctx.getApplicationContext().getFilesDir(),  imagem);

            if(brasao.exists() && brasao.canRead()){
                final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
                view.setImagem(drawable);
            }
        }

    }

    Observer<List<CidadeEstado>> cidadesObserver = new Observer<List<CidadeEstado>>() {
        @Override
        public void onChanged(List<CidadeEstado> cidades) {
            adapter.cidades = cidades;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<BairroCidade> bairroObserver = new Observer<BairroCidade>() {
        @Override
        public void onChanged(BairroCidade bairro) {

            if(viewModel.escolhaAtual == 0){
                if(bairro != null){
                    binding.cardViewPartida.setVisibility(View.VISIBLE);
                    viewModel.escolhaAtual = 1;
                    binding.textViewBairroPartida.setText(bairro.getBairro().getNome());
                    binding.textViewCidadePartida.setText(bairro.getNomeCidade());
                    setimagem(binding.circleViewPartida, bairro.getBrasao());
                }
            } else{
                if(bairro != null){
                    binding.cardViewDestino.setVisibility(View.VISIBLE);
                    binding.textViewBairroDestino.setText(bairro.getBairro().getNome());
                    binding.textViewCidadeDestino.setText(bairro.getNomeCidade());
                    setimagem(binding.circleViewDestino, bairro.getBrasao());
                    mostraResultado();
                }
            }

        }
    };

    Observer<HorarioItinerario> itinerarioObserver = new Observer<HorarioItinerario>() {
        @Override
        public void onChanged(HorarioItinerario horario) {

            if(horario != null){
                System.out.println("HORARIO:: "+horario.getHorario());
            } else{
                viewModel.carregaResultadoDiaSeguinte(date);
            }

        }
    };

    @Override
    public String onSelected(String id) {

        FormBairro formBairro = new FormBairro();

        Bundle bundle = new Bundle();
        bundle.putString("cidade", id);

        formBairro.setArguments(bundle);
        formBairro.setCtx(ctx.getApplication());
        formBairro.setListener(this);
        formBairro.show(ctx.getSupportFragmentManager(), "formBairro");

        return null;
    }

    @Override
    public String onItemSelected(String id) {
        BairroCidade bairroCidade = new BairroCidade();
        bairroCidade.getBairro().setId(id);

        if(viewModel.escolhaAtual == 0){
            viewModel.setBairroPartida(bairroCidade);
            viewModel.bairroPartida.observe(this, bairroObserver);
        } else{
            viewModel.setBairroDestino(bairroCidade);
            viewModel.bairroDestino.observe(this, bairroObserver);
        }


        return null;
    }

    private String getDiaAtual(){
        DateTime dateTime = new DateTime();
        String dia = "";

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 0:
                dia = "domingo";
                break;
            case 1:
                dia = "segunda";
                break;
            case 2:
                dia = "terca";
                break;
            case 3:
                dia = "quarta";
                break;
            case 4:
                dia = "quinta";
                break;
            case 5:
                dia = "sexta";
                break;
            case 6:
                dia = "sabado";
                break;
        }

        return dia;
    }

    private void mostraResultado(){

        DateTime dateTime = new DateTime();
        String dia = "";

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 0:
                dia = "domingo";
                break;
            case 1:
                dia = "segunda";
                break;
            case 2:
                dia = "terca";
                break;
            case 3:
                dia = "quarta";
                break;
            case 4:
                dia = "quinta";
                break;
            case 5:
                dia = "sexta";
                break;
            case 6:
                dia = "sabado";
                break;
        }

        viewModel.carregaResultado(DateTimeFormat.forPattern("HH:mm:00").print(dateTime), dia);

        viewModel.itinerario.observe(this, itinerarioObserver);

        binding.cardView.setVisibility(View.GONE);
        binding.cardViewResultado.setVisibility(View.VISIBLE);

//        binding.textViewBairroPartidaResultado.setText(viewModel.);

    }

}
