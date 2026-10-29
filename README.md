# Nexus

Launcher Android em Kotlin e Jetpack Compose com interface cyberpunk responsiva e personalizável. O nome exibido pelo Android é **Nexus** e o ícone oficial usa a arte de oni vermelho e branco sobre fundo preto enviada pelo usuário.

## Reformulação

- Nome do aplicativo no Android: **Nexus**.
- Ícone do aplicativo com a arte de oni vermelho/branco sobre preto, gerado para ícones legados e adaptativos do Android.
- Oito paletas de cores selecionáveis: Redline, Cyber Cyan, Violet, Toxic Green, Gold, Electric Blue, Hot Pink e Ice.
- A paleta escolhida é salva localmente e reaplicada ao abrir o launcher.
- Widgets configuráveis para bateria, rede e data; preferências preservadas.
- Relógio atualizado em tempo real.
- Telas Base, Apps, Alertas e Configurações com navegação inferior.
- Lista de aplicativos instalados com pesquisa e inicialização.
- Áreas de conteúdo roláveis para telas menores.
- Workflow de CI para validar o ZIP de origem, executar testes, compilar o APK, verificar o pacote e publicar o artefato com checksum.

## Compilar localmente

Requisitos: JDK 17, Android SDK e Gradle 8.11.1.

```bash
gradle testDebugUnitTest --no-daemon
gradle assembleDebug --no-daemon
```

O APK de depuração é gerado em `app/build/outputs/apk/debug/`.

## GitHub Actions

O workflow **Nexus - Build and Verify** pode ser iniciado manualmente em Actions ou executado automaticamente em alterações relevantes. Ao concluir com sucesso, o artefato `Nexus-Android-debug` contém o APK `Nexus-debug.apk` e seu SHA-256.

> O workflow valida a compilação e a integridade do APK; um teste visual e de interação em um dispositivo Android físico ainda deve ser realizado no aparelho.
