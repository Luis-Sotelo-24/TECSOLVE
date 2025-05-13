/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 *
 * @author Guillermo
 */
public class bitacora {
    private int idBitacora;
    private int idUsuario;
    private String accion;
    private String fechaAccion;

    public bitacora() {
    }

    public bitacora(int idBitacora, int idUsuario, String accion, String fechaAccion) {
        this.idBitacora = idBitacora;
        this.idUsuario = idUsuario;
        this.accion = accion;
        this.fechaAccion = fechaAccion;
    }

    public int getIdBitacora() {
        return idBitacora;
    }

    public void setIdBitacora(int idBitacora) {
        this.idBitacora = idBitacora;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getFechaAccion() {
        return fechaAccion;
    }

    public void setFechaAccion(String fechaAccion) {
        this.fechaAccion = fechaAccion;
    }

    @Override
    public String toString() {
        return getAccion();
    }
    
}
