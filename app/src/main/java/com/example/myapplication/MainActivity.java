package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.na_at.fad.sdk.commons.config.FadSignConfig;
import com.na_at.fad.sdk.commons.config.data.AssetSource;
import com.na_at.fad.sdk.commons.config.data.FadSource;
import com.na_at.fad.sdk.commons.config.data.FileSource;
import com.na_at.fad.sdk.commons.config.data.RelatedProcess;
import com.na_at.fad.sdk.commons.config.data.UriSource;
import com.na_at.fad.sdk.sign.FadActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SIGN_REQUEST_CODE = 2002;

    // main manager object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start_biometric).setOnClickListener(v -> startSignSDK(null));
    }

    private void showLoader() {
        findViewById(R.id.loader_view).setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        findViewById(R.id.loader_view).setVisibility(View.GONE);
    }

    private void startSignSDK(String processId) {
        showLoader();

        String endpoint = "https://uat.firmaautografa.com";

        FadSource xmlSource = new AssetSource("data.xml");
        FadSource pdfSource = new AssetSource("pdf.pdf");

        FadSignConfig.Builder buildConfig = new FadSignConfig.Builder()
                .signerName("Jair Amezcua")
                .timeToSign(60 * 5)
                .baseUrl(endpoint)
                .pdfSource(convertSource(pdfSource))
                .xmlSource(convertSource(xmlSource));

        int processType = 0;

        if (processId != null && !processId.equals("-"))
            buildConfig.getRelatedProcesses().add(new RelatedProcess(processId, "" + processType));

        Intent intent = new Intent(this, FadActivity.class);
        intent.putExtra("config", buildConfig.build());
        startActivityForResult(intent, SIGN_REQUEST_CODE);
    }

    private FadSource convertSource(FadSource source) {
        if (source instanceof AssetSource) {
            return new com.na_at.fad.sdk.commons.config.data.AssetSource(((AssetSource) source).getAssetName());
        } else if (source instanceof FileSource) {
            return new com.na_at.fad.sdk.commons.config.data.FileSource(((FileSource) source).getFile());
        }
        return new com.na_at.fad.sdk.commons.config.data.UriSource(((UriSource) source).getUri());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == SIGN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle result = data.getExtras();
                boolean success = result.getBoolean("success");
                String processId = result.getString("processId");

                if (success) {
                    Toast.makeText(this, "Proceso de firma completado.", Toast.LENGTH_SHORT).show();
                    // process completed
                    // showResult(processId);
                } else {
                    // process with error
                    String error = result.getString("error");
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Proceso de firma cancelado.", Toast.LENGTH_SHORT).show();
            }
            hideLoader();
        }
    }
}