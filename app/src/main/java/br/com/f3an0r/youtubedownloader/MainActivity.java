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

    } catch (Exception e) {

        statusText.setText(
                "Falha ao inicializar yt-dlp/FFmpeg: "
                        + e.getMessage()
        );
        downloadButton.setEnabled(false);
    }
}
