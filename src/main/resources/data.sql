-- View para Alunos
CREATE OR REPLACE VIEW V_ALUNOS AS
SELECT
    u.id,
    u.nome,
    u.cpf,
    u.senha,
    u.status
FROM
    usuario u
JOIN
    aluno a ON u.id = a.id;

-- View para Professores
CREATE OR REPLACE VIEW V_PROFESSORES AS
SELECT
    u.id,
    u.nome,
    u.cpf,
    u.senha,
    u.status
FROM
    usuario u
JOIN
    professor p ON u.id = p.id;

-- View para Coordenadores
CREATE OR REPLACE VIEW V_COORDENADORES AS
SELECT
    u.id,
    u.nome,
    u.cpf,
    u.senha,
    u.status
FROM
    usuario u
JOIN
    coordenador c ON u.id = c.id;