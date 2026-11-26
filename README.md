#Tema do projeto

##Identificação

Nome do projeto: GamePort
Tema: Jogo 2026
Objetivo em uma frase: Permitir o cadastro e a pesquisa de jogos, seus valores, gêneros e plataformas compatíveis.

##Entidade de classificação

Nome no singular: Gênero
Nome no plural: Gêneros
Exemplo 1: Ação/Aventura
Exemplo 2: Corrida

##Entidade principal

Nome no singular: Jogo
Nome no plural: Jogos
Código único: id_jogo
Descrição: Sinopse
Medida quantitativa: Nº de cópias
Valor monetário: Preço do jogo
Data relevante: Data de lançamento
Status: Em estoque/Fora de estoque

##Relacionamento

Uma classificação pode possuir vários: Jogos
Cada registro principal pertence a: Um gênero

##Três exemplos de registros

1.God of War II — Ação/Aventura — R$ 99,90 — PlayStation 2
2.Forza Horizon 5 — Corrida — R$ 249,90 — Xbox Series X/S, Xbox One e PC
3.The Legend of Zelda: Breath of the Wild — Ação/Aventura — R$ 299,90 — Nintendo Switch e Wii U

## Aula 10 — Pessoas, fornecedores e vendas

O domínio usa `Pessoa` como identidade e associa os papéis de `Cliente` e `Colaborador` por composição. `Fornecedor` é separado de `Desenvolvedora`: o primeiro fornece jogos para a loja; a segunda criou o jogo.

Configure o banco `jogos2026_test` no `.env` e execute `.\mvnw.cmd test` no PowerShell. Para iniciar a API, rode `.\mvnw.cmd spring-boot:run`; a documentação fica em `http://localhost:8080/swagger-ui/index.html`.

A coleção `postman/GamePort-Aula10.postman_collection.json` conduz o fluxo de pessoas, fornecedor, jogo e venda. Ela gera CPF/CNPJ válidos matematicamente e verifica preço histórico, baixa de estoque, cancelamento e proteção do histórico.
