<h2>Digital Innovation: Bootcamp GFT Start #2 Java - Desenvolvimento de uma API REST de gerenciamento de estoques de produtos de beleza</h2>

Seguino a necessidade de um controle de estoque para vendas de produtos de beleza Natura, foi desenvolvida a API REST com as seguinte funcionalidades:

* Cadastro de Produtos segundo sua categoria especifica; 
* Cadastro de Estoque mínimo por produto;
* Busca por nome de produto;
* Busca por id de produto; 
* Listagem geral dos produtos cadastrados;
* Exclusão de produto por id;
* Teste unitários

Para executar o projeto no terminal, digite o seguinte comando:

```shell script
mvn spring-boot:run 
```

Para executar a suíte de testes desenvolvida, basta executar o seguinte comando:

```shell script
mvn clean test
```

Após executar o comando acima, basta apenas abrir o seguinte endereço e visualizar a execução do projeto:

```
http://localhost:8080/api/v1/products
```



