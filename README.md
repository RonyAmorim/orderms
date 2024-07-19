# Microserviço de Gerenciamento de Pedidos (orderms)

Este projeto é um Microserviço de Gerenciamento de Pedidos construído usando Spring Boot. Ele fornece funcionalidades para criar, rastrear e gerenciar pedidos via uma API RESTful.

## Desafio a Ser Resolvido

O projeto visa resolver o desafio de comunicação eficiente com o banco de dados e gerenciamento de pedidos utilizando RabbitMQ e MongoDB.

## Funcionalidades

- **Criação de Pedidos:** Crie novos pedidos.
- **Rastreamento de Pedidos:** Acompanhe o status dos pedidos.
- **Persistência:** Utiliza banco de dados MongoDB para armazenar informações dos pedidos.
- **Fila de Mensagens:** Integração com RabbitMQ para processamento assíncrono.

## Tecnologias Utilizadas

- **Spring Boot:** Framework para construção do microserviço.
- **Spring Data MongoDB:** Para interações com o banco de dados.
- **RabbitMQ:** Para gerenciamento de filas de mensagens.
- **Docker:** Para containerizar a aplicação.

## Começando

### Pré-requisitos

- Java 21 ou superior
- Docker
- Maven

## Funcionalidade de Disponibilizar as Informações via API
- **Recuperar pedidos:**
  - **GET** `/customers/{customerId}/orders`
  - Lista de pedidos realizados por cliente
  - Valor total de um pedido
  - Valor total de todos os pedidos
  - Quantidade de pedidos por cliente

### Testes

Foram realizados testes unitários usando JUnit e Mockito para garantir a qualidade do código e a funcionalidade correta do sistema.
