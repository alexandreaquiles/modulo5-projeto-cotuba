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
    
        Container(gerador, "Servico Gerador de Ebooks", "Java, Spring Boot", "Servico que eh o Motor da transformacao de livros Markdown para PDF e EPUB")
    
        ContainerDb(db, "BD Relacional", "PostgreSQL (AWS RDS)", "Armazenar dados financeiros, dos usuarios, catalogo de livros")

        ContainerDb(storage, "Armazenamento de Arquivos", "Object Storage (AWS S3)", "Capas dos livros, PDF, EPUB (binarios) ")
    }

    Rel(autor, webapp, "", "HTTPS")
    Rel(leitor, webapp, "", "HTTPS")

    Rel(webapp, api, "", "HTTPS/JSON")
    Rel(webapp, storage, "Faz download de ebooks e imagens", "HTTPS")

    Rel(api, db, "Transactional, queries", "JDBC")
    Rel(api, pagamento, "Processa pagamentos", "HTTPS/JSON")
    Rel(api, email, "Envia dados pra enviar avisos, notificacoes e recibos", "HTTPS/JSON")
    Rel(api, storage, "Salva capa dos livros e gera URLs de download", "S3 API")
    Rel(api, gerador, "Solicita geracao dos ebooks pesados", "HTTPS/JSON")

    Rel(gerador, git, "Clona o repositorio do livro", "SSH")
    Rel(gerador, storage, "Faz upload de ebooks PDF/EPUB gerados", "SSH")

    System_Ext(git, "Provedor de Git Externo", "Armazenar o codigo fonte (Markdown e imagens) dos livros")
    System_Ext(pagamento, "Gateway de Pagamentos", "Stripe - Processar pagamentos da venda (Pix e Cartao de Credito)")
    System_Ext(email, "Sistema de Email Externo", "AWS SES - Enviar recibos, notificacoes, avisos para os usuarios")
```