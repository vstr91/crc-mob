package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
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
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.TextView;
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
import java.util.List;

import br.com.vostre.circular.BuildConfig;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityCameraBinding;
import br.com.vostre.circular.databinding.ActivitySobreBinding;
import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.utils.CameraPreview;
import br.com.vostre.circular.utils.GraphicOverlay;
import br.com.vostre.circular.utils.TextGraphic;
import br.com.vostre.circular.viewModel.SobreViewModel;

public class CameraActivity extends BaseActivity {

    ActivityCameraBinding binding;
//    SobreViewModel viewModel;
    Camera camera;
    CameraPreview preview;
    Bitmap bitmap;
    private Camera.PictureCallback mPicture;
    GraphicOverlay mGraphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Câmera");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

//        viewModel = ViewModelProviders.of(this).get(SobreViewModel.class);
//        viewModel.parametros.observe(this, parametrosObserver);

        mGraphicOverlay = binding.graphicOverlay;
        binding.imageView9.setVisibility(View.GONE);
        binding.btnFechar.setVisibility(View.GONE);

        camera = Camera.open();
        camera.setDisplayOrientation(90);

        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);

        mPicture = getPictureCallback();

        preview = new CameraPreview(this, camera);
        binding.preview.addView(preview);

        binding.btnfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, mPicture);
            }
        });

        camera.startPreview();

        binding.btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnFechar.setVisibility(View.GONE);
                binding.imageView9.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if(camera == null) {
            camera = Camera.open();
            camera.setDisplayOrientation(90);

            Camera.Parameters params = camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(params);

            mPicture = getPictureCallback();
            preview.refreshCamera(camera);
            Log.d("nu", "null");
        }else {
            Log.d("nu","no null");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

//    Observer<ParametroInterno> parametrosObserver = new Observer<ParametroInterno>() {
//        @Override
//        public void onChanged(ParametroInterno parametros) {
//            binding.setParametros(parametros);
//        }
//    };

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                bitmap = ajustarImagem(bitmap);
                // ajustando para portrait
                bitmap = rotateImage(bitmap, 90);
                processaImagem(bitmap);
            }
        };
        return picture;
    }

    private void processaImagem(Bitmap bmp){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);

        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        final Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                                processTextRecognitionResult(firebaseVisionText);

                                String res = firebaseVisionText.getText();
                                System.out.println(res);

                                for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
                                    String blockText = block.getText();
                                    Float blockConfidence = block.getConfidence();
                                    List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                    Point[] blockCornerPoints = block.getCornerPoints();
                                    Rect blockFrame = block.getBoundingBox();

                                    for (FirebaseVisionText.Line line: block.getLines()) {
                                        String lineText = line.getText();
                                        Float lineConfidence = line.getConfidence();
                                        List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                        Point[] lineCornerPoints = line.getCornerPoints();
                                        Rect lineFrame = line.getBoundingBox();

                                        System.out.println("LINE TEXT: "+lineText);

                                        for (FirebaseVisionText.Element element: line.getElements()) {
                                            String elementText = element.getText();
                                            Float elementConfidence = element.getConfidence();
                                            List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                            Point[] elementCornerPoints = element.getCornerPoints();
                                            Rect elementFrame = element.getBoundingBox();

                                            System.out.println("TEXT:: "+elementText);
                                        }

                                    }

                                }

                            }

                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });

        Toast.makeText(getApplicationContext(), "Finalizou!", Toast.LENGTH_SHORT).show();
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
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            return;
        }

        mGraphicOverlay.clear();

        Canvas canvas = new Canvas(bitmap);

        Paint p2 = new Paint();
        p2.setColor(Color.RED);
        p2.setStyle(Paint.Style.STROKE);
        p2.setStrokeWidth(10);

        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
//                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
//                    mGraphicOverlay.add(textGraphic);

                    FirebaseVisionText.Element e = elements.get(k);

                    canvas.drawRect(e.getBoundingBox(), p2);
                    canvas.drawText(e.getText(), e.getBoundingBox().top, e.getBoundingBox().left, new Paint());

                }
            }
        }

        System.out.println("AAAAA");

        binding.imageView9.setImageBitmap(bitmap);
        binding.imageView9.setVisibility(View.VISIBLE);
        binding.btnFechar.setVisibility(View.VISIBLE);

    }

}
