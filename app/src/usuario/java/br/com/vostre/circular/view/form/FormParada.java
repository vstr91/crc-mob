package br.com.vostre.circular.view.form;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormParadaBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.view.adapter.BairroAdapterSpinner;
import br.com.vostre.circular.viewModel.BairrosViewModel;
import br.com.vostre.circular.viewModel.MapaViewModel;
import br.com.vostre.circular.viewModel.ParadasViewModel;

public class FormParada extends FormBase {

    FormParadaBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

    ImageView imageViewFoto;
    Button btnTrocarFoto;

    Double latitude;
    Double longitude;

    ParadaSugestao parada;
    ParadaBairro paradaRelativa;
    public Boolean flagInicioEdicao;
    static Application ctx;
    MapaViewModel viewModel;

    BairroAdapterSpinner adapter;

    public static final int PICK_IMAGE = 400;

    int indexBairro = -1;
    static FormParada thiz;

    public ParadaBairro getParadaRelativa() {
        return paradaRelativa;
    }

    public void setParadaRelativa(ParadaBairro paradaRelativa) {
        this.paradaRelativa = paradaRelativa;
    }

    public ParadaSugestao getParada() {
        return parada;
    }

    public void setParada(ParadaSugestao parada) {
        this.parada = parada;
    }

    public static Application getCtx() {
        return ctx;
    }

    public static void setCtx(Application ctx) {
        FormParada.ctx = ctx;
    }

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
                inflater, R.layout.form_parada, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(MapaViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        thiz = this;

        imageViewFoto = binding.imageView;
        btnTrocarFoto = binding.btnTrocarFoto;

        imageViewFoto.setVisibility(View.GONE);
        btnTrocarFoto.setVisibility(View.GONE);

        viewModel.bairros.observe(this, bairrosObserver);

        if(parada != null){
            viewModel.paradaNova.setParada(parada);

            if(parada.getImagem() != null){
                File foto = new File(ctx.getFilesDir(), parada.getImagem());

                if(foto.exists() && foto.canRead()){
                    Bitmap bmp = BitmapFactory.decodeFile(foto.getAbsolutePath());
                    viewModel.setFotoParada(bmp);
                    exibeImagem();
                } else{
                    ocultaImagem();
                }
            } else{
                ocultaImagem();
            }

            flagInicioEdicao = true;
        } else{
            viewModel.paradaNova.setParada(new ParadaSugestao());
        }

        if(paradaRelativa != null){
            viewModel.paradaNova.getParada().setParada(paradaRelativa.getParada().getId());

            viewModel.paradaNova.getParada().setNome(paradaRelativa.getParada().getNome());
            viewModel.paradaNova.getParada().setLatitude(paradaRelativa.getParada().getLatitude());
            viewModel.paradaNova.getParada().setLongitude(paradaRelativa.getParada().getLongitude());
            viewModel.paradaNova.getParada().setTaxaDeEmbarque(paradaRelativa.getParada().getTaxaDeEmbarque());
            viewModel.paradaNova.getParada().setSlug(paradaRelativa.getParada().getSlug());
            viewModel.paradaNova.getParada().setBairro(paradaRelativa.getParada().getBairro());
            viewModel.paradaNova.getParada().setSentido(paradaRelativa.getParada().getSentido());
        }

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        if(latitude != null && longitude != null){
            viewModel.paradaNova.getParada().setLatitude(latitude);
            viewModel.paradaNova.getParada().setLongitude(longitude);
        }

        if(parada != null){
            viewModel.editarParada();
        } else{
            viewModel.salvarParada();
        }

        viewModel.retorno.observe(this, retornoObserver);

    }

    public void onClickFechar(View v){
        dismiss();
    }

    private void ocultaImagem(){
        imageViewFoto.setVisibility(View.GONE);
        btnTrocarFoto.setVisibility(View.GONE);
        viewModel.foto = null;
        viewModel.paradaNova.getParada().setImagem(null);
        binding.btnFoto.setVisibility(View.VISIBLE);
    }

    public void exibeImagem(){
        imageViewFoto.setImageBitmap(viewModel.foto);
        imageViewFoto.invalidate();
        imageViewFoto.setVisibility(View.VISIBLE);
        btnTrocarFoto.setVisibility(View.VISIBLE);
        binding.btnFoto.setVisibility(View.GONE);
    }

    @BindingAdapter("entriesParada")
    public static void setSpinnerEntries(Spinner spinner, LiveData<List<BairroCidade>> bairros){

        if(bairros.getValue() != null){
            BairroAdapterSpinner adapter = new BairroAdapterSpinner(ctx, R.layout.linha_bairros_spinner,
                    R.id.textViewNome, bairros.getValue());
            spinner.setAdapter(adapter);

            thiz.setaBairroParada();
        }

    }

    public void onClickBtnFoto(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Escolha uma foto da parada"), PICK_IMAGE);
    }

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }

    }

    @BindingAdapter("android:text")
    public static void setText(TextView view, Double valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();

        if(valor != null){
            view.setText(nf.format(valor));
        }

    }

    @InverseBindingAdapter(attribute = "android:text")
    public static Double getText(TextView view) {

        if(view.getText().toString().equals("null") || view.getText().toString().equals("")){
            return 0.0;
        } else{

            try{
                String valor = view.getText().toString();
                valor = valor.replace(".", "");
                valor = valor.replace(",", ".");
                Double d = Double.parseDouble(valor);
                return d;
            } catch(NumberFormatException e){
                return 0.0;
            }

        }


    }

    public void setSpinnerEntries(Spinner spinner, List<BairroCidade> bairros){

        if(bairros != null){
            BairroAdapterSpinner adapter = new BairroAdapterSpinner(ctx, R.layout.linha_bairros_spinner,
                    R.id.textViewNome, bairros);
            spinner.setAdapter(adapter);

            setaBairroParada();

        }

    }

    void setaBairroParada() {
        if(parada != null){
            BairroCidade bairro = new BairroCidade();
            bairro.getBairro().setId(parada.getBairro());
            indexBairro = viewModel.bairros.getValue().indexOf(bairro);
            binding.spinnerBairro.setSelection(indexBairro, true);
        }

        if(paradaRelativa != null){
            BairroCidade bairro = new BairroCidade();
            bairro.getBairro().setId(paradaRelativa.getParada().getBairro());
            indexBairro = viewModel.bairros.getValue().indexOf(bairro);
            binding.spinnerBairro.setSelection(indexBairro, true);
        }
    }

    public void onItemSelectedSpinnerBairro (AdapterView<?> adapterView, View view, int i, long l){

        if(indexBairro > 0){
            viewModel.bairro = viewModel.bairros.getValue().get(indexBairro);
        } else{
            viewModel.bairro = viewModel.bairros.getValue().get(i);
        }


    }

    Observer<List<BairroCidade>> bairrosObserver = new Observer<List<BairroCidade>>() {
        @Override
        public void onChanged(List<BairroCidade> bairros) {
            setSpinnerEntries(binding.spinnerBairro, bairros);
        }
    };

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getContext().getApplicationContext(), "Sugestão de parada cadastrada!", Toast.LENGTH_SHORT).show();
                viewModel.setParadaNova(new ParadaSugestaoBairro());
                dismiss();
            } else if(retorno == 0){
                Toast.makeText(getContext().getApplicationContext(),
                        "Dados necessários não informados. Por favor preencha " +
                                "todos os dados obrigatórios!",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

}
