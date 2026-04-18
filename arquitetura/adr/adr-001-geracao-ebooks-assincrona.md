# ADR 001: Geração de E-books via Mensageria Assíncrona

## Status
Aceito

## Contexto
O sucesso do Cotubify trouxe um aumento massivo de tráfego. Atualmente, quando um autor clica em "Publicar", a API Principal faz uma chamada HTTP síncrona para o Serviço Gerador de E-books, aguardando a conversão do Markdown para PDF e EPUB. Como esse processo é intensivo em CPU e leva de 15 a 20 segundos, as *threads* da nossa API Principal ficam bloqueadas aguardando a resposta. 

Durante horários de pico, o pool de conexões da API se esgota rapidamente, causando lentidão generalizada e *timeouts*. Isso não apenas impede a publicação de novos livros, mas também derruba a plataforma inteira, impedindo que os leitores naveguem e comprem no catálogo.

## Opções Consideradas

1. **Manter HTTP Síncrono com Escalabilidade Horizontal:** Adicionar mais servidores da API Principal e do Gerador. 
   * *Problema:* Apenas adia o gargalo. As threads da API continuariam sendo desperdiçadas aguardando processamento externo.
2. **Mensageria com Fila Assíncrona (Message Broker como RabbitMQ ou AWS SQS):** A API Principal atua como *Publisher*, enviando um evento/comando (ex: `GerarEbookCommand`) para uma fila. A API responde imediatamente ao usuário ("Processamento Iniciado"). O Serviço Gerador passa a atuar como um *Worker* (Consumidor), puxando as mensagens da fila no seu próprio ritmo.

## Decisão
Decidimos adotar a **Opção 2 (Mensageria com Fila Assíncrona)**. 

Escolhemos o AWS SQS como Message Broker.

A API Principal não fará mais chamadas HTTP diretas para o Serviço Gerador. Em vez disso, introduziremos um *Message Broker* na arquitetura. A comunicação entre a API e o Gerador passará a ser baseada em eventos/mensagens.

## Consequências

### Positivas (*Trade-offs* a favor)
* **Alta Disponibilidade e Throughput:** A API Principal não fica mais bloqueada. Ela responde em milissegundos, liberando a thread para atender milhares de leitores simultaneamente.
* **Resiliência a Picos de Carga (Buffer):** Se 1.000 autores publicarem ao mesmo tempo, as mensagens ficarão seguras na fila. O Gerador consumirá aos poucos, sem sobrecarregar a CPU ou cair.
* **Escalabilidade Independente:** Podemos adicionar mais instâncias (Workers) do Gerador para esvaziar a fila mais rápido, de forma totalmente transparente para a API Principal.

### Negativas (*Trade-offs* contra)
* **Complexidade de Infraestrutura:** Precisaremos provisionar, monitorar e manter uma nova peça de infraestrutura (o Message Broker).
* **Consistência Eventual na UI:** O autor não terá o PDF pronto imediatamente ao clicar no botão. O *Frontend* precisará ser adaptado para lidar com esse estado intermediário (ex: exibindo um status de "Processando..." e fazendo um *polling* ou usando *WebSockets* para avisar quando o arquivo for enviado para o Object Storage).
