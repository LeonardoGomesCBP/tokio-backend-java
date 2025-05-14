@echo off
echo Construindo e executando o projeto com Docker...
docker-compose up --build -d

echo Aguardando inicializacao dos servicos...
timeout /t 10 /nobreak > nul

echo Verificando status dos conteineres:
docker-compose ps

echo Aplicacao disponivel em: http://localhost:8080
echo Credenciais iniciais:
echo Email: admin@example.com
echo Senha: admin123

echo Para ver os logs: docker-compose logs -f
echo Para parar os servicos: docker-compose down 