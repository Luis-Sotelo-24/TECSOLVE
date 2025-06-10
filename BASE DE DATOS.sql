-- Usamos la base de datos 'tecsolve'
USE tecsolve;

-- Creamos la tabla de categorías de productos
CREATE TABLE categorias (
    idCategoria INT AUTO_INCREMENT, -- Clave primaria autoincremental (1, 2, 3...) para identificar cada categoría
    nombreCategoria VARCHAR(100),  -- Nombre de la categoría (ej: "Laptops", "Accesorios")
    PRIMARY KEY (idCategoria)      -- Establecemos idCategoria como clave primaria
);

-- Tabla para registrar los distintos estados de una venta (por ejemplo: pendiente, enviado, entregado)
CREATE TABLE estadoVenta (
    idEstado INT AUTO_INCREMENT,   -- Identificador del estado (ej: 1 = pendiente, 2 = completado)
    descripcion VARCHAR(50),       -- Texto que describe el estado
    PRIMARY KEY (idEstado)         -- Clave primaria
);

-- Tabla de usuarios del sistema (clientes o administradores)
CREATE TABLE usuario (
    idUsuario INT AUTO_INCREMENT,  -- Identificador único para cada usuario
    nombre VARCHAR(20),            -- Nombre del usuario
    apellido VARCHAR(20),          -- Apellido del usuario
    direccion VARCHAR(50),         -- Dirección física
    telefono VARCHAR(9),           -- Número de teléfono (se guarda como texto para incluir guiones o códigos)
    dni VARCHAR(8),                -- Documento Nacional de Identidad (texto porque no se calcula)
    correo VARCHAR(30),            -- Correo electrónico del usuario
    clave VARCHAR(32),             -- Contraseña cifrada (usualmente hash MD5, aunque hoy se prefiere más seguridad)
    rol ENUM('admin','cliente'),   -- Rol del usuario: puede ser administrador o cliente
    fechaRegistro DATETIME,        -- Fecha y hora en la que se registró
    PRIMARY KEY (idUsuario)        -- Clave primaria
);

-- Tabla de productos disponibles en la tienda
CREATE TABLE producto (
    idProducto INT AUTO_INCREMENT,     -- Identificador único para cada producto
    idCategoria INT,                   -- Relación con la tabla 'categorias'
    nombreProducto VARCHAR(20),       -- Nombre del producto
    descripcion VARCHAR(100),         -- Descripción del producto
    precio DECIMAL(10,2),             -- Precio del producto (hasta 99999999.99)
    stock INT,                        -- Cantidad disponible en inventario
    imagenUrl VARCHAR(255),           -- URL de la imagen del producto
    PRIMARY KEY (idProducto),         -- Clave primaria
    FOREIGN KEY (idCategoria) REFERENCES categorias(idCategoria) -- Relación con tabla categorías
);

-- Tabla que representa cada venta realizada
CREATE TABLE ventas (
    idVenta INT AUTO_INCREMENT,       -- ID único para cada venta
    idUsuario INT,                    -- Usuario que realizó la compra
    idEstado INT,                     -- Estado actual de la venta (relación con estadoVenta)
    fechaVenta DATETIME,              -- Fecha en que se realizó la venta
    totalVenta DECIMAL(10,2),         -- Monto total de la venta
    PRIMARY KEY (idVenta),            -- Clave primaria
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario),  -- Relación con usuario
    FOREIGN KEY (idEstado) REFERENCES estadoVenta(idEstado) -- Relación con estadoVenta
);

-- Detalle de cada venta (qué productos y cuántos se compraron)
CREATE TABLE detalleVenta (
    idDetalle INT AUTO_INCREMENT,     -- ID único para cada detalle
    idVenta INT,                      -- ID de la venta a la que pertenece el detalle
    idProducto INT,                   -- Producto que se compró
    cantidad INT,                     -- Cuántas unidades se compraron
    precioUnitario DECIMAL(10,2),     -- Precio por unidad en ese momento (importante si el precio cambia después)
    PRIMARY KEY (idDetalle),          -- Clave primaria
    FOREIGN KEY (idVenta) REFERENCES ventas(idVenta),         -- Relación con venta
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto)  -- Relación con producto
);

-- Comentarios que los usuarios pueden dejar sobre productos
CREATE TABLE comentario (
    idComentario INT AUTO_INCREMENT,  -- ID único del comentario
    idUsuario INT,                    -- Usuario que hizo el comentario
    idProducto INT,                   -- Producto al que se refiere el comentario
    calificacion TINYINT,             -- Calificación del producto (de 1 a 5 usualmente)
    opinion TEXT,                     -- Texto de la opinión
    fechaComentario DATETIME,        -- Fecha y hora del comentario
    PRIMARY KEY (idComentario),      -- Clave primaria
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario),    -- Relación con usuario
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto)  -- Relación con producto
);

-- Registro de acciones realizadas por los usuarios (bitácora o historial)
CREATE TABLE bitacora (
    idBitacora INT AUTO_INCREMENT,   -- ID de registro de acción
    idUsuario INT,                   -- Usuario que realizó la acción
    accion VARCHAR(255),             -- Descripción de la acción (ej: "Agregó un producto", "Inició sesión")
    fachaAccion DATETIME,            -- Fecha y hora de la acción (hay un typo, debería ser "fechaAccion")
    PRIMARY KEY (idBitacora),        -- Clave primaria
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario) -- Relación con usuario
);
