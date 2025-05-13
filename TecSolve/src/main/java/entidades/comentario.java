package entidades;

public class comentario {

    private int idComentario;
    private int idUsuario;
    private int idProducto;
    private int calificacion;
    private String opinion;
    private String fechaComentario;

    public comentario() {
    }

    public comentario(int idComentario, int idUsuario, int idProducto, int calificacion, String opinion, String fechaComentario) {
        this.idComentario = idComentario;
        this.idUsuario = idUsuario;
        this.idProducto = idProducto;
        this.calificacion = calificacion;
        this.opinion = opinion;
        this.fechaComentario = fechaComentario;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(String fechaComentario) {
        this.fechaComentario = fechaComentario;
    }
    
    
            
}
