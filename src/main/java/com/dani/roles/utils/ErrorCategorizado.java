package com.dani.roles.utils;

import lombok.Getter;

@Getter
public enum ErrorCategorizado {

    USER_NOT_FOUND("USR001", "Usuario no encontrado"),
    USER_ALREADY_EXISTS("USR002", "Usuario ya existe"),
    USER_NOT_CREATED("USR003", "Usuario no creado"),
    USER_NOT_UPDATED("USR004", "Usuario no actualizado"),
    USER_NOT_DELETED("USR005", "Usuario no eliminado"),
    USER_NOT_FOUND_BY_USERNAME("USR006", "Usuario no encontrado por username"),
    USER_NOT_FOUND_BY_EMAIL("USR007", "Usuario no encontrado por email"),
    USER_NOT_FOUND_BY_ID("USR008", "Usuario no encontrado por id"),

    ROLE_NOT_FOUND("ROL001", "Rol no encontrado"),
    ROLE_ALREADY_EXISTS("ROL002", "Rol ya existe"),
    ROLE_NOT_CREATED("ROL003", "Rol no creado"),

    UNAUTHORIZED_ACCESS("AUT001", "Acceso no autorizado");




    private final String code;
    private final String message;

    ErrorCategorizado(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
