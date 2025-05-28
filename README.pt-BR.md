# E-commerce API

API RESTful desenvolvida com Java 17 e Spring Boot para gerenciamento completo de uma plataforma de e-commerce. A aplicação oferece recursos como autenticação com JWT e OAuth2, gerenciamento de produtos, pedidos, cupons, carrinho de compras, painel administrativo e notificações em tempo real.

Para a versão original em [**Inglês**](README.md)

---

## Documentação

Para uma documentação completa e detalhada da API, incluindo **endpoints**, **autenticação**, **regras de negócio** e **como utilizá-la**, por favor, consulte nossa [**Documentação na Wiki**](https://github.com/gabrielpetry23/ecommerce-api-spring/wiki/Documentacao-Completa).

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Diagrama Entidade-Relacionamento](#diagrama-entidade-relacionamento)
- [Documentação Completa](#documentação-completa)
- [Funcionalidades Principais](#funcionalidades-principais)
- [Autenticação e Segurança](#autenticação-e-segurança)
- [Testes Automatizados](#testes-automatizados)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Como Utilizar a API](#como-utilizar-a-api)
- [Melhorias Futuras](#melhorias-futuras)
- [Autor](#autor)

---

## Sobre o Projeto

A E-commerce API foi desenvolvida com base em boas práticas de arquitetura em camadas, utilizando autenticação robusta, persistência com PostgreSQL, integração com OAuth2, WebSocket para notificações em tempo real e controle de acesso baseado em roles.

---

## Diagrama Entidade-Relacionamento

O diagrama ER pode ser visualizado na imagem abaixo (ou acessado na pasta `/docs`).

![Diagrama ER](./docs/ER%20Diagram.png)

---

## Documentação Completa

A documentação técnica detalhada, cobrindo endpoints, exemplos, regras de negócio, fluxos e muito mais, está disponível em:

[`/docs/pt-br/Documentacao Completa.pdf`](./docs/pt-br/Documentacao%20Completa.pdf)
[`/docs/en/Full Documentation.pdf`](./docs/en/Full%20Documentation.pdf)

---

## Funcionalidades Principais

- Cadastro e autenticação de usuários
- Gerenciamento de produtos, categorias e cupons
- Carrinho de compras com lembretes de abandono
- Processamento de pedidos com rastreamento simulado
- Notificações em tempo real via WebSocket
- Painel de métricas administrativas
- Integração com login social via OAuth2

---

## Autenticação e Segurança

- Autenticação com JWT (JSON Web Token)
- Login social via OAuth2
- Autorização baseada em roles: `USER`, `MANAGER`, `ADMIN`
- Validação de dados com Jakarta Bean Validation
- Senhas criptografadas com bcrypt
- Proteção contra CSRF (opcional)
- Controle de acesso com anotações @PreAuthorize

---

## Testes Automatizados

A API conta com uma suíte de testes de integração utilizando:

- Spring Boot Test
- MockMvc
- JUnit 5
- Mockito
- Spring Security Test

---

## Tecnologias Utilizadas

- Java 17+
- Spring Boot, Spring Security, Spring Data JPA, Spring WebSocket
- Hibernate, HikariCP, PostgreSQL
- OAuth2, JWT
- Swagger / OpenAPI
- MapStruct, Lombok
- Maven
- Docker, Docker Compose

---

## Como Utilizar a API

### Pré-requisitos

- Docker e Docker Compose instalados

### Clonar o repositório

```bash
git clone https://github.com/gabrielpetry23/ecommerce-api.git
cd ecommerce-api
```

### Configurar variáveis de ambiente

No arquivo `docker-compose.yml`, substitua os valores das variáveis (`CLIENT_ID`, `EMAIL_USER`, etc.) com suas credenciais ou valores de teste.

### Iniciar a aplicação

```bash
docker-compose down --volumes
docker-compose up -d --build
```

Verifique os logs com:

```bash
docker-compose logs -f
```

A aplicação será iniciada em: `http://localhost:8080`

### Acessar Swagger

Acesse o Swagger UI para explorar os endpoints:

`http://localhost:8080/swagger-ui/index.html`

---

### Rotas Principais

- Autenticação: `/users/login`, `/oauth2/login`
- Usuários: `/users`, `/users/{id}`, `/users/me`
- Produtos: `/products`, `/products/{id}`, `/products/search`
- Categorias: `/categories`, `/categories/{id}`
- Carrinhos: `/carts`, `/carts/{id}/items`, `/users/{userId}/cart`
- Pedidos: `/orders`, `/orders/{id}`, `/orders/{orderId}/tracking`
- Cupons: `/coupons`
- Notificações: `/users/{userId}/notifications`
- Endereços: `/users/{userId}/addresses`
- Métodos de Pagamento: `/users/{userId}/payment-methods`
- Métricas: `/dashboard/metrics`

Usuários de teste e fluxo de autenticação completo estão descritos na documentação da wiki.

---

## Melhorias Futuras

- Adição de testes unitários
- Paginação e ordenação avançadas
- Logs centralizados para monitoramento
- Integração com gateway de pagamento real
- Verificação de e-mail com link de confirmação
- Sistema de cache para dados de leitura frequente

---

## Autor

Gabriel Petry  
Email: gabrielpetry23@protonmail.com  
GitHub: https://github.com/gabrielpetry23
