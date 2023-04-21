package edu.pis.codebyte.model;

import edu.pis.codebyte.model.exceptions.InvalidEmailException;
import edu.pis.codebyte.model.exceptions.TermsAndConditionsNotAcceptedException;
import edu.pis.codebyte.model.exceptions.WeakPasswordException;

public class LoginUtils {

    public static String generateUsernameFromEmail(String email) {
        String username;
        // Obtener la parte del correo electrónico antes del símbolo '@'
        int index = email.indexOf('@');
        if (index > 0) {
            username = email.substring(0, index);
        } else {
            // Si el correo no tiene '@', utilizar todo el correo electrónico
            username = email;
        }
        // Eliminar caracteres no alfanuméricos y convertir a minúsculas
        username = username.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return username;
    }

    public static boolean isValidEmail(String email) throws InvalidEmailException {
        // Expresión regular para validar el formato del email
        String emailRegex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";;
        // Validar el email utilizando la expresión regular
        if (!email.matches(emailRegex)) {
            throw new InvalidEmailException("El email proporcionado no es válido.");
        }
        return true;
    }

    public static boolean isSecurePassword(String password) throws WeakPasswordException {
        // Verificar que la contraseña tenga al menos 8 caracteres
        if (password.length() < 8) {
            throw new WeakPasswordException("La contraseña debe tener al menos 8 caracteres.");
        }
        // Verificar que la contraseña tenga al menos una letra mayúscula, una letra minúscula y un número
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        if (!password.matches(passwordRegex)) {
            throw new WeakPasswordException("La contraseña debe tener al menos una letra mayúscula, una letra minúscula y un número.");
        }
        return true;
    }

    public static boolean areTermsAndConditionsAccepted(boolean checked) throws TermsAndConditionsNotAcceptedException {
        if (!checked){
            throw new TermsAndConditionsNotAcceptedException("Debes aceptar los terminos y condiciones");
        }
        return true;
    }
}
