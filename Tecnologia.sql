-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: tecnologia
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cliente_roles`
--

DROP TABLE IF EXISTS `cliente_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente_roles` (
  `id_cli` int NOT NULL,
  `id_rol` int NOT NULL,
  PRIMARY KEY (`id_cli`,`id_rol`),
  KEY `id_rol` (`id_rol`),
  CONSTRAINT `cliente_roles_ibfk_1` FOREIGN KEY (`id_cli`) REFERENCES `clientes` (`id_cli`) ON DELETE CASCADE,
  CONSTRAINT `cliente_roles_ibfk_2` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente_roles`
--

LOCK TABLES `cliente_roles` WRITE;
/*!40000 ALTER TABLE `cliente_roles` DISABLE KEYS */;
INSERT INTO `cliente_roles` VALUES (8,1),(11,1),(14,2);
/*!40000 ALTER TABLE `cliente_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id_cli` int NOT NULL AUTO_INCREMENT,
  `nombre_cli` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `apellidos_cli` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `telefono` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `dni` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `correo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `contraseña` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id_cli`),
  UNIQUE KEY `dni` (`dni`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (8,'Pedrito','Soza','Jr Bogota 325','945123546','74512568','pedrito@gmail.com','$2a$10$tIKv83.CfuftfnWbEd6dXua.XaI7mHD6qKPQSsq1tvp4yScpKbJ3i'),(11,'Carlita','Meza','Jr Paruro','954874512','78965412','carla@gmail.com','$2a$10$XePpGt8B2WmiDgwSmzBR5ejGno3wZqr9MuHXSSG5Fl5sQVVN4/nGi'),(14,'admin','admin','Tecsolve','999999999','11111111','admin@admin.com','$2a$10$MFDvRXr6ecyuaDVNsvIFueqpbldiWFYlbH2X/iKLM.ZsjDbi2iSiS');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido` (
  `id_pedido` bigint NOT NULL AUTO_INCREMENT,
  `id_cli` int NOT NULL,
  `fecha` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'pendiente',
  `total` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_pedido`),
  KEY `id_cli` (`id_cli`),
  CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`id_cli`) REFERENCES `clientes` (`id_cli`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido_item`
--

DROP TABLE IF EXISTS `pedido_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido_item` (
  `id_item` bigint NOT NULL AUTO_INCREMENT,
  `id_pedido` bigint NOT NULL,
  `id_prod` bigint NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(38,2) DEFAULT NULL,
  `total` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id_item`),
  KEY `id_pedido` (`id_pedido`),
  KEY `id_prod` (`id_prod`),
  CONSTRAINT `pedido_item_ibfk_1` FOREIGN KEY (`id_pedido`) REFERENCES `pedido` (`id_pedido`) ON DELETE CASCADE,
  CONSTRAINT `pedido_item_ibfk_2` FOREIGN KEY (`id_prod`) REFERENCES `producto` (`id_prod`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido_item`
--

LOCK TABLES `pedido_item` WRITE;
/*!40000 ALTER TABLE `pedido_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedido_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id_prod` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `precio` double NOT NULL,
  `stock` int NOT NULL,
  `imagen` varchar(255) DEFAULT NULL,
  `categoria` varchar(255) NOT NULL,
  PRIMARY KEY (`id_prod`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'Xiaomi watch 1','Disponible de Lunes a Sábado.',359,100,'https://casemotions.pe/wp-content/uploads/2024/10/RDWATCH5ACTIV_0004.jpg','Smart Watch'),(2,'Samsung watch 3','Disponible de Lunes a Sábado.',499,100,'https://backoffice.max.com.gt/media/catalog/product/cache/94dd7777337ccc7ac42c8ee85d48fab6/S/M/SMR861NID_1_23072024110115.jpg','Smart Watch'),(3,'Smart Lg Tv','Disponible de Lunes a Sábado.',1599,15,'https://www.lg.com/ar/images/televisores/md06198516/gallery/DES_1_N.jpg','Tv'),(4,'Samsung 4K Tv','Disponible de Lunes a Sábado.',2899,15,'https://mercury.vtexassets.com/arquivos/ids/20750457/shp-11430233-0.jpg?v=638821697125800000','Tv'),(5,'Samsung Plasma 8k','Disponible de Lunes a Sábado.',5599,10,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSifgyxLDIkM10-WCRp13aZY3McX6972pEvoQ&s','Tv'),(6,'Audifono redmi buds 2','Disponible de Lunes a Sábado.',149,10,'https://casemotions.pe/wp-content/uploads/2024/01/BUDS5PRO_000.jpg','Audifonos'),(7,'Computadora oficina #1','Disponible de Lunes a Sábado.',1199,15,'https://gztienda.com.ar/img/Public/1116-producto-221-08-03-2023-05-03-34-img-oficina-5372.jpg','Computadoras'),(8,'Computadora gamer #1','Disponible de Lunes a Sábado.',4899,10,'https://compuciber.com/wp-content/uploads/2024/12/Computadora-Gamer-core-i5-12400f-12-ava-generacion.jpg','Computadoras'),(9,'Computadora Gamer #2','Disponible de Lunes a Sábado.',3999,8,'https://compuciber.com/wp-content/uploads/2024/12/Computadora-core-i9-11AVA-Generacion-600x600.jpg','Computadoras'),(10,'hyundai watch ultra','Disponible de Lunes a Sábado.',299,100,'https://http2.mlstatic.com/D_Q_NP_972903-MLU78774746014_092024-O.webp','Smart Watch'),(11,'Xiaomi redmi 14','Disponible de Lunes a Sábado.',1599,100,'https://plazavea.vteximg.com.br/arquivos/ids/30404716-418-418/imageUrl_1.jpg','Smartphones'),(12,'Samsung S23','Disponible de Lunes a Sábado.',2099,100,'https://smselectronic.com/wp-content/uploads/2023/05/SM-S911B-GREEN_01-600x600.jpg','Smartphones'),(13,'Iphone 11','Disponible de Lunes a Sábado.',2199,19,'https://m.media-amazon.com/images/I/51U8WCTTmCL.jpg','Smartphones'),(14,'Apple watch pro','Pan saludable con fibra y granos enteros.',899,80,'https://www.apple.com/newsroom/images/2024/09/introducing-apple-watch-series-10/article/Apple-Watch-Series-10-aluminum-jet-black-240909_inline.jpg.large.jpg','Smart Watch'),(15,'Samsung S25 Ultra','Disponible de Lunes a Sabado.',5499,5,'https://www.bing.com/th?id=OPHS.oTaFhXhBymG70A474C474&o=5&pid=21.1&w=171&h=173&qlt=100&dpr=1,3','Smartphones');
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'ROLE_ADMIN'),(1,'ROLE_USER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-14  4:34:11
