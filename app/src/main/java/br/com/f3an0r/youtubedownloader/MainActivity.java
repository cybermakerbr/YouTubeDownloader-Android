package br.com.f3an0r.youtubedownloader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;
import kotlin.jvm.functions.Function3;

public class MainActivity extends AppCompatActivity {

    private EditText urlInput;
    private RadioButton videoMode;
    private RadioButton mp3Mode;
    private Button downloadButton;
    private Button cancelButton;
    private ProgressBar progressBar;
    private TextView progressText;
    private TextView statusText;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final String processId =
            "youtube-downloader";

    private volatile boolean running = false;

    private final Function3<Float, Long, String, Unit> callback =
            new Function3<Float, Long, String, Unit>() {

                @Override
                public Unit invoke(
                        Float progress,
                        Long etaInSeconds,
                        String line
                ) {

                    runOnUiThread(() -> {

                        int value = Math.max(
                                0,
                                Math.min(
                                        100,
                                        Math.round(progress)
                                )
                        );

                        progressBar.setProgress(value);

                        String eta =
                                etaInSeconds >= 0
                                        ? "ETA: "
                                                + etaInSeconds
                                                + "s"
                                        : "ETA: --";

                        progressText.setText(
                                value
                                        + "%  |  "
                                        + eta
                        );

                        if (
                                line != null
                                        &&
                                !line.trim().isEmpty()
                        ) {

                            statusText.setText(
                                    line.trim()
                            );
                        }
                    });

                    return Unit.INSTANCE;
                }
            };

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );

        bindViews();

        initializeEngines();

        handleIncomingShareIntent(
                getIntent()
        );
    }

    private void bindViews() {

        urlInput =
                findViewById(
                        R.id.urlInput
                );

        videoMode =
                findViewById(
                        R.id.videoMode
                );

        mp3Mode =
                findViewById(
                        R.id.mp3Mode
                );

        downloadButton =
                findViewById(
                        R.id.downloadButton
                );

        cancelButton =
                findViewById(
                        R.id.cancelButton
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        progressText =
                findViewById(
                        R.id.progressText
                );

        statusText =
                findViewById(
                        R.id.statusText
                );

        downloadButton.setOnClickListener(
                v -> startDownload()
        );

        cancelButton.setOnClickListener(
                v -> cancelDownload()
        );
    }

    private void initializeEngines() {

        try {

            YoutubeDL.getInstance().init(
                    getApplicationContext()
            );

            FFmpeg.getInstance().init(
                    getApplicationContext()
            );

            statusText.setText(
                    "yt-dlp e FFmpeg inicializados."
            );

            downloadButton.setEnabled(true);

        } catch (Exception e) {

            statusText.setText(
                    "Falha ao inicializar yt-dlp/FFmpeg: "
                            + getExceptionMessage(e)
            );

            downloadButton.setEnabled(false);
        }
    }

    private void handleIncomingShareIntent(
            Intent intent
    ) {

        if (intent == null) {
            return;
        }

        if (
                Intent.ACTION_SEND.equals(
                        intent.getAction()
                )
                        &&
                "text/plain".equals(
                        intent.getType()
                )
        ) {

            String sharedText =
                    intent.getStringExtra(
                            Intent.EXTRA_TEXT
                    );

            if (
                    sharedText != null
                            &&
                    !sharedText.trim().isEmpty()
            ) {

                urlInput.setText(
                        sharedText.trim()
                );

                urlInput.setSelection(
                        urlInput.length()
                );
            }
        }
    }

    private void startDownload() {

        String url =
                urlInput
                        .getText()
                        .toString()
                        .trim();

        if (url.isEmpty()) {

            urlInput.setError(
                    "Informe uma URL."
            );

            return;
        }

        if (running) {
            return;
        }

        running = true;

        downloadButton.setEnabled(
                false
        );

        cancelButton.setEnabled(
                true
        );

        progressBar.setProgress(0);

        progressText.setText(
                "Iniciando..."
        );

        statusText.setText(
                "Processando URL..."
        );

        final boolean mp3 =
                mp3Mode.isChecked();

        executor.execute(() -> {

            try {

                File externalFilesDir =
                        getExternalFilesDir(
                                Environment.DIRECTORY_DOWNLOADS
                        );

                if (externalFilesDir == null) {

                    throw new Exception(
                            "Diretório de armazenamento indisponível."
                    );
                }

                File workDir =
                        new File(
                                externalFilesDir,
                                "YouTubeDownloader"
                        );

                if (
                        !workDir.exists()
                                &&
                        !workDir.mkdirs()
                ) {

                    throw new Exception(
                            "Não foi possível criar a pasta de trabalho."
                    );
                }

                String outputTemplate =
                        new File(
                                workDir,
                                "%(playlist_index)s - %(title)s.%(ext)s"
                        ).getAbsolutePath();

                YoutubeDLRequest request =
                        new YoutubeDLRequest(
                                url
                        );

                request.addOption(
                        "-o",
                        outputTemplate
                );

                request.addOption(
                        "--no-mtime"
                );

                request.addOption(
                        "--no-playlist-reverse"
                );

                request.addOption(
                        "--newline"
                );

                if (mp3) {

                    request.addOption(
                            "-f",
                            "bestaudio/best"
                    );

                    request.addOption(
                            "--extract-audio"
                    );

                    request.addOption(
                            "--audio-format",
                            "mp3"
                    );

                    request.addOption(
                            "--audio-quality",
                            "192K"
                    );

                } else {

                    request.addOption(
                            "-f",
                            "bestvideo[height<=1080][ext=mp4]+"
                                    + "bestaudio[ext=m4a]/"
                                    + "best[height<=1080][ext=mp4]/"
                                    + "best[height<=1080]"
                    );

                    request.addOption(
                            "--merge-output-format",
                            "mp4"
                    );
                }

                YoutubeDL.getInstance().execute(
                        request,
                        processId,
                        callback
                );

                copyCompletedFilesToDownloads(
                        workDir,
                        mp3
                );

                runOnUiThread(() -> {

                    progressBar.setProgress(
                            100
                    );

                    progressText.setText(
                            "100%  |  Concluído"
                    );

                    statusText.setText(
                            "Download concluído em Download/YouTube."
                    );
                });

            } catch (Exception e) {

                String message =
                        getExceptionMessage(e);

                runOnUiThread(() -> {

                    progressText.setText(
                            "Falha"
                    );

                    statusText.setText(
                            "Erro: " + message
                    );
                });

            } finally {

                running = false;

                runOnUiThread(() -> {

                    downloadButton.setEnabled(
                            true
                    );

                    cancelButton.setEnabled(
                            false
                    );
                });
            }
        });
    }

    private void cancelDownload() {

        if (!running) {
            return;
        }

        try {

            YoutubeDL
                    .getInstance()
                    .destroyProcessById(
                            processId
                    );

            statusText.setText(
                    "Cancelamento solicitado."
            );

        } catch (Exception e) {

            statusText.setText(
                    "Erro ao cancelar: "
                            + getExceptionMessage(e)
            );
        }
    }

    private void copyCompletedFilesToDownloads(
            File workDir,
            boolean mp3
    ) throws Exception {

        File[] files =
                workDir.listFiles();

        if (files == null) {
            return;
        }

        String wantedExtension =
                mp3
                        ? ".mp3"
                        : ".mp4";

        for (File file : files) {

            if (!file.isFile()) {
                continue;
            }

            String name =
                    file.getName()
                            .toLowerCase();

            if (
                    !name.endsWith(
                            wantedExtension
                    )
            ) {
                continue;
            }

            saveToMediaStore(
                    file,
                    file.getName(),
                    mp3
            );

            if (!file.delete()) {
                // Cópia já concluída.
            }
        }
    }

    private void saveToMediaStore(
            File source,
            String displayName,
            boolean audio
    ) throws Exception {

        ContentResolver resolver =
                getContentResolver();

        ContentValues values =
                new ContentValues();

        values.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                displayName
        );

        values.put(
                MediaStore.MediaColumns.MIME_TYPE,
                audio
                        ? "audio/mpeg"
                        : "video/mp4"
        );

        values.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
                        + "/YouTube"
        );

        values.put(
                MediaStore.MediaColumns.IS_PENDING,
                1
        );

        Uri collection =
                MediaStore.Downloads
                        .EXTERNAL_CONTENT_URI;

        Uri uri =
                resolver.insert(
                        collection,
                        values
                );

        if (uri == null) {

            throw new Exception(
                    "Não foi possível criar o arquivo em Download/YouTube."
            );
        }

        try {

            OutputStream output =
                    resolver.openOutputStream(
                            uri
                    );

            if (output == null) {

                throw new Exception(
                        "Não foi possível abrir o destino."
                );
            }

            try (
                    FileInputStream input =
                            new FileInputStream(
                                    source
                            )
            ) {

                byte[] buffer =
                        new byte[1024 * 1024];

                int read;

                while (
                        (read =
                                input.read(buffer))
                                != -1
                ) {

                    output.write(
                            buffer,
                            0,
                            read
                    );
                }

                output.flush();
            }

            output.close();

            ContentValues done =
                    new ContentValues();

            done.put(
                    MediaStore.MediaColumns.IS_PENDING,
                    0
            );

            resolver.update(
                    uri,
                    done,
                    null,
                    null
            );

        } catch (Exception e) {

            resolver.delete(
                    uri,
                    null,
                    null
            );

            throw e;
        }
    }

    private String getExceptionMessage(
            Exception e
    ) {

        if (e == null) {
            return "Erro desconhecido.";
        }

        String message =
                e.getMessage();

        if (
                message != null
                        &&
                !message.trim().isEmpty()
        ) {

            return message;
        }

        return e.toString();
    }

    @Override
    protected void onNewIntent(
            Intent intent
    ) {

        super.onNewIntent(
                intent
        );

        setIntent(
                intent
        );

        handleIncomingShareIntent(
                intent
        );
    }

    @Override
    protected void onDestroy() {

        if (running) {

            try {

                YoutubeDL
                        .getInstance()
                        .destroyProcessById(
                                processId
                        );

            } catch (Exception ignored) {
            }
        }

        executor.shutdownNow();

        super.onDestroy();
    }
}
