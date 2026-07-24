package com.peribook.post.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la creacion de una publicacion.
 * 

 * Es un  de Java 21 que solo contiene el campo .
 * No incluye el ID del autor porque ese se extrae del token JWT en el controlador,
 * no del cuerpo de la peticion. Esto evita que un cliente malicioso intente
 * suplantar a otro usuario enviando un autorId en el JSON.
 * 

 /> Las validaciones con Jakarta Validation (@NotBlank, @Size) aseguran que:
 * 
 *    * - El contenido no sea nulo ni este vacio (ni solo espacios)
 *    * - El contenido no exceda los 500 caracteres
 * 
 * Son las mismas reglas que protege el dominio en String),
 * pero las repito aqui para tener validacion temprana (fail-fast) antes de que la
 * peticion llegue al caso de uso. Esto se conoce como "Boundary Validation" o
 * "Defensive Validation".
 *
 * @author Alexander Rubio Caceres
 */
public record CrearPublicacionRequest(
        @NotBlank @Size(max = 500) String contenido
) {}
