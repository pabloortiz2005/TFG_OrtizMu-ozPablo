// Clase auxiliar para lógica de validación
object ValidadorUsuario {
    fun validar(nombre: String, correo: String, contrasena: String, confirmar: String): ResultadoValidacion {
        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
            return ResultadoValidacion.ERROR_CAMPOS_VACIOS
        }
        if (contrasena != confirmar) {
            return ResultadoValidacion.ERROR_CONTRASEÑAS_NO_COINCIDEN
        }
        return ResultadoValidacion.EXITO
    }

    enum class ResultadoValidacion {
        EXITO,
        ERROR_CAMPOS_VACIOS,
        ERROR_CONTRASEÑAS_NO_COINCIDEN
    }
}
