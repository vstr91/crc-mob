package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
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
import android.os.AsyncTask;
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
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityHorarioPorImagemBinding;
import br.com.vostre.circular.listener.HorarioCarregadoListener;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ocr.Bloco;
import br.com.vostre.circular.utils.GraphicOverlay;
import br.com.vostre.circular.utils.StringUtils;
import br.com.vostre.circular.utils.TextGraphic;
import br.com.vostre.circular.view.adapter.HorarioAdapter;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.viewModel.HorariosPorImagemViewModel;

public class HorarioPorImagemActivity extends BaseActivity {

    ActivityHorarioPorImagemBinding binding;
    HorariosPorImagemViewModel viewModel;

    RecyclerView listHorarios;
    List<HorarioItinerarioNome> horarios;
    List<Horario> todosHorarios;
    HorarioItinerarioAdapter adapter;

    boolean carregado = false;
    Uri imagem;
    Bitmap bitmap;

    boolean processando = false;
    FirebaseVisionText resultadoOCR;

    GraphicOverlay mGraphicOverlay;
    String itinerario;
    List<HorarioItinerarioNome> hors;

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

        hors = new ArrayList<>();

        adapter = new HorarioItinerarioAdapter(hors, this, null, false);

//        adapter = new HorarioAdapter(horarios, this);

        listHorarios.setAdapter(adapter);

        mGraphicOverlay = binding.graphicOverlay;

        itinerario = getIntent().getStringExtra("itinerario");
        viewModel.setItinerario(itinerario);

        viewModel.horarios.observe(this, horariosObserver);
        viewModel.itinerario.observe(this, itinerarioObserver);


        ocultaPrevia();

        binding.btnProcessarOCR.setEnabled(false);

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
        p2.setStrokeWidth(3);

