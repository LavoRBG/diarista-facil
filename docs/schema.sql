-- Diarista Fácil - estrutura do banco (SQLite)
-- As mesmas tabelas são criadas automaticamente pela classe Conexao na primeira execução.
CREATE TABLE usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    login TEXT NOT NULL UNIQUE,
    senha_hash TEXT NOT NULL,
    perfil TEXT NOT NULL CHECK (perfil IN ('ATENDENTE','DIARISTA'))
);
CREATE TABLE cliente (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    telefone TEXT NOT NULL,
    endereco TEXT NOT NULL,
    bairro TEXT,
    observacao TEXT,
    data_cadastro TEXT NOT NULL DEFAULT (date('now','localtime'))
);
CREATE TABLE diarista (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    telefone TEXT NOT NULL,
    ativa INTEGER NOT NULL DEFAULT 1
);
CREATE TABLE servico (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao TEXT NOT NULL,
    valor_base REAL NOT NULL
);
CREATE TABLE agendamento (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id INTEGER NOT NULL REFERENCES cliente(id),
    diarista_id INTEGER NOT NULL REFERENCES diarista(id),
    servico_id INTEGER NOT NULL REFERENCES servico(id),
    data TEXT NOT NULL,
    turno TEXT NOT NULL CHECK (turno IN ('MANHA','TARDE','INTEGRAL')),
    valor REAL NOT NULL,
    status TEXT NOT NULL DEFAULT 'AGENDADO',
    pago INTEGER NOT NULL DEFAULT 0
);
