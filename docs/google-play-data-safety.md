# Preparação do formulário "Data safety" da Play Store

Este resumo é baseado no código Android e nas informações fornecidas pelo responsável do app. Revise as definições do formulário no Play Console e confirme o comportamento do backend e dos provedores antes de enviar a declaração.

| Categoria a revisar | O que o app trata | Finalidade principal |
|---|---|---|
| Informações pessoais: nome, e-mail, telefone e outras informações pessoais | Dados de contato e conteúdo dos atendimentos pastorais | Funcionalidade do app |
| Fotos e vídeos: fotos | Fotos anexadas a atendimentos e imagem local da congregação | Funcionalidade do app |
| IDs do dispositivo ou outros IDs | Identificador aleatório persistido no dispositivo e enviado no cabeçalho `X-Device-Id` | Funcionalidade do app |

Pontos confirmados no código:

- Os dados de perfil e atendimento são transmitidos por HTTPS para a API configurada em `unibookback.onrender.com`.
- O app não integra SDKs de anúncios ou analytics.
- O Android criptografa as preferências locais com AES-GCM e desativa o backup do app.
- O usuário pode excluir atendimentos no app. O perfil não tem exclusão no app; pedidos de exclusão devem ser encaminhados ao contato da política de privacidade.
- A retenção informada pelo responsável é de até um ano.
- Não há criação de conta, dados financeiros, contatos do aparelho ou localização no fluxo encontrado.

Antes de concluir o formulário, confirme com quem opera a API se há dados adicionais em logs, backups ou serviços de hospedagem, como os dados são excluídos após um ano e se algum provedor recebe ou processa os dados. A classificação de coleta/compartilhamento deve refletir esses fluxos reais, não apenas os campos do app.
