package com.example.andaluciaskills.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;



@Entity
@Getter
@Setter
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    private String nombre;

    private String apellidos;

    private String username;

    private String password;

    private String dni;

    private String role; // Puede ser ADMIN o EXPERTO

    @ManyToOne
    @JoinColumn(name = "Especialidad_id")
    @JsonBackReference
    private Especialidad especialidad;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Usamos un HashSet para garantizar la colección correcta
        HashSet<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.toUpperCase()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Aquí puedes implementar lógica adicional si es necesario
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Aquí puedes implementar lógica adicional si es necesario
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Aquí puedes implementar lógica adicional si es necesario
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Aquí puedes implementar lógica adicional si es necesario
        return true;
    }
}
