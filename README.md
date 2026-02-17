# URL Shortener 🔗

Sistema de encurtamento de URLs desenvolvido com Spring Boot, PostgreSQL e Redis, totalmente containerizado com Docker.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 4.0.2**
- **PostgreSQL 16**
- **Redis 7**
- **Docker & Docker Compose**
- **Maven**

## 📋 Pré-requisitos

- Docker (v20+)
- Docker Compose (v2+)
- Java 21 (apenas para desenvolvimento local)

## ⚙️ Configuração

### 1. Clone o repositório

```bash
git clone <seu-repositorio>
cd url-shortener
```

### 2. Configure as variáveis de ambiente

```bash
cp .env.example .env
```

Edite o arquivo `.env` conforme necessário:

```env
# Database
POSTGRES_DB=urlshortener
POSTGRES_USER=postgres
POSTGRES_PASSWORD=sua_senha_aqui

# Application
DB_URL=jdbc:postgresql://postgres:5432/urlshortener
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_aqui
APP_BASE_URL=http://localhost:8080/
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

### 3. Build e inicialização

```bash
# Build da aplicação
./mvnw clean package -DskipTests

# Subir os containers
docker compose up -d --build
```

### 4. Verificar logs

```bash
docker compose logs -f app
```

## 🎯 Uso da API

### Encurtar URL

```bash
POST http://localhost:8080/api/urls
Content-Type: application/json

{
  "url": "https://example.com/very/long/url"
}
```

**Resposta:**

```json
{
  "id": 1,
  "shortCode": "abc123",
  "originalUrl": "https://example.com/very/long/url",
  "shortUrl": "http://localhost:8080/abc123",
  "clicks": 0,
  "createdAt": "2026-02-17T10:30:00",
  "lastAccessedAt": null,
  "expiresAt": "2026-02-24T10:30:00",
  "expired": false
}
```

### Redirecionar para URL original

```bash
GET http://localhost:8080/abc123
```

Redireciona automaticamente para a URL original.

### Obter estatísticas

```bash
GET http://localhost:8080/api/urls/abc123
```

**Resposta:**

```json
{
  "id": 1,
  "shortCode": "abc123",
  "originalUrl": "https://example.com/very/long/url",
  "shortUrl": "http://localhost:8080/abc123",
  "clicks": 5,
  "createdAt": "2026-02-17T10:30:00",
  "lastAccessedAt": "2026-02-17T11:45:00",
  "expiresAt": "2026-02-24T10:30:00",
  "expired": false
}
```

### Listar URLs

```bash
GET http://localhost:8080/api/urls?page=0&size=20
```

### Deletar URL

```bash
DELETE http://localhost:8080/api/urls/abc123
```

## 🛠️ Comandos Úteis

### Docker

```bash
# Subir aplicação (com rebuild)
docker compose up -d --build

# Subir aplicação (sem rebuild)
docker compose up -d

# Parar containers (mantém dados)
docker compose down

# Parar e limpar dados
docker compose down -v

# Ver logs
docker compose logs -f app

# Status dos containers
docker compose ps
```

### Desenvolvimento Local (sem Docker)

```bash
# Iniciar serviços locais
sudo systemctl start postgresql
sudo systemctl start redis

# Rodar aplicação
./mvnw spring-boot:run
```

### Testes

```bash
# Rodar todos os testes
./mvnw test

# Build com testes
./mvnw clean package
```

## 📁 Estrutura do Projeto

```
.
├── src/
│   ├── main/
│   │   ├── java/com/techatow/url_shortner/
│   │   │   ├── config/          # Configurações (CORS, etc)
│   │   │   ├── controllers/     # Endpoints REST
│   │   │   ├── dtos/            # Data Transfer Objects
│   │   │   ├── entities/        # Entidades JPA
│   │   │   ├── exceptions/      # Exceções customizadas
│   │   │   ├── handlers/        # Exception handlers
│   │   │   ├── repositories/    # Repositories JPA
│   │   │   ├── services/        # Lógica de negócio
│   │   │   └── utils/           # Utilitários
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── .env.example
└── pom.xml
```

## 🔒 Segurança

- URLs internas (localhost, 127.0.0.1, IPs privados) são bloqueadas
- Validação de protocolos (apenas http/https)
- URLs expiram após 7 dias
- Códigos curtos gerados com SecureRandom (62 caracteres: a-z, A-Z, 0-9)

## 🐛 Troubleshooting

### Porta já em uso

Se você tem PostgreSQL ou Redis rodando localmente:

```bash
# Parar serviços locais
sudo systemctl stop postgresql
sudo systemctl stop redis

# Ou mudar portas no docker-compose.yml
ports:
  - "5433:5432"  # PostgreSQL
  - "6380:6379"  # Redis
```

### Container não sobe

```bash
# Ver logs detalhados
docker compose logs app

# Rebuild forçado
docker compose down -v
./mvnw clean package -DskipTests
docker compose up -d --build
```

### Limpar tudo e recomeçar

```bash
docker compose down -v
docker system prune -a
./mvnw clean package -DskipTests
docker compose up -d --build
```

## 📝 Variáveis de Ambiente

| Variável               | Descrição                    | Padrão                                         |
| ---------------------- | ---------------------------- | ---------------------------------------------- |
| `POSTGRES_DB`          | Nome do banco de dados       | `urlshortener`                                 |
| `POSTGRES_USER`        | Usuário do PostgreSQL        | `postgres`                                     |
| `POSTGRES_PASSWORD`    | Senha do PostgreSQL          | -                                              |
| `DB_URL`               | JDBC URL de conexão          | `jdbc:postgresql://postgres:5432/urlshortener` |
| `APP_BASE_URL`         | URL base da aplicação        | `http://localhost:8080/`                       |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas para CORS | `http://localhost:5173`                        |

## 📄 Licença

Este projeto está sob a licença MIT.

## 👤 Autor

Antonio Gomes - [LinkedIn](https://www.linkedin.com/in/antonio-gomes-dev/)
