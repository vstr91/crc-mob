package br.com.vostre.circular.view.form;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormPontoInteresseBinding;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.view.adapter.BairroAdapterSpinner;
import br.com.vostre.circular.viewModel.PontosInteresseViewModel;

public class FormPontoInteresse extends FormPOIBase {

    static FormPontoInteresseBinding binding;
    Calendar dataInicio;
    Calendar dataFim;

    TextView textViewInicio;
    Button btnTrocarInicio;

    TextView textViewFim;
    Button btnTrocarFim;

    ImageView imageViewFoto;
    Button btnTrocarFoto;

    Double latitude;
    Double longitude;

    static PontoInteresse pontoInteresse;

    public Boolean flagInicioEdicao;
    static Application ctx;
    static PontosInteresseViewModel viewModel;

    public static final int PICK_IMAGE = 500;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public PontoInteresse getPontoInteresse() {
        return pontoInteresse;
    }

    public void setPontoInteresse(PontoInteresse pontoInteresse) {
        this.pontoInteresse = pontoInteresse;
    }

    public static Application getCtx() {
        return ctx;
    }

    public static void setCtx(Application ctx) {
        FormPontoInteresse.ctx = ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.form_pais, container, false);
//
//        if(this.getDialog() != null){
//            this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        }
//
//        return view;

