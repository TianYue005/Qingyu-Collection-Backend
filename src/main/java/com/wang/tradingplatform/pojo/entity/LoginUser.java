package com.wang.tradingplatform.pojo.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class LoginUser implements UserDetails {
    // 持有数据库查询出来的原始用户对象
    private userCertification user;

    public LoginUser(userCertification user) {
        this.user = user;
    }

    // 框架需要用户名，从数据库user中获取
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 框架需要密码，从数据库user中获取
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 封装权限 todo
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getPerms()));
        return authorities;
    }

    // 账号是否启用，根据数据库status字段判断
    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }

    // 下面三个默认true，根据业务拓展
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
}