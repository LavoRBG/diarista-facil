# Diarista Fácil

Protótipo de sistema desktop para organizar a agenda de faxinas de duas diaristas.
Projeto Integrado do curso de Análise e Desenvolvimento de Sistemas (UNOPAR) - Lavousier Robspierre Barros Gomes.

## Funcionalidades implementadas
1. **Cadastro de clientes** - grava o cliente no banco SQLite e lista os já cadastrados (com busca por nome).
2. **Agendamento de faxina** - lê clientes, diaristas e serviços do banco, impede marcar a mesma diarista
   em dia/turno já ocupado e grava o agendamento.
3. **Agenda do dia** (consulta) - mostra as faxinas de uma data, por diarista, com o total previsto.
4. Login com senha (hash SHA-256) e perfis Atendente / Diarista.

## Tecnologias
Java 17+ (Swing), SQLite, driver JDBC `org.xerial:sqlite-jdbc`.

## Como executar
1. Baixe o driver `sqlite-jdbc` em https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/ e salve em `lib/sqlite-jdbc.jar`.
2. Compile:
   ```
   javac -encoding UTF-8 -d out -cp lib/sqlite-jdbc.jar $(find src -name "*.java")
   ```
   No Windows (PowerShell): `javac -encoding UTF-8 -d out -cp lib\sqlite-jdbc.jar (Get-ChildItem -Recurse src -Filter *.java).FullName`
3. Execute (use `;` no lugar de `:` no Windows):
   ```
   java -cp out:lib/sqlite-jdbc.jar br.com.diaristafacil.Main
   ```
O arquivo `diarista_facil.db` é criado na primeira execução.

Usuários de teste: `ana` / `ana123` (atendente), `maria` / `maria123` e `joana` / `joana123` (diaristas).

## Estrutura
```
src/br/com/diaristafacil/
  Main.java
  model/  Cliente, Diarista, Servico, Agendamento, Turno, Usuario
  dao/    Conexao, ClienteDAO, AgendamentoDAO, CadastroBasicoDAO, UsuarioDAO
  view/   TelaLogin, TelaPrincipal, TelaClientes, TelaAgendamento, TelaAgendaDia
docs/schema.sql
```
