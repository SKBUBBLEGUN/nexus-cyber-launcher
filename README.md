# NEXUS Cyber Launcher

Launcher Android em Kotlin e Jetpack Compose com interface cyberpunk responsiva e personalizável.

## Reformulação

- Oito paletas de cores selecionáveis: Redline, Cyber Cyan, Violet, Toxic Green, Gold, Electric Blue, Hot Pink e Ice.
- A paleta escolhida é salva localmente e reaplicada ao abrir o launcher.
- Widgets configuráveis para bateria, rede e data; preferências preservadas.
- Relógio atualizado em tempo real.
- Telas Base, Apps, Alertas e Configurações com navegação inferior.
- Lista de aplicativos instalados com pesquisa e inicialização.
- Áreas de conteúdo roláveis para telas menores.
- Workflow de CI para validar o ZIP de origem, executar testes, compilar o APK e publicar o artefato com checksum.

## Compilar localmente

Requisitos: JDK 17, Android SDK e Gradle 8.11.1.

```bash
gradle testDebugUnitTest --no-daemon
gradle assembleDebug --no-daemon
```

O APK de depuração é gerado em `app/build/outputs/apk/debug/`.

## GitHub Actions

O workflow **NEXUS Cyber Launcher - Build and Verify** pode ser iniciado manualmente em Actions ou executado automaticamente em alterações relevantes. Ao concluir com sucesso, o artefato `NEXUS-Cyber-Launcher-debug` contém o APK e seu SHA-256.

> O workflow valida a compilação e a integridade do APK; um teste visual e de interação em um dispositivo Android físico ainda deve ser realizado no aparelho.
