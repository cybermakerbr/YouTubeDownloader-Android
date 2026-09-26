# Roteiro de teste — Moto G53 5G / Android 14

## Identificação

```text
Modelo: Moto G53 5G
Android: 14
Build: U1TPS34.29-83-7-3-10-1-6
Kernel: 5.4.295-MOTO-17290-GE598945BF79F
```

## Teste 1 — URL de vídeo

1. Abrir o aplicativo.
2. Colar uma URL pública de vídeo.
3. Selecionar MP4.
4. Pressionar BAIXAR.
5. Confirmar progresso.
6. Confirmar arquivo em:

```text
Download/YouTube/
```

## Teste 2 — MP3

1. Colar uma URL pública.
2. Selecionar MP3 192 kbps.
3. Pressionar BAIXAR.
4. Confirmar que o resultado é `.mp3`.
5. Reproduzir o arquivo.

## Teste 3 — Playlist

1. Colar uma URL de playlist.
2. Selecionar MP4.
3. Baixar.
4. Confirmar todos os itens.
5. Repetir em MP3.

## Teste 4 — Compartilhar

No YouTube:

```text
Compartilhar → YouTube Downloader
```

Confirmar preenchimento automático da URL.

## Teste 5 — Cancelamento

1. Iniciar download.
2. Pressionar CANCELAR.
3. Confirmar encerramento do processo.

## Resultado esperado

Registrar:

```text
MP4: PASS/FAIL
MP3: PASS/FAIL
Playlist MP4: PASS/FAIL
Playlist MP3: PASS/FAIL
Compartilhamento: PASS/FAIL
Cancelamento: PASS/FAIL
```

Também registrar:

- tamanho do arquivo;
- resolução do MP4;
- duração;
- codec;
- mensagem de erro, se houver.
