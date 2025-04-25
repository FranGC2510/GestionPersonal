package org.dam.fcojavier.gestionpersonal.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtilidades {
    /**
     * Genera un hash de la contraseña utilizando el algoritmo BCrypt.
     * @param password La contraseña en texto plano que se desea hashear.
     * @return El hash de la contraseña generado mediante BCrypt.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     * @param password La contraseña en texto plano a verificar.
     * @param hashedPassword El hash de la contraseña con el que se comparará.
     * @return true si la contraseña coincide con el hash, false en caso contrario.
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
    /**
     * Valida si una contraseña cumple con los requisitos de seguridad:
     * - Al menos 8 caracteres.
     * - Al menos una letra minúscula, una letra mayúscula y un número.
     *
     * @param password La contraseña a validar.
     * @return true si la contraseña es válida según los requisitos, false en caso contrario.
     */
    public static boolean validaPassword(String password){
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }
}
