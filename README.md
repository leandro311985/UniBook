# UniBook

Aplicativo multiplataforma para registrar e acompanhar atendimentos pastorais. A interface e a maior parte da lógica são compartilhadas entre Android e iOS usando Kotlin Multiplatform e Compose Multiplatform.

## Estado atual

- O app tem três áreas principais: lista de atendimentos, cadastro/edição e detalhes.
- Os atendimentos são gravados localmente primeiro. A sincronização com a API é tentada em seguida; itens que não sincronizam permanecem pendentes para novas tentativas.
- Os dados locais são persistidos como JSON nas preferências da plataforma: `SharedPreferences` no Android e `NSUserDefaults` no iOS. Não há banco de dados SQL neste momento.
- A aba **Perfil** permite salvar nickname e função/ministério no próprio dispositivo, com persistência via `SharedPreferences` no Android e `NSUserDefaults` no iOS.
- A data inicial do cadastro e o atalho **Hoje** usam a data local do dispositivo. Ao editar, a data existente é mantida.
- O agendamento de notificações de lembrete está implementado no Android com WorkManager. O código iOS atual não possui um fluxo equivalente de notificações.
- A API está configurada no código para `https://unibookback.onrender.com`.

## Tecnologias

- Kotlin Multiplatform e Compose Multiplatform
- Kotlin Coroutines e StateFlow
- Ktor Client com serialização kotlinx
- Koin para injeção de dependências
- WorkManager e notificações locais no Android
- SwiftUI como ponto de entrada do app iOS, hospedando a interface compartilhada

As versões dos plugins e bibliotecas ficam em `gradle/libs.versions.toml`.

## Requisitos para desenvolvimento

- JDK 21
- Android SDK com a plataforma indicada por `android-compileSdk` em `gradle/libs.versions.toml` (atualmente API 37)
- Xcode para compilar e executar o app iOS

O projeto inclui o Gradle Wrapper, portanto não é necessário instalar Gradle globalmente. No macOS/Linux, use `./gradlew`; no Windows, use `gradlew.bat`.

## Executar e validar

```bash
# Compilar o app Android
./gradlew :androidApp:assembleDebug

# Compilar as fontes compartilhadas para Android
./gradlew :shared:compileAndroidMain

# Executar os testes compartilhados na JVM do host Android
./gradlew :shared:testAndroidHostTest
```

Para iOS, abra `iosApp/iosApp.xcodeproj` no Xcode e execute o scheme `iosApp` em um simulador ou dispositivo compatível.

O workflow de CI em `.github/workflows/ci.yml` executa `:shared:testAndroidHostTest` e `:androidApp:assembleDebug`.

## Estrutura do repositório

```text
androidApp/                  Aplicativo Android e integração com WorkManager/notificações
iosApp/                      Projeto Xcode e entry point SwiftUI
shared/src/commonMain/       UI, domínio, dados e lógica compartilhada
shared/src/androidMain/      Implementações específicas do Android
shared/src/iosMain/          Implementações específicas do iOS
shared/src/commonTest/       Testes compartilhados
gradle/libs.versions.toml    Catálogo de versões e dependências
```

### Fluxo principal

`App.kt` controla a navegação entre lista, formulário e detalhes. As telas principais ficam em `attendance/SpiritualCare.kt` e `home/`. A implementação de atendimentos segue este fluxo:

```text
Compose UI
  -> AttendanceViewModel
  -> use cases (Create / Update / Get)
  -> AttendanceRepository
  -> API Ktor e armazenamento local
```

Os bindings de dependência estão em `di/AttendanceModule.kt` e `di/DeviceModule.kt`; cada plataforma fornece suas implementações `actual`.

### Pastas importantes

- `attendance/domain/`: modelos, contrato do repositório e casos de uso.
- `attendance/presentation/`: estado e ViewModel do formulário.
- `attendance/data/remote/`: cliente da API, configuração da URL e DTOs.
- `attendance/data/local/`: cache, estado local e interface de persistência.
- `home/`: lista, detalhes e configurações do perfil da igreja.
- `core/device/`: identificador do dispositivo enviado à API.

## Persistência e sincronização

`AttendanceLocalDataSource` mantém a lista em `StateFlow` e persiste os registros serializados por `AttendanceStorageRepository`. As implementações de plataforma estão em:

- Android: `shared/src/androidMain/.../AndroidAttendanceStorageRepository.kt`
- iOS: `shared/src/iosMain/.../IosAttendanceStorageRepository.kt`

O `AttendanceRepositoryImpl` salva novos registros com estado `PENDING_SYNC` antes de tentar enviá-los. A lista observa o estado local e pode ser exibida sem rede. A busca remota usa cache em memória por até cinco minutos; a sincronização pendente pode ocorrer ao buscar novamente.

## API

A URL base atual está em `AttendanceApiConfig.android.kt` e `AttendanceApiConfig.ios.kt`. `AttendanceApiService` envia o identificador do dispositivo no cabeçalho `X-Device-Id` e usa estas rotas:

| Operação | Rota |
|---|---|
| Listar atendimentos | `GET /api/atendimentos` |
| Criar atendimento | `POST /api/atendimentos` |
| Editar atendimento | `PUT /api/atendimentos/{id}` |
| Buscar lembretes | `GET /api/atendimentos/lembretes` |
| Confirmar lembrete | `POST /api/atendimentos/{id}/lembrete/concluir` |

O envio de um atendimento com foto tenta multipart; o caminho JSON também está disponível. O formato esperado para `dataAtendimento` é `AAAA-MM-DD`.

## Lembretes no Android

`MainActivity` agenda o trabalho periódico em `ReminderScheduler` e solicita a permissão de notificações no Android 13 ou superior. O `ReminderWorker` consulta a API periodicamente (WorkManager usa intervalos aproximados de pelo menos 15 minutos) e enfileira notificações locais com 30 minutos de atraso; depois que a notificação é exibida, `DelayedNotificationWorker` confirma o lembrete na API. As implementações estão em `androidApp/src/main/kotlin/com/leandro/unibook/worker/`.

Esse fluxo é polling da API seguido de notificação local no Android, não push remoto por Firebase/APNs. Push remoto exige integração de tokens e serviço de envio no backend. O app iOS ainda não possui implementação de notificações.

## Observações para quem está chegando

- Ao adicionar uma API específica de plataforma, mantenha o `expect` em `commonMain` e os `actual` nos source sets de cada plataforma, no mesmo package.
- Prefira adicionar novos valores de versão e bibliotecas ao catálogo `gradle/libs.versions.toml`.
- A URL do backend está atualmente definida nos arquivos Kotlin por plataforma; não há configuração de ambiente separada no projeto.
- O workflow CI instala Android SDK API 35/build-tools 35.0.0, enquanto o catálogo do projeto declara `compileSdk` API 37. Se a compilação no CI começar a falhar por falta da plataforma 37, alinhe a instalação do workflow com `android-compileSdk`.
