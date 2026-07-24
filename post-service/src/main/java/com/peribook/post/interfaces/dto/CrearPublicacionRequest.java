package com.peribook.post.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la creacion de una publicacion.
 * <p>
 * Es un {@code record} de Java 21 que solo contiene el campo {@code contenido}.
 * No incluye el ID del autor porque ese se extrae del token JWT en el controlador,
 * no del cuerpo de la peticion. Esto evita que un cliente malicioso intente
 * suplantar a otro usuario enviando un autorId en el JSON.
 * <p>
 /> Las validaciones con Jakarta Validation (@NotBlank, @Size) aseguran que:
 * <ul>
 *   <li>El contenido no sea nulo ni este vacio (ni solo espacios)</li>
 *   <li>El contenido no exceda los 500 caracteres</li>
 * </ul>
 * Son las mismas reglas que protege el dominio en {@link com.peribook.post.domain.Publicacion#crear(UUID, String)},
 * pero las repito aqui para tener validacion temprana (fail-fast) antes de que la
 * peticion llegue al caso de uso. Esto se conoce como "Boundary Validation" o
 * "Defensive Validation".
 *
 * @author Alexander Rubio Caceres
 */
public record CrearPublicacionRequest(
        @NotBlank @Size(max = 500) String contenido
) {}
