package br.com.vostre.circular.view;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityQrcodeBinding;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.viewModel.QRCodeViewModel;
import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class QRCodeActivity extends BaseActivity {

    ActivityQrcodeBinding binding;
    private SurfaceView cameraView;
    QRCodeViewModel viewModel;

    private QREader qrEader;
    AppCompatActivity ctx;

    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_qrcode);
        super.onCreate(savedInstanceState);
        setTitle("Ler QR Code");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        viewModel = ViewModelProviders.of(this).get(QRCodeViewModel.class);
        ctx = this;

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

                if(!flag){

                    //Toast.makeText(ctx, "Leitura efetuada! Processando...", Toast.LENGTH_SHORT).show();

                    String[] parametros = data.split("\\/");

                    String uf = parametros[5];
                    String local = parametros[6];
                    String bairro = parametros[7];
                    String slugParada = parametros[8];

                    viewModel.carregaParadaQRCode(uf, local, bairro, slugParada);

                    viewModel.parada.observe(ctx, paradaObserver);
                    flag = true;
                }


            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(cameraView.getHeight())
                .width(cameraView.getWidth())
                .build();

        qrEader.initAndStart(cameraView);
    }

    Observer<ParadaBairro> paradaObserver = new Observer<ParadaBairro>() {
        @Override
        public void onChanged(ParadaBairro parada) {

            if(parada != null){
                Intent i = new Intent(ctx, DetalheParadaActivity.class);
                i.putExtra("parada", parada.getParada().getId());
                ctx.startActivity(i);
            } else{
                Toast.makeText(ctx, "Parada não encontrada. QR Code Inválido.", Toast.LENGTH_SHORT).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        flag = false;
                    }
                }, 2000);

            }

        }
    };

}
