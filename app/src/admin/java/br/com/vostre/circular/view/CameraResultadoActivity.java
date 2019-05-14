package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityCameraBinding;
import br.com.vostre.circular.databinding.ActivityCameraResultadoBinding;
import br.com.vostre.circular.utils.CameraPreview;
import br.com.vostre.circular.utils.GraphicOverlay;
import br.com.vostre.circular.utils.ImageUtils;
import br.com.vostre.circular.utils.TextGraphic;

public class CameraResultadoActivity extends BaseActivity {

    ActivityCameraResultadoBinding binding;
//    SobreViewModel viewModel;
    Bitmap bitmap;
    GraphicOverlay mGraphicOverlay;
    Boolean processando = false;

    int acao = 1;
    Uri imagem;

    FirebaseVisionText resultadoOCR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera_resultado);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Câmera");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        String uri = getIntent().getStringExtra("imagem");

        imagem = Uri.parse(uri);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagem);

            binding.imageView9.setImageURI(imagem);

            processaImagem(bitmap);

//        viewModel = ViewModelProviders.of(this).get(SobreViewModel.class);
//        viewModel.parametros.observe(this, parametrosObserver);

            mGraphicOverlay = binding.graphicOverlay;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

//    Observer<ParametroInterno> parametrosObserver = new Observer<ParametroInterno>() {
//        @Override
//        public void onChanged(ParametroInterno parametros) {
//            binding.setParametros(parametros);
//        }
//    };

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

    private Bitmap ajustarImagem(Bitmap bitmap){

        Bitmap rotatedBitmap = null;

        try {
            File file = new File(getApplication().getFilesDir(), "temp54335.png");
            FileOutputStream filecon = null;
            filecon = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, filecon);

            ExifInterface ei = new ExifInterface(file.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

            if(filecon != null){
                filecon.close();
            }

            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotatedBitmap;

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {

        if(source != null){
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } else{
            return null;
        }


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

    public void onClickBtnProcessar(View v){

        Toast.makeText(getApplicationContext(), "Horários Encontrados", Toast.LENGTH_SHORT).show();

        binding.imageView9.setImageBitmap(bitmap);

        Map<String, Integer> elementos = new HashMap<>();

        for (FirebaseVisionText.TextBlock block: resultadoOCR.getTextBlocks()) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();

            //System.out.println("BLOCK TEXT: "+blockText+" | "+blockFrame.bottom);

            for (FirebaseVisionText.Line line: block.getLines()) {
                String lineText = line.getText();
                Float lineConfidence = line.getConfidence();
                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();

                System.out.println("LINE TEXT: "+lineText+" | "+lineFrame.bottom);

                elementos.put(lineText, lineFrame.bottom);

                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    Float elementConfidence = element.getConfidence();
                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();

                    DateTime f;

                    //System.out.println("TEXT: "+elementText+" | "+elementFrame.bottom);

                    try{
                        f = DateTimeFormat.forPattern("HH:mm").parseDateTime(elementText);

                        Toast.makeText(getApplicationContext(), DateTimeFormat.forPattern("HH:mm").print(f), Toast.LENGTH_SHORT).show();
                    } catch(Exception e){
                        System.out.println("ERROR DT: "+e.getMessage());
                    }

                    System.out.println("OCR:: "+elementText);
                }

            }

        }

        Set<Map.Entry<String, Integer>> set = elementos.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(
                set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        System.out.println(list);

    }

}
