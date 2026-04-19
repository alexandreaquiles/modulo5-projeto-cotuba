# ADR 003: Resiliência e Idempotência para o Fluxo de Pagamentos

## Status

Em elaboracao.

## Contexto

Durante o lançamento exclusivo do livro do autor Estavao Rei, o Cotubify enfrentou um pico de 100 mil requisições de compra simultâneas. Embora nossa arquitetura interna (API, Fila, Cache, DB) tenha suportado a carga, nossa dependência externa (o Gateway de Pagamentos) degradou drasticamente, levando até 40 segundos para responder antes de sair do ar completamente.

Como o nosso fluxo era síncrono, as threads da API Principal ficaram presas esperando a resposta do parceiro, causando um colapso em cascata (Thread Exhaustion). Para piorar, a ausência de resposta fez com que os usuários clicassem múltiplas vezes no botão "Comprar". Quando o Gateway voltou, processou todas essas requisições como compras independentes, gerando múltiplas cobranças no cartão de crédito do mesmo usuário para o mesmo e-book. 

## Opções Consideradas

1. **Circuit Breaker com Falha Rápida (Síncrono):**

2. **Processamento Assíncrono (Fila de Pagamentos):**

3. **Idempotency Key (Chave de Idempotência):**

## Decisão

Decidimos implementar uma **Solução Híbrida**:

1. **Idempotent Receiver (Proteção contra duplicidade):**
2. **Desacoplamento Assíncrono (Proteção contra lentidão):**
3. **Worker com Circuit Breaker & Retry:**

## Consequências

### Positivas

* **Prevenção Total de Duplicidade:** 
* **Fim das Falhas em Cascata:**
* **Recuperação  no retorno do Gateway de Pagamentos:**


### Negativas

* **Mudança Profunda de UX (User Experience):**
* **Complexidade Transacional:** 
