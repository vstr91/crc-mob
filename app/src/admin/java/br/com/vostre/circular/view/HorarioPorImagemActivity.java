package br.com.vostre.circular.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityHorarioPorImagemBinding;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ocr.Bloco;
import br.com.vostre.circular.utils.GraphicOverlay;
import br.com.vostre.circular.utils.TextGraphic;
import br.com.vostre.circular.view.adapter.HorarioAdapter;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.viewModel.HorariosPorImagemViewModel;

public class HorarioPorImagemActivity extends BaseActivity {

    ActivityHorarioPorImagemBinding binding;
    HorariosPorImagemViewModel viewModel;

    RecyclerView listHorarios;
    List<HorarioItinerarioNome> horarios;
    HorarioItinerarioAdapter adapter;

    boolean carregado = false;
    Uri imagem;
    Bitmap bitmap;

    boolean processando = false;
    FirebaseVisionText resultadoOCR;

    GraphicOverlay mGraphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_horario_por_imagem);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(HorariosPorImagemViewModel.class);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Horários Por Imagem");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listHorarios = binding.listHorarios;

//        adapter = new HorarioAdapter(horarios, this);

        listHorarios.setAdapter(adapter);

        mGraphicOverlay = binding.graphicOverlay;

        ocultaPrevia();

    }

    public void onClickBtnCamera(View v){

        CropImage.activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                imagem = resultUri;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagem);
                    processaImagem(bitmap);
                    mostraPrevia();
                } catch (IOException e) {
                    e.printStackTrace();
                }



//                Intent i = new Intent(getApplicationContext(), CameraResultadoActivity.class);
//                i.putExtra("imagem", resultUri.toString());
//                startActivity(i);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void processaImagem(Bitmap bmp){
        processando = true;
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);

        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

//        Toast.makeText(getApplicationContext(), "Iniciando processamento...", Toast.LENGTH_SHORT).show();

        final Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(final FirebaseVisionText firebaseVisionText) {

                                processTextRecognitionResult(firebaseVisionText);

                                resultadoOCR = firebaseVisionText;

                                String res = firebaseVisionText.getText();
                                System.out.println(res);

                                processando = false;

                            }

                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        processando = false;
                                    }
                                });

//        Toast.makeText(getApplicationContext(), "Finalizou!", Toast.LENGTH_SHORT).show();

        //ImageUtils.deletarImagem(new File(imagem.getPath()));

    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            return;
        }

        mGraphicOverlay.clear();

        Bitmap b = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        binding.graphicOverlay.setVisibility(View.GONE);

        Canvas canvas = new Canvas(b);

        Paint p2 = new Paint();
        p2.setColor(Color.RED);
        p2.setStyle(Paint.Style.STROKE);
        p2.setStrokeWidth(10);

        canvas.drawBitmap(bitmap, 0, 0, new Paint());

        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);

                    textGraphic.draw(canvas);

                    FirebaseVisionText.Element e = elements.get(k);

                    canvas.drawRect(e.getBoundingBox(), p2);
                    canvas.drawText(e.getText(), -e.getBoundingBox().top, -e.getBoundingBox().left, new Paint());

                }
            }
        }

        binding.imageView9.setImageBitmap(b);

    }

    public void onClickBtnProcessarOCR(View v){

        boolean segSex = binding.checkBoxSegSex.isChecked();
        boolean sabado = binding.checkBoxSabado.isChecked();
        boolean domingo = binding.checkBoxDom.isChecked();

        System.out.println("DIAS: "+segSex+" | "+sabado+" | "+domingo);

        List<HorarioItinerario> hors = new ArrayList<>();

        if(!segSex && !sabado && !domingo){
            Toast.makeText(getApplicationContext(), "Ao menos um dia/período deve ser escolhido!", Toast.LENGTH_SHORT).show();
        } else{

            Toast.makeText(getApplicationContext(), "Horários Encontrados", Toast.LENGTH_SHORT).show();

            binding.imageView9.setImageBitmap(bitmap);

            List<Bloco> elementos = new ArrayList<>();

            for (FirebaseVisionText.TextBlock block: resultadoOCR.getTextBlocks()) {

                for (FirebaseVisionText.Line line: block.getLines()) {
                    String lineText = line.getText();
                    Rect lineFrame = line.getBoundingBox();

                    Bloco bloco = new Bloco();
                    bloco.setTexto(lineText);
                    bloco.setCoordenada(lineFrame.bottom);

                    elementos.add(bloco);
                }

            }

            Collections.sort(elementos, new Comparator<Bloco>() {
                public int compare(Bloco o1,
                                   Bloco o2) {
                    return o1.getCoordenada().compareTo(o2.getCoordenada());
                }
            });

            HorarioItinerario hi = null;

            for(Bloco b : elementos){
                System.out.println("BLOCO:"+b.getTexto()+" - "+b.getCoordenada());

                if(hi == null){
                    hi = new HorarioItinerario();
                }

                try{
                    DateTime hora = DateTimeFormat.forPattern("HH:mm").parseDateTime(b.getTexto());

                    hi.setHorario(DateTimeFormat.forPattern("HH:mm").print(hora));

                } catch(Exception e){

                    if(hi != null){
                        hi.setObservacao(b.getTexto());
                        hors.add(hi);
                        hi = null;
                    }

                } finally {
                    if(hi != null){
                        hors.add(hi);
                        hi = null;
                    }
                }



            }

            binding.checkBoxSegSex.setChecked(false);
            binding.checkBoxSabado.setChecked(false);
            binding.checkBoxDom.setChecked(false);

            ocultaPrevia();
        }

        System.out.println(horarios);

        for(HorarioItinerario h : hors){

            if(h.getObservacao() != null && !h.getObservacao().isEmpty()){
                Toast.makeText(getApplicationContext(), h.getHorario()+" "+h.getObservacao(), Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getApplicationContext(), h.getHorario(), Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void ocultaPrevia(){
        binding.btnProcessarOCR.setVisibility(View.GONE);
        binding.cardView4.setVisibility(View.GONE);
        binding.imageView9.setVisibility(View.GONE);
        binding.graphicOverlay.setVisibility(View.GONE);

        // mostrando elementos comuns
        binding.listHorarios.setVisibility(View.VISIBLE);
        binding.btnProcessar.setVisibility(View.VISIBLE);
        binding.btnAbrirCamera.setVisibility(View.VISIBLE);
    }

    private void mostraPrevia(){
        binding.btnProcessarOCR.setVisibility(View.VISIBLE);
        binding.cardView4.setVisibility(View.VISIBLE);
        binding.imageView9.setVisibility(View.VISIBLE);
        binding.graphicOverlay.setVisibility(View.VISIBLE);

        // ocultando elementos comuns
        binding.listHorarios.setVisibility(View.GONE);
        binding.btnProcessar.setVisibility(View.GONE);
        binding.btnAbrirCamera.setVisibility(View.GONE);
    }

}
