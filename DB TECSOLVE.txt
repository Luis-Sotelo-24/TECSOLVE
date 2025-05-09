use tecsolve;

create table categorias(
	idCategoria int auto_increment,
    nombreCategoria varchar(100),
    PRIMARY KEY (idCategoria)
);

create table estadoVenta (
	idEstado int auto_increment,
    descripcion varchar(50),
    PRIMARY KEY (idEstado)
);

create table usuario (
	idUsuario int AUTO_INCREMENT,    
    nombre varchar(20),
    apellido varchar(20),
    direccion varchar(50),
    telefono varchar(9),
    dni varchar(8),
    correo varchar(30),
    clave varchar(32),
    rol enum('admin','cliente'),
    fechaRegistro datetime,
    PRIMARY KEY(idUsuario)
);

create table producto(
	idProducto int auto_increment,
    idCategoria int,
    nombreProducto varchar(20),
    descripcion varchar (100),
    precio decimal(10,2),
    stock int,
    imagenUrl varchar(255),
    PRIMARY KEY (idProducto),
    FOREIGN KEY (idCategoria) REFERENCES categorias(idCategoria)
);

create table ventas(
	idVenta int auto_increment,
    idUsuario int,
    idEstado int,
    fechaVenta datetime,
    totalVenta decimal(10,2),
    PRIMARY KEY (idVenta),
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario),
    FOREIGN KEY (idEstado) REFERENCES estadoVenta(idEstado)
);



create table detalleVenta (
	idDetalle int auto_increment,
    idVenta int,
    idProducto int,
    cantidad int,
    precioUnitario decimal(10,2),
    PRIMARY KEY (idDetalle),
    FOREIGN KEY (idVenta) REFERENCES ventas(idVenta),
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto)
);

create table comentario (
	idComentario int auto_increment,
    idUsuario int,
    idProducto int,
    calificacion tinyint,
    opinion text,
    fechaComentario datetime,
    PRIMARY KEY (idComentario),
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario),
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto)
);

create table bitacora (
	idBitacora int auto_increment,
    idUsuario int,
    accion varchar(255),
    fachaAccion datetime,
    PRIMARY KEY (idBitacora),
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
)