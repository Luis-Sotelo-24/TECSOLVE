package entidades;

public class venta {
    private int idVenta;
    private int idUsuario;
    private int idEstado;
    private String fechaVenta;
    private double totalVenta;

    public venta() {
    }

    public venta(int idVenta, int idUsuario, int idEstado, String fechaVenta, double totalVenta) {
        this.idVenta = idVenta;
        this.idUsuario = idUsuario;
        this.idEstado = idEstado;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }

    
    
}
