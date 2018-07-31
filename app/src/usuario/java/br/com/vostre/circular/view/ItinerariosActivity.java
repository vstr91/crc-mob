package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.util.List;

import br.com.vostre.circleview.CircleView;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityItinerariosBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.adapter.CidadeAdapter;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.listener.ItemListener;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;

public class ItinerariosActivity extends BaseActivity implements SelectListener, ItemListener {

    ActivityItinerariosBinding binding;

    RecyclerView listCidadesPartida;
    CidadeAdapter adapter;

    static AppCompatActivity ctx;

    ItinerariosViewModel viewModel;

    String dia = "";
    String diaSeguinte = "";
    boolean consultaDiaSeguinte = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ctx = this;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_itinerarios);
        binding.setView(this);
        super.onCreate(savedInstanceState);
        setTitle("Itinerários");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        viewModel = ViewModelProviders.of(this).get(ItinerariosViewModel.class);
        viewModel.cidades.observe(this, cidadesObserver);
        viewModel.escolhaAtual = 0;

        binding.setViewModel(viewModel);

        listCidadesPartida = binding.listCidadesPartida;
        adapter = new CidadeAdapter(viewModel.cidades.getValue(), this);
        adapter.setListener(this);

        listCidadesPartida.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @BindingAdapter("app:imagemParada")
    public static void setimagem(ImageView view, String imagem){

        if(imagem != null){
            final File brasao = new File(ctx.getApplicationContext().getFilesDir(),  imagem);

            if(brasao.exists() && brasao.canRead()){
                final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
                view.setImageDrawable(drawable);
            }
        }

    }

    @BindingAdapter("app:imagem")
    public static void setimagem(CircleView view, String imagem){

        if(imagem != null){
            final File brasao = new File(ctx.getApplicationContext().getFilesDir(),  imagem);

            if(brasao.exists() && brasao.canRead()){
                final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
                view.setImagem(null);
                view.setImagem(drawable);
                view.refreshDrawableState();
            }
        }

    }

    public void onClickBtnEditarPartida(View v){
        binding.cardViewPartida.setVisibility(View.GONE);
        binding.cardViewListPartida.setVisibility(View.VISIBLE);
        viewModel.escolhaAtual = 0;
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
                    binding.cardViewListPartida.setVisibility(View.GONE);
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

    Observer<HorarioItinerarioNome> itinerarioObserver = new Observer<HorarioItinerarioNome>() {
        @Override
        public void onChanged(HorarioItinerarioNome horario) {

            if(horario != null && horario.getIdHorario() != null){
                exibeDados(horario);
                viewModel.carregaHorarios(horario);
                viewModel.horarioAnterior.observe(ctx, horarioAnteriorObserver);
                viewModel.horarioSeguinte.observe(ctx, horarioSeguinteObserver);
            } else if(!consultaDiaSeguinte){
                consultaDiaSeguinte = true;
                viewModel.carregaResultadoDiaSeguinte(diaSeguinte);
                viewModel.itinerario.observe(ctx, itinerarioObserver);
            }

        }
    };

    Observer<HorarioItinerarioNome> horarioAnteriorObserver = new Observer<HorarioItinerarioNome>() {
        @Override
        public void onChanged(HorarioItinerarioNome horario) {

            if(horario != null && horario.getIdHorario() != null){
                exibeHorarioAnterior(horario);
            } else if(!consultaDiaSeguinte){
//                consultaDiaSeguinte = true;
//                viewModel.carregaResultadoDiaSeguinte(diaSeguinte);
//                viewModel.itinerario.observe(ctx, itinerarioObserver);
            }

        }
    };

    Observer<HorarioItinerarioNome> horarioSeguinteObserver = new Observer<HorarioItinerarioNome>() {
        @Override
        public void onChanged(HorarioItinerarioNome horario) {

            if(horario != null && horario.getIdHorario() != null){
                exibeHorarioSeguinte(horario);
            } else if(!consultaDiaSeguinte){
//                consultaDiaSeguinte = true;
//                viewModel.carregaResultadoDiaSeguinte(diaSeguinte);
//                viewModel.itinerario.observe(ctx, itinerarioObserver);
            }

        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioResultadoObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {

            if(itinerario != null){
                System.out.println("HORARIO:: "+itinerario.getNomeBairroPartida()+" | "
                        +itinerario.getNomeBairroDestino());
                exibeDadosResultado();
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

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 0:
                dia = "domingo";
                diaSeguinte = "segunda";
                break;
            case 1:
                dia = "segunda";
                diaSeguinte = "terca";
                break;
            case 2:
                dia = "terca";
                diaSeguinte = "quarta";
                break;
            case 3:
                dia = "quarta";
                diaSeguinte = "quinta";
                break;
            case 4:
                dia = "quinta";
                diaSeguinte = "sexta";
                break;
            case 5:
                dia = "sexta";
                diaSeguinte = "sabado";
                break;
            case 6:
                dia = "sabado";
                diaSeguinte = "domingo";
                break;
        }

        return dia;
    }

    private void mostraResultado(){

        DateTime dateTime = new DateTime();
        String dia = getDiaAtual();

        viewModel.carregaResultado(DateTimeFormat.forPattern("HH:mm:00").print(dateTime), dia);

        viewModel.itinerario.observe(this, itinerarioObserver);

        binding.cardViewListDestino.setVisibility(View.GONE);
        binding.cardViewResultado.setVisibility(View.VISIBLE);

//        binding.textViewBairroPartidaResultado.setText(viewModel.);

    }

    public void exibeDados(HorarioItinerarioNome horario){
        viewModel.carregaItinerarioResultado();
        viewModel.itinerarioResultado.observe(this, itinerarioResultadoObserver);
    }

    public void exibeHorarioAnterior(HorarioItinerarioNome horario){
        binding.textViewHorarioAnterior.setText(DateTimeFormat.forPattern("HH:mm").print(horario.getNomeHorario()));
    }

    public void exibeHorarioSeguinte(HorarioItinerarioNome horario){
        binding.textViewHorarioSeguinte.setText(DateTimeFormat.forPattern("HH:mm").print(horario.getNomeHorario()));
    }

    public void exibeDadosResultado(){
        binding.textViewBairroPartidaResultado.setText(viewModel.itinerarioResultado.getValue().getNomeBairroPartida());
        binding.textViewBairroDestinoResultado.setText(viewModel.itinerarioResultado.getValue().getNomeBairroDestino());

        binding.textViewCidadePartidaResultado.setText(viewModel.itinerarioResultado.getValue().getNomeCidadePartida());
        binding.textViewCidadeDestinoResultado.setText(viewModel.itinerarioResultado.getValue().getNomeCidadeDestino());

        binding.textViewHorario.setText(DateTimeFormat.forPattern("HH:mm")
                .print(viewModel.itinerario.getValue().getNomeHorario()));
        binding.textViewDuracao.setText(DateTimeFormat.forPattern("HH:mm")
                .print(viewModel.itinerarioResultado.getValue().getItinerario().getTempo()));

        binding.textViewDistancia.setText(viewModel.itinerarioResultado.getValue().getItinerario().getDistancia().toString());
        binding.textView11.setText(viewModel.itinerarioResultado.getValue().getNomeEmpresa());
        binding.textViewParada.setText(viewModel.itinerarioResultado.getValue().getNomePartida());

        if(viewModel.itinerario.getValue().getHorarioItinerario().getObservacao() != null){
            binding.textViewObservacao.setText(viewModel.itinerario.getValue().getHorarioItinerario().getObservacao());
        } else{
            binding.textViewObservacao.setVisibility(View.GONE);
        }

        if(!viewModel.itinerarioResultado.getValue().getItinerario().getAcessivel()){
            binding.imageView8.setVisibility(View.GONE);
        }

        binding.textViewTarifa.setText(viewModel.itinerarioResultado.getValue().getItinerario().getTarifa().toString());

    }

}
