package com.techsolvers.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "servicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede exceder 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String descripcion;
    
    @NotBlank(message = "El tipo de servicio es obligatorio")
    @Column(name = "tipo_servicio", nullable = false, length = 50)
    private String tipoServicio; // "reparacion_pc", "reparacion_celular", "reparacion_impresora"
    
    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;
    
    @Column(name = "contenido_detallado", length = 2000)
    private String contenidoDetallado; // HTML content para mostrar en la página
    
    @Column(name = "precio_desde", precision = 10, scale = 2)
    private java.math.BigDecimal precioDesde; // Precio referencial "desde S/"
    
    @Column(name = "tiempo_estimado", length = 100)
    private String tiempoEstimado; // "1-3 días hábiles"
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "mostrar_en_inicio")
    private Boolean mostrarEnInicio = true; // Para mostrar en página principal
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relación con Usuario (quien creó el servicio)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;
    
    // Relación con ContactoServicio
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContactoServicio> contactos;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
        if (mostrarEnInicio == null) {
            mostrarEnInicio = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Constructor para crear servicio rápidamente
    public Servicio(String titulo, String descripcion, String tipoServicio, Usuario creadoPor) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoServicio = tipoServicio;
        this.creadoPor = creadoPor;
        this.activo = true;
        this.mostrarEnInicio = true;
    }
}