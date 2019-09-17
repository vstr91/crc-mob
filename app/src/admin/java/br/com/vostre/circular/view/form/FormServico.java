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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormParadaBinding;
import br.com.vostre.circular.databinding.FormServicoBinding;
import br.com.vostre.circular.model.Servico;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.adapter.BairroAdapterSpinner;
import br.com.vostre.circular.viewModel.ParadasViewModel;
import br.com.vostre.circular.viewModel.ServicosViewModel;

public class FormServico extends FormBase {

    FormServicoBinding binding;

    ImageView imageViewFoto;
    Button btnTrocarFoto;

    static Servico servico;
    public Boolean flagInicioEdicao;
    static Application ctx;
    static ServicosViewModel viewModel;

    public static final int PICK_IMAGE = 400;

    public static Servico getServico() {
        return servico;
    }

    public static void setServico(Servico servico) {
        FormServico.servico = servico;
    }

    public static Application getCtx() {
        return ctx;
    }

    public static void setCtx(Application ctx) {
        FormServico.ctx = ctx;
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
                inflater, R.layout.form_servico, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(ServicosViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        imageViewFoto = binding.imageView;
        btnTrocarFoto = binding.btnTrocarFoto;

        imageViewFoto.setVisibility(View.GONE);
        btnTrocarFoto.setVisibility(View.GONE);

        if(servico != null){
            viewModel.servico = servico;

            if(servico.getIcone() != null){
                File foto = new File(ctx.getFilesDir(), servico.getIcone());

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

            flagInicioEdicao = true;
        }

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        if(servico != null){
            viewModel.editarServico();
        } else{
            viewModel.salvarServico();
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
        viewModel.servico.setIcone(null);
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
        getActivity().startActivityForResult(Intent.createChooser(intent, "Escolha um ícone"), PICK_IMAGE);
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

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getContext().getApplicationContext(), "Serviço cadastrado!", Toast.LENGTH_SHORT).show();
                viewModel.setServico(new Servico());
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
