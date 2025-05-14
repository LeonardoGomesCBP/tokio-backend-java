#!/bin/bash

echo "Construindo e executando o projeto com Docker..."
docker-compose up --build -d

echo "Aguardando inicialização dos serviços..."
sleep 10

echo "Verificando status dos contêineres:"
docker-compose ps

echo "Aplicação disponível em: http://localhost:8080"
echo "Credenciais iniciais:"
echo "Email: admin@example.com"
echo "Senha: admin123"

echo "Para ver os logs: docker-compose logs -f"
echo "Para parar os serviços: docker-compose down" 