-- DADOS INICIAIS PARA TESTE E DEMONSTRAÇÃO
-- Recomenda-se usar "docker-compose down --volumes" antes de "docker-compose up -d"
-- para garantir que o banco seja reinicializado com estes dados.

-- USUÁRIOS
INSERT INTO users (id, name, email, password, role, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Gabriel Silva', 'gabriel.user@example.com', '$2a$10$bPcz1FRL4KD3zgmUfq2KJeJppGyS0/eEPyc.fLe5HUYzM/zebZvd6', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- gabriel123
    (gen_random_uuid(), 'Ana Gerente', 'gerente.admin@example.com', '$2a$10$P.Npi8ciMlNjgnHNazmM3et8ZunWtU9C5M77lNTIun1pTfVENjMVy', 'MANAGER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- gerente123
    (gen_random_uuid(), 'Maria Souza', 'maria.customer@example.com', '$2a$10$1KVva43JqEE3jneySMYidO7/phJ.w5qjQXwYuiQ0H0VVlzzDfL4oS', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- maria123
    (gen_random_uuid(), 'João Pereira', 'joao.customer@example.com', '$2a$10$lu3Ok7dmyiVV62T.sMb5HuiIzUmgAUTe50UXoscow4k5ukcOM/AHm', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- joao123

-- CATEGORIAS
INSERT INTO categories (id, name) VALUES
    (gen_random_uuid(), 'Eletrônicos'),
    (gen_random_uuid(), 'Roupas'),
    (gen_random_uuid(), 'Livros'),
    (gen_random_uuid(), 'Casa e Cozinha'),
    (gen_random_uuid(), 'Esportes e Lazer');

-- PRODUTOS
INSERT INTO products (id, name, description, price, stock, user_id, category_id, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Laptop Gamer XYZ', 'Notebook de alta performance para jogos com RTX 3080.', 7500.00, 15,
     (SELECT id FROM users WHERE email = 'gerente.admin@example.com'),
     (SELECT id FROM categories WHERE name = 'Eletrônicos'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Smart TV 55 polegadas', 'TV 4K com HDR e sistema operacional inteligente.', 3200.00, 25,
     (SELECT id FROM users WHERE email = 'gerente.admin@example.com'),
     (SELECT id FROM categories WHERE name = 'Eletrônicos'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Fone de Ouvido Bluetooth', 'Fones sem fio com cancelamento de ruído.', 250.00, 120,
     (SELECT id FROM users WHERE email = 'gerente.admin@example.com'),
     (SELECT id FROM categories WHERE name = 'Eletrônicos'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Camiseta Algodão Orgânico', 'Camiseta confortável e sustentável em diversas cores.', 59.90, 200,
     (SELECT id FROM users WHERE email = 'gerente.admin@example.com'),
     (SELECT id FROM categories WHERE name = 'Roupas'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Calça Jeans Skinny', 'Calça jeans de corte moderno, cintura alta.', 120.00, 150,
     (SELECT id FROM users WHERE email = 'gerente.admin@example.com'),
     (SELECT id FROM categories WHERE name = 'Roupas'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'O Guia do Mochileiro das Galáxias', 'Clássico da ficção científica por Douglas Adams.', 45.00, 80,
     (SELECT id FROM users WHERE email = 'gerente.admin@example.com'),
     (SELECT id FROM categories WHERE name = 'Livros'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- IMAGENS DE PRODUTOS
INSERT INTO product_images (id, product_id, image_url, is_main) VALUES
    (gen_random_uuid(), (SELECT id FROM products WHERE name = 'Laptop Gamer XYZ'), 'http://example.com/images/laptop_gamer_main.jpg', TRUE),
    (gen_random_uuid(), (SELECT id FROM products WHERE name = 'Laptop Gamer XYZ'), 'http://example.com/images/laptop_gamer_side.jpg', FALSE),
    (gen_random_uuid(), (SELECT id FROM products WHERE name = 'Smart TV 55 polegadas'), 'http://example.com/images/smarttv_main.jpg', TRUE),
    (gen_random_uuid(), (SELECT id FROM products WHERE name = 'Fone de Ouvido Bluetooth'), 'http://example.com/images/fone_main.jpg', TRUE);

-- ENDEREÇOS
INSERT INTO addresses (id, user_id, street, number, complement, city, state, zip_code, country) VALUES
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'gabriel.user@example.com'), 'Rua das Flores', '123', 'Apto 101', 'São Paulo', 'SP', '01000-000', 'Brasil'),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'gerente.admin@example.com'), 'Avenida Principal', '456', NULL, 'Rio de Janeiro', 'RJ', '20000-000', 'Brasil'),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'maria.customer@example.com'), 'Alameda dos Sonhos', '789', 'Casa B', 'Belo Horizonte', 'MG', '30000-000', 'Brasil'),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'joao.customer@example.com'), 'Travessa da Paz', '10', NULL, 'Porto Alegre', 'RS', '90000-000', 'Brasil');

-- MÉTODOS DE PAGAMENTO
INSERT INTO payment_methods (id, type, provider, payment_token, last4_digits, card_brand, user_id, created_at, updated_at) VALUES
    (gen_random_uuid(), 'CREDIT_CARD', 'Visa', 'tok_visa_1234567890abc', '1234', 'Visa', (SELECT id FROM users WHERE email = 'gabriel.user@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'DEBIT_CARD', 'Mastercard', 'tok_mc_9876543210def', '5678', 'Mastercard', (SELECT id FROM users WHERE email = 'maria.customer@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'PIX', 'Banco Central', 'pix_chave_joao@example.com', NULL, NULL, (SELECT id FROM users WHERE email = 'joao.customer@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- CUPONS
INSERT INTO coupons (id, code, discount_amount, discount_percentage, valid_until, is_active, created_at, updated_at) VALUES
    (gen_random_uuid(), 'DESCONTO10', 10.00, 0.00, '2025-12-31', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'PRIMEIRACOMPRA20', 0.00, 20.00, '2025-11-30', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'FRETEGRATIS', 0.00, 0.00, '2024-07-15', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'CUPOMEXPIRADO', 5.00, 0.00, '2024-01-01', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- AVALIAÇÕES DE PRODUTOS
INSERT INTO product_reviews (id, product_id, user_id, rating, comment, created_at) VALUES
    (gen_random_uuid(), (SELECT id FROM products WHERE name = 'Laptop Gamer XYZ'), (SELECT id FROM users WHERE email = 'gabriel.user@example.com'), 9, 'Excelente laptop, muito rápido para jogos e trabalho!', CURRENT_TIMESTAMP),
    (gen_random_uuid(), (SELECT id FROM products WHERE name = 'Smart TV 55 polegadas'), (SELECT id FROM users WHERE email = 'maria.customer@example.com'), 8, 'Imagem muito boa, som razoável. Ótimo custo-benefício.', CURRENT_TIMESTAMP),
    (gen_random_uuid(), (SELECT id FROM products WHERE name = 'Fone de Ouvido Bluetooth'), (SELECT id FROM users WHERE email = 'joao.customer@example.com'), 7, 'Confortável, mas a bateria poderia durar mais.', CURRENT_TIMESTAMP);

-- CARRINHOS (Inicialmente vazios ou com itens)
INSERT INTO carts (id, user_id, created_at, updated_at, total) VALUES
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'gabriel.user@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0.00),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'maria.customer@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0.00);

-- ITENS DO CARRINHO (Usando subconsultas para IDs de produtos)
INSERT INTO cart_items (id, cart_id, product_id, quantity, total) VALUES
    (gen_random_uuid(), (SELECT id FROM carts WHERE user_id = (SELECT id FROM users WHERE email = 'gabriel.user@example.com')), (SELECT id FROM products WHERE name = 'Smart TV 55 polegadas'), 1, 3200.00),
    (gen_random_uuid(), (SELECT id FROM carts WHERE user_id = (SELECT id FROM users WHERE email = 'gabriel.user@example.com')), (SELECT id FROM products WHERE name = 'Fone de Ouvido Bluetooth'), 2, 500.00);

-- PEDIDOS
-- Pedido PENDENTE (Gabriel)
INSERT INTO orders (id, user_id, status, total, delivery_address_id, payment_method_id, coupon_id, created_at, updated_at) VALUES
    (gen_random_uuid(),
     (SELECT id FROM users WHERE email = 'gabriel.user@example.com'),
     'PENDING', 3700.00, -- 3200 (TV) + 500 (Fone x2)
     (SELECT id FROM addresses WHERE user_id = (SELECT id FROM users WHERE email = 'gabriel.user@example.com') LIMIT 1),
     (SELECT id FROM payment_methods WHERE user_id = (SELECT id FROM users WHERE email = 'gabriel.user@example.com') LIMIT 1),
     NULL,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES
    (gen_random_uuid(), (SELECT id FROM orders WHERE user_id = (SELECT id FROM users WHERE email = 'gabriel.user@example.com') AND status = 'PENDING' LIMIT 1), (SELECT id FROM products WHERE name = 'Smart TV 55 polegadas'), 1, 3200.00),
    (gen_random_uuid(), (SELECT id FROM orders WHERE user_id = (SELECT id FROM users WHERE email = 'gabriel.user@example.com') AND status = 'PENDING' LIMIT 1), (SELECT id FROM products WHERE name = 'Fone de Ouvido Bluetooth'), 2, 250.00);


-- Pedido PAGO (Maria)
INSERT INTO orders (id, user_id, status, total, delivery_address_id, payment_method_id, coupon_id, created_at, updated_at) VALUES
    (gen_random_uuid(),
     (SELECT id FROM users WHERE email = 'maria.customer@example.com'),
     'PAID', 7500.00,
     (SELECT id FROM addresses WHERE user_id = (SELECT id FROM users WHERE email = 'maria.customer@example.com') LIMIT 1),
     (SELECT id FROM payment_methods WHERE user_id = (SELECT id FROM users WHERE email = 'maria.customer@example.com') LIMIT 1),
     (SELECT id FROM coupons WHERE code = 'DESCONTO10'), -- Aplicando cupom
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES
    (gen_random_uuid(), (SELECT id FROM orders WHERE user_id = (SELECT id FROM users WHERE email = 'maria.customer@example.com') AND status = 'PAID' LIMIT 1), (SELECT id FROM products WHERE name = 'Laptop Gamer XYZ'), 1, 7500.00);

-- Tracking Details para pedido PAGO da Maria
INSERT INTO tracking_details (id, order_id, tracking_code, carrier, status, estimated_delivery, created_at, updated_at) VALUES
    (gen_random_uuid(), (SELECT id FROM orders WHERE user_id = (SELECT id FROM users WHERE email = 'maria.customer@example.com') AND status = 'PAID' LIMIT 1), 'BR123456789PT', 'Correios', 'EM_TRANSITO', '2025-06-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Notificação para pedido PAGO da Maria
INSERT INTO notifications (id, user_id, type, content, read_at, created_at, updated_at) VALUES
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'maria.customer@example.com'), 'ORDER_STATUS_UPDATE', 'Seu pedido #order_maria_paid_id foi PAGO e está em preparação!', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Email Queue para pedido PAGO da Maria
INSERT INTO email_queue (id, recipient_email, subject, body, status, attempts, created_at, updated_at) VALUES
    (gen_random_uuid(), 'maria.customer@example.com', 'Confirmação de Pagamento - Pedido #order_maria_paid_id', 'Olá Maria, seu pagamento foi confirmado! Acompanhe seu pedido com o código de rastreio BR123456789PT.', 'PENDING', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- Pedido ENTREGUE (João)
INSERT INTO orders (id, user_id, status, total, delivery_address_id, payment_method_id, coupon_id, created_at, updated_at) VALUES
    (gen_random_uuid(),
     (SELECT id FROM users WHERE email = 'joao.customer@example.com'),
     'DELIVERED', 59.90,
     (SELECT id FROM addresses WHERE user_id = (SELECT id FROM users WHERE email = 'joao.customer@example.com') LIMIT 1),
     (SELECT id FROM payment_methods WHERE user_id = (SELECT id FROM users WHERE email = 'joao.customer@example.com') LIMIT 1),
     (SELECT id FROM coupons WHERE code = 'FRETEGRATIS'),
     '2025-04-01 10:00:00', '2025-04-10 15:30:00');

INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES
    (gen_random_uuid(), (SELECT id FROM orders WHERE user_id = (SELECT id FROM users WHERE email = 'joao.customer@example.com') AND status = 'DELIVERED' LIMIT 1), (SELECT id FROM products WHERE name = 'Camiseta Algodão Orgânico'), 1, 59.90);

-- Tracking Details para pedido ENTREGUE do João
INSERT INTO tracking_details (id, order_id, tracking_code, carrier, status, estimated_delivery, created_at, updated_at) VALUES
    (gen_random_uuid(), (SELECT id FROM orders WHERE user_id = (SELECT id FROM users WHERE email = 'joao.customer@example.com') AND status = 'DELIVERED' LIMIT 1), 'LOG987654321BR', 'Total Express', 'ENTREGUE', '2025-04-10', '2025-04-02 09:00:00', '2025-04-10 15:30:00');

-- Notificação para pedido ENTREGUE do João
INSERT INTO notifications (id, user_id, type, content, read_at, created_at, updated_at) VALUES
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'joao.customer@example.com'), 'ORDER_STATUS_UPDATE', 'Seu pedido #order_joao_delivered_id foi ENTREGUE!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Email Queue para pedido ENTREGUE do João
INSERT INTO email_queue (id, recipient_email, subject, body, status, attempts, created_at, updated_at) VALUES
    (gen_random_uuid(), 'joao.customer@example.com', 'Seu Pedido Chegou!', 'Olá João, seu pedido foi entregue com sucesso! Esperamos que goste.', 'SENT', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- CLIENTE OAUTH2
INSERT INTO client (id, client_id, client_secret, redirect_uri, scope) VALUES
    (gen_random_uuid, 'my-client-app', 'secret_for_demo_app', 'http://localhost:8080/authorized', 'read write');

-- NOTIFICAÇÕES GERAIS (Além das de pedido)
INSERT INTO notifications (id, user_id, type, content, read_at, created_at, updated_at) VALUES
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'gabriel.user@example.com'), 'PROMOTION', 'Confira nossas novas ofertas de eletrônicos!', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), (SELECT id FROM users WHERE email = 'gerente.admin@example.com'), 'INFO', 'Relatório de vendas mensal disponível.', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- FILA DE E-MAILS (Além das de pedido)
INSERT INTO email_queue (id, recipient_email, subject, body, status, attempts, created_at, updated_at) VALUES
    (gen_random_uuid(), 'gerente.admin@example.com', 'Alerta de Baixo Estoque', 'O produto "Laptop Gamer XYZ" está com estoque baixo (15 unidades).', 'PENDING', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'maria.customer@example.com', 'Promoção Exclusiva para Você!', 'Maria, temos uma oferta especial para você nos fones de ouvido!', 'PENDING', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);