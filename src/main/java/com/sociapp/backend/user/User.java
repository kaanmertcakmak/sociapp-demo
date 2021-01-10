package com.sociapp.backend.user;

import com.sociapp.backend.auth.Token;
import com.sociapp.backend.content.Content;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

@Data
@Entity
public class User implements UserDetails {

	private static final long serialVersionUID = 3203364416943033232L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "{sociapp.constraint.username.NotNull.message}")
	@Size(min = 4, max = 50)
	@UniqueUsername
	private String username;

	@NotNull(message = "{sociapp.constraint.displayname.NotNull.message}")
	@Size(min = 4, max = 50)
	private String displayName;

	@NotNull(message = "{sociapp.constraint.password.NotNull.message}")
	@Size(min = 6, max = 50)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "{sociapp.constraint.password.Pattern.message}")
	private String password;

	private String image;

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Content> contents;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList("Role_User");
	}

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Token> tokens;

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
