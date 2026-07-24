package com.peribook.auth.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la petición de login.
 * <p>
 * Es un {@code record} simple con anotaciones de Jakarta Bean Validation
 * que Spring valida automáticamente antes de llegar al controller. Las
 * reglas de validación aquí son sintácticas (formato, no vacío), no de
 * negocio — la validación de negocio (longitud mínima de la contraseña)
 * se replica también en {@link com.peribook.auth.domain.Password#fromRaw}
 * por seguridad, para que el dominio no dependa de la capa HTTP.
 * </p>
 * <p>
 * Prefiero tener esta validación duplicada (DTO + dominio) que omitirla
 * en alguna capa. El DTO protege al controller de datos malformados, el
 * dominio protege al modelo de datos inválidos aunque el controller cambie.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
public record LoginRequest(
        /**
         * Email del usuario. No puede estar en blanco y debe tener formato
         * de email válido (validación Jakarta {@code @Email}).
         */
        @NotBlank @Email String email,

        /**
         * Contraseña en texto plano. No puede estar en blanco y debe tener
         * al menos 8 caracteres (coherente con la validación en el dominio).
         */
        @NotBlank @Size(min = 8) String password
) {}
