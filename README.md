# Teste Técnico - Tokio

API RESTful de CRUD desenvolvida com Spring Boot, Java 21 e PostgreSQL.

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Docker

## Funcionalidades

- Autenticação e autorização de usuários com JWT
- CRUD completo de usuários
- CRUD completo de endereços vinculados a usuários
- Validação de dados
- Paginação e ordenação de resultados
- Busca por termos em usuários e endereços

## Pré-requisitos

Para executar este projeto, você precisa ter instalado:

- [Docker](https://www.docker.com/products/docker-desktop/) (v20.10+)
- [Docker Compose](https://docs.docker.com/compose/install/) (já incluído no Docker Desktop)

> **Observação**: É possível executar o projeto diretamente no seu computador sem Docker, porém as configurações serão mais complexas e demoradas, exigindo instalação e configuração manual do Java 21, PostgreSQL e variáveis de ambiente.

## Executando com Docker

Todo o ambiente necessário (aplicação e banco de dados PostgreSQL) será configurado automaticamente com Docker:

### Usando os scripts de execução:

#### No Linux/Mac:
```bash
# Dê permissão de execução ao script
chmod +x run-docker.sh

# Execute o script
./run-docker.sh
```

#### No Windows:
```bash
# Execute o script batch
run-docker.bat
```
Ou simplesmente dê um duplo clique no arquivo `run-docker.bat`.

### Manualmente com Docker Compose:
```bash
# Clone o repositório
git clone https://github.com/LeonardoGomesCBP/tokio-backend-java
cd crud

# Execute com Docker Compose
docker-compose up
```

A aplicação estará disponível em http://localhost:8080

O PostgreSQL será iniciado automaticamente na porta 5432 com:
- Banco de dados: crudapp
- Usuário: postgres
- Senha: postgres

## Usuário Admin Inicial

Ao iniciar a aplicação pela primeira vez, um usuário administrador é criado automaticamente:

- Email: admin@example.com
- Senha: admin123

## Coleção Postman

O projeto inclui uma coleção Postman para facilitar os testes manuais da API. A coleção contém todos os endpoints organizados por funcionalidade e implementa gerenciamento automático de tokens JWT.

### Como usar a coleção:

1. **Importe a coleção**:
   - O arquivo `Teste_Tecnico_Tokio.postman_collection.json` está na raiz do projeto
   - No Postman: Importe → Arquivo → selecione o arquivo da coleção

2. **Fluxo de testes**:
   - Execute primeiro "Login Admin" para obter token de administrador
   - Execute "Signup User" para criar um usuário comum
   - Execute "Login User" para obter token de usuário comum

3. **Variáveis automáticas**:
   - A coleção gerencia automaticamente tokens JWT para usuário e admin
   - Os IDs de usuário e endereço são armazenados como variáveis após criação
   - O `baseUrl` já está configurado como `http://localhost:8080`
