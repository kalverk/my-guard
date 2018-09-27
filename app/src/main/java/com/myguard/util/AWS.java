package com.myguard.util;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

/**
 * Created by user on 24.02.2018.
 */

public class AWS {

    private static final String COGNITO_POOL_ID = "eu-central-1:0cc75cbc-a224-48f4-a5b2-577bf94270d1";
    private static final String COGNITO_POOL_REGION = "eu-central-1";
    private static final String BUCKET_NAME = "my-guard";
    private static final String BUCKET_REGION = "eu-central-1";

    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;

    private static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
            sS3Client.setRegion(Region.getRegion(Regions.fromName(BUCKET_REGION)));
        }
        return sS3Client;
    }

    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    COGNITO_POOL_ID,
                    Regions.fromName(COGNITO_POOL_REGION));
        }
        return sCredProvider;
    }

    public static void uploadData(Context context, final File file) {
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(context)
                        .s3Client(AWS.getS3Client(context))
                        .build();

        TransferObserver uploadObserver = transferUtility.upload(AWS.BUCKET_NAME, file.getName(), file);

        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Log.d(this.getClass().getSimpleName(), "Upload complete");
                    file.delete();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d(this.getClass().getSimpleName(), "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(this.getClass().getSimpleName(), ex.getMessage());
            }
        });
    }

}
