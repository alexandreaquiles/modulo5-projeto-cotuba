# Arquitetura Cotubify

O Cotubify eh uma plataforma de autopublicacao e venda de ebooks.

O sistema permite autores tecnicos conectarem repositorio Git com arquivos no formato Markdown para geracao automatizada de ebooks nos formatos PDF e EPUB. Os autores tambem tem um painel de vendas.

Permite tambem que os leitores acessem uma loja online para navegacao, compra e download das obras. Os leitores recebem recibos e notificacoes via email.

## Diagrama do Contexto (C4 Model)

```mermaid
C4Context
    title Cotubify, uma plataforma de autopublicacao de books

    Person(autor, "Autor", "")
    Person(leitor, "Leitor", "")

    System(cotubify, "Cotubify", "Gerenciar venda de ebooks, geracao e publicacao de ebooks em PDF e EPUB")

    Rel(autor, cotubify, "Configura conta, publica livros, verifica painel de vendas, solicita saque")
    Rel(leitor, cotubify, "Navega na loja, faz compras e baixa ebooks")

    Rel(cotubify, git, "Clona repositorio para obter o codigo fonte do livro")
    Rel(cotubify, pagamento, "Envia e recebe cobrancas financeiras")
    Rel(cotubify, email, "Envia emails")

    System_Ext(git, "Provedor de Git Externo", "Armazenar o codigo fonte (Markdown e imagens) dos livros")
    System_Ext(pagamento, "Gateway de Pagamentos", "Processar pagamentos da venda (Pix e Cartao de Credito)")
    System_Ext(email, "Sistema de Email Externo", "Enviar recibos, notificacoes, avisos para os usuarios")
```

## Diagrama de Containers (C4 Model)

```mermaid
C4Container
    title Diagrama de Container - Cotubify

    Person(autor, "Autor", "")
    Person(leitor, "Leitor", "")

    System_Boundary(cotubify, "Cotubify", "Gerenciar venda de ebooks, geracao e publicacao de ebooks em PDF e EPUB") {
        Container(webapp, "Frontend", "", "UI para os autores e leitores")
        Container(api, "API Principal", "Java, Spring Boot", "Regras de negocio, catalogo de vendas, seguranca, financeiro")
    
        Container(gerador, "Servico Gerador de Ebooks (Worker)", "Java, Spring Boot", "Servico que eh o Motor da transformacao de livros Markdown para PDF e EPUB")

        Container(worker_pagamento, "Servico Processamento Pagamento (Worker)", "Java, Spring Boot", "Servico que integra com o Gateway de Pagamentos")

        ContainerDb(db, "BD Relacional", "PostgreSQL (AWS RDS)", "Armazenar dados financeiros, dos usuarios, catalogo de livros")

        ContainerQueue(broker, "Message Broker", "AWS SQS", "Fila de mensagens assíncronas para desacoplar tarefas pesadas.")

        ContainerDb(cache, "Cache em Memória", "Redis (AWS Elasticache)", "Armazena o catálogo de livros mais vendidos (Top 100) para leitura ultrarrápida.")

        ContainerDb(storage, "Armazenamento de Arquivos", "Object Storage (AWS S3)", "Capas dos livros, PDF, EPUB (binarios) ")
    }

    Rel(autor, webapp, "", "HTTPS")
    Rel(leitor, webapp, "", "HTTPS")

    Rel(webapp, api, "", "HTTPS/JSON")
    Rel(webapp, storage, "Faz download de ebooks e imagens", "HTTPS")

    Rel(api, cache, "Lê/Escreve catálogo em cache (Cache-Aside)", "TCP")
    Rel(api, db, "Transactional, queries", "JDBC")
    Rel(api, pagamento, "Processa pagamentos", "HTTPS/JSON")
    Rel(api, email, "Envia dados pra enviar avisos, notificacoes e recibos", "HTTPS/JSON")
    Rel(api, storage, "Salva capa dos livros e gera URLs de download", "S3 API")
    Rel(api, broker, "Solicita geracao dos ebooks pesados e publica eventos de pagamento", "HTTP")

    Rel(gerador, git, "Clona o repositorio do livro", "SSH")
    Rel(gerador, storage, "Faz upload de ebooks PDF/EPUB gerados", "SSH")

    Rel(worker_pagamento, pagamento, "Processa a transacao de pagamento integrando com o Gateway", "HTTPS/JSON")

    Rel(gerador, broker, "GeracaoEbookCommand vai ser consumido pelo Gerador", "HTTP")
    Rel(worker_pagamento, broker, "PagamentoEvent vai ser consumido pelo Worker de Pagamento", "HTTP")

    System_Ext(git, "Provedor de Git Externo", "Armazenar o codigo fonte (Markdown e imagens) dos livros")
    System_Ext(pagamento, "Gateway de Pagamentos", "Stripe - Processar pagamentos da venda (Pix e Cartao de Credito)")
    System_Ext(email, "Sistema de Email Externo", "AWS SES - Enviar recibos, notificacoes, avisos para os usuarios")
```

## Decisoes Arquiteturais (ADRs)

- [ADR 001: Geracao de ebooks via Mensageria Assincrona](adr/adr-001-geracao-ebooks-assincrona.md)
- [ADR 002: Introdução de Cache em Memória para o Catálogo de E-books](adr/adr-002-cache-catalogo.md)
- [ADR 003: Resiliência e Idempotência para o Fluxo de Pagamentos](adr/adr-003-resiliencia-pagamentos.md)

## Diagrama de Sequência

```mermaid
sequenceDiagram
    actor U as Usuário
    participant F as Frontend (Web)
    participant API as API Principal
    participant RD as Redis
    participant MQ as Message Broker (Fila)
    participant W as Worker de Pagamentos
    participant GW as Gateway Externo

    U->>F: Clica em "Comprar" (1ª vez)
    F->>API: POST /checkout (Header: Idempotency-Key=123-ABC)
    API->>RD: Verifica chave '123-ABC' (Lock)
    RD-->>API: Não encontrada (Nova compra)
    API->>RD: Insere Pedido (Status: PENDENTE, Chave: 123-ABC)
    API->>MQ: Publica evento "Processar_Pagamento"
    API-->>F: HTTP 202 Accepted (Status: Processando)
    F-->>U: Exibe tela de "Aguarde..."

    U->>F: Clica em "Comprar" (2ª, 3ª, 4ª vez...)
    F->>API: POST /checkout (Header: Idempotency-Key=123-ABC)
    API->>RD: Verifica chave '123-ABC'
    RD-->>API: Encontrada! (Pedido já existe)
    API-->>F: HTTP 200 OK (Retorna o status PENDENTE)

    MQ-->>W: Worker consome "Processar_Pagamento"
    W->>GW: POST /charge (Comunicação Síncrona Externa)
    GW-->>W: HTTP 503 / Timeout Exception

    W->>MQ: NACK / Devolve mensagem para a Fila (Retry com Backoff)
```