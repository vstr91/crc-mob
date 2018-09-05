package br.com.vostre.circular.view;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityQrcodeBinding;
import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class QRCodeActivity extends BaseActivity {

    ActivityQrcodeBinding binding;
    private SurfaceView cameraView;

    private QREader qrEader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_qrcode);
        super.onCreate(savedInstanceState);
        setTitle("Ler QR Code");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA
                ).withListener(new MultiplePermissionsListener() {

            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    configuraActivity();
                } else{
                    Toast.makeText(getApplicationContext(), "Acesso à câmera é necessário para escanear o QR Code!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Init and Start with SurfaceView
        // -------------------------------

        if(cameraView != null){
            qrEader.initAndStart(cameraView);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Cleanup in onPause()
        // --------------------

        if(qrEader != null){
            qrEader.releaseAndCleanup();
        }


    }

    private void configuraActivity(){
        cameraView = binding.cameraView;

        qrEader = new QREader.Builder(this, cameraView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                System.out.println("DATAAAA::: "+data);

                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
                    }
                };


            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(cameraView.getHeight())
                .width(cameraView.getWidth())
                .build();

        qrEader.initAndStart(cameraView);
    }

}
