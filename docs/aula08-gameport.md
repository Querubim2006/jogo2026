# Aula 08 aplicada ao GamePort

## Tradução do domínio

| Aula | GamePort |
|---|---|
| Grupo de produto | Gênero de jogo (`GeneroJogo`) |
| Produto | Jogo (`Jogo`) |
| Fornecedor | Desenvolvedora (`Desenvolvedora`) |
| Saldo e estoque mínimo | Saldo de cópias e estoque mínimo do jogo |

O `PUT /api/jogos/{id}` altera título, preço, data de lançamento, estoque mínimo, gênero e desenvolvedora. Código de barras e saldo ficam fora do DTO; status muda pela rota própria e o saldo muda somente pelas movimentações. A desenvolvedora é opcional. O `PUT /api/generos/{id}` altera o nome do gênero.

## Contrato da API

| Operação | Rota | Comportamento |
|---|---|---|
| Pesquisar gêneros | `GET /api/generos?nome=&status=&pagina=0&tamanho=20&ordenarPor=id&direcao=ASC` | `PaginaResponse`, filtros no banco |
| Alterar gênero | `PUT /api/generos/{id}` | substitui o nome editável |
| Alterar status do gênero | `PUT /api/generos/{id}/status` | recebe `{"status":"ATIVO"}` ou `INATIVO` |
| Excluir gênero | `DELETE /api/generos/{id}` | `409` enquanto houver jogos associados; caso contrário `204` |
| Pesquisar jogos | `GET /api/jogos?titulo=&status=&generoId=&desenvolvedoraId=&abaixoEstoqueMinimo=true&pagina=0&tamanho=20&ordenarPor=id&direcao=ASC` | `PaginaResponse`, filtros no banco |
| Alterar jogo | `PUT /api/jogos/{id}` | atualiza os campos editáveis sem mudar código ou saldo |
| Alterar status do jogo | `PUT /api/jogos/{id}/status` | recebe status `ATIVO` ou `INATIVO` |
| Entrada e saída | `POST /api/jogos/{id}/estoque/entradas` e `/saidas` | registra movimento; saída acima do saldo retorna `400` sem alterar o saldo |
| Excluir jogo | `DELETE /api/jogos/{id}` | `204` sem corpo ou `404` |

As pesquisas usam `Specification` no PostgreSQL, paginação inicia em zero, `tamanho` aceita de 1 a 100 e a ordenação aceita apenas campos simples definidos nos services. O contrato de página inclui `conteudo`, `pagina`, `tamanho`, `totalElementos`, `totalPaginas`, `primeira` e `ultima`. A troca de gênero atualiza os dois lados da associação dentro da transação.

`PUT` expressa a substituição dos campos editáveis e é idempotente. Estoque usa `POST` porque cada repetição registra outro movimento. Inativar preserva o registro e suas referências; excluir remove o registro e pode falhar com `409` quando há dependências.

## Migrações e rollback do estoque mínimo

O changeset `003-05-fill-estoque-minimo-jogo` atualiza `estoque_minimo` de `NULL` para `0`. Ele já consta como aplicado no histórico informado. Não foi editado: mudar um changeset aplicado altera seu checksum e pode impedir a inicialização do Liquibase.

Depois dessa atualização, um zero pode significar tanto “era `NULL`” quanto “já era zero”. O estado original não pode ser inferido apenas do valor atual. Um rollback genérico como `UPDATE jogo SET estoque_minimo = NULL WHERE estoque_minimo = 0` apagaria valores válidos. Se existir backup anterior à execução, recupere dele os valores originais; sem essa evidência, a restauração exata não é possível. Para voltar todo o banco a um ponto anterior à migração, use um backup consistente do banco feito antes da implantação, validado primeiro em uma instância isolada.

O arquivo `008-preservar-estoque-minimo-anterior.yaml` foi removido do master porque estava pendente no histórico informado e só gravava os IDs que atualmente têm zero. Não guardava os valores anteriores e seu rollback apenas apagava a tabela auxiliar; aplicá-lo não tornaria o changeset 003-05 reversível. A ordem intencional de `006-compatibilidade-para-titulo` antes de `005-check-titulo-jogo` foi mantida.

Para uma futura alteração de dados que exija reversão, a migração deve capturar os valores originais e a distinção entre `NULL` e zero antes de atualizá-los, no mesmo changeset/transação quando suportado pelo banco. O rollback deve restaurar essa cópia antes de remover a tabela auxiliar. Essa técnica só funciona se executada antes da atualização, não como correção retroativa.

## Evidências

Os testes de Aula 8 cobrem movimentação válida e inválida, saldo inalterado após rejeição, status, atualização sem alteração do saldo, filtros, paginação, ordenação limitada, exclusão, conflito por gênero em uso e unicidade normalizada no PostgreSQL. A contagem esperada de 19 changesets corresponde ao histórico 001–007; o `008` pendente não deve ser aplicado.
