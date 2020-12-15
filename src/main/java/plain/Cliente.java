package plain;

import sockets.SocketServidor;

public class Cliente {
    private String documento;
    private String nombres;
    private String apellidos;
    private String ciudad;
    private String telefonos;
    private String email;
    private String numeroCuenta;
    private float valorMovimiento;
    private SocketServidor.Transaciones tipoTransaccion;

    public Cliente(){
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) { this.documento = documento; }

    public String getNombres() { return nombres; }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public float getValorMovimiento() {
        return valorMovimiento;
    }

    public void setValorMovimiento(float valorMovimiento) {
        this.valorMovimiento = valorMovimiento;
    }

    public void setTipoTransaccion(SocketServidor.Transaciones tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public SocketServidor.Transaciones getTipoTransaccion() {
        return tipoTransaccion;
    }
}