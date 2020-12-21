package br.com.vostre.circular.utils;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import br.com.vostre.circular.TarefaAssincronaListener;
import br.com.vostre.circular.model.works.SyncWorker;
import br.com.vostre.circular.model.works.TemporariasWorker;

public class WorksUtils {

    public static void iniciaWorkTempsSingle(Context ctx, String tag){
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(TemporariasWorker.class)
                .addTag(tag)
                .build();

        WorkManager.getInstance(ctx).enqueue(workRequest);
    }

    public static void iniciaWorkAtualizacaoSingle(Context ctx, String tag){
        WorkRequest syncWorkRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .addTag(tag)
                .build();

        WorkManager.getInstance(ctx).enqueue(syncWorkRequest);
    }

    public static void iniciaWorkAtualizacao(Context ctx, int minutos, String tag, Constraints constraints){
        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, minutos, TimeUnit.MINUTES)
                .addTag(tag)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.KEEP, syncWorkRequest);
    }

}
