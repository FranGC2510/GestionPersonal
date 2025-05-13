package org.dam.fcojavier.gestionpersonal.utils;

public class Validacion {
    // Constantes para los mensajes de error
    public static final String ERROR_EMAIL = "El formato del email no es válido";
    public static final String ERROR_TELEFONO = "El teléfono debe contener exactamente 9 números";
    public static final String ERROR_PASSWORD = "La contraseña debe tener al menos 8 caracteres e incluir mayúsculas, minúsculas y números";

    /**
     * Valida que un email tenga un formato correcto.
     * El formato debe ser: texto@texto.texto
     *
     * @param email El email a validar
     * @return true si el email es válido, false en caso contrario
     */
    public static boolean isValidoEmail(String email) {
        boolean valido = false;
        if (email != null && !email.isEmpty()) {
            valido = email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        }

        return valido;
    }

    /**
     * Valida que un número de teléfono contenga exactamente 9 dígitos.
     *
     * @param telefono El número de teléfono a validar
     * @return true si el teléfono es válido, false en caso contrario
     */
    public static boolean isValidoTelefono(String telefono) {
        boolean valido = false;
        if (telefono != null && !telefono.isEmpty()) {
            valido = telefono.matches("\\d{9}");
        }
        // El teléfono debe contener exactamente 9 números
        return valido;
    }

    /**
     * Valida si una contraseña cumple con los requisitos de seguridad:
     * - Al menos 8 caracteres.
     * - Al menos una letra minúscula, una letra mayúscula y un número.
     *
     * @param password La contraseña a validar.
     * @return true si la contraseña es válida según los requisitos, false en caso contrario.
     */
    public static boolean isValidaPassword(String password){
        boolean valida = false;
        if (password != null && !password.isEmpty()) {
            valida = password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$");
        }
        return valida;
    }

    /**
     * Valida un email y devuelve un mensaje de error si no es válido.
     *
     * @param email El email a validar
     * @return null si el email es válido, un mensaje de error en caso contrario
     */
    public static String validateEmail(String email) {
        return isValidoEmail(email) ? null : ERROR_EMAIL;
    }

    /**
     * Valida un teléfono y devuelve un mensaje de error si no es válido.
     *
     * @param phone El teléfono a validar
     * @return null si el teléfono es válido, un mensaje de error en caso contrario
     */
    public static String validateTelefono(String phone) {
        return isValidoTelefono(phone) ? null : ERROR_TELEFONO;
    }

    /**
     * Valida una contraseña y devuelve un mensaje de error si no es válida.
     *
     * @param password La contraseña a validar
     * @return null si la contraseña es válida, un mensaje de error en caso contrario
     */
    public static String validaPassword(String password) {
        return isValidaPassword(password) ? null : ERROR_PASSWORD;
    }

}
