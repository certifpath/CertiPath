package com.appAuditza.Auditza.Backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le prénom est requis")
    private String name;

    @NotBlank(message = "Le nom de famille est requis")
    private String lastName;

    // Le champ société peut être optionnel, donc on peut enlever @NotBlank si besoin
    private String company;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    // --- MODIFICATIONS POUR LE MOT DE PASSE ---
    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 12, message = "Le mot de passe doit contenir au moins 12 caractères")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
        message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial (@#$%^&+=!)"
    )
    private String password;
    // --- FIN DES MODIFICATIONS ---
    
    @NotBlank(message = "Le rôle est requis")
    private String role;
}