        binding = DataBindingUtil.inflate(
                inflater, R.layout.form_ponto_interesse, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(PontosInteresseViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        textViewInicio = binding.textViewInicio;
        btnTrocarInicio = binding.btnTrocarInicio;

        textViewFim = binding.textViewFim;
        btnTrocarFim = binding.btnTrocarFim;

        imageViewFoto = binding.imageView;
        btnTrocarFoto = binding.btnTrocarFoto;

        textViewInicio.setVisibility(View.GONE);
        btnTrocarInicio.setVisibility(View.GONE);

        textViewFim.setVisibility(View.GONE);
        btnTrocarFim.setVisibility(View.GONE);

        imageViewFoto.setVisibility(View.GONE);
        btnTrocarFoto.setVisibility(View.GONE);

        viewModel.bairros.observe(this, bairrosObserver);

        if(pontoInteresse != null){
            viewModel.pontoInteresse = pontoInteresse;

            if(pontoInteresse.getImagem() != null){
                File foto = new File(ctx.getFilesDir(), pontoInteresse.getImagem());

                if(foto.exists() && foto.canRead()){
                    Bitmap bmp = BitmapFactory.decodeFile(foto.getAbsolutePath());
                    viewModel.setFoto(bmp);
                    exibeImagem();
                } else{
                    ocultaImagem();
                }
            } else{
                ocultaImagem();
            }

            if(viewModel.pontoInteresse.getDataInicial() == null){
                textViewInicio.setVisibility(View.GONE);
                btnTrocarInicio.setVisibility(View.GONE);
                binding.textView30.setVisibility(View.GONE);
            } else{
                exibeDataEscolhida(0);
            }

            if(viewModel.pontoInteresse.getDataFinal() == null){
                textViewFim.setVisibility(View.GONE);
                btnTrocarFim.setVisibility(View.GONE);
                binding.textView31.setVisibility(View.GONE);
            } else{
                exibeDataEscolhida(1);
            }

            flagInicioEdicao = true;
        }

        ctx = this.getActivity().getApplication();

        return binding.getRoot();

    }

    public void onClickSalvar(View v){
        viewModel.pontoInteresse.setLatitude(latitude);
        viewModel.pontoInteresse.setLongitude(longitude);

        if(pontoInteresse != null){
            viewModel.editarPontoInteresse();
        } else{
            viewModel.salvarPontoInteresse();
        }

        viewModel.retorno.observe(this, retornoObserver);
    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocarInicio(View v){
        FormCalendarioPOI formCalendario = new FormCalendarioPOI();
        formCalendario.setParent(this);

        if(viewModel.pontoInteresse.getDataInicial() != null){
            formCalendario.setDataInicioAnterior(viewModel.pontoInteresse.getDataInicial().toCalendar(null));
        }

        formCalendario.setQual(0);
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onClickTrocarFim(View v){
        FormCalendarioPOI formCalendario = new FormCalendarioPOI();
        formCalendario.setParent(this);

        if(viewModel.pontoInteresse.getDataFinal() != null){
            formCalendario.setDataFimAnterior(viewModel.pontoInteresse.getDataFinal().toCalendar(null));
        }

        formCalendario.setQual(1);
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    @Override
    public void setDataInicio(Calendar umaData) {
        this.dataInicio = umaData;
        exibeDataEscolhida(0);

        viewModel.pontoInteresse.setDataInicial(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.pontoInteresse.getDataInicial() == null){
            ocultaDataEscolhida(0);
        } else{
            exibeDataEscolhida(0);
        }

    }

    @Override
    public void setDataFim(Calendar umaData) {
        viewModel.pontoInteresse.setDataFinal(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.pontoInteresse.getDataFinal() == null){
            ocultaDataEscolhida(1);
        } else{
            exibeDataEscolhida(1);
        }

    }

    private void ocultaDataEscolhida(Integer qual){

        if(qual == 0){
            textViewInicio.setVisibility(View.GONE);
            textViewInicio.setText("");
            btnTrocarInicio.setVisibility(View.GONE);
            viewModel.pontoInteresse.setDataInicial(null);
            binding.btnDataInicio.setVisibility(View.VISIBLE);
            binding.textView30.setVisibility(View.GONE);
        } else{
            textViewFim.setVisibility(View.GONE);
            textViewFim.setText("");
            btnTrocarFim.setVisibility(View.GONE);
            viewModel.pontoInteresse.setDataFinal(null);
            binding.btnDataFim.setVisibility(View.VISIBLE);
            binding.textView31.setVisibility(View.GONE);
        }


    }

    private void exibeDataEscolhida(Integer qual){

        if(qual == 0){
            textViewInicio.setText(DateTimeFormat
                    .forPattern("dd/MM/yy HH:mm").print(viewModel.pontoInteresse.getDataInicial()));

            textViewInicio.setVisibility(View.VISIBLE);
            btnTrocarInicio.setVisibility(View.VISIBLE);
            binding.btnDataInicio.setVisibility(View.GONE);
            binding.textView30.setVisibility(View.VISIBLE);
        } else{
            textViewFim.setText(DateTimeFormat
                    .forPattern("dd/MM/yy HH:mm").print(viewModel.pontoInteresse.getDataFinal()));

            textViewFim.setVisibility(View.VISIBLE);
            btnTrocarFim.setVisibility(View.VISIBLE);
            binding.btnDataFim.setVisibility(View.GONE);
            binding.textView31.setVisibility(View.VISIBLE);
        }


    }

    private void ocultaImagem(){
        imageViewFoto.setVisibility(View.GONE);
        btnTrocarFoto.setVisibility(View.GONE);
        viewModel.foto = null;
        viewModel.pontoInteresse.setImagem(null);
        binding.btnFoto.setVisibility(View.VISIBLE);
    }

    public void exibeImagem(){
        imageViewFoto.setImageBitmap(viewModel.foto);
        imageViewFoto.invalidate();
        imageViewFoto.setVisibility(View.VISIBLE);
        btnTrocarFoto.setVisibility(View.VISIBLE);
        binding.btnFoto.setVisibility(View.GONE);
    }

    public void onClickBtnFoto(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Escolha uma foto do ponto de interesse"), PICK_IMAGE);
    }

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {

            if(data != null){
                try {
                    InputStream inputStream = ctx.getContentResolver().openInputStream(data.getData());
                    viewModel.foto = BitmapFactory.decodeStream(inputStream);
                    exibeImagem();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    Observer<List<BairroCidade>> bairrosObserver = new Observer<List<BairroCidade>>() {
        @Override
        public void onChanged(List<BairroCidade> bairros) {
            setSpinnerEntries(binding.spinnerBairro, bairros);
        }
    };

    public void setSpinnerEntries(Spinner spinner, List<BairroCidade> bairros){

        if(bairros != null){
            BairroAdapterSpinner adapter = new BairroAdapterSpinner(ctx, R.layout.linha_bairros_spinner,
                    R.id.textViewNome, bairros);
            spinner.setAdapter(adapter);

            if(pontoInteresse != null){
                BairroCidade bairro = new BairroCidade();
                bairro.getBairro().setId(pontoInteresse.getBairro());
                int i = viewModel.bairros.getValue().indexOf(bairro);
                binding.spinnerBairro.setSelection(i);
            }

        }

    }

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getContext().getApplicationContext(), "Ponto de Interesse cadastrado!", Toast.LENGTH_SHORT).show();
                viewModel.setPontoInteresse(new PontoInteresse());
                dismiss();
            } else if(retorno == 0){
                Toast.makeText(getContext().getApplicationContext(),
                        "Dados necessários não informados. Por favor preencha " +
                                "todos os dados obrigatórios!",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

    public void onItemSelectedSpinnerBairro (AdapterView<?> adapterView, View view, int i, long l){
        viewModel.bairro = viewModel.bairros.getValue().get(i);
    }

    @BindingAdapter("entries")
    public static void setSpinnerEntries(Spinner spinner, LiveData<List<BairroCidade>> bairros){

        if(bairros.getValue() != null){
            BairroAdapterSpinner adapter = new BairroAdapterSpinner(ctx, R.layout.linha_bairros_spinner,
                    R.id.textViewNome, bairros.getValue());
            spinner.setAdapter(adapter);

            if(pontoInteresse != null){
                BairroCidade bairro = new BairroCidade();
                bairro.getBairro().setId(pontoInteresse.getBairro());
                int i = viewModel.bairros.getValue().indexOf(bairro);
                binding.spinnerBairro.setSelection(i);
            }

        }

    }

}
