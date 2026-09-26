# YouTube Downloader Android

Versão inicial: **0.1.0**

Aplicativo Android experimental para o Moto G53 5G / Android 14.

## Funções

- URL individual.
- URL de playlist.
- Vídeo MP4 até 1080p quando disponível.
- Áudio MP3 em 192 kbps.
- Barra de progresso.
- Cancelamento.
- Recebe URL pelo menu Compartilhar do Android.
- Salva os arquivos em `Download/YouTube`.

## Ambiente-alvo

- Moto G53 5G
- Android 14
- ABI inicial: `arm64-v8a`
- minSdk: 24
- targetSdk: 35
- compileSdk: 36

O número de build e o kernel usados no teste inicial do projeto foram:

```text
Android: 14
Build: U1TPS34.29-83-7-3-10-1-6
Kernel: 5.4.295-MOTO-17290-GE598945BF79F
```

## Dependências

O projeto usa `youtubedl-android` 0.18.1, que fornece yt-dlp/Python embutidos, e o módulo FFmpeg correspondente.

A documentação do projeto informa que yt-dlp e Python são embutidos e que o módulo FFmpeg deve ser incluído para recursos como `--extract-audio`.

## Compilação local

Requisitos:

- Android Studio recente.
- JDK 17.
- Android SDK.
- SDK Platform 36.
- Build Tools compatíveis.
- Internet para baixar as dependências Maven.

Na raiz:

```bash
gradle assembleDebug
```

O APK será gerado em:

```text
app/build/outputs/apk/debug/
```

## GitHub Actions

O repositório inclui um workflow em:

```text
.github/workflows/build-apk.yml
```

Ele gera automaticamente o APK de debug e publica o arquivo como artifact da execução.

## Instalação

O APK debug pode ser instalado no Android 14 para teste.

Se o Android bloquear a instalação, habilite a permissão de instalação de aplicativos da fonte utilizada para abrir o APK.

## Uso

### Vídeo

Cole:

```text
https://www.youtube.com/watch?v=...
```

Selecione:

```text
MP4 até 1080p
```

e toque em:

```text
BAIXAR
```

### MP3

Selecione:

```text
MP3 192 kbps
```

O aplicativo baixa o melhor áudio disponível e utiliza o FFmpeg embutido para gerar MP3.

### Playlist

Cole uma URL de playlist. O yt-dlp processará os itens da playlist.

## Compartilhar

No YouTube:

```text
Compartilhar
→ YouTube Downloader
```

A URL será preenchida automaticamente.

## Limitações da v0.1.0

- Download é executado enquanto o processo do aplicativo estiver vivo; ainda não há Foreground Service.
- Não há fila visual de múltiplas tarefas.
- Não há histórico de downloads dentro do aplicativo.
- Não há autenticação/cookies.
- O APK foi direcionado inicialmente para `arm64-v8a`.
- A aplicação deve ser testada no Moto G53 antes de considerar esta versão estável.

## Licenças

Este projeto depende de componentes de terceiros. Consulte os respectivos arquivos de licença e avisos dos componentes antes de redistribuir o APK.

A biblioteca `youtubedl-android` é licenciada sob GPL-3.0.

O projeto próprio deste repositório é distribuído sob GPL-3.0.

## Referências

- https://github.com/yausername/youtubedl-android
- https://github.com/yt-dlp/yt-dlp
