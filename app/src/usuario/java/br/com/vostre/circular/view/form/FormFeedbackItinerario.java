package br.com.vostre.circular.view.form;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormFeedbackItinerarioBinding;
import br.com.vostre.circular.databinding.FormParadaBinding;
import br.com.vostre.circular.model.FeedbackItinerario;
import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.view.adapter.BairroAdapterSpinner;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;
import br.com.vostre.circular.viewModel.MapaViewModel;

import static android.app.Activity.RESULT_OK;

public class FormFeedbackItinerario extends FormBase {

    FormFeedbackItinerarioBinding binding;

    ImageView imageViewFoto;
    Button btnTrocarFoto;

    Itinerario itinerario;
    FeedbackItinerario feedback;

    static Application ctx;
    DetalhesItinerarioViewModel viewModel;

    public static final int PICK_IMAGE = 400;

    static FormFeedbackItinerario thiz;

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public static Application getCtx() {
        return ctx;
    }

    public static void setCtx(Application ctx) {
        FormFeedbackItinerario.ctx = ctx;
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
                inflater, R.layout.form_feedback_itinerario, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(DetalhesItinerarioViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        thiz = this;

        imageViewFoto = binding.imageView;
        btnTrocarFoto = binding.btnTrocarFoto;

        imageViewFoto.setVisibility(View.GONE);
        btnTrocarFoto.setVisibility(View.GONE);

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        viewModel.feedback.setItinerario(itinerario.getId());

        if(viewModel.feedback.valida(viewModel.feedback)){

            if(viewModel.foto != null){
                viewModel.salvarFoto(viewModel.feedback, viewModel.foto);
            }

            viewModel.salvarFeedback(viewModel.feedback);

            viewModel.retorno.observe(this, retornoObserver);
        } else{
           Toast.makeText(getContext(), "Por favor, nos descreva a inconsistência.", Toast.LENGTH_SHORT).show();
        }



    }

    public void onClickFechar(View v){
        dismiss();
    }

    private void ocultaImagem(){
        imageViewFoto.setVisibility(View.GONE);
        btnTrocarFoto.setVisibility(View.GONE);
        viewModel.foto = null;
        viewModel.feedback.setImagem(null);
        binding.btnFoto.setVisibility(View.VISIBLE);
    }

    public void exibeImagem(Bitmap bitmap){
        imageViewFoto.setImageBitmap(bitmap);
        imageViewFoto.invalidate();
        imageViewFoto.setVisibility(View.VISIBLE);
        btnTrocarFoto.setVisibility(View.VISIBLE);
        binding.btnFoto.setVisibility(View.GONE);
    }

    public void onClickBtnFoto(View v){
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        getActivity().startActivityForResult(Intent.createChooser(intent, "Escolha uma foto"), PICK_IMAGE);
        CropImage.activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Escolher Foto")
                .start(ctx, this);
    }

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                Bitmap bmp = BitmapFactory.decodeFile(resultUri.getPath());

                Bitmap bmpPrev = viewModel.preProcessarFoto(bmp);

                viewModel.foto = bmp;
                exibeImagem(bmpPrev);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(ctx, "Houve um problema ao processar a foto. Por favor tente novamente.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getContext().getApplicationContext(), "Obrigado por sua contribuição!", Toast.LENGTH_SHORT).show();
                viewModel.setFeedback(new FeedbackItinerario());
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
