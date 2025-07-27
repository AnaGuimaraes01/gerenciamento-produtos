-- DROP DAS TABELAS E VIEW (em ordem segura)
DROP VIEW IF EXISTS pedidos_por_status;
DROP TABLE IF EXISTS informacoes_pedidos;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS produtos;
DROP TABLE IF EXISTS clientes;

-- TABELA CLIENTES
CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    cpf VARCHAR(15) UNIQUE NOT NULL
);

-- TABELA PRODUTOS
CREATE TABLE produtos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    quantidade INT NOT NULL
);

-- TABELA PEDIDOS
CREATE TABLE pedidos ( 
    id SERIAL PRIMARY KEY,
    cliente_id INT NOT NULL REFERENCES clientes(id),
    valor_total NUMERIC(10, 2) NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDENTE',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    codigo VARCHAR(50) UNIQUE NOT NULL
);

-- TABELA INTERMEDIÁRIA: informações_pedidos
CREATE TABLE informacoes_pedidos (
    pedido_id INT NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    produto_id INT NOT NULL REFERENCES produtos(id),
    quantidade INT NOT NULL CHECK (quantidade > 0),
    PRIMARY KEY (pedido_id, produto_id)
);

-- FUNCTION: total de pedidos por cliente
CREATE OR REPLACE FUNCTION obter_total_pedidos(cpf_input VARCHAR)
RETURNS INTEGER AS $$
DECLARE
    total INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO total
    FROM pedidos p
    JOIN clientes c ON p.cliente_id = c.id
    WHERE c.cpf = cpf_input;

    RETURN total;
END;
$$ LANGUAGE plpgsql;

-- TRIGGER: impedir valor_total <= 0 em pedidos
CREATE OR REPLACE FUNCTION verificar_valor_total()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.valor_total <= 0 THEN
        RAISE EXCEPTION 'Valor total deve ser maior que zero.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_valor_total
BEFORE INSERT OR UPDATE ON pedidos
FOR EACH ROW
EXECUTE FUNCTION verificar_valor_total();

-- PROCEDURE: Atualizar status do pedido pelo código
CREATE OR REPLACE PROCEDURE atualizar_status_pedido(
    codigo_input VARCHAR,
    novo_status VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE pedidos
    SET status = novo_status
    WHERE codigo = codigo_input;

    IF NOT FOUND THEN
        RAISE NOTICE 'Pedido com código % não encontrado.', codigo_input;
    ELSE
        RAISE NOTICE 'Status do pedido atualizado para %.', novo_status;
    END IF;
END;
$$;

-- VIEW: pedidos por status (consulta com GROUP BY)
CREATE OR REPLACE VIEW pedidos_por_status AS
SELECT status, COUNT(*) AS quantidade
FROM pedidos
GROUP BY status;