        Paint p3 = new Paint();
        p3.setColor(Color.BLUE);
        p3.setStyle(Paint.Style.STROKE);
        p3.setStrokeWidth(1);
        p3.setTextSize(0.2f);

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
                    canvas.drawText(e.getText(), -e.getBoundingBox().top, -e.getBoundingBox().left, p3);

                }
            }
        }

        binding.imageView9.setImageBitmap(b);

    }

    public void onClickBtnLimparLista(View v){
        hors = new ArrayList<>();
        adapter.horarios = hors;
        adapter.notifyDataSetChanged();
    }

    public void onClickBtnComparar(View v){

        for(HorarioItinerarioNome h : hors){
//            Toast.makeText(getApplicationContext(), DateTimeFormat.forPattern("HH:mm").print(h.getNomeHorario())+" | "
//                    +h.getHorarioItinerario().getDomingo()+", "
//                    +h.getHorarioItinerario().getSegunda()+", "
//                    +h.getHorarioItinerario().getTerca()+", "
//                    +h.getHorarioItinerario().getQuarta()+", "
//                    +h.getHorarioItinerario().getQuinta()+", "
//                    +h.getHorarioItinerario().getSexta()+", "
//                    +h.getHorarioItinerario().getSabado()+" | "+h.getHorarioItinerario().getObservacao(), Toast.LENGTH_SHORT).show();
        }

        Intent i = new Intent(getApplicationContext(), ComparaHorariosActivity.class);
        i.putExtra("horariosProcessados", (Serializable) hors);
        i.putExtra("itinerario", itinerario);
        startActivity(i);

    }

    public void onClickBtnProcessarOCR(View v){

        boolean segSex = binding.checkBoxSegSex.isChecked();
        boolean sabado = binding.checkBoxSabado.isChecked();
        boolean domingo = binding.checkBoxDom.isChecked();

        System.out.println("DIAS: "+segSex+" | "+sabado+" | "+domingo);

        if(!segSex && !sabado && !domingo){
            Toast.makeText(getApplicationContext(), "Ao menos um dia/período deve ser escolhido!", Toast.LENGTH_SHORT).show();
        } else{

            //Toast.makeText(getApplicationContext(), "Horários Encontrados", Toast.LENGTH_SHORT).show();

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

            HorarioItinerarioNome hi = null;
            int qtdBlocos = elementos.size();
            int cont = 0;
            boolean flagEdicao = false;
            int index = -1;
            String horaString = "";
            int contFind = 0;

            Pattern MY_PATTERN = Pattern.compile("\\d{2}+:\\d{2}+");

            for(Bloco b : elementos){
                System.out.println("BLOCO: "+b.getTexto()+" - "+b.getCoordenada());

                if(hi == null){
                    hi = new HorarioItinerarioNome();
                }

                try{

                    if(b.getTexto().length() > 3){

                        Matcher m = MY_PATTERN.matcher(b.getTexto());
                        while (m.find() && contFind < 1) {
                            horaString = m.group();
                            contFind++;
                        }

                        DateTime hora = DateTimeFormat.forPattern("HH:mm").parseDateTime(horaString);

                        if(hi.getHorarioItinerario().getHorario() != null){

                            if(!flagEdicao){
                                hors.add(hi);
                            }


                            hi = new HorarioItinerarioNome();
                        }

                        Horario horario = new Horario();
                        horario.setNome(hora);

                        horario = todosHorarios.get(todosHorarios.indexOf(horario));

                        hi.getHorarioItinerario().setHorario(horario.getId());
                        hi.setNomeHorario(horario.getNome().getMillis());
                        hi.setIdHorario(horario.getId());

                        hi.getHorarioItinerario().setItinerario(itinerario);

                        if(hors.indexOf(hi) > -1){
                            hi = hors.get(hors.indexOf(hi));
                            flagEdicao = true;
                        } else{
                            flagEdicao = false;
                        }

                        if(binding.checkBoxDom.isChecked()){
                            hi.getHorarioItinerario().setDomingo(true);
                        }

                        if(binding.checkBoxSegSex.isChecked()){
                            hi.getHorarioItinerario().setSegunda(true);
                            hi.getHorarioItinerario().setTerca(true);
                            hi.getHorarioItinerario().setQuarta(true);
                            hi.getHorarioItinerario().setQuinta(true);
                            hi.getHorarioItinerario().setSexta(true);
                        }

                        if(binding.checkBoxSabado.isChecked()){
                            hi.getHorarioItinerario().setSabado(true);
                        }

                        if(contFind > 0){
                            contFind = 0;

                            String obs = b.getTexto().replace(horaString, "").trim();

                            if(!obs.isEmpty()){
                                hi.getHorarioItinerario().setObservacao(obs);
                            }

                            horaString = "";

                        }

                    } else{
                        DateTime hora = DateTimeFormat.forPattern("HH:mm").parseDateTime(horaString);
                    }

                } catch(Exception e){

                    if(hi != null && hi.getHorarioItinerario().getHorario() != null && !hi.getHorarioItinerario().getHorario().isEmpty()){

                        hi.getHorarioItinerario().setObservacao(b.getTexto());

                        if(!flagEdicao){
                            hors.add(hi);
                        }

                        hi = null;
                    } else{
                        hi = null;
                    }

                }

                if(cont+1 == qtdBlocos && hi != null){

                    if(!flagEdicao){
                        hors.add(hi);
                    }

                }

                cont++;

            }

            binding.checkBoxSegSex.setChecked(false);
            binding.checkBoxSabado.setChecked(false);
            binding.checkBoxDom.setChecked(false);

            ocultaPrevia();
        }

//        for(HorarioItinerarioNome h : hors){
//
//            if(h.getHorarioItinerario().getObservacao() != null && !h.getHorarioItinerario().getObservacao().isEmpty()){
//                Toast.makeText(getApplicationContext(), h.getHorarioItinerario().getHorario()+" "+h.getHorarioItinerario().getObservacao(), Toast.LENGTH_SHORT).show();
//            } else{
//                Toast.makeText(getApplicationContext(), h.getHorarioItinerario().getHorario(), Toast.LENGTH_SHORT).show();
//            }
//
//        }

        //vinculaHorarios(hors);

        List<HorarioItinerarioNome> horariosFiltrados = new ArrayList<>();

        if(binding.editTextIgnorar.getText().toString().trim().length() > 0){

            String filtro = binding.editTextIgnorar.getText().toString();
            filtro = filtro.toLowerCase();
            filtro = StringUtils.removeAcentos(filtro).trim();

            for(HorarioItinerarioNome h : hors){

                if((h.getHorarioItinerario().getObservacao() != null && !StringUtils.removeAcentos(h.getHorarioItinerario().getObservacao()).toLowerCase().endsWith(filtro)) ||
                        h.getHorarioItinerario().getObservacao() == null){
                    horariosFiltrados.add(h);
                }

            }

            if(horariosFiltrados.size() > 0){
                adapter.horarios = horariosFiltrados;
            } else{
                adapter.horarios = hors;

                Toast.makeText(getApplicationContext(), "Nenhum horário correspondente ao filtro foi encontrado. Utilizando lista original.", Toast.LENGTH_SHORT).show();
            }

            if(binding.checkBoxDesconsiderarObservacoes.isChecked()){
                apagaObservacoes(adapter.horarios);
            }

            Collections.sort(adapter.horarios, new Comparator<HorarioItinerarioNome>() {
                @Override
                public int compare(HorarioItinerarioNome horarioItinerarioNome, HorarioItinerarioNome t1) {
                    return horarioItinerarioNome.getNomeHorario().compareTo(t1.getNomeHorario());
                }
            });

            adapter.notifyDataSetChanged();

        } else{
            adapter.horarios = hors;

            if(binding.checkBoxDesconsiderarObservacoes.isChecked()){
                apagaObservacoes(adapter.horarios);
            }

            Collections.sort(adapter.horarios, new Comparator<HorarioItinerarioNome>() {
                @Override
                public int compare(HorarioItinerarioNome horarioItinerarioNome, HorarioItinerarioNome t1) {
                    return horarioItinerarioNome.getNomeHorario().compareTo(t1.getNomeHorario());
                }
            });

            adapter.notifyDataSetChanged();
        }



    }

    private void apagaObservacoes(List<HorarioItinerarioNome> horarios){

        for(HorarioItinerarioNome h : horarios){
            h.getHorarioItinerario().setObservacao("");
        }

    }

    private void ocultaPrevia(){
        binding.btnProcessarOCR.setVisibility(View.GONE);
        binding.cardView4.setVisibility(View.GONE);
        binding.imageView9.setVisibility(View.GONE);
        binding.graphicOverlay.setVisibility(View.GONE);
        binding.btnCarregarFoto.setVisibility(View.GONE);

        // mostrando elementos comuns
        binding.listHorarios.setVisibility(View.VISIBLE);
        binding.btnProcessar.setVisibility(View.VISIBLE);
        binding.btnAbrirCamera.setVisibility(View.VISIBLE);
        binding.btnLimparLista.setVisibility(View.VISIBLE);
    }

    private void mostraPrevia(){
        binding.btnProcessarOCR.setVisibility(View.VISIBLE);
        binding.cardView4.setVisibility(View.VISIBLE);
        binding.imageView9.setVisibility(View.VISIBLE);
        binding.graphicOverlay.setVisibility(View.VISIBLE);
        binding.btnCarregarFoto.setVisibility(View.VISIBLE);

        // ocultando elementos comuns
        binding.listHorarios.setVisibility(View.GONE);
        binding.btnProcessar.setVisibility(View.GONE);
        binding.btnAbrirCamera.setVisibility(View.GONE);
        binding.btnLimparLista.setVisibility(View.GONE);
    }

    Observer<List<Horario>> horariosObserver = new Observer<List<Horario>>() {
        @Override
        public void onChanged(List<Horario> hors) {
            todosHorarios = hors;
            binding.btnProcessarOCR.setEnabled(true);
        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {
            binding.setItinerario(itinerario);
        }
    };

}
