import org.junit.Assert.*
import org.junit.Test

class ValidarDatosTest {

    @Test
    fun testCamposVacios() {
        val resultado = ValidadorUsuario.validar("", "correo@test.com", "1234", "1234")
        assertEquals(ValidadorUsuario.ResultadoValidacion.ERROR_CAMPOS_VACIOS, resultado)
    }

    @Test
    fun testContraseñasNoCoinciden() {
        val resultado = ValidadorUsuario.validar("Juan", "correo@test.com", "1234", "5678")
        assertEquals(ValidadorUsuario.ResultadoValidacion.ERROR_CONTRASEÑAS_NO_COINCIDEN, resultado)
    }

    @Test
    fun testValidacionExitosa() {
        val resultado = ValidadorUsuario.validar("Juan", "correo@test.com", "1234", "1234")
        assertEquals(ValidadorUsuario.ResultadoValidacion.EXITO, resultado)
    }
}
